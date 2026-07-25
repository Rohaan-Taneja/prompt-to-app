package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Req.chatRequestDto;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface aiGenerationService {
    Flux<String> getChatResponse(UUID projectId , String chatMessage);
}
