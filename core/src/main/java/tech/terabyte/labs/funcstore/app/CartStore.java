package tech.terabyte.labs.funcstore.app;

import tech.terabyte.labs.funcstore.domain.CartLine;

import java.util.List;
import java.util.Optional;

public interface CartStore {
    void save(String cartId, List<CartLine> items);
    Optional<List<CartLine>> find(String cartId);
    void delete(String cartId);
}
