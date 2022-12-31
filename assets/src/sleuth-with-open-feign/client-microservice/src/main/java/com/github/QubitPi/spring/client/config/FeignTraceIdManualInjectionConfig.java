package com.github.QubitPi.spring.client.config;

import brave.Tracer;
import feign.RequestInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

import javax.validation.constraints.NotNull;

/**
 * {@code }
 */
public class FeignTraceIdManualInjectionConfig {

    private static final Logger LOG = LoggerFactory.getLogger(FeignTraceIdManualInjectionConfig.class);

    private final Tracer tracer;

    @Autowired
    public FeignTraceIdManualInjectionConfig(@NotNull final Tracer tracer) {
        this.tracer = Objects.requireNonNull(tracer, "tracer");
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return request -> {
            String traceId = getTracer().currentSpan().context().traceIdString();
            String nextSpanId = getTracer().nextSpan().context().spanIdString();

            LOG.info("Adding header[X-B3-TraceId:{}]", traceId);
            request.header("X-B3-TraceId", traceId);

            LOG.info("Adding header[X-B3-ParentSpanId:{}]", traceId);
            request.header("X-B3-ParentSpanId", traceId);

            LOG.info("Adding header[X-B3-SpanId:{}]", nextSpanId);
            request.header("X-B3-SpanId", nextSpanId);

            request.header("X-B3-Sampled", "1");
        };
    }

    private Tracer getTracer() {
        return tracer;
    }
}
