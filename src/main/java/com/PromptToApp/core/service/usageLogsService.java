package com.PromptToApp.core.service;

import java.util.UUID;

public interface usageLogsService {

    void addUserUsageLogs(UUID projectId , UUID userId , Integer tokenUsed , String prompt);

    Integer getTokenUsedByUser(UUID userId);
}
