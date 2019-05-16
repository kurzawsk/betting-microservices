package pl.kk.services.common.datamodel.domain;

public interface Auditable {

    Audit getAudit();

    void setAudit(Audit audit);
}