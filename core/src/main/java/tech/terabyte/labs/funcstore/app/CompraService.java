package tech.terabyte.labs.funcstore.app;

import tech.terabyte.labs.funcstore.domain.CompraRequest;
import tech.terabyte.labs.funcstore.domain.CompraResult;

public interface CompraService {
    CompraResult calcular(CompraRequest req);
}
