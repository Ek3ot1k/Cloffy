package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.AiChatRequestDTO;
import ru.amin.Rest.dto.AiChatResponseDTO;
import ru.amin.Rest.services.AiAssistantService;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiAssistantService aiAssistantService;

    public AiController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    // Отправить сообщение ИИ-ассистенту
    // Опционально: передай lat/lng — получишь рекомендации рядом с твоей локацией
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponseDTO> chat(@RequestBody @Valid AiChatRequestDTO dto) {
        String reply = aiAssistantService.chat(dto.getMessage(), dto.getLat(), dto.getLng());
        return ResponseEntity.ok(new AiChatResponseDTO(reply));
    }
}
