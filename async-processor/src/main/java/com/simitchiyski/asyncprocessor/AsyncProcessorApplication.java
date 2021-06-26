package com.simitchiyski.asyncprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AsyncProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncProcessorApplication.class, args);
    }

}
