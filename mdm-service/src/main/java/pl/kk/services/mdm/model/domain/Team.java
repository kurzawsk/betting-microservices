package pl.kk.services.mdm.model.domain;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kk.services.common.datamodel.domain.Audit;
import pl.kk.services.common.datamodel.domain.AuditListener;
import pl.kk.services.common.datamodel.domain.Auditable;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TEAM")
@EntityListeners(AuditListener.class)
public class Team extends ManagedEntity implements Auditable {

    @Column(name = "NAME", nullable = false, unique = true, updatable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "TEAM_ALT_NAME", joinColumns = @JoinColumn(name = "TEAM_ID"))
    @Column(name = "VALUE", nullable = false)
    private Set<String> alternativeNames = Sets.newHashSet();

    @ElementCollection
    @CollectionTable(name = "TEAM_FALSE_NAME", joinColumns = @JoinColumn(name = "TEAM_ID"))
    @Column(name = "VALUE", nullable = false)
    private Set<String> falseNames = Sets.newHashSet();

    @Embedded
    private Audit audit;

    @Version
    private long version;
}
