package pl.kk.services.common.datamodel.dto.reporting;

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
public class SendEmailDTO {

    @NonNull
    private String subject;

    private boolean html;

    @NonNull
    private String content;
}
