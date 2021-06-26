package com.simitchiyski.server.core.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@Validated
public class DefaultIntegerConverter implements IntegerConverter {
    @Override
    public String toString(final @NotNull @Positive Integer integer, final String prefix) {
        log.info("Converting integer={} with prefix={}", integer, prefix);

        if (nonNull(prefix)) {
            return format("%s%d", prefix, integer);
        }

        return valueOf(integer);
    }
}
