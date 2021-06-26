package com.simitchiyski.server.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
