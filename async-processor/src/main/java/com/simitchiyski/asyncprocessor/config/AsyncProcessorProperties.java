package com.simitchiyski.asyncprocessor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.async.processor")
public class AsyncProcessorProperties {
    @Positive
    private int threadCount;
    @NotNull
    private Duration timeout;
}
