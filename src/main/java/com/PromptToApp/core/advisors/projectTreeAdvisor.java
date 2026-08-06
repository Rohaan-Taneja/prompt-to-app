package com.PromptToApp.core.advisors;

import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.service.fileService;
import com.PromptToApp.core.utils.SystemPrompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;


/**
 * this advisor is called and we will add our system prompt + respective file tree as a system prompt
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class projectTreeAdvisor implements StreamAdvisor {

    private final fileService fileService;


    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        log.info("I am in the project file tree advisor");

        UUID project_id = (UUID) chatClientRequest.context().get("projectId");

        log.info("this is the project id {}" , project_id);

        List<FileResDto> fileTree = fileService.getProjectFilesTree((UUID) chatClientRequest.context().get("projectId"));

        log.info("this is the file tree response {}" , fileTree);

        String fileTreePrompt = """
                Current Project Tree:
                
                %s
                """.formatted(fileTree);

        String PreBuildSystemPrompt = SystemPrompt.getSystemPrompt();

        ChatClientRequest updatedRequest = chatClientRequest.mutate()
                .prompt(chatClientRequest.prompt().augmentSystemMessage(PreBuildSystemPrompt + fileTreePrompt))
                .build();

        log.info("this is the system prompt {}", updatedRequest.prompt());
        return streamAdvisorChain.nextStream(updatedRequest);
    }

    @Override
    public String getName() {
        return "project-tree-advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
