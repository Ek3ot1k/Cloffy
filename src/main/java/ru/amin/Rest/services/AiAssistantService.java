package ru.amin.Rest.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiAssistantService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    private final RestTemplate restTemplate;

    public AiAssistantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String chat(String userMessage, Double lat, Double lng) {
        String systemPrompt = buildSystemPrompt(lat, lng);

        // Формируем тело запроса в формате OpenAI-совместимого API DeepSeek
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "max_tokens", 500
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return (String) message.get("content");
        } catch (Exception e) {
            return "Ассистент временно недоступен. Попробуйте позже.";
        }
    }

    private String buildSystemPrompt(Double lat, Double lng) {
        StringBuilder prompt = new StringBuilder(
                "Ты — умный помощник в приложении Cloffy, аналоге Zenly для iPhone. " +
                "Помогаешь пользователям находить интересные места рядом: кафе, рестораны, парки, магазины. " +
                "Отвечай коротко, дружелюбно и по делу. Отвечай на том языке, на котором пишет пользователь."
        );

        if (lat != null && lng != null) {
            prompt.append(String.format(
                    " Текущие координаты пользователя: широта %.6f, долгота %.6f. " +
                    "Учитывай их при рекомендациях.", lat, lng
            ));
        }

        return prompt.toString();
    }
}
