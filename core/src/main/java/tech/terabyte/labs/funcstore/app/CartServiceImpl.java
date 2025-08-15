package tech.terabyte.labs.funcstore.app;

import tech.terabyte.labs.funcstore.domain.CartItemResult;
import tech.terabyte.labs.funcstore.domain.CartLine;
import tech.terabyte.labs.funcstore.domain.CartResult;
import tech.terabyte.labs.funcstore.domain.Categoria;
import tech.terabyte.labs.funcstore.domain.ProductCatalog;
import tech.terabyte.labs.funcstore.domain.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class CartServiceImpl implements CartService {
    private static final String G_ADAPTADOR = "ADAPTADOR_BT";

    private final ProductCatalog productCatalog;
    private final PromotionEngine promotionEngine;

    public CartServiceImpl(ProductCatalog productCatalog, PromotionEngine promotionEngine) {
        this.productCatalog = productCatalog;
        this.promotionEngine = promotionEngine;
    }

    @Override
    public CartResult checkout(List<CartLine> lines) {

        if (lines == null || lines.isEmpty()) {
            return new CartResult(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, Map.of());
        }

        final List<_LineResolved> resolvedList = new ArrayList<>();
        for (var line : lines) {
            if (line.cantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para SKU " + line.sku());
            }
            var p = productCatalog.bySku(line.sku()).orElseThrow(() -> new NoSuchElementException("SKU no encontrado:" + line.sku()));
            var sub = p.precio().multiply(BigDecimal.valueOf(line.cantidad())).setScale(2, RoundingMode.HALF_UP);
            resolvedList.add(new _LineResolved(p, line.cantidad(), sub));
        }

        // Descuentos y regalos SOLO para AURICULARES (por marca, como en las imágenes)
        Map<String, BigDecimal> descPorSku = new HashMap<>();
        Map<String, Integer> gifts = new HashMap<>();

        var auriculares = resolvedList.stream()
          .filter(r -> r.producto().categoria() == Categoria.AURICULARES)
          .collect(Collectors.groupingBy(r -> r.producto().marca()));

        for (var e : auriculares.entrySet()) {
            var marca = e.getKey();
            var group = e.getValue();

            int totalQty = group.stream().mapToInt(_LineResolved::qty).sum();
            BigDecimal subtotalMarca = group.stream().map(_LineResolved::subtotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

            // % descuento según cantidad DE ESA MARCA
            BigDecimal pct = promotionEngine.discountPct(totalQty);
            BigDecimal descMarca = subtotalMarca.multiply(pct).setScale(2, RoundingMode.HALF_UP);

            // Repartimos descuento proporcional por línea
            for (var r : group) {
                BigDecimal proportion = (subtotalMarca.signum() == 0)
                  ? BigDecimal.ZERO
                  : r.subtotal().divide(subtotalMarca, 8, RoundingMode.HALF_UP);
                BigDecimal descLinea = descMarca.multiply(proportion).setScale(2, RoundingMode.HALF_UP);
                descPorSku.merge(r.producto().sku(), descLinea, BigDecimal::add);
            }

            // Regalos por marca (adaptadores)
            int adapters = promotionEngine.giftsFor(marca, totalQty);
            if (adapters > 0) gifts.merge(G_ADAPTADOR, adapters, Integer::sum);
        }

        // Construimos resultados por línea
        List<CartItemResult> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descTotal = BigDecimal.ZERO;

        for (var r : resolvedList) {
            var desc = descPorSku.getOrDefault(r.producto().sku(), BigDecimal.ZERO);
            var tot = r.subtotal().subtract(desc).setScale(2, RoundingMode.HALF_UP);
            items.add(new CartItemResult(r.producto(), r.qty(), r.subtotal(), desc, tot));
            subtotal = subtotal.add(r.subtotal());
            descTotal = descTotal.add(desc);
        }

        BigDecimal total = subtotal.subtract(descTotal).setScale(2, RoundingMode.HALF_UP);
        return new CartResult(List.copyOf(items), subtotal, descTotal, total, Map.copyOf(gifts));

    }

    private record _LineResolved(Producto producto, int qty, BigDecimal subtotal) {
    }
}
