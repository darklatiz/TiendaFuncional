package tech.terabyte.labs.funcstore.app;

import java.math.BigDecimal;
import java.util.function.IntPredicate;

public record DiscountRule(IntPredicate when, BigDecimal pct) {
}


