package tech.terabyte.labs.funcstore.rest.model;

import java.util.Map;

public record ApiError(
  String code,
  String message,
  Map<String, Object> details
) {
}
