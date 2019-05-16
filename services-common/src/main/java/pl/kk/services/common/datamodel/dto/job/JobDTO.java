package pl.kk.services.common.datamodel.dto.job;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.time.ZonedDateTime;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class JobDTO {
    @NonNull
    private Long id;
    @NonNull
    private String code;
    @NonNull
    private String urlSuffix;
    @NonNull
    private String serviceName;
    @NonNull
    private boolean enabled;

    private ZonedDateTime lastExecutionStartTime;
    private ZonedDateTime lastExecutionFinishTime;
    private String lastExecutionJobStatus;
    private String lastExecutionErrorMessage;
    private String description;
}
