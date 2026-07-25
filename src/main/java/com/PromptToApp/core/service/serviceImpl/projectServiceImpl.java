package com.PromptToApp.core.service.serviceImpl;


import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.CustomExceptionHandling.customBadRequestException;
import com.PromptToApp.core.Dto.Req.newProjectReqDto;
import com.PromptToApp.core.Dto.Res.ProjectBasicDetailsResponseDto;
import com.PromptToApp.core.Dto.Res.UserProfileResponseDto;
import com.PromptToApp.core.Dto.Res.projectDetailsResDto;
import com.PromptToApp.core.Entity.Project;
import com.PromptToApp.core.Entity.ProjectMember;
import com.PromptToApp.core.Entity.SubscriptionPlan;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.enums.ProjectMemberRole;
import com.PromptToApp.core.enums.SubscriptionStatus;
import com.PromptToApp.core.repository.projectMemberRepository;
import com.PromptToApp.core.repository.projectRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.repository.userSubscriptionRepository;
import com.PromptToApp.core.service.projectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class projectServiceImpl implements projectService {

    private final userRepository user_repo;

    private final userSubscriptionRepository userSubscriptionRepo;

    private final projectRepository projectRepo;

    private final projectMemberRepository projectMemberRepo;


    /**
     *
     * @param user_id
     * @param newProjectDetails
     * @return we will check can this user create new project or not
     * we will then register a new project user as owner
     * and also we will add user as a project member
     */
    public ProjectBasicDetailsResponseDto createNewProject(UUID user_id, newProjectReqDto newProjectDetails) {

        User owner = user_repo.getReferenceById(user_id);

        if (!canCreateProject(user_id)) {
            throw new customBadRequestException("you have created the max no no of projects in your plan , kindly upgrade your plan");
        }

        Project newProject = Project.builder()
                .name(newProjectDetails.name())
                .description(newProjectDetails.description())
                .owner(owner)
                .build();

        Project saved_project = projectRepo.save(newProject);

        ProjectMember ownerMember = ProjectMember.builder()
                .member(owner)
                .project(newProject)
                .projectMemberRole(ProjectMemberRole.OWNER)
                .build();

        projectMemberRepo.save(ownerMember);


        return ProjectBasicDetailsResponseDto.builder().project_id(saved_project.getId()).name(saved_project.getName()).description(saved_project.getName()).build();
    }


    /**
     * why pre authorize is here in service layer and not controller?
     * because this query is calling the db ,so this function can only be called after auth
     * else if we ally on controller , then other controller can call it
     */
    @PreAuthorize("@securityAccessChecker.checkUserAccessToProject(#project_id)")
    public projectDetailsResDto getProjectDetails(UUID user_id, UUID project_id) {
//
//        we will get project + owner field populated
        Project project = projectRepo.getProjectAndOwner(project_id).orElseThrow(() -> new ResourceNotFoundException("no project with this id exist"));

        UserProfileResponseDto OwnerProfile = UserProfileResponseDto.builder().id(project.getOwner().getId()).name(project.getOwner().getName()).email(project.getOwner().getEmail()).role(ProjectMemberRole.OWNER.toString()).build();

        return projectDetailsResDto.builder().name(project.getName()).description(project.getDescription()).owner(OwnerProfile).build();
    }


    public List<ProjectBasicDetailsResponseDto> getUserAllProjects(UUID user_id) {

        return projectRepo.getUserAllAccessibleProjects(user_id).stream()
                .map(project -> ProjectBasicDetailsResponseDto.builder().project_id(project.getId()).name(project.getName()).description(project.getDescription()).build())
                .toList();
    }


    /**
     *
     * @param userId
     * @return we will check can this user creaete a new project according to it subscription
     * we will get user subscription and current total project
     * and check for buffer
     */
    private boolean canCreateProject(UUID userId) {

        SubscriptionPlan userSubscriptionPlan = userSubscriptionRepo.getByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("user dont have any active subscription"));

        Integer current_total_projects = projectRepo.getUserNoOfProjects(userId);

        return userSubscriptionPlan.getPlan().getMaxProjects() > current_total_projects;


    }
}
