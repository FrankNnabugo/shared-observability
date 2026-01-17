package com.shared_observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricsFacade {

    @Autowired(required = false)
    private MeterRegistry registry;

    public void increment(String name) {
        if (registry != null) {
            registry.counter(name).increment();
        }
    }
}
