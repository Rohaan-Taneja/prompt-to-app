package com.PromptToApp.core.advisors;

import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class getFileContentAdvisorTool {

    private final fileService fileService;

    @Tool(description = "this is a tool to the file content")
    public ProjectFileDetailsResDto getFileContent(UUID id){
        log.info("we are in tool calling and llm want this file {}" , id);
        return fileService.getProjectFileContent(id);

    }
}
