package com.example.blog.config;

import com.example.blog.client.BlogClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = BlogClient.class)
public class FeignClientsConfig {
}
