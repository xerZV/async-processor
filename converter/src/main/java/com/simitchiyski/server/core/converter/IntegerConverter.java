package com.simitchiyski.server.core.converter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public interface IntegerConverter {

    String toString(final @NotNull @Positive Integer integer, final String prefix);
}
