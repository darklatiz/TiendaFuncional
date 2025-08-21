package tech.terabyte.labs.funcstore.infra;

import tech.terabyte.labs.funcstore.domain.Categoria;
import tech.terabyte.labs.funcstore.domain.Marca;
import tech.terabyte.labs.funcstore.domain.ProductCatalog;
import tech.terabyte.labs.funcstore.domain.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductCatalog implements ProductCatalog {
    // Ahora usamos TreeMap con un Comparator custom
    private final Map<String, Producto> bySku;

    public InMemoryProductCatalog() {
        // --- Datos ---
        List<Producto> data = List.of(
            new Producto("AUR-SON-001", "Auriculares Sony WH-CH520", Categoria.AURICULARES, Marca.SONY, new BigDecimal("120.00"), 50, 4.5),
            new Producto("AUR-JBL-001", "Auriculares JBL Tune 510BT", Categoria.AURICULARES, Marca.JBL, new BigDecimal("135.50"), 40, 4.4),
            new Producto("AUR-LOG-001", "Auriculares Logitech H390", Categoria.AURICULARES, Marca.LOGITECH, new BigDecimal("98.75"), 60, 4.2),
            new Producto("AUR-RED-001", "Auriculares Redragon Zeus", Categoria.AURICULARES, Marca.REDRAGON, new BigDecimal("110.00"), 35, 4.3),
            new Producto("PER-RED-001", "Teclado Mecánico Redragon K552", Categoria.PERIFERICOS, Marca.REDRAGON, new BigDecimal("1599.00"), 15, 4.8),
            new Producto("MOU-LOG-001", "Mouse Logitech G502", Categoria.MOUSE, Marca.LOGITECH, new BigDecimal("1499.00"), 20, 4.7),
            new Producto("LIB-FUN-001", "Libro: Programación Funcional", Categoria.LIBROS, Marca.GENERICA, new BigDecimal("699.00"), 25, 4.9),
            new Producto("HOG-CAF-001", "Cafetera XPress", Categoria.HOGAR, Marca.GENERICA, new BigDecimal("899.50"), 10, 4.2),
            new Producto("ROP-PLA-001", "Playera Negra", Categoria.ROPA, Marca.GENERICA, new BigDecimal("249.00"), 100, 4.1),
            new Producto("JUG-BLO-001", "Set Bloques Niño", Categoria.JUGUETES, Marca.GENERICA, new BigDecimal("399.00"), 30, 4.5),
            new Producto("ALI-CER-001", "Cereal Integral", Categoria.ALIMENTOS, Marca.GENERICA, new BigDecimal("89.00"), 100, 4.0)
        );

        // 2) Ordenamos por CATEGORÍA y luego por NOMBRE
        Comparator<Producto> byCategoryThenName = Comparator
          .comparing(Producto::categoria) // por ordinal del enum
          .thenComparing(Producto::nombre, String.CASE_INSENSITIVE_ORDER);

        List<Producto> sorted = data.stream()
          .sorted(byCategoryThenName)
          .toList();

        // 3) Volcamos en LinkedHashMap para preservar el orden
        Map<String, Producto> linked = new LinkedHashMap<>();
        for (Producto p : sorted) {
            linked.put(p.sku(), p);
        }
        this.bySku = Collections.unmodifiableMap(linked);

    }

    @Override
    public Optional<Producto> bySku(String sku) {
        return Optional.ofNullable(bySku.get(sku));
    }

    @Override
    public List<Producto> all() {
        return new ArrayList<>(bySku.values());
    }
}
