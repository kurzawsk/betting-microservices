package pl.kk.services.mdm.model.dto.mapping;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.util.Set;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class TeamMatchingResultDTO {

    @NonNull
    private String teamName;
    @NonNull
    private Long matchedTeamId;
    @NonNull
    private Set<String> matchedTeamNames;
    @NonNull
    private Double similarityFactor;
}
