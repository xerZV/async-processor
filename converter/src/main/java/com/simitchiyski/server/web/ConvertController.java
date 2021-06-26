package com.simitchiyski.server.web;

import com.simitchiyski.server.core.converter.IntegerConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/integers")
public class ConvertController {
    private final IntegerConverter integerConverter;

    @PostMapping("/to-string")
    public ResponseEntity<String> convert(final @Valid @RequestBody @NotNull IntegersToStringRequestDTO dto){
        log.info("Received dto={}", dto);

        return ResponseEntity.ok(integerConverter.toString(dto.getInteger(), dto.getPrefix()));
    }
}
