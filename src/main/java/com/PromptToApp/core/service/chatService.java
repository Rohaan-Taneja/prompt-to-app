package com.PromptToApp.core.service;

import com.PromptToApp.core.enums.ChatBy;

import java.util.UUID;

public interface chatService {

    void saveChat(ChatBy chatBy , UUID projectId , String chatContent , Integer tokenConsumed , UUID userId);
}
