package tech.terabyte.labs.funcstore.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CartResult(
  List<CartItemResult> items,
  BigDecimal subtotal,
  BigDecimal descuentoTotal,
  BigDecimal total,
  Map<String, Integer> regalos
) {
}
