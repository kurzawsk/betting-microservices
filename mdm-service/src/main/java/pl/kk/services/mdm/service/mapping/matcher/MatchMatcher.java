package pl.kk.services.mdm.service.mapping.matcher;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.mdm.BasicMatchDTO;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.dto.MatchCandidateDTO;
import pl.kk.services.mdm.model.dto.mapping.MatchMappingDTO;
import pl.kk.services.mdm.model.dto.mapping.TeamMatchingResultDTO;
import pl.kk.services.mdm.repository.MatchRepository;
import pl.kk.services.mdm.service.MatchConverter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static pl.kk.services.mdm.repository.MatchRepository.candidatesChunk;

@Service
@Transactional
public class MatchMatcher {

    private static final int MATCH_CANDIDATE_REQUEST_LIMIT = 400;

    private final MatchRepository matchRepository;
    private final MatchConverter matchConverter;
    private final TeamMatcher teamMatcher;


    public MatchMatcher(MatchRepository matchRepository, MatchConverter matchConverter, TeamMatcher teamMatcher) {
        this.matchConverter = matchConverter;
        this.matchRepository = matchRepository;
        this.teamMatcher = teamMatcher;
    }

    public BasicMatchDTO findMatchByExactNames(String homeTeamName, String awayTeamName, ZonedDateTime startTime) {
        List<Long> homeTeamIds = teamMatcher.findByExactName(homeTeamName);
        List<Long> awayTeamIds = teamMatcher.findByExactName(awayTeamName);

        List<MatchCandidateDTO> candidates = homeTeamIds
                .stream()
                .flatMap(ht -> awayTeamIds
                        .stream()
                        .map(at -> MatchCandidateDTO.builder()
                                .homeTeamId(ht)
                                .awayTeamId(at)
                                .startTime(startTime)
                                .build()))
                .collect(toList());

        if (!candidates.isEmpty()) {
            List<Match> matches = matchRepository.findAll(candidatesChunk(candidates));

            if (matches.size() == 1) {
                return matchConverter.toBasicDTO(matches.get(0));
            }
        }
        return null;
    }

    public List<MatchMappingDTO> findSimilarMatches(String homeTeamName, String awayTeamName, ZonedDateTime startTime, String sourceSystemName) {
        List<TeamMatchingResultDTO> homeTeamCandidates = teamMatcher.findSimilar(homeTeamName);
        List<TeamMatchingResultDTO> awayTeamCandidates = teamMatcher.findSimilar(awayTeamName);

        List<Triple<TeamMatchingResultDTO, TeamMatchingResultDTO, Double>> teamPairsCandidates = homeTeamCandidates
                .stream()
                .map(htc -> awayTeamCandidates
                        .stream()
                        .map(atc -> Triple.of(htc, atc, htc.getSimilarityFactor() * atc.getSimilarityFactor())))
                .flatMap(identity())
                .sorted(comparing(Triple::getRight, reverseOrder()))
                .limit(MATCH_CANDIDATE_REQUEST_LIMIT)
                .collect(toList());

        Map<Long, Double> homeTeamCandidatesSimilarityFactors = teamPairsCandidates.stream().distinct()
                .collect(toMap(t -> t.getLeft().getMatchedTeamId(), t -> t.getLeft().getSimilarityFactor(), (p, q) -> p));
        Map<Long, Double> awayTeamCandidatesSimilarityFactors = teamPairsCandidates.stream()
                .collect(toMap(t -> t.getMiddle().getMatchedTeamId(), t -> t.getMiddle().getSimilarityFactor(), (p, q) -> p));

        List<MatchCandidateDTO> candidates = teamPairsCandidates
                .stream()
                .map(tpc -> MatchCandidateDTO.builder()
                        .homeTeamId(tpc.getLeft().getMatchedTeamId())
                        .awayTeamId(tpc.getMiddle().getMatchedTeamId())
                        .startTime(startTime)
                        .build())
                .collect(toList());

        List<Match> matches = matchRepository.findAll(candidatesChunk(candidates));
        return matches
                .stream()
                .map(match -> MatchMappingDTO.builder()
                        .matchId(match.getId())
                        .homeTeamName(homeTeamName)
                        .awayTeamName(awayTeamName)
                        .sourceSystemName(sourceSystemName)
                        .homeSimilarityFactor(homeTeamCandidatesSimilarityFactors.get(match.getHomeTeam().getId()))
                        .awaySimilarityFactor(awayTeamCandidatesSimilarityFactors.get(match.getAwayTeam().getId()))
                        .build())
                .collect(toList());
    }

}
