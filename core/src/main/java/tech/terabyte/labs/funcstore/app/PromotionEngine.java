package tech.terabyte.labs.funcstore.app;

import tech.terabyte.labs.funcstore.domain.Marca;

import java.math.BigDecimal;

public interface PromotionEngine {
    BigDecimal discountPct(int cantidad);

    /** Cantidad de adaptadores obsequiados según marca y cantidad */
    int giftsFor(Marca marca, int cantidad);
}
