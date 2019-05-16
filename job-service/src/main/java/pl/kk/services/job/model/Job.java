package pl.kk.services.job.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Job extends ManagedEntity {

    @Column(unique = true, updatable = false, nullable = false)
    private String code;

    private String description;

    @Column(name="URL_SUFFIX" ,updatable = false, nullable = false)
    private String urlSuffix;

    @Column(name = "SERVICE_NAME", updatable = false, nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "LAST_EXECUTION_KEY")
    private String lastExecutionKey;

    @Column(name = "LAST_EXECUTION_START_TIME")
    private ZonedDateTime lastExecutionStartTime;

    @Column(name = "LAST_EXECUTION_FINISH_TIME")
    private ZonedDateTime lastExecutionFinishTime;

    @Column(name = "LAST_EXECUTION_JOB_STATUS")
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus lastExecutionJobStatus;

    @Column(name = "LAST_EXECUTION_ERROR_MSG")
    private String lastExecutionErrorMessage;

    @Version
    private Long version;
}
