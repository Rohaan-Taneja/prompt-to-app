package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface chatRepository extends JpaRepository<ChatMessage , UUID> {
}
