package pl.kk.services.mdm.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceKeyValueDataDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceTableDataDTO;
import pl.kk.services.mdm.model.domain.Bookmaker;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.domain.MatchOdd;
import pl.kk.services.mdm.repository.MatchOddRepository;
import pl.kk.services.mdm.repository.MatchRepository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportingService {

    private final MatchRepository matchRepository;
    private final MatchOddRepository matchOddRepository;

    public ReportingService(MatchRepository matchRepository, MatchOddRepository matchOddRepository) {
        this.matchRepository = matchRepository;
        this.matchOddRepository = matchOddRepository;
    }

    public ReportInstanceKeyValueDataDTO generateMatchResultTypeReport(ZonedDateTime from, ZonedDateTime to) {
        List<Match> allStartingBetweenFromAndTo = matchRepository.findAllByStartTimeBetween(from, to);
        Map<String, Integer> allStartingBetweenFromAndToByResultTypeCount = allStartingBetweenFromAndTo
                .stream()
                .collect(Collectors.groupingBy(m -> m.getResultType().name(), Collectors.summingInt(m -> 1)));

        List<Map.Entry<String, String>> result = allStartingBetweenFromAndToByResultTypeCount
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> Pair.of(e.getKey(), e.getValue().toString()))
                .collect(Collectors.toList());

        return ReportInstanceKeyValueDataDTO.builder()
                .keyValueData(result).build();
    }

    public ReportInstanceTableDataDTO generateAbnormalMatchResultTypeReport(ZonedDateTime from, ZonedDateTime to) {
        List<Match> allStartingBetweenFromAndTo = matchRepository.findAllByStartTimeBetweenAndResultTypeNotIn(from, to, ImmutableList.of(Match.ResultType.NORMAL));
        List<String> header = Lists.newArrayList("Id", "Source system name", "Source system id", "Start time", "Result type", "Team names");
        List<List<String>> rows = allStartingBetweenFromAndTo.stream().map(match ->
                Lists.newArrayList(match.getId().toString(),
                        match.getSourceSystemName(),
                        match.getSourceSystemId(),
                        match.getStartTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
                        match.getResultType().name(), match.getHomeTeam().getName() + " - " + match.getAwayTeam().getName()
                )).collect(Collectors.toList());

        List<List<String>> tableData = Lists.<List<String>>newArrayList(header);
        tableData.addAll(rows);

        return ReportInstanceTableDataDTO.builder()
                .tableData(tableData).build();
    }


    public ReportInstanceKeyValueDataDTO generateMatchesAndMatchOddsCreatedReport(ZonedDateTime from, ZonedDateTime to) {
        long matchesCreatedCount = matchRepository.findByCreatedOnBetween(from, to);
        long matchOddsCreatedCount = matchOddRepository.findCountByCreatedOnBetween(from, to);

        List<Map.Entry<String, String>> result = Lists.newArrayListWithCapacity(2);

        result.add(Pair.of("Matches created", String.valueOf(matchesCreatedCount)));
        result.add(Pair.of("Match odd created", String.valueOf(matchOddsCreatedCount)));
        return ReportInstanceKeyValueDataDTO.builder()
                .keyValueData(result).build();
    }

    public ReportInstanceTableDataDTO generateMatchOddsCoverage(ZonedDateTime from, ZonedDateTime to) {
        List<MatchOdd> matchOddsCreated = matchOddRepository.findByCreatedOnBetween(from, to);
        Map<Bookmaker, List<MatchOdd>> oddsByBookmaker = matchOddsCreated
                .stream()
                .collect(Collectors.groupingBy(MatchOdd::getBookmaker));

        List<String> header = Lists.newArrayList("Bookmaker", "1X2", "DC", "BTS", "OU 0.5", "OU 1.5", "OU 2.5", "OU 3.5", "OU 4.5", "OU 5.5", "OU 6.5");
        List<String> allAggBookmakersRow = oddsStatsToTableRow("All", countOddsByType(matchOddsCreated));
        List<List<String>> bookmakersTableData = oddsByBookmaker
                .entrySet()
                .stream()
                .map(e -> oddsStatsToTableRow(e.getKey().getName(), countOddsByType(e.getValue())))
                .collect(Collectors.toList());

        List<List<String>> tableData = Lists.newArrayList();
        tableData.add(header);
        tableData.add(allAggBookmakersRow);
        tableData.addAll(bookmakersTableData);

        return ReportInstanceTableDataDTO.builder()
                .tableData(tableData).build();
    }

    private Map<MatchOddType, Integer> countOddsByType(List<MatchOdd> matchOdds) {
        return matchOdds.stream()
                .map(this::getMatchOddTypes)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(m -> 1)));
    }

    private List<String> oddsStatsToTableRow(String bookmaker, Map<MatchOddType, Integer> stats) {
        List<String> result = Lists.newArrayListWithCapacity(10);
        result.add(bookmaker);
        result.add(stats.getOrDefault(MatchOddType._1X2, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.BTS, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.DC, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_05, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_15, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_25, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_35, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_45, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_55, 0).toString());
        result.add(stats.getOrDefault(MatchOddType.OU_65, 0).toString());
        return result;
    }

    private List<MatchOddType> getMatchOddTypes(MatchOdd matchOdd) {
        List<MatchOddType> result = Lists.newArrayList();
        if (Objects.nonNull(matchOdd.getOdd1())) {
            result.add(MatchOddType._1X2);
        }
        if (Objects.nonNull(matchOdd.getOddBTSY())) {
            result.add(MatchOddType.BTS);
        }
        if (Objects.nonNull(matchOdd.getOdd1X())) {
            result.add(MatchOddType.DC);
        }
        if (Objects.nonNull(matchOdd.getOddO05())) {
            result.add(MatchOddType.OU_05);
        }
        if (Objects.nonNull(matchOdd.getOddO15())) {
            result.add(MatchOddType.OU_15);
        }
        if (Objects.nonNull(matchOdd.getOddO25())) {
            result.add(MatchOddType.OU_25);
        }
        if (Objects.nonNull(matchOdd.getOddO35())) {
            result.add(MatchOddType.OU_35);
        }
        if (Objects.nonNull(matchOdd.getOddO45())) {
            result.add(MatchOddType.OU_45);
        }
        if (Objects.nonNull(matchOdd.getOddO55())) {
            result.add(MatchOddType.OU_55);
        }
        if (Objects.nonNull(matchOdd.getOddO65())) {
            result.add(MatchOddType.OU_65);
        }

        return result;
    }

    private enum MatchOddType {
        _1X2("1X2"), DC("DC"), BTS("BTS"), OU_05("OU 0.5"), OU_15("OU 1.5"), OU_25("OU 2.5"), OU_35("OU 3.5"), OU_45("OU 4.5"), OU_55("OU 5.5"), OU_65("OU 6.5");

        private final String label;

        MatchOddType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
