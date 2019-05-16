package pl.kk.services.mdm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.mdm.BasicMatchDTO;
import pl.kk.services.common.datamodel.dto.mdm.MatchDTO;
import pl.kk.services.mdm.model.domain.Match;

import java.util.Objects;

@Service
public class MatchConverter {

    private final TeamConverter teamConverter;

    @Autowired
    public MatchConverter(TeamConverter teamConverter) {
        this.teamConverter = teamConverter;
    }

    public MatchDTO toDTO(Match domain) {
        return MatchDTO
                .builder()
                .id(domain.getId())
                .sourceSystemName(domain.getSourceSystemName())
                .sourceSystemId(domain.getSourceSystemId())
                .homeTeam(teamConverter.toBasicDTO(domain.getHomeTeam()))
                .awayTeam(teamConverter.toBasicDTO(domain.getAwayTeam()))
                .homeScore(domain.getHomeScore())
                .awayScore(domain.getAwayScore())
                .startTime(domain.getStartTime())
                .markedAsFinishedTime(domain.getMarkedAsFinishedTime())
                .resultType(Objects.nonNull(domain.getResultType()) ? domain.getResultType().name() : null)
                .build();
    }

    public BasicMatchDTO toBasicDTO(Match domain) {
        return BasicMatchDTO
                .builder()
                .id(domain.getId())
                .homeTeamId(domain.getHomeTeam().getId())
                .awayTeamId(domain.getAwayTeam().getId())
                .homeScore(domain.getHomeScore())
                .awayScore(domain.getAwayScore())
                .startTime(domain.getStartTime())
                .resultType(Objects.nonNull(domain.getResultType()) ? domain.getResultType().name() : null)
                .build();
    }

}
