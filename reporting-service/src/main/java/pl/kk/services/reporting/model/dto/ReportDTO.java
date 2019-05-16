package pl.kk.services.reporting.model.dto;


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
public class ReportDTO {

    @NonNull
    private Long id;
    @NonNull
    private String code;
    private String title;
    private String description;
    @NonNull
    private String urlSuffix;
    @NonNull
    private String serviceName;
    private String defaultParameters;

    private boolean enabled;
    private ZonedDateTime lastExecutionStartTime;
    private ZonedDateTime lastExecutionFinishTime;
    private String lastExecutionResultData;
    private String lastExecutionParameters;
}
