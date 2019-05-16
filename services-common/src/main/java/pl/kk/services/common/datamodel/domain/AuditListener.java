package pl.kk.services.common.datamodel.domain;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZonedDateTime;
import java.util.Objects;

public class AuditListener {

    @PrePersist
    public void setCreatedOn(Auditable auditable) {
        Audit audit = auditable.getAudit();

        if (Objects.isNull(audit)) {
            audit = new Audit();
            auditable.setAudit(audit);
        }

        ZonedDateTime now = ZonedDateTime.now();
        String principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        audit.setCreatedOn(now);
        audit.setCreatedBy(principal);

        audit.setUpdatedOn(now);
        audit.setUpdatedBy(principal);
    }

    @PreUpdate
    public void setUpdatedOn(Auditable auditable) {
        Audit audit = auditable.getAudit();

        audit.setUpdatedOn(ZonedDateTime.now());
        audit.setUpdatedBy(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString());
    }
}