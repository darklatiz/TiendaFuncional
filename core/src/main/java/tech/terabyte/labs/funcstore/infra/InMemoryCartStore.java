package tech.terabyte.labs.funcstore.infra;

import tech.terabyte.labs.funcstore.app.CartStore;
import tech.terabyte.labs.funcstore.domain.CartLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCartStore implements CartStore {

    private final Map<String, List<CartLine>> carts = new ConcurrentHashMap<>();


    @Override
    public void save(String cartId, List<CartLine> items) {
        carts.put(cartId, new ArrayList<>(items));
    }

    @Override
    public Optional<List<CartLine>> find(String cartId) {
        return Optional.ofNullable(carts.get(cartId))
          .map(ArrayList::new);
    }

    @Override
    public void delete(String cartId) {
        carts.remove(cartId);
    }
}
