package com.PromptToApp.core.security;

import com.PromptToApp.core.enums.ProjectMemberRole;
import com.PromptToApp.core.repository.projectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *  it contain function to check if the provided user is a project memeber of the project or not
 */
@Component(value = "securityAccessChecker")
@RequiredArgsConstructor
public class securityAccessCheck {

    private final projectMemberRepository project_member_repo;
    private final authUtilService auth_util_service;


    /**
     *
     * @param projectId -> which this user wants to see
     * @return we will check is this user part of this project
     */
    public boolean checkUserAccessToProject(UUID projectId) {
        UUID userId = auth_util_service.getUserId();

        return project_member_repo.checkUserAccessToProject(userId, projectId)
                .map(role -> role.equals(ProjectMemberRole.VIEWER) || role.equals(ProjectMemberRole.OWNER) || role.equals(ProjectMemberRole.EDITOR))
                .orElse(false);


    }


    /**
     *
     * @param projectId -> which this user wants to see
     * @return we will check is this user part of this project , if yes , then can he edit the project ?
     */
    public boolean checkUserAccessAndEditToProject(UUID projectId) {
        UUID userId = auth_util_service.getUserId();

        return project_member_repo.checkUserAccessToProject(userId, projectId)
                .map(role -> role.equals(ProjectMemberRole.OWNER) || role.equals(ProjectMemberRole.EDITOR))
                .orElse(false);

    }
}
