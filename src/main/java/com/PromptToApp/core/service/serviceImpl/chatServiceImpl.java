package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.Entity.ChatMessage;
import com.PromptToApp.core.Entity.Project;
import com.PromptToApp.core.Entity.ProjectMember;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.enums.ChatBy;
import com.PromptToApp.core.repository.chatRepository;
import com.PromptToApp.core.repository.projectRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.service.chatService;
import com.PromptToApp.core.service.projectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class chatServiceImpl implements chatService {

    private final projectRepository projectRepo;

    private final userRepository userRepo;

    private final projectMemberService projectMemberService;

    private final chatRepository chatRepo;

    /**
     *
     * this can save both AI Assistance chat + user prompt
     * if chatBy is ai then we dont need userId for projectMember
     */
    public void saveChat(ChatBy chatBy, UUID projectId, String chatContent, Integer tokenConsumed, UUID userId) {

        log.info("i am saving the chat");
        Project project = projectRepo.getReferenceById(projectId);

        User user = userRepo.getReferenceById(userId);

        ProjectMember projectMember = null;

//        load projectMember if chat by User
//        for chat by ai , it will be null
        if (chatBy.equals(ChatBy.USER)) {
            log.info("chat by user , so finding project mneber");
            projectMember = projectMemberService.getProjectMember(projectId, userId);
        }

        log.info("i am just after getting project member");
        ChatMessage chatMessage = ChatMessage.builder().project(project).projectMember(projectMember).content(chatContent).tokenConsumed(tokenConsumed).chatBy(chatBy).build();

        log.info("i have creatd the chat message object");

        chatRepo.save(chatMessage);

        log.info("I have saved the chat object");


    }
}
