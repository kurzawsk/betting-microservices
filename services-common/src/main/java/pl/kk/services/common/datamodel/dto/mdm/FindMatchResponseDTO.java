package pl.kk.services.common.datamodel.dto.mdm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class FindMatchResponseDTO implements Serializable {

    private Long matchId;

    private List<Long> mappingCasesIds;

    @lombok.NonNull
    @NotNull
    private ResponseType responseType;


    public enum ResponseType {MATCH_FOUND, MATCH_NOT_FOUND, MAPPING_CASES_CREATED}

}
