package pl.kk.services.mdm.model.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kk.services.common.datamodel.domain.Audit;
import pl.kk.services.common.datamodel.domain.AuditListener;
import pl.kk.services.common.datamodel.domain.Auditable;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MAPPING_CASE")
@EntityListeners(AuditListener.class)
public class MappingCase extends ManagedEntity implements Auditable {

    @Column(name = "SOURCE_SYSTEM_NAME", nullable = false, updatable = false)
    private String sourceSystemName;

    @Column(name = "HOME_TEAM_NAME", nullable = false, updatable = false)
    private String homeTeamName;

    @Column(name = "AWAY_TEAM_NAME", nullable = false, updatable = false)
    private String awayTeamName;

    @ManyToOne(targetEntity = Match.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false, updatable = false)
    private Match match;

    @Column(name = "HOME_SIMILARITY_FACTOR", nullable = false, updatable = false)
    private Double homeSimilarityFactor;

    @Column(name = "AWAY_SIMILARITY_FACTOR", nullable = false, updatable = false)
    private Double awaySimilarityFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private Status status;

    @Embedded
    private Audit audit = new Audit();

    @Version
    private long version;

    public enum Status {
        NEW, REJECTED, ACCEPTED
    }
}
