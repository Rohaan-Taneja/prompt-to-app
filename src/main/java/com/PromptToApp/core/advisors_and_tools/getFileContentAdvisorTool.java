package com.PromptToApp.core.advisors_and_tools;

import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class getFileContentAdvisorTool {

    private final fileService fileService;

    @Tool(description = "this is a tool to get the file details/content , pass in the file id present in the project tree to get the file details/content of that file")
    public List<ProjectFileDetailsResDto> getFileContent(List<UUID> file_ids) {
        log.info("we are in tool calling and llm want this file {}", file_ids);
        List<ProjectFileDetailsResDto> files = fileService.getProjectFileContent(file_ids);

        files.forEach(file -> log.info("this is the file asked {}", file.getPath()));

        return files;

    }
}
