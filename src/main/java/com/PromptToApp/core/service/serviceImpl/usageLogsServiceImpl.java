package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.Entity.Project;
import com.PromptToApp.core.Entity.UsageLogs;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.repository.projectRepository;
import com.PromptToApp.core.repository.usageLogsRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.service.usageLogsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class usageLogsServiceImpl implements usageLogsService {

    private final userRepository userRepo;

    private final projectRepository projectRepo;

    private final usageLogsRepository usageLogsRepo;

    @Override
    public void addUserUsageLogs(UUID projectId, UUID userId, Integer tokenUsed, String prompt) {

        log.info("i am in usage logs");
        User user = userRepo.getReferenceById(userId);

        log.info("I am foudn the user");

        Project project = projectRepo.getReferenceById(projectId);

        log.info("I am foudn the project object");

        UsageLogs userUsageLogs = UsageLogs.builder().user(user).project(project).prompt(prompt).tokenUsed(tokenUsed).Duration_ms(5).build();

        log.info("I am creaetd the usage logs object");
        usageLogsRepo.save(userUsageLogs);

        log.info("I am saved the user usage losg");


    }

    @Override
    public Integer getTokenUsedByUser(UUID userId) {
        return usageLogsRepo.getTokenUsedByUserToday(userId);
    }


}
