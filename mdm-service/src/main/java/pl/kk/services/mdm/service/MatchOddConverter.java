package pl.kk.services.mdm.service;

import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.mdm.AddMatchOddDTO;
import pl.kk.services.common.datamodel.dto.mdm.MatchOddDTO;
import pl.kk.services.mdm.model.domain.Bookmaker;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.domain.MatchOdd;

@Service
public class MatchOddConverter {

    public MatchOddDTO toDTO(MatchOdd domain) {
        return MatchOddDTO
                .builder()
                .id(domain.getId())
                .matchId(domain.getMatch().getId())
                .bookmakerId(domain.getBookmaker().getId())
                .bookmakerName(domain.getBookmaker().getName())
                .updatedOn(domain.getAudit().getUpdatedOn())
                .odd1(domain.getOdd1())
                .oddX(domain.getOddX())
                .odd2(domain.getOdd2())
                .odd1X(domain.getOdd1X())
                .odd12(domain.getOdd12())
                .oddX2(domain.getOddX2())
                .oddBTSY(domain.getOddBTSY())
                .oddBTSN(domain.getOddBTSN())
                .oddO05(domain.getOddO05())
                .oddO15(domain.getOddO15())
                .oddO25(domain.getOddO25())
                .oddO35(domain.getOddO35())
                .oddO45(domain.getOddO45())
                .oddO55(domain.getOddO55())
                .oddO65(domain.getOddO65())
                .oddU05(domain.getOddU05())
                .oddU15(domain.getOddU15())
                .oddU25(domain.getOddU25())
                .oddU35(domain.getOddU35())
                .oddU45(domain.getOddU45())
                .oddU55(domain.getOddU55())
                .oddU65(domain.getOddU65())
                .build();
    }

    public MatchOdd toMatchOdd(AddMatchOddDTO dto) {
        MatchOdd matchOdd = new MatchOdd();
        matchOdd.setOdd1(dto.getOdd1());
        matchOdd.setOddX(dto.getOddX());
        matchOdd.setOdd2(dto.getOdd2());
        matchOdd.setOdd1X(dto.getOdd1X());
        matchOdd.setOdd12(dto.getOdd12());
        matchOdd.setOddX2(dto.getOddX2());
        matchOdd.setOddBTSN(dto.getOddBTSN());
        matchOdd.setOddBTSY(dto.getOddBTSY());

        matchOdd.setOddO05(dto.getOddO05());
        matchOdd.setOddU05(dto.getOddU05());

        matchOdd.setOddO15(dto.getOddO15());
        matchOdd.setOddU15(dto.getOddU15());

        matchOdd.setOddO25(dto.getOddO25());
        matchOdd.setOddU25(dto.getOddU25());

        matchOdd.setOddO35(dto.getOddO35());
        matchOdd.setOddU35(dto.getOddU35());

        matchOdd.setOddO45(dto.getOddO45());
        matchOdd.setOddU45(dto.getOddU45());

        matchOdd.setOddO55(dto.getOddO55());
        matchOdd.setOddU55(dto.getOddU55());

        matchOdd.setOddO65(dto.getOddO65());
        matchOdd.setOddU65(dto.getOddU65());

        Match match = new Match();
        match.setId(dto.getMatchId());
        matchOdd.setMatch(match);

        Bookmaker bookmaker = new Bookmaker();
        bookmaker.setId(dto.getBookmakerId());
        matchOdd.setBookmaker(bookmaker);

        return matchOdd;
    }

}
