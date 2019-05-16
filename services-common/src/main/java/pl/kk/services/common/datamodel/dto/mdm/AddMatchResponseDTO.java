package pl.kk.services.common.datamodel.dto.mdm;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

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
public class AddMatchResponseDTO implements Serializable {

    private BasicMatchDTO match;

    private List<Long> mappingCasesIds;

    @NonNull
    private ResponseType responseType;

    public enum ResponseType {
        MATCH_ADDED, MATCH_ALREADY_EXISTS, MAPPING_CASES_CREATED, AMBIG_MATCH_TEAM_CASE_CREATED, MATCH_POSTPONED_SILENTLY, TEAMS_SWITCHED
    }
}
