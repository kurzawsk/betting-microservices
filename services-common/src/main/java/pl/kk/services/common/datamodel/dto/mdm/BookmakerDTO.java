package pl.kk.services.common.datamodel.dto.mdm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class BookmakerDTO implements Serializable {

    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private Map<String, String> alternativeNames;
    @NonNull
    private BigDecimal taxPercent;

    private String url;

}
