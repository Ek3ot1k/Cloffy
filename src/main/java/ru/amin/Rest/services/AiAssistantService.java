package ru.amin.Rest.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class AiAssistantService {
    private static final Logger log = LoggerFactory.getLogger(AiAssistantService.class);

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    @Value("${openrouter.http.referer:}")
    private String openRouterReferer;

    @Value("${openrouter.x.title:Cloffy}")
    private String openRouterTitle;

    private final RestTemplate restTemplate;

    public AiAssistantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String chat(String userMessage, Double lat, Double lng) {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI-ассистент не настроен: добавь DEEPSEEK_API_KEY в .env и перезапусти Docker Compose.";
        }

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
        if (apiUrl != null && apiUrl.contains("openrouter.ai")) {
            if (openRouterReferer != null && !openRouterReferer.isBlank()) {
                headers.set("HTTP-Referer", openRouterReferer);
            }
            headers.set("X-Title", openRouterTitle);
        }

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
            log.error("AI provider request failed: {}", e.getMessage(), e);
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
