package tech.terabyte.labs.funcstore.app;

import tech.terabyte.labs.funcstore.domain.CartLine;
import tech.terabyte.labs.funcstore.domain.CartResult;

import java.util.List;

public interface CartService {
    CartResult checkout(List<CartLine> lines);
}
