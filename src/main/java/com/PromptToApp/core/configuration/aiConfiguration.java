package com.PromptToApp.core.configuration;

import com.PromptToApp.core.advisors.getFileContentAdvisorTool;
import com.PromptToApp.core.advisors.projectTreeAdvisor;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class aiConfiguration {

    private final projectTreeAdvisor projectTreeAdvisor;
    private final getFileContentAdvisorTool getFileContentAdvisorTool;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        log.info("I am in chat client");
        return builder
                .defaultAdvisors(projectTreeAdvisor) //registering the advisor , so that every llm call passes through this advisor and project file tree in system prompt context
                .defaultTools(getFileContentAdvisorTool)
                .build();
    }
}
