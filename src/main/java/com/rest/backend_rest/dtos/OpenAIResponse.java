package com.rest.backend_rest.dtos;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        public Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }
}
