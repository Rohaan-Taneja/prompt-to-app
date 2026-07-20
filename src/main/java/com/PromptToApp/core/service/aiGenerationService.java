package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Req.chatRequestDto;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

public interface aiGenerationService {
    Flux<String> getChatResponse(@Valid chatRequestDto chatRequest);
}
