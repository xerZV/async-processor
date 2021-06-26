package com.simitchiyski.asyncprocessor.core.processor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public interface AsyncProcessor {
    /**
     * The method will apply {@code Function<T, R> process} argument on each element of {@code List<T> elements} argument.<br>
     * It will try to spawn {@code preferredThreadCount} threads - but the real thread count depends on the System capabilities.<br>
     * The threads will be executed simultaneously - the elements in each thread (partition) will be processed sequentially.<br>
     * The whole process will be interrupted exceptionally with {@link java.util.concurrent.TimeoutException} if the duration
     * exceeds the {@code Duration timeout} argument.
     *
     * @param elements             to be processed
     * @param preferredThreadCount preferred threads count, must be > 0. The real threads count depends on the system.
     *                             If preferredThreadCount > system max threads capabilities it will take the max system max threads count
     * @param timeout              timeout for each thread which will process elements in a partition
     * @param process              how each element should be processed
     * @param <T>                  input type
     * @param <R>                  output type
     * @return all elements processed with {@code Function<T, R> process} argument from {@code T} type to {@code R} type
     * @throws TimeoutException if the process duration exceeds the {@code Duration timeout} argument
     */
    <T, R> List<R> process(final @NotEmpty List<T> elements, final @Positive int preferredThreadCount,
                           final @NotNull Duration timeout, final @NotNull Function<T, R> process);
}
