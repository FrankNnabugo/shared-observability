package com.shared_observability.tracing;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.servlet.*;

import java.io.IOException;

public class TraceContextFilter implements Filter {

    private final Tracer tracer =
            GlobalOpenTelemetry.getTracer("shared_observability");

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        Span span = tracer.spanBuilder("http-request").startSpan();

        try (Scope scope = span.makeCurrent()) {
            chain.doFilter(request, response);
        } finally {
            span.end();
        }
    }
}


