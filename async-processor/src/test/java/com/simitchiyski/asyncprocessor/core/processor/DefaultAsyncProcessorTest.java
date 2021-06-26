package com.simitchiyski.asyncprocessor.core.processor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;

import javax.validation.ConstraintViolation;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class DefaultAsyncProcessorTest {

    @Test
    void givenValidArguments_andThreadsGreaterThanOne_whenDurationDoesNotExceed_thenReturnResult() {
        final List<Integer> integers = getIntegersCollection(999);

        final List<String> processed = asyncProcessor().process(integers, 20, ofSeconds(5), String::valueOf);

        assertNotNull(processed);
        assertFalse(processed.isEmpty());
        assertEquals(integers.size(), processed.size());
    }

    @Test
    void givenValidArguments_andThreadsIsOne_whenDurationDoesNotExceed_thenReturnResult() {
        final List<Integer> integers = getIntegersCollection(1000);

        final List<String> processed = asyncProcessor().process(integers, 1, ofSeconds(5), String::valueOf);

        assertNotNull(processed);
        assertFalse(processed.isEmpty());
        assertEquals(integers.size(), processed.size());
    }

    @Test
    void givenInvalidArguments_whenDurationDoesNotExceed_thenThrowValidationException() throws NoSuchMethodException {
        final ExecutableValidator executableValidator = buildDefaultValidatorFactory().getValidator().forExecutables();

        final AsyncProcessor asyncProcessor = asyncProcessor();
        final Method method = AsyncProcessor.class.getMethod("process", List.class, int.class, Duration.class, Function.class);
        final Object[] parameterValues = {null, 0, null, null};
        final Set<ConstraintViolation<AsyncProcessor>> violations = executableValidator.validateParameters(asyncProcessor, method, parameterValues);

        assertNotNull(violations);
        assertFalse(violations.isEmpty());

        assertTrue(anyMatch(violations, "must not be empty", "process.elements"));
        assertTrue(anyMatch(violations, "must be greater than 0", "process.preferredThreadCount"));
        assertTrue(anyMatch(violations, "must not be null", "process.timeout"));
        assertTrue(anyMatch(violations, "must not be null", "process.process"));
    }

    @Test
    void givenValidArguments_whenDurationExceed_thenThrowException() {
        final List<Integer> integers = getIntegersCollection(1000);

        final CompletionException exception = assertThrows(CompletionException.class,
                () -> asyncProcessor()
                        .process(integers, 20, ofMillis(100), integer -> {
                            if (integer == 800) {
                                try {
                                    sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            return String.valueOf(integer);
                        }));

        assertNotNull(exception);
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof TimeoutException);
    }

    private boolean anyMatch(Set<ConstraintViolation<AsyncProcessor>> violations, final String expectedMessage, final String onPath) {
        return violations.stream().anyMatch(violation -> violation.getMessage().equals(expectedMessage) && violation.getPropertyPath().toString().equals(onPath));
    }

    private DefaultAsyncProcessor asyncProcessor() {
        return new DefaultAsyncProcessor(new TraceableExecutorService(new DefaultListableBeanFactory(), newFixedThreadPool(20)));
    }

    private List<Integer> getIntegersCollection(final int i) {
        return range(0, i).boxed().collect(toList());
    }

}
