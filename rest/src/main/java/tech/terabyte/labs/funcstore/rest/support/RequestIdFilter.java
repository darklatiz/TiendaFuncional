package tech.terabyte.labs.funcstore.rest.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, jakarta.servlet.ServletException {
        String id = req.getHeader(HEADER);
        if (id == null || id.isBlank()) id = UUID.randomUUID().toString();

        // put into context + logging MDC
        RequestIdContext.set(id);
        MDC.put(MDC_KEY, id);

        try {
            res.setHeader(HEADER, id); // echo back for clients
            chain.doFilter(req, res);
        } finally {
            MDC.remove(MDC_KEY);
            RequestIdContext.clear();
        }
    }
}
