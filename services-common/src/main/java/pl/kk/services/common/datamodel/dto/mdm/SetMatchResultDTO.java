package pl.kk.services.common.datamodel.dto.mdm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class SetMatchResultDTO implements Serializable {

    private Integer homeTeamScore;
    private Integer awayTeamScore;

    private AbnormalMatchFinishReason abnormalMatchFinishReason;

    public enum AbnormalMatchFinishReason {
        CANCELLED, POSTPONED, NOT_FOUND
    }

}
