package com.example.inspire_jpa.features.commons.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;

@Configuration 
public class OpenAiConfig {

    @Bean 
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    // @Bean 
    // public ChatClient chatClient(ChatClient.Builder builder) {
    //     return builder.build();
    // }

    @Bean 
    public OkHttpClient okHttpClient(){
        return new OkHttpClient();
    }


}
