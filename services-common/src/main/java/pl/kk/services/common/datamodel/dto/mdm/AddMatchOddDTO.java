package pl.kk.services.common.datamodel.dto.mdm;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class AddMatchOddDTO implements Serializable {

    @NonNull
    private Long matchId;

    @NonNull
    private Long bookmakerId;

    @NonNull
    private BigDecimal odd1;

    @NonNull
    private BigDecimal odd2;

    @NonNull
    private BigDecimal oddX;

    private BigDecimal odd1X;
    private BigDecimal odd12;
    private BigDecimal oddX2;
    private BigDecimal oddBTSY;
    private BigDecimal oddBTSN;
    private BigDecimal oddO05;
    private BigDecimal oddO15;
    private BigDecimal oddO25;
    private BigDecimal oddO35;
    private BigDecimal oddO45;
    private BigDecimal oddO55;
    private BigDecimal oddO65;
    private BigDecimal oddU05;
    private BigDecimal oddU15;
    private BigDecimal oddU25;
    private BigDecimal oddU35;
    private BigDecimal oddU45;
    private BigDecimal oddU55;
    private BigDecimal oddU65;
}
