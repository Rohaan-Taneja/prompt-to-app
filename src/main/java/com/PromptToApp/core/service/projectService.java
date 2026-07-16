package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Req.newProjectReqDto;
import com.PromptToApp.core.Dto.Res.ProjectBasicDetailsResponseDto;
import com.PromptToApp.core.Dto.Res.projectDetailsResDto;

import java.util.List;
import java.util.UUID;

public interface projectService {

    ProjectBasicDetailsResponseDto createNewProject(UUID user_id , newProjectReqDto newProjectDetails);

    projectDetailsResDto getProjectDetails(UUID user_id , UUID project_id);


    List<ProjectBasicDetailsResponseDto> getUserAllProjects(UUID user_id );
}
