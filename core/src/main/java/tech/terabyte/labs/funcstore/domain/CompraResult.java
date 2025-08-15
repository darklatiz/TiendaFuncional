package tech.terabyte.labs.funcstore.domain;

import java.math.BigDecimal;

public record CompraResult(
  Marca marca,
  int cantidad,
  BigDecimal precioUnitario,
  BigDecimal importeCompra,
  BigDecimal porcentajeDescuento,
  BigDecimal importeDescuento,
  BigDecimal totalAPagar,
  int adaptadoresObsequiados) {
}
