package tech.terabyte.labs.funcstore.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.terabyte.labs.funcstore.app.CartService;
import tech.terabyte.labs.funcstore.app.CartStore;
import tech.terabyte.labs.funcstore.domain.CartLine;
import tech.terabyte.labs.funcstore.domain.CartResult;
import tech.terabyte.labs.funcstore.rest.model.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static tech.terabyte.labs.funcstore.rest.model.Responses.created;
import static tech.terabyte.labs.funcstore.rest.model.Responses.ok;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartStore cartStore;
    private final CartService cartService;

    public CartController(CartStore cartStore, CartService cartService) {
        this.cartStore = cartStore;
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> createCart() {
        String id = UUID.randomUUID().toString();
        cartStore.save(id, new ArrayList<>());
        return created(Map.of("cartId", id));
    }

    @PutMapping("/{id}/items")
    public ResponseEntity<ApiResponse<List<CartLine>>> addItem(@PathVariable("id") String id, @RequestBody CartLine item) {
        var lines = cartStore.find(id).orElseThrow(() -> new NoSuchElementException("Cart not found: " + id));
        lines.removeIf(l -> l.sku().equals(item.sku())); // replace
        lines.add(item);
        cartStore.save(id, lines);
        return ok(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<CartLine>>> viewCart(@PathVariable("id") String id) {
        var lines = cartStore.find(id).orElseThrow(() -> new NoSuchElementException("Cart not found: " + id));
        return ok(lines);
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<ApiResponse<CartResult>> checkout(@PathVariable("id") String id) {
        var lines = cartStore.find(id).orElseThrow(() -> new NoSuchElementException("Cart not found: " + id));
        var result = cartService.checkout(lines);
        cartStore.delete(id); // clear after checkout
        return ok(result);
    }

}
