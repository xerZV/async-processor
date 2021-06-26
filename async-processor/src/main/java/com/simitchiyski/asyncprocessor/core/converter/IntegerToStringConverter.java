package com.simitchiyski.asyncprocessor.core.converter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public interface IntegerToStringConverter {
    List<String> toString(final @NotEmpty List<Integer> integers);
    List<String> toString(final @NotEmpty List<Integer> integers, @NotEmpty final String prefix);
}
