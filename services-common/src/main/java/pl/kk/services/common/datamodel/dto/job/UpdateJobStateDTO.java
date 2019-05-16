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
public class UpdateJobStateDTO {

    @NonNull
    private Operation operation;

    public enum Operation {
        RUN_JOB, ENABLE_JOB, DISABLE_JOB
    }
}


