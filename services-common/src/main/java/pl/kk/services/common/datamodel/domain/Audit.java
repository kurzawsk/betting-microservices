package pl.kk.services.common.datamodel.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Embeddable
public class Audit {

    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_on")
    private ZonedDateTime updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

}