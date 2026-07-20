package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.Dto.Req.chatRequestDto;
import com.PromptToApp.core.service.aiGenerationService;
import reactor.core.publisher.Flux;

public class aiGenerationServiceImpl implements aiGenerationService {

    @Override
    public Flux<String> getChatResponse(chatRequestDto chatRequest) {
        return null;
    }
}
