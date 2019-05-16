package pl.kk.services.mdm.service;

import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.mdm.BasicTeamDTO;
import pl.kk.services.common.datamodel.dto.mdm.TeamDTO;
import pl.kk.services.mdm.model.domain.Team;

@Service
public class TeamConverter {

    public TeamDTO toDTO(Team domain) {
        return TeamDTO
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .alternativeNames(domain.getAlternativeNames())
                .falseNames(domain.getFalseNames())
                .build();
    }

    public BasicTeamDTO toBasicDTO(Team domain) {
        return BasicTeamDTO
                .builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

}
