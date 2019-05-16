package pl.kk.services.common.datamodel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class PagedSearchRequestDTO {
    Integer page;
    Integer size;

    @JsonProperty("sort-att")
    String sortAtt;

    @JsonProperty("sort-desc")
    boolean sortDesc;
}
