package pl.kk.services.betexplorer.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import pl.kk.services.common.datamodel.domain.Audit;
import pl.kk.services.common.datamodel.domain.AuditListener;
import pl.kk.services.common.datamodel.domain.Auditable;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MATCH")
@EntityListeners(AuditListener.class)
public class Match extends ManagedEntity implements Auditable {

    @NaturalId
    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Column(name = "MDM_MATCH_ID", nullable = false, updatable = false, unique = true)
    private Long mdmMatchId;

    @Column(name = "HOME_TEAM_NAME", nullable = false)
    private String homeTeamName;

    @Column(name = "AWAY_TEAM_NAME", nullable = false)
    private String awayTeamName;

    @Column(name = "START_TIME", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "IS_FINISHED")
    private boolean finished;

    @Embedded
    private Audit audit = new Audit();

}