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
public class JobExecutionDTO {
    @NonNull
    private Long id;

    @NonNull
    private Long jobId;

    @NonNull
    private ZonedDateTime startTime;

    private ZonedDateTime finishTime;

    @NonNull
    private String startedBy;

    @NonNull
    private String jobExecutionStatus;

    private String errorMessage;
}
