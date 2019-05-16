package pl.kk.services.mdm.model.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kk.services.common.datamodel.domain.Audit;
import pl.kk.services.common.datamodel.domain.AuditListener;
import pl.kk.services.common.datamodel.domain.Auditable;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MATCH_ODD")
@EntityListeners(AuditListener.class)
public class MatchOdd extends ManagedEntity implements Auditable {

    private static final long serialVersionUID = -1L;

    @ManyToOne(targetEntity = Match.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false)
    private Match match;

    @OneToOne(targetEntity = Bookmaker.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "BOOKMAKER_ID", nullable = false)
    private Bookmaker bookmaker;

    @Column(name = "ODD_1", precision = 9, scale = 2, nullable = false)
    private BigDecimal odd1;

    @Column(name = "ODD_2", precision = 9, scale = 2, nullable = false)
    private BigDecimal odd2;

    @Column(name = "ODD_X", precision = 9, scale = 2, nullable = false)
    private BigDecimal oddX;

    @Column(name = "ODD_1X", precision = 9, scale = 2)
    private BigDecimal odd1X;

    @Column(name = "ODD_12", precision = 9, scale = 2)
    private BigDecimal odd12;

    @Column(name = "ODD_X2", precision = 9, scale = 2)
    private BigDecimal oddX2;

    @Column(name = "ODD_BTS_Y", precision = 9, scale = 2)
    private BigDecimal oddBTSY;

    @Column(name = "ODD_BTS_N", precision = 9, scale = 2)
    private BigDecimal oddBTSN;

    @Column(name = "ODD_O_05", precision = 9, scale = 2)
    private BigDecimal oddO05;

    @Column(name = "ODD_O_15", precision = 9, scale = 2)
    private BigDecimal oddO15;

    @Column(name = "ODD_O_25", precision = 9, scale = 2)
    private BigDecimal oddO25;

    @Column(name = "ODD_O_35", precision = 9, scale = 2)
    private BigDecimal oddO35;

    @Column(name = "ODD_O_45", precision = 9, scale = 2)
    private BigDecimal oddO45;

    @Column(name = "ODD_O_55", precision = 9, scale = 2)
    private BigDecimal oddO55;

    @Column(name = "ODD_O_65", precision = 9, scale = 2)
    private BigDecimal oddO65;

    @Column(name = "ODD_U_05", precision = 9, scale = 2)
    private BigDecimal oddU05;

    @Column(name = "ODD_U_15", precision = 9, scale = 2)
    private BigDecimal oddU15;

    @Column(name = "ODD_U_25", precision = 9, scale = 2)
    private BigDecimal oddU25;

    @Column(name = "ODD_U_35", precision = 9, scale = 2)
    private BigDecimal oddU35;

    @Column(name = "ODD_U_45", precision = 9, scale = 2)
    private BigDecimal oddU45;

    @Column(name = "ODD_U_55", precision = 9, scale = 2)
    private BigDecimal oddU55;

    @Column(name = "ODD_U_65", precision = 9, scale = 2)
    private BigDecimal oddU65;

    @Embedded
    private Audit audit = new Audit();

    @Version
    private long version;

}
