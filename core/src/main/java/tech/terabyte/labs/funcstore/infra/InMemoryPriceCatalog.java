package tech.terabyte.labs.funcstore.infra;

import tech.terabyte.labs.funcstore.domain.GiftPolicy;
import tech.terabyte.labs.funcstore.domain.Marca;
import tech.terabyte.labs.funcstore.domain.PriceCatalog;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public final class InMemoryPriceCatalog implements PriceCatalog {

    private final Map<Marca, BigDecimal> prices = new EnumMap<>(Marca.class);
    private final Map<Marca, GiftPolicy> gifts = new EnumMap<>(Marca.class);

    public InMemoryPriceCatalog() {
        prices.put(Marca.SONY,     new BigDecimal("120.00"));
        prices.put(Marca.JBL,      new BigDecimal("135.50"));
        prices.put(Marca.LOGITECH, new BigDecimal("98.75"));
        prices.put(Marca.REDRAGON, new BigDecimal("110.00"));

        gifts.put(Marca.SONY,     new GiftPolicy(1, 2)); // 1 c/2
        gifts.put(Marca.JBL,      new GiftPolicy(2, 3)); // 2 c/3
        gifts.put(Marca.LOGITECH, new GiftPolicy(1, 1)); // 1 c/u
        gifts.put(Marca.REDRAGON, new GiftPolicy(2, 1)); // 2 c/u
    }



    @Override
    public BigDecimal unitPrice(Marca marca) {
        return prices.get(marca);
    }

    @Override
    public GiftPolicy giftPolicy(Marca marca) {
        return gifts.get(marca);
    }
}
