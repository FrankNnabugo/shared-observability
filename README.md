### Observability Library

A lightweight, reusable observability library for Java/Spring applications that provides structured logging, 
metrics tracking, and distributed tracing support. Designed to standardize monitoring across microservices and ensure consistent observability practices.

### Features

Request Correlation & Logging: Automatically logs incoming requests with correlation IDs.

Metrics Tracking: Capture custom metrics for business events or system performance.

Distributed Tracing: Trace requests across microservices with minimal setup.

Feign Integration: Supports tracing outgoing HTTP calls via Feign clients.

Auto-configuration: Simplifies integration via Spring Boot auto-configuration.

### Installation

Add the library as a Maven or Gradle dependency:

Maven

<dependency>
    <groupId>com.shared</groupId>
    <artifactId>shared-observability</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>


Gradle

implementation 'com.shared:shared-observability:0.0.1-SNAPSHOT'


### Make sure all transitive dependencies are resolved, especially for logging frameworks like SLF4J.

### Usage

1. Auto-configuration

ObservabilityAutoconfiguration registers all necessary beans automatically:

CorrelationAndAccessLogFilter

CidFeignInterceptor

MetricsFacade

TraceContextFilter

Simply include the library in your Spring Boot project, and the beans are registered automatically.

2. Request Correlation & Logging

CorrelationAndAccessLogFilter automatically attaches a correlation ID to each incoming request and logs request details:
// No manual setup required if auto-configuration is enabled
// Logs will include correlation IDs automatically

3. Feign Interceptor

CidFeignInterceptor ensures that outgoing Feign requests carry the correlation ID, enabling distributed tracing across services:

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @GetMapping("/payments/{id}")
    Payment getPayment(@PathVariable String id);
}

4. Metrics

Use MetricsFacade to record custom metrics:

@Autowired
private MetricsFacade metricsFacade;

metricsFacade.incrementCounter("payments.success.count");
metricsFacade.recordTimer("payments.processing.time", durationInMillis);

5. Trace Context

TraceContextFilter manages trace IDs for requests to ensure distributed tracing works across filters and services.

// Auto-applied if ObservabilityAutoconfiguration is enabled

### Configuration

You can configure behavior via application.properties or application.yml:

observability.logging.level=INFO
observability.metrics.enabled=true
observability.tracing.enabled=true

### Contributing

Contributions are welcome. Please follow these steps:

Fork the repository

Create a new branch for your feature/fix

Write unit tests for any new functionality

Submit a pull request with a clear description
