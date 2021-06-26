package com.simitchiyski.asyncprocessor.core.processor;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.executable.ExecutableValidator;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static okhttp3.mockwebserver.SocketPolicy.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultAsyncProcessorTimeoutTest {

    @Test
    void timeoutExceptionally() throws IOException {
        final MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start(1234);
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(NO_RESPONSE));

        final RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(ofSeconds(1))
                .setReadTimeout(ofSeconds(1))
                .build();

        final String url = format("http://localhost:%d", mockWebServer.getPort());

        final List<Integer> integers = range(0, 10).boxed().collect(toList());

        final CompletionException exception = assertThrows(CompletionException.class,
                () -> asyncProcessor().process(
                        integers,
                        20,
                        ofMillis(500),
                        integer -> {
                            restTemplate.getForObject(url, String.class);
                            return String.valueOf(integer);
                        }));

        assertNotNull(exception);
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof TimeoutException);
        mockWebServer.shutdown();
    }

    private DefaultAsyncProcessor asyncProcessor() {
        return new DefaultAsyncProcessor(new TraceableExecutorService(new DefaultListableBeanFactory(), newFixedThreadPool(20)));
    }

}
