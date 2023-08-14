package com.pcdev.expensemanagerbackend.config;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }

}
