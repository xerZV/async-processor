package com.simitchiyski.asyncprocessor.core.converter.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = PRIVATE)
public class IntegersToStringRequestDTO {
    @NotNull
    @Positive
    Integer integer;

    String prefix;
}
