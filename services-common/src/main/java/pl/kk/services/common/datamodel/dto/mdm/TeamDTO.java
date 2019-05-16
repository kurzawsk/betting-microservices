package pl.kk.services.common.datamodel.dto.mdm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class TeamDTO implements Serializable {
    @NonNull
    private Long id;
    @NonNull
    @NotBlank
    private String name;

    private Set<String> alternativeNames;

    private Set<String> falseNames;
}
