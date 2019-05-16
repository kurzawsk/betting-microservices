package pl.kk.services.reporting.model.domain;

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
public class Report extends ManagedEntity {

    @Column(unique = true, updatable = false, nullable = false)
    private String code;

    @Column(updatable = false, nullable = false)
    private String title;

    private String description;

    @Column(name = "URL_SUFFIX", updatable = false, nullable = false)
    private String urlSuffix;

    @Column(name = "SERVICE_NAME", updatable = false, nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "DEFAULT_PARAMETERS", updatable = false, nullable = false)
    private String defaultParameters;

    @Column(name = "LAST_EXECUTION_START_TIME")
    private ZonedDateTime lastExecutionStartTime;

    @Column(name = "LAST_EXECUTION_FINISH_TIME")
    private ZonedDateTime lastExecutionFinishTime;

    @Column(name = "LAST_EXECUTION_RESULT_DATA")
    private String lastExecutionResultData;

    @Column(name = "LAST_EXECUTION_PARAMETERS")
    private String lastExecutionParameters;

    @Column(name = "DATA_TYPE")
    @Enumerated(EnumType.STRING)
    private DataType type;

    @Version
    private Long version;

    public enum DataType {
        KEY_VALUE, TABLE
    }
}
