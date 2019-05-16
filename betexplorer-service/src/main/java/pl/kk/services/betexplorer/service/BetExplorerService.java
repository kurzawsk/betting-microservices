package pl.kk.services.betexplorer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.betexplorer.model.Match;
import pl.kk.services.betexplorer.service.web.BetExplorerWebService;
import pl.kk.services.common.datamodel.dto.job.RunJobDTO;
import pl.kk.services.common.datamodel.dto.mdm.*;
import pl.kk.services.common.misc.BusinessRuntimeException;
import pl.kk.services.common.misc.FeignBadResponseWrapper;
import pl.kk.services.common.service.job.AsyncJobRunner;
import pl.kk.services.common.service.mdm.MdmServiceClient;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BetExplorerService {

    private static final String SYSTEM_NAME = "www.betexplorer.com";
    private static final Logger LOGGER = LoggerFactory.getLogger(BetExplorerService.class);

    private final MatchService matchService;
    private final BetExplorerWebService betExplorerWebService;
    private final MdmServiceClient mdmServiceClient;
    private final AsyncJobRunner asyncJobRunner;
    private final ObjectMapper objectMapper;

    public BetExplorerService(MatchService matchService, BetExplorerWebService betExplorerWebService,
                              MdmServiceClient mdmServiceClient, AsyncJobRunner asyncJobRunner, ObjectMapper objectMapper) {
        this.matchService = matchService;
        this.betExplorerWebService = betExplorerWebService;
        this.mdmServiceClient = mdmServiceClient;
        this.asyncJobRunner = asyncJobRunner;
        this.objectMapper = objectMapper;
    }

    public void findAndInsertNewMatches(RunJobDTO runJobDTO) {
        asyncJobRunner.runJob(runJobDTO.getJobId(), runJobDTO.getKey(), () -> {
            findAndInsertNewMatches();
            return null;
        });
    }

    public void updateMatchOdds(RunJobDTO runJobDTO) {
        asyncJobRunner.runJob(runJobDTO.getJobId(), runJobDTO.getKey(), () -> {
            updateMatchOdds();
            return null;
        });
    }

    public void checkMatchResults(RunJobDTO runJobDTO) {
        asyncJobRunner.runJob(runJobDTO.getJobId(), runJobDTO.getKey(), () -> {
            checkMatchResults();
            return null;
        });
    }

    private void findAndInsertNewMatches() {
        List<Match> matchesFromWebPage = betExplorerWebService.findAllNewMatches();
        List<Match> pendingMatches = matchService.findAllPendingMatches();
        List<Match> matchesToAdd = filterAlreadyExistingMatches(matchesFromWebPage, pendingMatches);
        List<String> errorMessages = Lists.newArrayList();

        for (Match match : matchesToAdd) {
            try {
                AddMatchResponseDTO response = mdmServiceClient.add(AddMatchRequestDTO.builder()
                        .startTime(match.getStartTime())
                        .homeTeamName(match.getHomeTeamName())
                        .awayTeamName(match.getAwayTeamName())
                        .sourceSystemId(match.getIdentifier())
                        .sourceSystemName(SYSTEM_NAME)
                        .build());
                processAddMatchResponseDTO(match, response, null, errorMessages);
            } catch (FeignBadResponseWrapper e) {
                LOGGER.error("FeignBadResponseWrapper occurred", e);
                processAddMatchResponseDTO(match, getResponseBody(e, AddMatchResponseDTO.class), e, errorMessages);
            } catch (Exception e) {
                LOGGER.error("Exception occurred", e);
                errorMessages.add(e.getMessage());
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new BusinessRuntimeException(errorMessages);
        }
    }

    private void updateMatchOdds() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Match> pendingMatches = matchService
                .findAllPendingMatches().stream()
                .filter(match -> match.getStartTime().isAfter(now))
                .collect(Collectors.toList());
        List<BookmakerDTO> bookmakers = mdmServiceClient.findAllBookmakers();

        List<AddMatchOddDTO> odds;
        try {
            odds = betExplorerWebService.findMatchOdds(pendingMatches, bookmakers);
        } catch (IOException e) {
            throw new BusinessRuntimeException(e);
        }

        Lists.partition(odds, 50)
                .forEach(mdmServiceClient::addOdds);
    }

    private void checkMatchResults() {
        List<String> errorMessages = Lists.newArrayList();
        List<Match> pendingMatches = matchService.findAllPendingMatches();
        Map<Long, SetMatchResultDTO> matchesToUpdateResultByMdmMatchId = betExplorerWebService.checkMatchResults(pendingMatches);

        for (Map.Entry<Long, SetMatchResultDTO> entry : matchesToUpdateResultByMdmMatchId.entrySet()) {
            List<String> errorMessagesForMatch = setMatchResult(entry.getKey(), entry.getValue());
            if (!errorMessagesForMatch.isEmpty()) {
                errorMessages.addAll(errorMessagesForMatch);
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new BusinessRuntimeException(errorMessages);
        }
    }

    private List<Match> filterAlreadyExistingMatches(List<Match> candidates, List<Match> alreadyTrackedMatches) {
        List<Match> result = Lists.newArrayList();
        for (Match candidate : candidates) {
            boolean isProbablyNewMatch = true;
            for (Match alreadyTrackedMatch : alreadyTrackedMatches) {
                if (candidate.getHomeTeamName().equals(alreadyTrackedMatch.getHomeTeamName()) &&
                        candidate.getAwayTeamName().equals(alreadyTrackedMatch.getAwayTeamName()) &&
                        Objects.equals(candidate.getIdentifier(), alreadyTrackedMatch.getIdentifier())) {
                    isProbablyNewMatch = false;
                    long diff = Math.abs(ChronoUnit.HOURS.between(candidate.getStartTime(), alreadyTrackedMatch.getStartTime()));
                    if (diff != 0) {
                        isProbablyNewMatch = true;
                    }
                } else if (candidate.getHomeTeamName().equals(alreadyTrackedMatch.getAwayTeamName()) && candidate.getAwayTeamName().equals(alreadyTrackedMatch.getHomeTeamName()) && Objects.equals(candidate.getIdentifier(), alreadyTrackedMatch.getIdentifier())) {
                    isProbablyNewMatch = false;
                    LOGGER.info("Found match teams switch. Candidate match: " + candidate + " already tracked Match: " + alreadyTrackedMatch);
                    result.add(alreadyTrackedMatch);
                }
            }
            if (isProbablyNewMatch) {
                result.add(candidate);
            }
        }
        return result;
    }

    private void processAddMatchResponseDTO(Match match, AddMatchResponseDTO response, Exception e, List<String> errorContainer) {
        if (Objects.nonNull(response) && Objects.nonNull(response.getResponseType())) {
            if (response.getResponseType() == AddMatchResponseDTO.ResponseType.MATCH_ADDED) {
                match.setMdmMatchId(response.getMatch().getId());
                matchService.addMatch(match);
            } else if (response.getResponseType() == AddMatchResponseDTO.ResponseType.TEAMS_SWITCHED) {
                String oldHomeTeamName = match.getHomeTeamName();
                match.setHomeTeamName(match.getAwayTeamName());
                match.setAwayTeamName(oldHomeTeamName);
                match.setMdmMatchId(response.getMatch().getId());
                matchService.addMatch(match);
            } else if (response.getResponseType() == AddMatchResponseDTO.ResponseType.MATCH_ALREADY_EXISTS) {
                if (!matchService.findByMdmMatchId(response.getMatch().getId()).isPresent()) {
                    match.setMdmMatchId(response.getMatch().getId());
                    matchService.addMatch(match);
                }
            } else if (response.getResponseType() == AddMatchResponseDTO.ResponseType.MATCH_POSTPONED_SILENTLY) {
                Optional<Match> oldMatch = matchService.findByMdmMatchId(response.getMatch().getId());
                if (oldMatch.isPresent()) {
                    oldMatch.get().setStartTime(response.getMatch().getStartTime());
                    matchService.addMatch(oldMatch.get());
                } else {
                    match.setStartTime(response.getMatch().getStartTime());
                    match.setMdmMatchId(response.getMatch().getId());
                    matchService.addMatch(match);
                }

            } else if (response.getResponseType() != AddMatchResponseDTO.ResponseType.MAPPING_CASES_CREATED) {
                errorContainer.add("Unexpected AddMatchResponseDTO response type received: " + response);
            }
        } else {
            errorContainer.add("Exception occurred while adding new match: " + match + " Root cause: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private List<String> setMatchResult(Long mdmMatchId, SetMatchResultDTO setMatchResultDTO) {
        List<String> errorMessages = Lists.newArrayList();
        try {
            mdmServiceClient.setMatchResult(setMatchResultDTO, mdmMatchId);
            markMatchAsFinished(mdmMatchId);
        } catch (FeignBadResponseWrapper e) {
            if (e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                LOGGER.error("Match " + mdmMatchId + "seems to be already finished in MDM", e);
                markMatchAsFinished(mdmMatchId);
            } else {
                LOGGER.error("Exception occurred while checking match results", e);
                errorMessages.add("Incorrect response while setting result of " + mdmMatchId + ", request: "
                        + setMatchResultDTO + " response error: " + e.getBody());
            }
        } catch (Exception e) {
            errorMessages.add(e.getMessage());
            LOGGER.error("Exception occurred while checking match results", e);
        }
        return errorMessages;
    }

    private void markMatchAsFinished(Long mdmMatchId) {
        Match match = matchService
                .findByMdmMatchId(mdmMatchId)
                .get();
        matchService.markMatchAsFinished(match);
    }

    private <T> T getResponseBody(FeignBadResponseWrapper feignBadResponseWrapper, Class<T> targetClass) {
        try {
            return objectMapper.readValue(feignBadResponseWrapper.getBody(), targetClass);
        } catch (IOException e) {
            return null;
        }
    }

}
