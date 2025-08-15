package tech.terabyte.labs.funcstore.app;

import tech.terabyte.labs.funcstore.domain.CompraRequest;
import tech.terabyte.labs.funcstore.domain.CompraResult;
import tech.terabyte.labs.funcstore.domain.GiftPolicy;
import tech.terabyte.labs.funcstore.domain.PriceCatalog;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CompraServiceImpl implements CompraService {
    private final PriceCatalog catalog;
    private final PromotionEngine promotionEngine;


    public CompraServiceImpl(PriceCatalog catalog, PromotionEngine promotionEngine) {
        this.catalog = catalog;
        this.promotionEngine = promotionEngine;
    }

    @Override
    public CompraResult calcular(CompraRequest req) {
        var marca = req.marca();
        var cantidad = req.cantidad();
        if (cantidad <= 0) throw new IllegalArgumentException("cantidad debe ser > 0");

        var unit = catalog.unitPrice(marca);
        var importeCompra = unit.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);

        var pct = promotionEngine.discountPct(cantidad);
        var importeDesc = importeCompra.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        var total = importeCompra.subtract(importeDesc).setScale(2, RoundingMode.HALF_UP);

        var gifts = promotionEngine.giftsFor(marca, cantidad);

        return new CompraResult(marca, cantidad, unit, importeCompra, pct, importeDesc, total, gifts);
    }

    private static BigDecimal descuentoPorCantidad(int c) {
        if (c < 2) return BigDecimal.ZERO;
        if (c < 4) return new BigDecimal("0.05");
        if (c < 9) return new BigDecimal("0.085");
        return new BigDecimal("0.12");
    }

    private static int calcularAdaptadores(GiftPolicy p, int cantidad) {
        if (p.perUnits() == 1) return cantidad * p.adapters();   // por cada unidad
        return (cantidad / p.perUnits()) * p.adapters();          // cada N unidades
    }


}
