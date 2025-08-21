package tech.terabyte.labs.funcstore.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tech.terabyte.labs.funcstore.app.CartService;
import tech.terabyte.labs.funcstore.app.CartServiceImpl;
import tech.terabyte.labs.funcstore.app.CartStore;
import tech.terabyte.labs.funcstore.app.PromotionEngine;
import tech.terabyte.labs.funcstore.domain.ProductCatalog;
import tech.terabyte.labs.funcstore.infra.DefaultPromotionEngine;
import tech.terabyte.labs.funcstore.infra.InMemoryCartStore;
import tech.terabyte.labs.funcstore.infra.InMemoryProductCatalog;
import tech.terabyte.labs.funcstore.rest.support.RequestIdFilter;

@SpringBootApplication
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    // Beans: se enchufan igual que en CLI
    @Bean
    public ProductCatalog productCatalog() {
        return new InMemoryProductCatalog();
    }

    @Bean
    public PromotionEngine promotionEngine() {
        return new DefaultPromotionEngine();
    }

    @Bean
    public CartStore cartStore() {
        return new InMemoryCartStore();
    }

    @Bean
    public CartService cartService(ProductCatalog catalog, PromotionEngine promo) {
        return new CartServiceImpl(catalog, promo);
    }

    @Bean
    public RequestIdFilter requestIdFilter() {
        return new RequestIdFilter();
    }


}
