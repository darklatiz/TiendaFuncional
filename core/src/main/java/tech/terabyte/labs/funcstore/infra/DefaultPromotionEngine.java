package tech.terabyte.labs.funcstore.infra;

import tech.terabyte.labs.funcstore.app.DiscountRule;
import tech.terabyte.labs.funcstore.app.PromotionEngine;
import tech.terabyte.labs.funcstore.domain.Marca;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

public class DefaultPromotionEngine implements PromotionEngine {

    private static final List<DiscountRule> DISCOUNT_RULES = List.of(
      new DiscountRule(q -> q < 2, BigDecimal.ZERO),
      new DiscountRule(q -> q >= 2 && q < 4, new BigDecimal("0.05")),
      new DiscountRule(q -> q >= 5 && q < 9, new BigDecimal("0.085")),
      new DiscountRule(q -> q >= 10, new BigDecimal("0.12"))
    );

    // Funciones por marca (gifts): f(q) -> adaptadores
    private static final Map<Marca, IntUnaryOperator> GIFTS = Map.of(
      Marca.SONY, q -> (q / 2),   // 1 por cada 2
      Marca.JBL, q -> (q / 3) * 2,   // 2 por cada 3
      Marca.LOGITECH, q -> q,         // 1 por cada 1
      Marca.REDRAGON, q -> q * 2          // 2 por cada 1
    );

    @Override
    public BigDecimal discountPct(int cantidad) {
        // La primera regla que “matchea” gana
        return DISCOUNT_RULES.stream()
          .filter(r -> r.when().test(cantidad))
          .findFirst()
          .map(DiscountRule::pct)
          .orElse(BigDecimal.ZERO);
    }

    @Override
    public int giftsFor(Marca marca, int cantidad) {
        return GIFTS.getOrDefault(marca, q -> 0).applyAsInt(cantidad);
    }

}
