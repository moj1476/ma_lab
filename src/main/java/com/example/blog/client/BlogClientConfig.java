package com.example.blog.client;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class BlogClientConfig {

    @Bean
    public Logger blogClientLogger() {
        return new OneLineFeignLogger();
    }

    @Bean
    public Retryer blogClientRetryer() {
        return new Retryer.Default(100, 500, 2);
    }
}
