package pl.kk.services.mdm.service.mapping;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.kk.services.mdm.model.domain.Team;
import pl.kk.services.mdm.model.dto.TeamUpdatedEventDTO;
import pl.kk.services.mdm.repository.TeamRepository;
import pl.kk.services.mdm.service.mapping.cleanse.CleanseService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Component
public class TeamDataHolder implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataHolder.class);

    private final TeamRepository teamRepository;
    private final CleanseService cleanseService;

    private Map<String, List<Long>> teamsIdByInitiallyCleansedName = Maps.newHashMap();
    private Map<String, Set<Long>> teamsIdByToken = Maps.newHashMap();
    private Map<String, Set<Long>> teamsIdByInitiallyCleansedFalseName = Maps.newHashMap();
    private Map<Long, Set<Set<String>>> tokensByNameByTeamId = Maps.newHashMap();
    private Map<Long, Set<String>> allRawNamesByTeamId = Maps.newHashMap();

    private Map<String, Set<String>> tokensByNameCache = Maps.newHashMap();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Autowired
    public TeamDataHolder(TeamRepository teamRepository, CleanseService cleanseService) {
        this.teamRepository = teamRepository;
        this.cleanseService = cleanseService;
    }

    @Override
    public void afterPropertiesSet() {
        processTeamNamesData();
    }

    @TransactionalEventListener
    public void onTeamDataChange(TeamUpdatedEventDTO teamUpdatedEventDTO) {
        LOGGER.info("Team data change, source: " + teamUpdatedEventDTO.getSource() + " refreshing team data");
        processTeamNamesData();
    }

    public List<Long> findTeamIdByNameInAllNames(String name) {
        try {
            rwLock.readLock().lock();
            return teamsIdByInitiallyCleansedName.getOrDefault(cleanseService.cleanseBasic(name), Collections.emptyList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Set<Long> getTeamIdsByTokens(Set<String> tokens) {
        try {
            rwLock.readLock().lock();
            return tokens.stream()
                    .flatMap(token -> teamsIdByToken
                            .getOrDefault(token, Collections.emptySet()).stream())
                    .collect(toSet());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean isFalseName(Long teamId, String name) {
        try {
            rwLock.readLock().lock();
            return teamsIdByInitiallyCleansedFalseName.getOrDefault(name, Collections.emptySet()).contains(teamId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Set<Set<String>> getTeamNamesTokensPartitionedByName(Long teamId) {
        try {
            rwLock.readLock().lock();
            return tokensByNameByTeamId.getOrDefault(teamId, Collections.emptySet());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Set<String> getAllTeamNames(Long teamId) {
        try {
            rwLock.readLock().lock();
            return allRawNamesByTeamId.getOrDefault(teamId, Collections.emptySet());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private void processTeamNamesData() {
        try {
            rwLock.writeLock().lock();
            long start = System.currentTimeMillis();
            List<Team> teams = teamRepository.findAllWithAllNames();
            long stopQuery = System.currentTimeMillis();

            long startProcessing = System.currentTimeMillis();
            List<Pair<String, Long>> allTeamNamePairs = teams
                    .parallelStream()
                    .flatMap(this::toAllNamesIdPairs)
                    .collect(toList());

            LOGGER.info("Processing 1 took: " + (System.currentTimeMillis() - startProcessing));
            startProcessing = System.currentTimeMillis();

            teamsIdByInitiallyCleansedName.clear();
            teamsIdByInitiallyCleansedName.putAll(allTeamNamePairs
                    .stream()
                    .collect(groupingBy(p -> cleanseService.cleanseBasic(p.getKey()),
                            mapping(Pair::getValue, toList()))));

            LOGGER.info("Processing 2 took: " + (System.currentTimeMillis() - startProcessing));
            startProcessing = System.currentTimeMillis();

            allRawNamesByTeamId.clear();
            allRawNamesByTeamId.putAll(allTeamNamePairs.stream().
                    collect(groupingBy(Pair::getValue,
                            mapping(Pair::getKey, toSet()))));


            LOGGER.info("Processing 3 took: " + (System.currentTimeMillis() - startProcessing));
            startProcessing = System.currentTimeMillis();

            teamsIdByInitiallyCleansedFalseName.clear();
            teamsIdByInitiallyCleansedFalseName.putAll(teams.parallelStream()
                    .flatMap(this::toIdByFalseNames)
                    .map(p -> Pair.of(p.getValue(), cleanseService.cleanseBasic(p.getKey())))
                    .collect(groupingBy(Pair::getValue,
                            mapping(Pair::getKey, toSet()))));

            LOGGER.info("Processing 4 took: " + (System.currentTimeMillis() - startProcessing));
            startProcessing = System.currentTimeMillis();

            tokensByNameByTeamId.clear();
            tokensByNameByTeamId.putAll(allTeamNamePairs
                    .stream()
                    .collect(groupingBy(Pair::getValue,
                            mapping(p -> getFullCleansedAndTokenizedName(p.getKey()), toSet()))));

            LOGGER.info("Processing 5 took: " + (System.currentTimeMillis() - startProcessing));
            startProcessing = System.currentTimeMillis();

            teamsIdByToken.clear();
            teamsIdByToken.putAll(tokensByNameByTeamId
                    .entrySet()
                    .parallelStream()
                    .map(e -> Pair.of(e.getKey(), e.getValue()
                            .stream()
                            .flatMap(Set::stream)
                            .flatMap(Stream::of)
                            .collect(toSet())))
                    .flatMap(p -> p.getValue()
                            .stream()
                            .map(s -> Pair.of(p.getKey(), s)))
                    .collect(groupingBy(Pair::getValue,
                            mapping(Pair::getKey, toSet()))));

            LOGGER.info("Processing 6 took: " + (System.currentTimeMillis() - startProcessing));

            long stop = System.currentTimeMillis();
            LOGGER.info("Processing team data took: " + (stop - start) + " ms, query took: " + (stopQuery - start) + " ms");
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private Stream<Pair<String, Long>> toAllNamesIdPairs(Team team) {
        return Stream.concat(
                team.getAlternativeNames().stream(), Stream.of(team.getName()))
                .map(name -> Pair.of(name, team.getId()));
    }

    private Stream<Pair<String, Long>> toIdByFalseNames(Team team) {
        return team.getFalseNames().stream()
                .map(name -> Pair.of(name, team.getId()));
    }

    private Set<String> getFullCleansedAndTokenizedName(String name) {
        if (!tokensByNameCache.containsKey(name)) {
            Set<String> cleansedAndTokenizedName = cleanseService.cleanseFullAndTokenize(name);
            tokensByNameCache.put(name, cleansedAndTokenizedName);
            return cleansedAndTokenizedName;
        }
        return tokensByNameCache.get(name);
    }
}
