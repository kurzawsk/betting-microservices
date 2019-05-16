package pl.kk.services.common.datamodel.dto.mdm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class BasicMatchDTO implements Serializable {
    @NonNull
    private Long id;
    @NonNull
    private Long homeTeamId;
    @NonNull
    private Long awayTeamId;
    @NonNull
    private ZonedDateTime startTime;

    private Integer homeScore;
    private Integer awayScore;
    private String resultType;

}
