package tech.terabyte.labs.funcstore.rest.model;

public record ApiResponse<T>(
  boolean success,
  T data,
  ApiError error,
  Meta meta
) {
}
