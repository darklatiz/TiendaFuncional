package tech.terabyte.labs.funcstore.domain;

import java.util.List;
import java.util.Optional;

public interface ProductCatalog {
    Optional<Producto> bySku(String sku);
    List<Producto> all();
}
