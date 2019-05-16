package pl.kk.services.common.misc;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@ToString
public class FeignBadResponseWrapper extends HystrixBadRequestException {
    private final int status;
    private final HttpHeaders headers;
    private final String body;

    public FeignBadResponseWrapper(String message, int status, HttpHeaders headers, String body) {
        super(message);
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String getMessage() {
        return toString();
    }
}