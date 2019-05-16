package pl.kk.services.common.datamodel.dto.job;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class CreateJobDTO {

    @NonNull
    private String code;
    @NonNull
    private String urlSuffix;
    @NonNull
    private String serviceName;
    @NonNull
    private boolean enabled;
    private String description;
}
