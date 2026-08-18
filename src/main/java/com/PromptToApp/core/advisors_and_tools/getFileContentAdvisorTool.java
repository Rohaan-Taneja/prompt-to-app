package com.PromptToApp.core.advisors_and_tools;

import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;




@Slf4j
@Component
@RequiredArgsConstructor
public class getFileContentAdvisorTool {

    private final fileService fileService;

    @Tool(description = "Retrieves the full content of one or more project files by their file IDs. "
            + "IMPORTANT: Before calling this tool, check whether the file's content is already visible "
            + "earlier in this conversation (e.g., from a previous tool response). If it is, reuse that "
            + "content directly instead of calling this tool again. Only call this tool for file IDs whose "
            + "content you do not already have.")
    public List<ProjectFileDetailsResDto> getFileContent(List<UUID> file_ids) {
        log.info("we are in tool calling and llm want this file {}", file_ids);
        List<ProjectFileDetailsResDto> files = fileService.getProjectFileContent(file_ids);

        files.forEach(file -> log.info("this is the file asked {}", file.getPath()));

        return files;

    }


}

//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class getFileContentAdvisorTool {
//
//    private final fileService fileService;
//    // conversationId -> set of file ids already served
//    private final Map<UUID, Set<UUID>> alreadyFetched = new ConcurrentHashMap<>();
//
//    @Tool(description = "Get file content for the given file ids from the project tree. "
//            + "Do not call this for a file id you have already received content for in this conversation.")
//    public List<ProjectFileDetailsResDto> getFileContent(
//            List<UUID> file_ids,
//            ToolContext toolContext) {
//
//        log.info("these are files llm asked for {}" , file_ids);
//
//
//        UUID conversationId = (UUID) toolContext.getContext().get("conversationId");
//
//        log.info("this is the chat id {}" , conversationId);
//        Set<UUID> seen = alreadyFetched.computeIfAbsent(conversationId, k -> ConcurrentHashMap.newKeySet());
//
//        List<UUID> newIds = file_ids.stream()
//                .filter(id -> !seen.contains(id))
//                .toList();
//
//        if (newIds.isEmpty()) {
//            log.warn("LLM re-requested already-fetched files {} for conversation {}", file_ids, conversationId);
//            // return something explicit instead of null/empty so the model gets a clear signal
//            return List.of(); // or a synthetic "already provided above" marker DTO
//        }
//
//        log.info("tool asked for these files {}" , file_ids);
//
//        List<ProjectFileDetailsResDto> files = fileService.getProjectFileContent(newIds);
//        seen.addAll(newIds);
//
//        files.forEach(file -> log.info("this is the file asked {}", file.getPath()));
//        return files;
//    }
//}
