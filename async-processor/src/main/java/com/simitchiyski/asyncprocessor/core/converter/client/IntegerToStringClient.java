package com.simitchiyski.asyncprocessor.core.converter.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@FeignClient("async-server")
public interface IntegerToStringClient {
    @PostMapping("/integers/to-string")
    String convert(final @Valid @NotNull IntegersToStringRequestDTO request);
}
