package tech.terabyte.labs.funcstore.domain;

import java.math.BigDecimal;

public interface PriceCatalog {
    BigDecimal unitPrice(Marca marca);
    GiftPolicy giftPolicy(Marca marca);
}
