package pl.kk.services.common.datamodel.dto.mdm;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class BasicMappingCaseDTO implements Serializable {

    @NonNull
    @NotNull
    private Long id;

    @NonNull
    @NotNull
    private String homeTeamName;

    @NonNull
    @NotNull
    private String awayTeamName;

    @NonNull
    @NotNull
    private Long matchId;

    @NonNull
    @NotNull
    private Double awaySimilarityFactor;

    @NonNull
    @NotNull
    private Double homeSimilarityFactor;

    @NonNull
    @NotNull
    private String sourceSystemName;

    @NonNull
    @NotNull
    private String status;
}
