package com.shared_observability.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Order(1) //Ensures this filter runs early in the chain
public class CorrelationAndAccessLogFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CorrelationAndAccessLogFilter.class);

    //MDC key name for correlation ID
    public static final String CID = "cid";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        //MDC key name for correlation ID
        String cid = req.getHeader("X-Request-Id");

        //If none present (first hop), generate a new UUID
        if (cid == null || cid.trim().isEmpty()) {
            cid = UUID.randomUUID().toString();
        }

        //Record start time for duration calculation
        long startNs = System.nanoTime();

        //Put core request metadata into MDC — this will auto-attach to all logs during this request
        MDC.put(CID, cid);
        MDC.put("method", req.getMethod());
        MDC.put("path", req.getRequestURI());

        res.setHeader("X-Request-Id", cid);

        try {

            //Log request entry — controller just received the call
            LOG.info("ENTER controller cid={} method={} path={}", cid, MDC.get("method"), MDC.get("path"));

            //Continue the filter chain (this hands over control to Spring MVC and controllers)
            chain.doFilter(req, res);

        }
        finally {

            //Compute request duration in milliseconds
            long durMs = (System.nanoTime() - startNs) / 1_000_000L;

            //Put response metadata into MDC before exit log
            MDC.put("status", Integer.toString(res.getStatus()));
            MDC.put("durMs", Long.toString(durMs));

            //Echo header again (defensive) — ensures response always has X-Request-Id
            res.setHeader("X-Request-Id", cid);

            //Log the exit line — a single concise access log line

            LOG.info(
                    "Exit controller cid={} method={} path={} status={} durMs={}",
                    cid,
                    MDC.get("method"),
                    MDC.get("path"),
                    MDC.get("status"),
                    MDC.get("durMs")
            );

            //Always clear MDC to avoid leaking context into unrelated threads/requests
            MDC.clear();
        }
    }
    }
