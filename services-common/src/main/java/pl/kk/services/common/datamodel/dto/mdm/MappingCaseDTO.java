package pl.kk.services.common.datamodel.dto.mdm;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.validation.constraints.NotNull;
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
public class MappingCaseDTO implements Serializable {

    @lombok.NonNull
    @NotNull
    private Long id;

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
    private String matchTeams;

    @lombok.NonNull
    @NotNull
    private Double awaySimilarityFactor;

    @lombok.NonNull
    @NotNull
    private Double homeSimilarityFactor;

    @lombok.NonNull
    @NotNull
    private String sourceSystemName;

    @lombok.NonNull
    @NotNull
    private String status;

    @lombok.NonNull
    @NotNull
    private ZonedDateTime createdOn;

    @lombok.NonNull
    @NotNull
    private ZonedDateTime updatedOn;

    @lombok.NonNull
    @NotNull
    private String createdBy;

    @lombok.NonNull
    @NotNull
    private String updatedBy;
}
