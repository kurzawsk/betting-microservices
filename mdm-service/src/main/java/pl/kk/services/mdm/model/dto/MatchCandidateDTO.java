package pl.kk.services.mdm.model.dto;

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
public class MatchCandidateDTO {

    @NonNull
    private Long homeTeamId;
    @NonNull
    private Long awayTeamId;
    @NonNull
    private ZonedDateTime startTime;
}
