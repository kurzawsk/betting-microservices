package pl.kk.services.mdm.model.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "SOURCE_SYSTEM_NAME", nullable = false)
    private String sourceSystemName;

    @Column(name = "SOURCE_SYSTEM_ID", nullable = false)
    private String sourceSystemId;

    @ManyToOne(targetEntity = Team.class)
    @JoinColumn(name = "HOME_TEAM_ID", nullable = false)
    private Team homeTeam;

    @ManyToOne(targetEntity = Team.class)
    @JoinColumn(name = "AWAY_TEAM_ID", nullable = false)
    private Team awayTeam;

    @Column(name = "START_TIME", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "HOME_SCORE")
    private Integer homeScore;

    @Column(name = "AWAY_SCORE")
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESULT_TYPE")
    private ResultType resultType;

    @Column(name = "MARKED_AS_FINISHED_TIME")
    private ZonedDateTime markedAsFinishedTime;

    @Version
    private long version;

    @Embedded
    private Audit audit = new Audit();

    public enum ResultType {
        NORMAL, UNKNOWN, POSTPONED, CANCELLED, NOT_FOUND
    }
}
