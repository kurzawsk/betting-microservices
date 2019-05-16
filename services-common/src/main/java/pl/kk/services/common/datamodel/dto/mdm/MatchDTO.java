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
public class MatchDTO implements Serializable {
    @NonNull
    private Long id;
    @NonNull
    private String sourceSystemName;
    @NonNull
    private String sourceSystemId;
    @NonNull
    private BasicTeamDTO homeTeam;
    @NonNull
    private BasicTeamDTO awayTeam;
    @NonNull
    private ZonedDateTime startTime;

    private Integer homeScore;
    private Integer awayScore;
    private String resultType;
    private ZonedDateTime markedAsFinishedTime;


}
