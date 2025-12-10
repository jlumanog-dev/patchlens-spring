package com.jlumanog_dev.patchlens_spring_backend.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnthropicApiConfig {
    @Value("${anthropic.apiKey}")
    private String key;
    @Bean
    public AnthropicClient AnthropicInstance(){
        return AnthropicOkHttpClient.builder().apiKey(this.key).build();
    }
}
