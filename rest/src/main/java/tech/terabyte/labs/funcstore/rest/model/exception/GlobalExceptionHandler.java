package tech.terabyte.labs.funcstore.rest.model.exception;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

import static tech.terabyte.labs.funcstore.rest.model.Responses.error;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public Object notFound(NoSuchElementException ex) {
        return error(404, "NOT_FOUND", ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object badRequest(IllegalArgumentException ex) {
        return error(400, "BAD_REQUEST", ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validation(MethodArgumentNotValidException ex) {

        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
          .map(fe -> Map.of("field", fe.getField(), "msg", fe.getDefaultMessage()))
          .toList();

        Map<String, Object> details = Map.of("fieldErrors", fieldErrors);

        return error(422, "VALIDATION_ERROR", "Validation failed", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object malformed(HttpMessageNotReadableException ex) {
        return error(400, "MALFORMED_JSON", "Malformed JSON body", null);
    }

    @ExceptionHandler(Exception.class)
    public Object internal(Exception ex) {
        // log ex; avoid leaking internals to clients
        return error(500, "INTERNAL_ERROR", "Unexpected server error", null);
    }

}
