package pl.kk.services.common.service.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.kk.services.common.misc.BusinessRuntimeException;
import pl.kk.services.common.misc.BusinessValidationException;
import pl.kk.services.common.misc.EntityNotFoundException;
import pl.kk.services.common.misc.FeignBadResponseWrapper;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FeignBadResponseWrapper.class})
    public ResponseEntity<?> handleFeignBadResponseWrapper(
            Exception ex, WebRequest request) {

        FeignBadResponseWrapper wrapper = (FeignBadResponseWrapper) ex;
        return new ResponseEntity<>(
                wrapper.getBody(), wrapper.getHeaders(), HttpStatus.valueOf(wrapper.getStatus()));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handleConstraintViolationException(
            Exception ex, WebRequest request) {
        return getResponseEntity(request, true, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BusinessValidationException.class})
    public ResponseEntity<?> handleBusinessValidationException(
            Exception ex, WebRequest request) {
        return getResponseEntity(request, false, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<?> handleEntityNotFoundException(
            Exception ex, WebRequest request) {
        return getResponseEntity(request, false, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BusinessRuntimeException.class})
    public ResponseEntity<?> handleBussinessRuntimeException(
            Exception ex, WebRequest request) {
        return getResponseEntity(request, true, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity getResponseEntity(WebRequest request, boolean showStackTrace, HttpStatus status) {
        DefaultErrorAttributes errorAttributes = new DefaultErrorAttributes();
        request.setAttribute("javax.servlet.error.status_code", status.value(), 0);
        return new ResponseEntity<>(errorAttributes.getErrorAttributes(request, showStackTrace), new HttpHeaders(), status);
    }
}