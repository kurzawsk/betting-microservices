package pl.kk.services.reporting.model.dto;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class ToggleReportDTO {

    boolean statusEnabled;
}
