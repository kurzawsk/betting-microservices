package pl.kk.services.mdm.service.mapping;

import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.mdm.BasicMappingCaseDTO;
import pl.kk.services.common.datamodel.dto.mdm.MappingCaseDTO;
import pl.kk.services.mdm.model.domain.MappingCase;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.dto.mapping.MatchMappingDTO;

@Service
public class MappingCaseConverter {

    public MappingCase toNewMappingCase(MatchMappingDTO matchMappingDTO) {
        MappingCase domain = new MappingCase();
        domain.setHomeTeamName(matchMappingDTO.getHomeTeamName());
        domain.setAwayTeamName(matchMappingDTO.getAwayTeamName());
        domain.setHomeSimilarityFactor(matchMappingDTO.getHomeSimilarityFactor());
        domain.setAwaySimilarityFactor(matchMappingDTO.getAwaySimilarityFactor());
        domain.setMatch(getMatch(matchMappingDTO.getMatchId()));
        domain.setStatus(MappingCase.Status.NEW);
        domain.setSourceSystemName(matchMappingDTO.getSourceSystemName());
        return domain;
    }

    public MappingCaseDTO toDTO(MappingCase domain) {
        return MappingCaseDTO.builder()
                .id(domain.getId())
                .homeTeamName(domain.getHomeTeamName())
                .homeSimilarityFactor(domain.getHomeSimilarityFactor())
                .awayTeamName(domain.getAwayTeamName())
                .awaySimilarityFactor(domain.getAwaySimilarityFactor())
                .matchId(domain.getMatch().getId())
                .matchTeams(String.join(" ", domain.getMatch().getHomeTeam().getName(), "-", domain.getMatch().getAwayTeam().getName()))
                .sourceSystemName(domain.getSourceSystemName())
                .status(domain.getStatus().name())
                .createdBy(domain.getAudit().getCreatedBy())
                .updatedBy(domain.getAudit().getUpdatedBy())
                .createdOn(domain.getAudit().getCreatedOn())
                .updatedOn(domain.getAudit().getUpdatedOn())
                .build();
    }

    public BasicMappingCaseDTO toBasicDTO(MappingCase domain) {
        return BasicMappingCaseDTO.builder()
                .id(domain.getId())
                .homeTeamName(domain.getHomeTeamName())
                .homeSimilarityFactor(domain.getHomeSimilarityFactor())
                .awayTeamName(domain.getAwayTeamName())
                .awaySimilarityFactor(domain.getAwaySimilarityFactor())
                .matchId(domain.getMatch().getId())
                .sourceSystemName(domain.getSourceSystemName())
                .status(domain.getStatus().name())
                .build();
    }

    private Match getMatch(Long id) {
        Match match = new Match();
        match.setId(id);
        return match;
    }
}
