package com.rest.backend_rest.services;

import com.rest.backend_rest.dtos.OpenAIRequest;
import com.rest.backend_rest.dtos.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OpenAIService {
    private final WebClient webClient;
    private final String apiKey;

    public OpenAIService(WebClient.Builder builder, @Value("${openai.url}") String baseUrl,
                         @Value("${openai.api.key}") String apiKey) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public OpenAIResponse callOpenAI(OpenAIRequest request) {
        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();
    }
}
