package pl.kk.services.mdm.service.mapping.matcher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pl.kk.services.mdm.model.dto.mapping.TeamMatchingResultDTO;
import pl.kk.services.mdm.service.mapping.TeamDataHolder;
import pl.kk.services.mdm.service.mapping.cleanse.CleanseService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.*;

@Service
public class TeamMatcher {

    private static final List<String> YOUNG_TEAM_MARKERS = ImmutableList.of("U23", "U2", "U21", "U20", "U19", "U18", "U17", "Jong");
    private static final double THRESHOLD_SIMILARITY_SCORE = 0.0005D;
    private static final double SIMILARITY_POWER_KEY = 4;

    private final TeamDataHolder teamDataHolder;
    private final CleanseService cleanseService;

    public TeamMatcher(TeamDataHolder teamDataHolder, CleanseService cleanseService) {
        this.teamDataHolder = teamDataHolder;
        this.cleanseService = cleanseService;
    }

    public List<Long> findByExactName(String name) {
        return teamDataHolder.findTeamIdByNameInAllNames(name);
    }

    public List<TeamMatchingResultDTO> findSimilar(String name) {
        String initiallyCleansedName = cleanseService.cleanseBasic(name);
        Set<String> tokens = cleanseService.cleanseFullAndTokenize(name);
        Set<Long> candidates = teamDataHolder.getTeamIdsByTokens(tokens);

        Set<Long> filteredCandidates = candidates
                .stream()
                .filter(c -> !teamDataHolder.isFalseName(c, initiallyCleansedName))
                .collect(toSet());

        Map<Long, Double> candidatesSimilarity = filteredCandidates.stream()
                .map(teamId -> Pair.of(teamId,
                        calculateMaxSimilarityFactor(tokens, teamDataHolder.getTeamNamesTokensPartitionedByName(teamId))))
                .collect(toMap(Pair::getKey, Pair::getValue));

        return filterAndTransformResult(name, candidatesSimilarity);
    }

    private double calculateMaxSimilarityFactor(Set<String> tokens, Set<Set<String>> candidateTokens) {
        return candidateTokens
                .stream()
                .map(ct -> this.calculateSimilarityFactor(tokens, ct))
                .mapToDouble(Double::doubleValue)
                .max()
                .getAsDouble();
    }

    private double calculateSimilarityFactor(Set<String> tokens, Set<String> candidateTokens) {
        Sets.SetView<String> commonTokens = Sets.intersection(tokens, candidateTokens);

        double commonTokensScore = calculateSumOfPoweredLengths(commonTokens);
        double tokensScore = calculateSumOfPoweredLengths(tokens);
        double candidateTokensScore = calculateSumOfPoweredLengths(candidateTokens);
        double maxWordsLength = Math.max(tokensScore, candidateTokensScore);
        double minWordsLength = Math.min(tokensScore, candidateTokensScore);

        return commonTokensScore * (minWordsLength / (Math.pow(maxWordsLength, SIMILARITY_POWER_KEY - 2)));
    }

    private double calculateSumOfPoweredLengths(Set<String> tokens) {
        return tokens
                .stream()
                .mapToDouble(a -> Math.pow(a.length(), SIMILARITY_POWER_KEY))
                .sum();
    }

    private List<TeamMatchingResultDTO> filterAndTransformResult(String teamName, Map<Long, Double> input) {
        return input.entrySet()
                .stream()
                .filter(e -> e.getValue() >= THRESHOLD_SIMILARITY_SCORE)
                .sorted(comparing(Map.Entry::getValue, reverseOrder()))
                .map(e -> TeamMatchingResultDTO
                        .builder()
                        .matchedTeamId(e.getKey())
                        .similarityFactor(e.getValue())
                        .teamName(teamName)
                        .matchedTeamNames(teamDataHolder.getAllTeamNames(e.getKey()))
                        .build())
                .filter(this::isTeamMatchedOnlyByYoungTeamMarkers)
                .collect(toList());
    }

    private boolean isTeamMatchedOnlyByYoungTeamMarkers(TeamMatchingResultDTO teamMatchingResultDTO) {
        return teamMatchingResultDTO
                .getMatchedTeamNames()
                .stream()
                .allMatch(mtn -> areNamesDifferingOnlyByYoungTeamModifier(mtn, teamMatchingResultDTO.getTeamName()));
    }

    private boolean areNamesDifferingOnlyByYoungTeamModifier(String name1, String name2) {
        Set<String> name1Words = Sets.newHashSet(name1.split("\\s+"));
        Set<String> name2Words = Sets.newHashSet(name2.split("\\s+"));
        return Sets.intersection(name1Words, name2Words)
                .stream()
                .anyMatch(w -> !YOUNG_TEAM_MARKERS.contains(w));
    }
}
