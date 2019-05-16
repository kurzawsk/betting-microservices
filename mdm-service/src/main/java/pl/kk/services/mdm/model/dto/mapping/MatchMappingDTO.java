package pl.kk.services.mdm.model.dto.mapping;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.validation.constraints.NotNull;


@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class MatchMappingDTO {

    @lombok.NonNull
    @NotNull
    private String homeTeamName;

    @lombok.NonNull
    @NotNull
    private String awayTeamName;

    @lombok.NonNull
    @NotNull
    private Long matchId;

    @lombok.NonNull
    @NotNull
    private Double awaySimilarityFactor;

    @lombok.NonNull
    @NotNull
    private Double homeSimilarityFactor;

    @lombok.NonNull
    @NotNull
    private String sourceSystemName;
}
