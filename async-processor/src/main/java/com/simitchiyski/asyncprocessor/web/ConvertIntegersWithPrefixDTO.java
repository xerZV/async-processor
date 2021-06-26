package com.simitchiyski.asyncprocessor.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = PRIVATE)
public class ConvertIntegersWithPrefixDTO {
    @NotEmpty
    List<Integer> integers;

    @NotEmpty
    String prefix;
}
