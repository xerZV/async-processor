package com.simitchiyski.asyncprocessor.core.processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static com.google.common.collect.Lists.partition;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DefaultAsyncProcessor implements AsyncProcessor {
    private final ScheduledExecutorService DELAYER = newScheduledThreadPool(1, threadFactory());
    private final TraceableExecutorService traceableExecutorService;

    @Override
    public <T, R> List<R> process(final @NotEmpty List<T> elements, final @Positive int preferredThreadCount,
                                  final @NotNull Duration timeout, final @NotNull Function<T, R> process) {
        log.info("Start processing elements on preferredThreadCount={}, each thread with timeout={}", preferredThreadCount, timeout);

        final int partitionSize = partitionSize(elements.size(), preferredThreadCount);
        final List<List<T>> partitions = partition(elements, partitionSize);
        log.info("partitions={}, partitionSize={}", partitions.size(), partitionSize);

        return sequence(toCompletableFutures(partitions, timeout, process))
                .thenApply(responses -> responses.stream().flatMap(List::stream).collect(toList()))
                .join();
    }

    private <T> CompletableFuture<List<T>> sequence(final List<CompletableFuture<T>> futures) {
        final CompletableFuture<Void> allDone = allOf(futures.toArray(new CompletableFuture[0]));

        return allDone.thenApply(
                v -> futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(toList())
        );
    }

    private <T, R> List<CompletableFuture<List<R>>> toCompletableFutures(final List<List<T>> partitions,
                                                                         final Duration timeout, final Function<T, R> process) {
        return partitions.stream()
                .map(partition -> within(supplyAsync(() -> processPartition(partition, process), traceableExecutorService), timeout))
                .collect(toList());
    }

    private <T> CompletableFuture<T> within(final CompletableFuture<T> responseFuture, final Duration timeout) {
        final CompletableFuture<T> timeoutAfter = failAfter(timeout);

        return responseFuture.applyToEither(timeoutAfter, identity());
    }

    private <T> CompletableFuture<T> failAfter(final Duration timeout) {
        final CompletableFuture<T> promise = new CompletableFuture<>();

        DELAYER.schedule(
                () -> promise.completeExceptionally(new TimeoutException("Timeout reached after " + timeout)),
                timeout.toMillis(),
                MILLISECONDS
        );

        return promise;
    }

    private <T, R> List<R> processPartition(final List<T> partition, final Function<T, R> process) {
        log.info("{} start processing it's partition", currentThread().getName());
        log.debug("{} start processing partition={}", currentThread().getName(), partition);

        return partition.stream()
                .peek(element -> log.debug("{} start processing element={}", currentThread().getName(), element))
                .map(process)
                .collect(toList());
    }

    private int partitionSize(final int size, final int preferredThreadCount) {
        if (preferredThreadCount == 1) {
            return size;
        }

        int partitionSize = size / preferredThreadCount;
        if (size % preferredThreadCount != 0) {
            partitionSize++;
        }

        return partitionSize;
    }

    private ThreadFactory threadFactory() {
        return new ThreadFactoryBuilder().setDaemon(true).setNameFormat("failAfter-%d").build();
    }
}
