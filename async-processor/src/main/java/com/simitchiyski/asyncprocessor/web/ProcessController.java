package com.simitchiyski.asyncprocessor.web;

import com.simitchiyski.asyncprocessor.core.converter.IntegerToStringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/integers")
public class ProcessController {
    private final IntegerToStringConverter integerToStringConverter;

    @PostMapping
    public ResponseEntity<List<String>> convert(final @Valid @RequestBody @NotEmpty List<Integer> integers) {
        return ok(integerToStringConverter.toString(integers));
    }

    @PostMapping("/with-prefix")
    public ResponseEntity<List<String>> convert(final @Valid @RequestBody @NotNull ConvertIntegersWithPrefixDTO dto) {
        return ok(integerToStringConverter.toString(dto.getIntegers(), dto.getPrefix()));
    }
}
