package pl.kk.services.betexplorer.service.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.springframework.stereotype.Service;
import pl.kk.services.betexplorer.model.Match;
import pl.kk.services.betexplorer.service.web.parser.*;
import pl.kk.services.common.datamodel.dto.mdm.AddMatchOddDTO;
import pl.kk.services.common.datamodel.dto.mdm.BookmakerDTO;
import pl.kk.services.common.datamodel.dto.mdm.SetMatchResultDTO;
import pl.kk.services.common.misc.AsyncUtil;
import pl.kk.services.common.misc.BusinessRuntimeException;
import pl.kk.services.common.misc.DateUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static pl.kk.services.betexplorer.service.web.BetExplorerWebService.RawOddData.RawOddDataType.*;

@Service
public class BetExplorerWebService {

    private static final Set<SetMatchResultDTO.AbnormalMatchFinishReason> FORMALLY_ANNULLED = Sets.immutableEnumSet(SetMatchResultDTO.AbnormalMatchFinishReason.CANCELLED,
            SetMatchResultDTO.AbnormalMatchFinishReason.POSTPONED);
    private static final String SYSTEM_NAME = "www.betexplorer.com";
    private static final String MATCH_IDENTIFIER_SYMBOL = "$matchIdentifier";
    private static final int CHECK_MATCH_RESULT_DAYS_OFFEST = 4;
    private static final int MIN_HOURS_LEFT_TO_ADD_MATCH = 12;
    private static final int MAX_HOURS_LEFT_TO_ADD_MATCH = 96;
    private static final String BASE_ODDS_URL_TEMPLATE = "https://www.betexplorer.com/gres/ajax/matchodds.php?e=";
    private static final String URL_1X2_TEMPLATE = BASE_ODDS_URL_TEMPLATE + MATCH_IDENTIFIER_SYMBOL + "&b=1x2";
    private static final String URL_OU_TEMPLATE = BASE_ODDS_URL_TEMPLATE + MATCH_IDENTIFIER_SYMBOL + "&b=ou";
    private static final String URL_1X2_DC_TEMPLATE = BASE_ODDS_URL_TEMPLATE + MATCH_IDENTIFIER_SYMBOL + "&b=dc";
    private static final String URL_BTS_TEMPLATE = BASE_ODDS_URL_TEMPLATE + MATCH_IDENTIFIER_SYMBOL + "&b=bts";
    private static final String MAIN_MATCHES_PAGE_LINK = "https://www.betexplorer.com/odds-filter/soccer/?rangeFrom=1&rangeTo=999&days=14";
    private static final String PATTERN_MATCH_RESULT_URL = "https://www.betexplorer.com/next/soccer/?year=#YYYY&month=#MM&day=#DD";
    private static final String REFERER = "Referer";
    private final Odds1X2Parser odds1X2Parser;
    private final OddsDCParser oddsDCParser;
    private final OddsOUParser oddsOUParser;
    private final OddsBTSParser oddsBTSParser;
    private final MatchResultParser matchResultParser;
    private final NewMatchParser newMatchParser;
    private final Consumer<String[]> blankReplacer = arr -> {
        for (int i = 0; i < arr.length; i++) {
            if (StringUtils.isBlank(arr[i])) {
                arr[i] = null;
            }
        }
    };

    public BetExplorerWebService(Odds1X2Parser odds1X2Parser, OddsDCParser oddsDCParser, OddsOUParser oddsOUParser,
                                 OddsBTSParser oddsBTSParser, MatchResultParser matchResultParser, NewMatchParser newMatchParser) {
        this.odds1X2Parser = odds1X2Parser;
        this.oddsDCParser = oddsDCParser;
        this.oddsOUParser = oddsOUParser;
        this.oddsBTSParser = oddsBTSParser;
        this.matchResultParser = matchResultParser;
        this.newMatchParser = newMatchParser;
    }

    public List<Match> findAllNewMatches() {
        List<Match> result = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        String body;
        try (AsyncHttpClient httpClient = Dsl.asyncHttpClient()){
            body = httpClient
                    .prepareGet(MAIN_MATCHES_PAGE_LINK)
                    .execute()
                    .get()
                    .getResponseBody();
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new BusinessRuntimeException(e);
        }
        Map<String, String[]> newMatchesRaw = newMatchParser.parse(body);

        for (Map.Entry<String, String[]> entry : newMatchesRaw.entrySet()) {
            LocalDateTime startTime = DateUtil.getDate(entry.getValue()[2], entry.getValue()[3], entry.getValue()[4],
                    entry.getValue()[5], entry.getValue()[6]);
            long diffInHours = ChronoUnit.HOURS.between(now, startTime);
            if (diffInHours <= MAX_HOURS_LEFT_TO_ADD_MATCH && diffInHours > MIN_HOURS_LEFT_TO_ADD_MATCH) {
                Match match = new Match();
                match.setIdentifier(entry.getKey());
                match.setHomeTeamName(entry.getValue()[0]);
                match.setAwayTeamName(entry.getValue()[1]);
                match.setStartTime(ZonedDateTime.of(startTime, ZoneId.systemDefault()));
                result.add(match);
            }
        }

        return result;
    }

    public Map<Long, SetMatchResultDTO> checkMatchResults(List<Match> matches) {
        if(matches.isEmpty()){
            return Collections.emptyMap();
        }

        Map<Long, SetMatchResultDTO> matchesToUpdate = Maps.newHashMap();
        LocalDate referenceDate = matches.stream()
                .map(Match::getStartTime)
                .min(Comparator.comparing(Function.identity()))
                .map(ZonedDateTime::toLocalDate)
                .get();
        Map<String, Match> matchesByIdentifier = matches.stream().
                collect(Collectors.toMap(Match::getIdentifier, Function.identity()));
        Map<String, String[]> rawResults;
        try {
            rawResults = getMatchResultsRaw(referenceDate);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new BusinessRuntimeException(e);
        }

        for (Map.Entry<String, Match> entry : matchesByIdentifier.entrySet()) {
            String[] rawResult = rawResults.get(entry.getKey());
            if (Objects.nonNull(rawResult)) {
                if (rawResult.length == 2) {
                    matchesToUpdate.put(entry.getValue().getMdmMatchId(),
                            SetMatchResultDTO.builder()
                                    .homeTeamScore(Integer.parseInt(rawResult[0]))
                                    .awayTeamScore(Integer.parseInt(rawResult[1]))
                                    .build());
                } else if (rawResult.length == 1) {
                    FORMALLY_ANNULLED.stream()
                            .filter(formallyAnnulled -> Objects.equals(formallyAnnulled.toString(), rawResult[0]))
                            .findFirst()
                            .ifPresent(formallyAnnulled ->
                                    matchesToUpdate.put(entry.getValue().getMdmMatchId(),
                                            SetMatchResultDTO.builder()
                                                    .abnormalMatchFinishReason(formallyAnnulled)
                                                    .build())
                            );
                }
            }
        }

        ZonedDateTime startTimeThresholdToMarkAsNotFound = ZonedDateTime.now().minusDays(1L);
        Map<Long, SetMatchResultDTO> matchesToMarkAsNotFound = matches.stream()
                .filter(m -> m.getStartTime().isBefore(startTimeThresholdToMarkAsNotFound))
                .collect(toMap(Match::getMdmMatchId,
                        m -> SetMatchResultDTO
                                .builder()
                                .abnormalMatchFinishReason(SetMatchResultDTO.AbnormalMatchFinishReason.NOT_FOUND)
                                .build()));
        matchesToUpdate.putAll(matchesToMarkAsNotFound);

        return matchesToUpdate;
    }

    public List<AddMatchOddDTO> findMatchOdds(List<Match> matches, List<BookmakerDTO> supportedBookmakers) throws IOException {
        int attemptsLeft = 10;
        List<AddMatchOddDTO> odds = Lists.newArrayList();
        Pair<List<AddMatchOddDTO>, List<Match>> result = Pair.of(null, matches);
        try(AsyncHttpClient httpClient = Dsl.asyncHttpClient()) {
            do {
                result = findMatchOddsAndGetFailures(httpClient, result.getValue(), supportedBookmakers);
                odds.addAll(result.getKey());
                attemptsLeft--;
            } while (!result.getValue().isEmpty() && attemptsLeft > 0);
        }

        if (attemptsLeft <= 0) {
            throw new BusinessRuntimeException("Too many failed attempts to find match odds", null);
        }

        return odds;
    }

    private Pair<List<AddMatchOddDTO>, List<Match>> findMatchOddsAndGetFailures(AsyncHttpClient httpClient, List<Match> matches, List<BookmakerDTO> supportedBookmakers) {
        List<AddMatchOddDTO> odds = Lists.newArrayList();
        List<Match> matchesToReprocess = Lists.newArrayList();
        for (Match match : matches) {
            List<CompletableFuture<RawOddData>> oddsRequests = getRawOddsDataFromWebPage(match, httpClient);
            try {
                List<RawOddData> rawOddsData = AsyncUtil.allOf(oddsRequests).join();
                odds.addAll(convertRawOddData(match.getMdmMatchId(), rawOddsData, supportedBookmakers));
            } catch (Exception e) {
                matchesToReprocess.add(match);
            }
        }
        return Pair.of(odds, matchesToReprocess);
    }

    private Map<String, String[]> getMatchResultsRaw(LocalDate referenceDate) throws IOException, ExecutionException, InterruptedException {
        Map<String,String[]> result = Maps.newHashMap();
        try(AsyncHttpClient httpClient = Dsl.asyncHttpClient()) {
            for (int i = 0; i <= CHECK_MATCH_RESULT_DAYS_OFFEST; i++) {
                String[] dayArr = DateUtil.getDateInFormat(referenceDate.minusDays(i), "dd.MM.yyyy").split("\\.");
                String url = PATTERN_MATCH_RESULT_URL.replace("#YYYY", dayArr[2]).replace("#MM", dayArr[1]).replace("#DD", dayArr[0]);
                String rawResponse = httpClient
                        .prepareGet(url)
                        .execute()
                        .get()
                        .getResponseBody();
                result.putAll(matchResultParser.parse(rawResponse));
            }
        }
        return result;
    }

    private List<AddMatchOddDTO> convertRawOddData(Long matchId, List<RawOddData> rawOddData, List<BookmakerDTO> supportedBookmakers) {
        Map<RawOddData.RawOddDataType, RawOddData> rawDataByType = rawOddData
                .stream()
                .collect(toMap(RawOddData::getType, Function.identity()));

        Map<String, String[]> odds1X2Raw = (Map<String, String[]>) rawDataByType.get(_1X2).getData();
        Map<String, String[]> odds1X2DCRaw = (Map<String, String[]>) rawDataByType.get(_1X2_DC).getData();
        Map<String, String[]> oddsBTSRaw = (Map<String, String[]>) rawDataByType.get(_BTS).getData();
        Table<String, String, String[]> oddsOURaw = (Table<String, String, String[]>) rawDataByType.get(_OU).getData();

        return supportedBookmakers.stream()
                .filter(bookmaker -> odds1X2Raw.keySet().contains(bookmaker.getAlternativeNames().get(SYSTEM_NAME)))
                .map(bookmaker -> {
                    String bookmakerName = bookmaker.getAlternativeNames().get(SYSTEM_NAME);
                    AddMatchOddDTO.AddMatchOddDTOBuilder matchOddBuilder = AddMatchOddDTO.builder();
                    matchOddBuilder
                            .matchId(matchId)
                            .bookmakerId(bookmaker.getId())
                            .odd1(NumberUtils.createBigDecimal(odds1X2Raw.get(bookmakerName)[0]))
                            .oddX(NumberUtils.createBigDecimal(odds1X2Raw.get(bookmakerName)[1]))
                            .odd2(NumberUtils.createBigDecimal(odds1X2Raw.get(bookmakerName)[2]));

                    if (Objects.nonNull(odds1X2DCRaw.get(bookmakerName))) {
                        matchOddBuilder
                                .odd1X(NumberUtils.createBigDecimal(odds1X2DCRaw.get(bookmakerName)[0]))
                                .odd12(NumberUtils.createBigDecimal(odds1X2DCRaw.get(bookmakerName)[1]))
                                .oddX2(NumberUtils.createBigDecimal(odds1X2DCRaw.get(bookmakerName)[2]));
                    }

                    if (Objects.nonNull(oddsBTSRaw.get(bookmakerName))) {
                        matchOddBuilder
                                .oddBTSY(NumberUtils.createBigDecimal(oddsBTSRaw.get(bookmakerName)[0]))
                                .oddBTSN(NumberUtils.createBigDecimal(oddsBTSRaw.get(bookmakerName)[1]));
                    }

                    if (oddsOURaw.contains(bookmakerName, SupportedOU._05.toString())) {
                        matchOddBuilder
                                .oddO05(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._05.toString())[0]))
                                .oddU05(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._05.toString())[1]));
                    }
                    if (oddsOURaw.contains(bookmakerName, SupportedOU._15.toString())) {
                        matchOddBuilder
                                .oddO15(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._15.toString())[0]))
                                .oddU15(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._15.toString())[1]));
                    }
                    if (oddsOURaw.contains(bookmakerName, SupportedOU._25.toString())) {
                        matchOddBuilder
                                .oddO25(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._25.toString())[0]))
                                .oddU25(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._25.toString())[1]));
                    }

                    if (oddsOURaw.contains(bookmakerName, SupportedOU._35.toString())) {
                        matchOddBuilder
                                .oddO35(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._35.toString())[0]))
                                .oddU35(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._35.toString())[1]));
                    }

                    if (oddsOURaw.contains(bookmakerName, SupportedOU._45.toString())) {
                        matchOddBuilder
                                .oddO45(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._45.toString())[0]))
                                .oddU45(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._45.toString())[1]));
                    }

                    if (oddsOURaw.contains(bookmakerName, SupportedOU._55.toString())) {
                        matchOddBuilder
                                .oddO55(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._55.toString())[0]))
                                .oddU55(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._55.toString())[1]));
                    }
                    if (oddsOURaw.contains(bookmakerName, SupportedOU._65.toString())) {
                        matchOddBuilder
                                .oddO65(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._65.toString())[0]))
                                .oddU65(NumberUtils.createBigDecimal(oddsOURaw.get(bookmakerName, SupportedOU._65.toString())[1]));
                    }

                    return matchOddBuilder.build();
                })
                .collect(toList());
    }

    private List<CompletableFuture<RawOddData>> getRawOddsDataFromWebPage(Match match, AsyncHttpClient http) {
        CompletableFuture<Map<String, String[]>> odds1X2Raw = getCurrentMatchOddsRaw(match.getIdentifier(), http, URL_1X2_TEMPLATE, odds1X2Parser);
        CompletableFuture<Map<String, String[]>> odds1X2DCRaw = getCurrentMatchOddsRaw(match.getIdentifier(), http, URL_1X2_DC_TEMPLATE, oddsDCParser);
        CompletableFuture<Map<String, String[]>> oddsBTSRaw = getCurrentMatchOddsRaw(match.getIdentifier(), http, URL_BTS_TEMPLATE, oddsBTSParser);
        CompletableFuture<Table<String, String, String[]>> oddsOURaw = getCurrentOUMatchOddsRaw(match.getIdentifier(), http)
                .thenApply(d -> {
                    d.values().forEach(blankReplacer);
                    return d;
                });

        return Arrays.asList(
                odds1X2Raw.thenApply(data -> new RawOddData(data, match.getMdmMatchId(), _1X2)),
                odds1X2DCRaw.thenApply(data -> new RawOddData(data, match.getMdmMatchId(), _1X2_DC)),
                oddsBTSRaw.thenApply(data -> new RawOddData(data, match.getMdmMatchId(), _BTS)),
                oddsOURaw.thenApply(data -> new RawOddData(data, match.getMdmMatchId(), _OU)));
    }

    private CompletableFuture<Map<String, String[]>> getCurrentMatchOddsRaw(String matchIdentifier, AsyncHttpClient http, String templateUrl, HtmlParser<Map<String, String[]>> parser) {
        String url = templateUrl.replace(MATCH_IDENTIFIER_SYMBOL, matchIdentifier);

        return http.prepareGet(url).addHeader(REFERER, url).execute()
                .toCompletableFuture()
                .thenApply(Response::getResponseBody)
                .thenApply(parser::parse)
                .thenApply(d -> {
                    d.values().forEach(blankReplacer);
                    return d;
                });
    }

    private CompletableFuture<Table<String, String, String[]>> getCurrentOUMatchOddsRaw(String matchIdentifier, AsyncHttpClient http) {
        String url = URL_OU_TEMPLATE.replace(MATCH_IDENTIFIER_SYMBOL, matchIdentifier);
        return http.executeRequest(new RequestBuilder().setUrl(url).setHeader(REFERER, url).build())
                .toCompletableFuture()
                .thenApply(Response::getResponseBody)
                .thenApply(oddsOUParser::parse);
    }

    public enum SupportedOU {
        _05("0.5"), _15("1.5"), _25("2.5"), _35("3.5"), _45("4.5"), _55("5.5"), _65("6.5");

        private final String text;

        SupportedOU(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    static class RawOddData<T> {

        long matchId;
        RawOddDataType type;
        T data;

        RawOddData(T data, long matchId, RawOddDataType type) {

            this.matchId = matchId;
            this.type = type;
            this.data = data;
        }

        long getMatchId() {
            return matchId;
        }

        void setMatchId(long matchId) {
            this.matchId = matchId;
        }

        RawOddDataType getType() {
            return type;
        }

        T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        enum RawOddDataType {
            _1X2, _1X2_DC, _BTS, _OU
        }
    }


}


