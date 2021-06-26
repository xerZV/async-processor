package com.simitchiyski.asyncprocessor.web;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GreetingsController {

    private final EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/greetings")
    public String greeting() {
        return String.format("Hello from '%s'!", eurekaClient.getApplication(applicationName).getName());
    }
}
