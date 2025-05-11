package com.rest.backend_rest.services;

import com.rest.backend_rest.dtos.OpenAIRequest;
import com.rest.backend_rest.dtos.OpenAIResponse;
import com.rest.backend_rest.models.DiaryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportAnalysisService {

    private final OpenAIService openAIService;

    @Autowired
    public ReportAnalysisService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    public String generateFinalReport(List<DiaryEntry> entries) {
        StringBuilder sb = new StringBuilder("Analyze the following 30-day emotional data:\n");

        for (DiaryEntry entry : entries) {
            sb.append("- ").append(entry.getTimeEntry()).append(": ").append(entry.getEmotions()).append("\n");
        }

        sb.append("\nReturn a detailed summary, behavioral suggestions, and mark emergencies if detected.");

        OpenAIRequest request = new OpenAIRequest("gpt-4o-mini", sb.toString());
        OpenAIResponse response = openAIService.callOpenAI(request);

        return response.getChoices().get(0).getMessage().getContent();
    }
}
