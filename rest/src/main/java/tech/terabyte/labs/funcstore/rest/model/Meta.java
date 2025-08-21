package tech.terabyte.labs.funcstore.rest.model;

import java.util.Map;

public record Meta(
  String requestId,
  String timestamp,     // ISO-8601 UTC
  Map<String, Object> pagination
) {
}
