package pl.kk.services.mdm.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.mdm.BasicMatchDTO;
import pl.kk.services.common.datamodel.dto.mdm.MatchDTO;
import pl.kk.services.common.datamodel.dto.mdm.SetMatchResultDTO;
import pl.kk.services.common.datamodel.dto.mdm.TeamDTO;
import pl.kk.services.common.misc.BusinessValidationException;
import pl.kk.services.common.misc.EntityNotFoundException;
import pl.kk.services.common.misc.RestUtils;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.domain.Team;
import pl.kk.services.mdm.model.dto.MatchFilterDTO;
import pl.kk.services.mdm.model.dto.TeamUpdatedEventDTO;
import pl.kk.services.mdm.repository.MatchRepository;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class MatchService {

    private static final String BETTING_EVENT_QUEUE_NAME = "bettingEvent";
    private final MatchRepository matchRepository;
    private final MatchConverter matchConverter;
    private final TeamService teamService;
    private final JmsTemplate jmsTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MatchService(MatchRepository matchRepository, MatchConverter matchConverter,
                        TeamService teamService, JmsTemplate jmsTemplate, ApplicationEventPublisher applicationEventPublisher) {
        this.matchRepository = matchRepository;
        this.matchConverter = matchConverter;
        this.teamService = teamService;
        this.jmsTemplate = jmsTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(readOnly = true)
    public PagedResultDTO find(Pageable pageRequest) {
        return find(pageRequest, null, true);
    }

    @Transactional(readOnly = true)
    public PagedResultDTO find(Pageable pageRequest, MatchFilterDTO matchFilterDTO, boolean basicInfoOnly) {
        Page<Match> paged;

        if (Objects.nonNull(matchFilterDTO)) {
            if (Objects.isNull(matchFilterDTO.getTeamId())) {
                if (StringUtils.isNotBlank(matchFilterDTO.getTeamsName())) {
                    paged = matchRepository.findByResultTypeInAndTeamsNamesLike(pageRequest, extractResultTypes(matchFilterDTO), matchFilterDTO.getTeamsName().toUpperCase());
                } else {
                    paged = matchRepository.findByResultTypeIn(pageRequest, extractResultTypes(matchFilterDTO));
                }
            } else {
                paged = matchRepository.findByTeam(pageRequest, matchFilterDTO.getTeamId());
            }
        } else {
            paged = matchRepository.findAll(pageRequest);
        }


        List<?> matches;
        if (basicInfoOnly) {
            matches = paged
                    .stream()
                    .map(matchConverter::toBasicDTO)
                    .collect(toList());
        } else {
            matches = paged
                    .stream()
                    .map(matchConverter::toDTO)
                    .collect(toList());
        }

        return RestUtils.getPagedResult(matches, paged.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Optional<BasicMatchDTO> find(Long homeTeamId, Long awayTeamId, ZonedDateTime startTime,
                                        String sourceSystem, String sourceSystemId) {
        return matchRepository
                .findByHomeTeamAndAwayTeamAndStartTimeAndSourceSystemNameAndSourceSystemId(homeTeamId, awayTeamId, startTime,
                        sourceSystem, sourceSystemId)
                .map(matchConverter::toBasicDTO);
    }

    @Transactional(readOnly = true)
    public boolean areAllMatchesNotFinished(List<Long> ids) {
        return matchRepository.areAllMatchesNotFinished(ids);
    }

    @Transactional(readOnly = true)
    public BasicMatchDTO findBasic(Long id) {
        return matchRepository
                .findById(id)
                .map(matchConverter::toBasicDTO)
                .orElseThrow(() -> new EntityNotFoundException("Match " + id + "does not exist"));
    }

    @Transactional(readOnly = true)
    public MatchDTO find(Long id) {
        return matchRepository
                .findById(id)
                .map(matchConverter::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Match " + id + "does not exist"));
    }

    @Transactional(readOnly = true)
    public List<BasicMatchDTO> find(List<Long> ids) {
        return matchRepository
                .findAllById(ids)
                .stream()
                .map(matchConverter::toBasicDTO)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Optional<BasicMatchDTO> find(Long homeTeamId, Long awayTeamId,
                                        ZonedDateTime lowerStartTime, ZonedDateTime upperStartTime, Match.ResultType resultType) {
        return matchRepository
                .findOneByHomeTeamIdAndAwayTeamIdAndStartTimeBetweenAndResultType(homeTeamId, awayTeamId, lowerStartTime, upperStartTime, resultType)
                .map(matchConverter::toBasicDTO);
    }

    @Transactional
    public BasicMatchDTO addMatch(Long homeTeamId, String awayNewTeamName, ZonedDateTime startTime,
                                  String sourceSystemName, String sourceSystemId) {
        TeamDTO awayTeam = teamService.createTeam(awayNewTeamName);
        applicationEventPublisher.publishEvent(generateTeamUpdatedEvent(sourceSystemName, awayTeam.getId()));
        return addMatch(homeTeamId, awayTeam.getId(), sourceSystemName, sourceSystemId, startTime);
    }

    @Transactional
    public BasicMatchDTO addMatch(String homeNewTeamName, Long awayTeamId, ZonedDateTime startTime,
                                  String sourceSystemName, String sourceSystemId) {
        TeamDTO homeTeam = teamService.createTeam(homeNewTeamName);
        applicationEventPublisher.publishEvent(generateTeamUpdatedEvent(sourceSystemName, homeTeam.getId()));
        return addMatch(homeTeam.getId(), awayTeamId, sourceSystemName, sourceSystemId, startTime);
    }

    @Transactional
    public BasicMatchDTO addMatch(String homeNewTeamName, String awayNewTeamName, ZonedDateTime startTime,
                                  String sourceSystemName, String sourceSystemId) {
        TeamDTO homeTeam = teamService.createTeam(homeNewTeamName);
        TeamDTO awayTeam = teamService.createTeam(awayNewTeamName);
        applicationEventPublisher.publishEvent(generateTeamUpdatedEvent(sourceSystemName, homeTeam.getId(), awayTeam.getId()));
        return addMatch(homeTeam.getId(), awayTeam.getId(), sourceSystemName, sourceSystemId, startTime);
    }

    @Transactional
    public BasicMatchDTO updateMatchStartTime(Long id, ZonedDateTime newStartTime) {
        return matchRepository.findById(id).map(match -> {
            match.setStartTime(newStartTime);
            return matchRepository.save(match);
        }).map(matchConverter::toBasicDTO).get();
    }

    @Transactional
    public BasicMatchDTO switchMatchTeams(Long id) {
        return matchRepository.findById(id).map(match -> {
            Team currentHomeTeam = match.getHomeTeam();
            match.setHomeTeam(match.getAwayTeam());
            match.setAwayTeam(currentHomeTeam);
            return matchRepository.save(match);
        }).map(matchConverter::toBasicDTO).get();
    }

    @Transactional
    public BasicMatchDTO addMatch(Long homeTeamId, Long awayNewTeamId, ZonedDateTime startTime,
                                  String sourceSystemName, String sourceSystemId) {
        return addMatch(homeTeamId, awayNewTeamId, sourceSystemName, sourceSystemId, startTime);
    }

    @Transactional
    public BasicMatchDTO setMatchResult(Long id, SetMatchResultDTO dto) {
        Match match = matchRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Match " + id + " does not exist"));

        if (nonNull(match.getMarkedAsFinishedTime()) || match.getResultType() != Match.ResultType.UNKNOWN) {
            throw new BusinessValidationException("Match " + id + " is already finished");
        }

        if (nonNull(dto.getAbnormalMatchFinishReason())) {
            match.setResultType(Match.ResultType.valueOf(dto.getAbnormalMatchFinishReason().name()));
        } else {
            match.setHomeScore(dto.getHomeTeamScore());
            match.setAwayScore(dto.getAwayTeamScore());
            match.setResultType(Match.ResultType.NORMAL);
        }

        match.setMarkedAsFinishedTime(ZonedDateTime.now());

        BasicMatchDTO finishedMatch = matchConverter.toBasicDTO(matchRepository.save(match));
        jmsTemplate.convertAndSend(BETTING_EVENT_QUEUE_NAME, finishedMatch);
        return finishedMatch;
    }

    private BasicMatchDTO addMatch(Long homeTeamId, Long awayTeamId, String sourceSystemName, String sourceSystemId, ZonedDateTime startTime) {
        Team homeTeamDomain = new Team();
        homeTeamDomain.setId(homeTeamId);

        Team awayTeamDomain = new Team();
        awayTeamDomain.setId(awayTeamId);

        Match match = new Match();
        match.setHomeTeam(homeTeamDomain);
        match.setAwayTeam(awayTeamDomain);
        match.setSourceSystemName(sourceSystemName);
        match.setSourceSystemId(sourceSystemId);
        match.setStartTime(startTime);
        match.setResultType(Match.ResultType.UNKNOWN);
        return matchConverter.toBasicDTO(matchRepository.save(match));
    }

    private List<Match.ResultType> extractResultTypes(MatchFilterDTO matchFilterDTO) {
        if (Objects.nonNull(matchFilterDTO) && Objects.nonNull(matchFilterDTO.getResultTypes()) && !matchFilterDTO.getResultTypes().isEmpty()) {
            return Stream.of(matchFilterDTO
                    .getResultTypes().split(","))
                    .map(Match.ResultType::valueOf)
                    .collect(toList());
        }
        return Arrays.asList(Match.ResultType.values());
    }

    private TeamUpdatedEventDTO generateTeamUpdatedEvent(String sourceSystemName, Long... teamIds) {
        String source = "New match is being added by " + sourceSystemName + ", and new team(s) teams ids are: " + Arrays.toString(teamIds);
        return TeamUpdatedEventDTO
                .builder()
                .source(source)
                .build();
    }
}
