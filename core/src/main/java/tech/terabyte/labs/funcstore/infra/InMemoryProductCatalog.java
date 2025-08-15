package tech.terabyte.labs.funcstore.infra;

import tech.terabyte.labs.funcstore.domain.Categoria;
import tech.terabyte.labs.funcstore.domain.Marca;
import tech.terabyte.labs.funcstore.domain.ProductCatalog;
import tech.terabyte.labs.funcstore.domain.Producto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductCatalog implements ProductCatalog {
    private final Map<String, Producto> bySku = new LinkedHashMap<>();

    public InMemoryProductCatalog() {
        // AURICULARES (con las marcas y precios de las imágenes)
        add(new Producto("AUR-SON-001", "Auriculares Sony WH-CH520", Categoria.AURICULARES, Marca.SONY, new BigDecimal("120.00"), 50, 4.5));
        add(new Producto("AUR-JBL-001", "Auriculares JBL Tune 510BT", Categoria.AURICULARES, Marca.JBL, new BigDecimal("135.50"), 40, 4.4));
        add(new Producto("AUR-LOG-001", "Auriculares Logitech H390", Categoria.AURICULARES, Marca.LOGITECH, new BigDecimal("98.75"), 60, 4.2));
        add(new Producto("AUR-RED-001", "Auriculares Redragon Zeus", Categoria.AURICULARES, Marca.REDRAGON, new BigDecimal("110.00"), 35, 4.3));

        // PERIFÉRICOS / MOUSE / LIBROS / HOGAR / ROPA / JUGUETES / ALIMENTOS
        add(new Producto("PER-RED-001", "Teclado Mecánico Redragon K552", Categoria.PERIFERICOS, Marca.REDRAGON, new BigDecimal("1599.00"), 15, 4.8));
        add(new Producto("MOU-LOG-001", "Mouse Logitech G502", Categoria.MOUSE, Marca.LOGITECH, new BigDecimal("1499.00"), 20, 4.7));
        add(new Producto("LIB-FUN-001", "Libro: Programación Funcional", Categoria.LIBROS, Marca.GENERICA, new BigDecimal("699.00"), 25, 4.9));
        add(new Producto("HOG-CAF-001", "Cafetera XPress", Categoria.HOGAR, Marca.GENERICA, new BigDecimal("899.50"), 10, 4.2));
        add(new Producto("ROP-PLA-001", "Playera Negra", Categoria.ROPA, Marca.GENERICA, new BigDecimal("249.00"), 100, 4.1));
        add(new Producto("JUG-BLO-001", "Set Bloques Niño", Categoria.JUGUETES, Marca.GENERICA, new BigDecimal("399.00"), 30, 4.5));
        add(new Producto("ALI-CER-001", "Cereal Integral", Categoria.ALIMENTOS, Marca.GENERICA, new BigDecimal("89.00"), 100, 4.0));
    }

    private void add(Producto p) {
        bySku.put(p.sku(), p);
    }

    @Override
    public Optional<Producto> bySku(String sku) {
        return Optional.ofNullable(bySku.get(sku));
    }

    @Override
    public List<Producto> all() {
        return List.copyOf(bySku.values());
    }
}
