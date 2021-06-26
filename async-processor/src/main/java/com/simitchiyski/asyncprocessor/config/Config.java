package com.simitchiyski.asyncprocessor.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Configuration
@EnableConfigurationProperties( {AsyncProcessorProperties.class} )
public class Config {

    @Bean
    public ExecutorService executorService(final MeterRegistry meterRegistry) {
        return monitor(meterRegistry, newFixedThreadPool(20), "executor.service");
    }

    @Bean
    public TraceableExecutorService traceableExecutorService(final BeanFactory beanFactory, final ExecutorService executorService) {
        return new TraceableExecutorService(beanFactory, executorService);
    }
}
