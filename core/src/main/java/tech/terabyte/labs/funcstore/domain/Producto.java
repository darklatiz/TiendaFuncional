package tech.terabyte.labs.funcstore.domain;

import java.math.BigDecimal;

public record Producto(
  String sku,
  String nombre,
  Categoria categoria,
  Marca marca,
  BigDecimal precio,
  int stock,
  double rating
) {
}
