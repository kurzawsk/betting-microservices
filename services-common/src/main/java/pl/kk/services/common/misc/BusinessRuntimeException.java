package pl.kk.services.common.misc;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Objects;

public class BusinessRuntimeException extends RuntimeException {

    private List<String> messages;

    public BusinessRuntimeException(List<String> messages, Throwable cause) {
        super(cause);
        this.messages = messages;
    }

    public BusinessRuntimeException(Throwable cause) {
        super(cause);
    }

    public BusinessRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }


    public BusinessRuntimeException(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return Objects.nonNull(messages) ?
                StringUtils.join(messages, ",") : super.getMessage();
    }
}
