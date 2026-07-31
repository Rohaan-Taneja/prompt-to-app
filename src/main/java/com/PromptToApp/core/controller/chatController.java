package com.PromptToApp.core.controller;

import com.PromptToApp.core.Dto.Req.chatRequestDto;
import com.PromptToApp.core.service.aiGenerationService;
import jakarta.validation.Valid;
import jdk.jfr.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class chatController {

    private final aiGenerationService ai_generation_service;


    //    server side events is a tech in which server can continuously send data/events to teh client
    @PostMapping(value = "get-chat-response", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> getUserResponse(@Valid @RequestBody() chatRequestDto chatRequest) {

        log.info("I am in ai controller {}"  , chatRequest.toString());
        return ai_generation_service.getChatResponse(chatRequest.projectId() , chatRequest.chatMessage())
                .map((event) ->
                        ServerSentEvent
                                .<String>builder()
                                .data(event)
                                .build());

    }
}
