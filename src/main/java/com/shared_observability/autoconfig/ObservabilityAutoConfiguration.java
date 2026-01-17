package com.shared_observability.autoconfig;
import com.shared_observability.context.CorrelationAndAccessLogFilter;
import com.shared_observability.feign.CidFeignInterceptor;
import com.shared_observability.metrics.MetricsFacade;
import com.shared_observability.tracing.TraceContextFilter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ObservabilityAutoConfiguration {


    @Bean
    @ConditionalOnClass(name = "feign.RequestInterceptor")
    public CidFeignInterceptor feignInterceptor() {
        return new CidFeignInterceptor();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            name = "commons.logging.filter.enabled",
            havingValue = "true",
            matchIfMissing = true
    )

    public CorrelationAndAccessLogFilter correlationAndAccessLogFilter() {
        return new CorrelationAndAccessLogFilter();
    }

    @Bean(name = "cidFeignInterceptor")
    @ConditionalOnMissingBean(name = "cidFeignInterceptor")
    @ConditionalOnProperty(
            name = "commons.logging.feign.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public feign.RequestInterceptor cidFeignInterceptor() {
        return new CidFeignInterceptor();
    }

    @Bean
    @ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
    public MetricsFacade metricsFacade() {
        return new MetricsFacade();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenTelemetry openTelemetry() {
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(
                                BatchSpanProcessor.builder(
                                        ZipkinSpanExporter.builder()
                                                .setEndpoint("http://localhost:9411/api/v2/spans")
                                                .build()
                                ).build()
                        )
                        .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }

    @Bean
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnWebApplication
    public TraceContextFilter traceContextFilter() {
        return new TraceContextFilter();
    }

}

