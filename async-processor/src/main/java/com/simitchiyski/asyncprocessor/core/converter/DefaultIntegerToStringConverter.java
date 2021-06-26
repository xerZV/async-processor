package com.simitchiyski.asyncprocessor.core.converter;

import com.simitchiyski.asyncprocessor.config.AsyncProcessorProperties;
import com.simitchiyski.asyncprocessor.core.converter.client.IntegerToStringClient;
import com.simitchiyski.asyncprocessor.core.converter.client.IntegersToStringRequestDTO;
import com.simitchiyski.asyncprocessor.core.processor.AsyncProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DefaultIntegerToStringConverter implements IntegerToStringConverter {

    private final AsyncProcessor asyncProcessor;
    private final IntegerToStringClient integerToStringClient;
    private final AsyncProcessorProperties asyncProcessorProperties;

    @Override
    public List<String> toString(final @NotEmpty List<Integer> integers) {
        log.info("Converting integers={}", integers);
        return asyncProcessor.process(
                integers, asyncProcessorProperties.getThreadCount(), asyncProcessorProperties.getTimeout(),
                (integer) -> integerToStringClient.convert(request(integer, null))
        );
    }

    @Override
    public List<String> toString(final @NotEmpty List<Integer> integers, final @NotEmpty String prefix) {
        log.info("Converting with prefix={} integers={}", prefix, integers);

        return asyncProcessor.process(
                integers, asyncProcessorProperties.getThreadCount(), asyncProcessorProperties.getTimeout(),
                (integer) -> integerToStringClient.convert(request(integer, prefix))
        );
    }

    private IntegersToStringRequestDTO request(final Integer integer, final String prefix) {
        return IntegersToStringRequestDTO.builder().withInteger(integer).withPrefix(prefix).build();
    }
}
