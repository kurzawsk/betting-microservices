package pl.kk.services.mdm.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.mdm.BasicTeamDTO;
import pl.kk.services.common.datamodel.dto.mdm.TeamDTO;
import pl.kk.services.common.misc.EntityNotFoundException;
import pl.kk.services.common.misc.RestUtils;
import pl.kk.services.mdm.model.domain.Team;
import pl.kk.services.mdm.model.dto.TeamFilterDTO;
import pl.kk.services.mdm.repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamConverter teamConverter;

    @Autowired
    public TeamService(TeamRepository teamRepository, TeamConverter teamConverter) {
        this.teamRepository = teamRepository;
        this.teamConverter = teamConverter;
    }

    @Transactional(readOnly = true)
    public TeamDTO find(Long id) {
        return teamRepository
                .findById(id)
                .map(teamConverter::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Team " + id + "does not exist"));
    }

    @Transactional(readOnly = true)
    public BasicTeamDTO findBasic(Long id) {
        return teamRepository
                .findById(id)
                .map(teamConverter::toBasicDTO)
                .orElseThrow(() -> new EntityNotFoundException("Team " + id + "does not exist"));
    }

    @Transactional(readOnly = true)
    public PagedResultDTO find(Pageable pageRequest, TeamFilterDTO teamFilterDTO, boolean basicInfoOnly) {
        Page<Team> paged;
        if (Objects.nonNull(teamFilterDTO) && StringUtils.isNotBlank(teamFilterDTO.getName())) {
            paged = teamRepository.findWithAllNames(pageRequest, teamFilterDTO.getName().toUpperCase());
        } else {
            paged = teamRepository.findWithAllNames(pageRequest);
        }

        List<?> teams;
        if (basicInfoOnly) {
            teams = paged
                    .stream()
                    .map(teamConverter::toBasicDTO)
                    .collect(Collectors.toList());
        } else {
            teams = paged
                    .stream()
                    .map(teamConverter::toDTO)
                    .collect(Collectors.toList());
        }
        return RestUtils.getPagedResult(teams, paged.getTotalElements());
    }

    @Transactional
    public TeamDTO createTeam(String name) {
        Team domain = new Team();
        domain.setName(name);
        return teamConverter.toDTO(teamRepository.save(domain));
    }

}
