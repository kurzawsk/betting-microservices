package pl.kk.services.common.datamodel.dto.reporting;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class ReportInstanceKeyValueDataDTO implements ReportInstanceDataDTO {

    @NonNull
    private List<Map.Entry<String, String>> keyValueData;
}
