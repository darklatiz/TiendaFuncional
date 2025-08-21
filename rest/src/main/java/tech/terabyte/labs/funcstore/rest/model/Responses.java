package tech.terabyte.labs.funcstore.rest.model;

import org.springframework.http.ResponseEntity;
import tech.terabyte.labs.funcstore.rest.support.RequestIdContext;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class Responses {
    private Responses() {
    }

    private static Meta meta(Map<String,Object> pagination) {
        String id = RequestIdContext.get(); // set by the filter
        return new Meta(id, DateTimeFormatter.ISO_INSTANT.format(Instant.now()), pagination);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, data, null, meta(null)));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(201).body(new ApiResponse<>(true, data, null, meta(null)));
    }

    public static ResponseEntity<ApiResponse<Object>> error(int httpStatus, String code, String message,
                                                            Map<String, Object> details) {
        return ResponseEntity.status(httpStatus)
          .body(new ApiResponse<>(false, null, new ApiError(code, message, details), meta(null)));
    }

}
