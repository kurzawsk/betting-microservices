package pl.kk.services.mdm.service.mapping;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.mdm.*;
import pl.kk.services.mdm.model.domain.MappingCase;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.dto.mapping.MatchMappingDTO;
import pl.kk.services.mdm.repository.MappingCaseRepository;
import pl.kk.services.mdm.service.MatchService;
import pl.kk.services.mdm.service.mapping.matcher.MatchMatcher;
import pl.kk.services.mdm.service.mapping.matcher.TeamMatcher;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static pl.kk.services.mdm.repository.MappingCaseRepository.byMappingParametersAndWithNewStatus;


@Service
@Transactional
public class MappingService {

    private static final long MATCH_SILENT_POSTPONE_HOURS_LOWER = 96L;
    private static final long MATCH_SILENT_POSTPONE_HOURS_UPPER = 96L;
    private final TeamMatcher teamMatcher;
    private final MatchMatcher matchMatcher;
    private final MappingCaseRepository mappingCaseRepository;
    private final MappingCaseConverter mappingConverter;
    private final MatchService matchService;

    public MappingService(MatchMatcher matchMatcher, MappingCaseRepository mappingCaseRepository,
                          MappingCaseConverter mappingConverter, TeamMatcher teamMatcher, MatchService matchService) {
        this.matchMatcher = matchMatcher;
        this.mappingCaseRepository = mappingCaseRepository;
        this.mappingConverter = mappingConverter;
        this.teamMatcher = teamMatcher;
        this.matchService = matchService;
    }

    public FindMatchResponseDTO processFindMatchRequest(FindMatchRequestDTO request, boolean createMappingCaseIfMatchNotFound) {
        BasicMatchDTO match = findMatchByExactNames(request.getHomeTeamName(), request.getAwayTeamName(), request.getStartTime());

        if (Objects.nonNull(match)) {
            return FindMatchResponseDTO.builder()
                    .matchId(match.getId())
                    .mappingCasesIds(Collections.emptyList())
                    .responseType(FindMatchResponseDTO.ResponseType.MATCH_FOUND)
                    .build();
        }

        if (createMappingCaseIfMatchNotFound) {
            List<Long> mappingCaseIds = findSimilarMatchesAndCreateCases(request.getHomeTeamName(), request.getAwayTeamName(), request.getStartTime(), request.getSourceSystemName())
                    .stream()
                    .map(BasicMappingCaseDTO::getId)
                    .collect(toList());
            FindMatchResponseDTO.ResponseType responseType = mappingCaseIds.isEmpty() ?
                    FindMatchResponseDTO.ResponseType.MATCH_NOT_FOUND
                    : FindMatchResponseDTO.ResponseType.MAPPING_CASES_CREATED;

            return FindMatchResponseDTO.builder()
                    .mappingCasesIds(mappingCaseIds)
                    .responseType(responseType)
                    .build();
        }

        return FindMatchResponseDTO.builder()
                .mappingCasesIds(Collections.emptyList())
                .responseType(FindMatchResponseDTO.ResponseType.MATCH_NOT_FOUND)
                .build();

    }

    public AddMatchResponseDTO processAddMatchRequest(AddMatchRequestDTO request) {
        BasicMatchDTO match = findMatchByExactNames(request.getHomeTeamName(), request.getAwayTeamName(), request.getStartTime());

        if (Objects.nonNull(match)) {
            return AddMatchResponseDTO.builder()
                    .match(match)
                    .mappingCasesIds(Collections.emptyList())
                    .responseType(AddMatchResponseDTO.ResponseType.MATCH_ALREADY_EXISTS)
                    .build();
        }

        List<BasicMappingCaseDTO> relatedMappingCases = findSimilarMatchesAndCreateCases(request.getHomeTeamName(), request.getAwayTeamName(), request.getStartTime(), request.getSourceSystemName());

        if (relatedMappingCases.isEmpty()) {
            List<Long> homeTeamCandidates = teamMatcher.findByExactName(request.getHomeTeamName());
            List<Long> awayTeamCandidates = teamMatcher.findByExactName(request.getAwayTeamName());

            if (homeTeamCandidates.isEmpty() && awayTeamCandidates.size() == 1) {
                return AddMatchResponseDTO.builder()
                        .match(matchService.addMatch(request.getHomeTeamName(),
                                awayTeamCandidates.get(0),
                                request.getStartTime(),
                                request.getSourceSystemName(),
                                request.getSourceSystemId())).responseType(AddMatchResponseDTO.ResponseType.MATCH_ADDED)
                        .mappingCasesIds(Collections.emptyList())
                        .build();

            } else if (homeTeamCandidates.size() == 1 && awayTeamCandidates.isEmpty()) {
                return AddMatchResponseDTO.builder()
                        .match(matchService.addMatch(homeTeamCandidates.get(0),
                                request.getAwayTeamName(),
                                request.getStartTime(),
                                request.getSourceSystemName(),
                                request.getSourceSystemId())).responseType(AddMatchResponseDTO.ResponseType.MATCH_ADDED)
                        .mappingCasesIds(Collections.emptyList())
                        .build();
            } else if (homeTeamCandidates.isEmpty() && awayTeamCandidates.isEmpty()) {
                return AddMatchResponseDTO.builder()
                        .match(matchService.addMatch(request.getHomeTeamName(),
                                request.getAwayTeamName(),
                                request.getStartTime(),
                                request.getSourceSystemName(),
                                request.getSourceSystemId()))
                        .mappingCasesIds(Collections.emptyList())
                        .responseType(AddMatchResponseDTO.ResponseType.MATCH_ADDED)
                        .build();
            } else if (homeTeamCandidates.size() == 1 && awayTeamCandidates.size() == 1) {
                Optional<BasicMatchDTO> silentTeamsSwitchedMatch = findMatchWithSilentlySwitchedTeams(homeTeamCandidates.get(0),
                        awayTeamCandidates.get(0),
                        request.getStartTime(),
                        request.getSourceSystemName(),
                        request.getSourceSystemId())
                        .map(m -> matchService.switchMatchTeams(m.getId()));

                if (silentTeamsSwitchedMatch.isPresent()) {
                    return AddMatchResponseDTO.builder()
                            .match(silentTeamsSwitchedMatch.get())
                            .mappingCasesIds(Collections.emptyList())
                            .responseType(AddMatchResponseDTO.ResponseType.TEAMS_SWITCHED)
                            .build();
                }

                Optional<BasicMatchDTO> silentlyPostponedMatch = findMatchSilentlyPostponed(homeTeamCandidates.get(0),
                        awayTeamCandidates.get(0),
                        request.getStartTime())
                        .map(m -> matchService.updateMatchStartTime(m.getId(), request.getStartTime()));


                return silentlyPostponedMatch.map(basicMatchDTO -> AddMatchResponseDTO.builder()
                        .match(basicMatchDTO)
                        .mappingCasesIds(Collections.emptyList())
                        .responseType(AddMatchResponseDTO.ResponseType.MATCH_POSTPONED_SILENTLY)
                        .build())
                        .orElseGet(() -> AddMatchResponseDTO.builder()
                                .match(matchService.addMatch(homeTeamCandidates.get(0),
                                        awayTeamCandidates.get(0),
                                        request.getStartTime(),
                                        request.getSourceSystemName(),
                                        request.getSourceSystemId()))
                                .mappingCasesIds(Collections.emptyList())
                                .responseType(AddMatchResponseDTO.ResponseType.MATCH_ADDED)
                                .build());
            } else {
                // raise new ambig_team_mapping_case
                //TODO implement when typersi/ betproposal source is added
                throw new NotImplementedException("Handling ambig team mapping case is not added yet, request "
                        + request + ", candidates: " + homeTeamCandidates + ", " + awayTeamCandidates);
            }
        } else {
            return AddMatchResponseDTO.builder()
                    .mappingCasesIds(relatedMappingCases
                            .stream()
                            .map(BasicMappingCaseDTO::getId)
                            .collect(toList()))
                    .responseType(AddMatchResponseDTO.ResponseType.MAPPING_CASES_CREATED)
                    .build();
        }
    }

    private BasicMatchDTO findMatchByExactNames(String homeTeamName, String awayTeamName, ZonedDateTime startTime) {
        return matchMatcher.findMatchByExactNames(homeTeamName, awayTeamName, startTime);
    }

    private List<MatchMappingDTO> findSimilarMatches(String homeTeamName, String awayTeamName, ZonedDateTime startTime, String sourceSystemName) {
        return matchMatcher.findSimilarMatches(homeTeamName, awayTeamName, startTime, sourceSystemName);
    }

    private List<BasicMappingCaseDTO> findSimilarMatchesAndCreateCases(String homeTeamName, String awayTeamName, ZonedDateTime startTime, String sourceSystemName) {
        List<MatchMappingDTO> matchMappingDTOs = findSimilarMatches(homeTeamName, awayTeamName, startTime, sourceSystemName);
        List<MappingCase> existingMappingCases = mappingCaseRepository.findAll(byMappingParametersAndWithNewStatus(matchMappingDTOs));
        List<MappingCase> mappingCasesToCreate = matchMappingDTOs.stream()
                .filter(matchMappingDTO -> !isMappingExisting(matchMappingDTO, existingMappingCases))
                .map(mappingConverter::toNewMappingCase)
                .collect(toList());

        return Stream.concat(existingMappingCases.stream(), mappingCaseRepository
                .saveAll(mappingCasesToCreate)
                .stream())
                .map(mappingConverter::toBasicDTO)
                .collect(toList());
    }

    private boolean isMappingExisting(MatchMappingDTO mapping, List<MappingCase> existingMappingCases) {
        return existingMappingCases.stream()
                .anyMatch(emc ->
                        emc.getMatch().getId().equals(mapping.getMatchId())
                                && emc.getHomeTeamName().equals(mapping.getHomeTeamName())
                                && emc.getAwayTeamName().equals(mapping.getAwayTeamName())
                );
    }

    private Optional<BasicMatchDTO> findMatchWithSilentlySwitchedTeams(Long homeTeamId, Long awayTeamId, ZonedDateTime startTime,
                                                                       String sourceSystem, String sourceSystemId) {
        return matchService.find(awayTeamId, homeTeamId, startTime, sourceSystem, sourceSystemId);
    }

    private Optional<BasicMatchDTO> findMatchSilentlyPostponed(Long homeTeamId, Long awayTeamId, ZonedDateTime startTime) {
        return matchService.find(homeTeamId, awayTeamId, startTime.minusHours(MATCH_SILENT_POSTPONE_HOURS_LOWER),
                startTime.plusHours(MATCH_SILENT_POSTPONE_HOURS_UPPER), Match.ResultType.UNKNOWN);
    }

}
