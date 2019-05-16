package pl.kk.services.mdm.model.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "BOOKMAKER")
public class Bookmaker extends ManagedEntity {

    @Column(name = "URL", nullable = false)
    private String url;

    @Column(name = "NAME", unique = true, length = 30)
    private String name;

    @Column(name = "MINIMAL_AMOUNT_TO_BET", precision = 9, scale = 2, nullable = false)
    private BigDecimal minimalAmountToBet = BigDecimal.ZERO;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "BOOKMAKER_NAME",
            joinColumns = @JoinColumn(name = "BOOKMAKER_ID"))
    @MapKeyColumn(name = "SYSTEM")
    @Column(name = "NAME")
    private Map<String, String> alternativeNames;

    @Column(name = "TAX_PERCENT", precision = 9, scale = 2, nullable = false)
    private BigDecimal taxPercent = BigDecimal.ZERO;

}
