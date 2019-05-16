package pl.kk.services.mdm.service.mapping;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.mdm.FinishMappingCaseDTO;
import pl.kk.services.common.datamodel.dto.mdm.MappingCaseDTO;
import pl.kk.services.common.misc.BusinessValidationException;
import pl.kk.services.mdm.model.domain.MappingCase;
import pl.kk.services.mdm.model.domain.Team;
import pl.kk.services.mdm.model.dto.TeamUpdatedEventDTO;
import pl.kk.services.mdm.repository.MappingCaseRepository;
import pl.kk.services.mdm.repository.TeamRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class MappingCaseService {

    private final MappingCaseRepository mappingCaseRepository;
    private final MappingCaseConverter mappingConverter;
    private final TeamRepository teamRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MappingCaseService(MappingCaseRepository mappingCaseRepository, MappingCaseConverter mappingConverter,
                              TeamRepository teamRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.mappingCaseRepository = mappingCaseRepository;
        this.mappingConverter = mappingConverter;
        this.teamRepository = teamRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(readOnly = true)
    public PagedResultDTO find(Pageable pageRequest) {
        return findByStatus(pageRequest, null);
    }

    @Transactional(readOnly = true)
    public PagedResultDTO findByStatus(Pageable pageRequest, String status) {
        Page<MappingCase> paged = Objects.isNull(status) ?
                mappingCaseRepository.findWithMatchTeamDataPopulated(pageRequest) :
                mappingCaseRepository.findByStatusWithMatchTeamDataPopulated(pageRequest, getStatusFromFilter(status));

        List<MappingCaseDTO> mappingCases = paged
                .stream()
                .map(mappingConverter::toDTO)
                .collect(toList());

        return PagedResultDTO.<MappingCaseDTO>builder()
                .items(mappingCases)
                .totalItemsCount(paged.getTotalElements())
                .build();
    }

    @Transactional
    public void finishCase(Long caseId, FinishMappingCaseDTO finishMappingCaseDTO) {
        MappingCase.Status targetStatus = finishMappingCaseDTO.getAccept() ?
                MappingCase.Status.ACCEPTED :
                MappingCase.Status.REJECTED;

        MappingCase mappingCase =
                mappingCaseRepository
                        .findById(caseId)
                        .orElseThrow(() -> new IllegalArgumentException("Mapping case " + caseId + " does not exist"));

        if (mappingCase.getStatus() != MappingCase.Status.NEW) {
            throw new BusinessValidationException("Mapping case " + caseId + " has been already completed, its status is:" + mappingCase.getStatus());
        }

        Team homeTeam = teamRepository.getOne(mappingCase.getMatch().getHomeTeam().getId());
        Team awayTeam = teamRepository.getOne(mappingCase.getMatch().getAwayTeam().getId());

        if (targetStatus == MappingCase.Status.ACCEPTED) {
            if (!homeTeam.getName().equals(mappingCase.getHomeTeamName())) {
                homeTeam.getAlternativeNames().add(mappingCase.getHomeTeamName());
            }
            if (!awayTeam.getName().equals(mappingCase.getAwayTeamName())) {
                awayTeam.getAlternativeNames().add(mappingCase.getAwayTeamName());
            }
        } else {
            homeTeam.getFalseNames().add(mappingCase.getHomeTeamName());
            awayTeam.getFalseNames().add(mappingCase.getAwayTeamName());
        }

        mappingCase.setStatus(targetStatus);

        teamRepository.save(homeTeam);
        teamRepository.save(awayTeam);
        mappingCaseRepository.save(mappingCase);
        applicationEventPublisher.publishEvent(generateTeamUpdatedEvent(caseId, homeTeam.getId(), awayTeam.getId(), mappingCase.getStatus()));
    }

    private List<MappingCase.Status> getStatusFromFilter(String statusFilter) {
        return Arrays.stream(statusFilter.split(","))
                .map(MappingCase.Status::valueOf)
                .collect(toList());

    }

    private TeamUpdatedEventDTO generateTeamUpdatedEvent(Long caseId, Long homeTeam, Long awayTeam, MappingCase.Status status) {
        String source = String.format("Mapping case %d was %s, homeTeam: %d, awayTeam: %d",
                caseId, status.name().toLowerCase(), homeTeam, awayTeam);
        return TeamUpdatedEventDTO
                .builder()
                .source(source)
                .build();
    }
}
