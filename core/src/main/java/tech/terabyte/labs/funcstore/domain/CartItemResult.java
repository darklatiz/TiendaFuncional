package tech.terabyte.labs.funcstore.domain;

import java.math.BigDecimal;

public record CartItemResult(
  Producto producto,
  int cantidad,
  BigDecimal subTotal,
  BigDecimal descuento,
  BigDecimal total
) {
}
