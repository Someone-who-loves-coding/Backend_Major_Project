package com.rest.backend_rest.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.backend_rest.dtos.OpenAIRequest;
import com.rest.backend_rest.dtos.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmotionAnalysisService {

    private final String apiKey;

    private final String model;

    private final WebClient webClient;

    public EmotionAnalysisService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.url}") String openAiUrl,
            @Value("${openai.model}") String model,
            WebClient.Builder builder
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = builder.baseUrl(openAiUrl).build();
    }

    public Map<String, Double> analyze(String text) {
        String prompt = buildPrompt(text);

        OpenAIRequest request = new OpenAIRequest(model, prompt);

        OpenAIResponse response = webClient.post()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(OpenAIResponse.class)
            .block();

        return extractEmotions(response);
    }

    private String buildPrompt(String text) {
        return "Analyze the following diary entry and respond with a JSON object mapping these 28 emotions to values between 0 and 1: \n\n" +
               "Emotions: admiration, amusement, anger, annoyance, approval, caring, confusion, curiosity, desire, disappointment, disapproval, disgust, embarrassment, excitement, fear, gratitude, grief, joy, love, nervousness, optimism, pride, realization, relief, remorse, sadness, surprise, neutral.\n\n" +
               "Entry:\n\"" + text + "\"";
    }

    private Map<String, Double> extractEmotions(OpenAIResponse response) {
        String content = response.getChoices().get(0).getMessage().getContent();

        String json = extractJsonFromMarkdown(content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse emotion JSON from OpenAI: " + content, e);
        }
    }

    private String extractJsonFromMarkdown(String text) {
        // This regex matches a block of text enclosed in triple backticks optionally followed by 'json'
        Pattern pattern = Pattern.compile("```(?:json)?\\s*(\\{.*?\\})\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Could not find a JSON block in the response: " + text);
        }
    }
}
