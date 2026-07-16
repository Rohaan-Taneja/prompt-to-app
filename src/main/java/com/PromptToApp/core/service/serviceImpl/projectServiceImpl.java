package com.PromptToApp.core.service.serviceImpl;


import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.Dto.Req.newProjectReqDto;
import com.PromptToApp.core.Dto.Res.ProjectBasicDetailsResponseDto;
import com.PromptToApp.core.Dto.Res.projectDetailsResDto;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.service.projectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class projectServiceImpl implements projectService {

    private final userRepository user_repo ;





    public ProjectBasicDetailsResponseDto createNewProject(UUID user_id , newProjectReqDto newProjectDetails){


        User owner = user_repo.getReferenceById(user_id);

//        todo
//        creaet new project
        return null;
    }


    /**
     * why pre authroize is here in service layer and not controller?
     * becasue this query is calling the db ,so this function can only be called after auth
     * else if we ally on controller , then other controller can call it 
     */
    @PreAuthorize("@securityAccessChecker.checkUserAccessToProject(#project-id)")
    public projectDetailsResDto getProjectDetails(UUID user_id , UUID project_id){
//        todo
//        check whetehr user is a project member in this project , if yes
//        then give project details

        return null;
    }


    public List<ProjectBasicDetailsResponseDto> getUserAllProjects(UUID user_id) {

        //        todo
//        we have user id , we want to get all the projects in which this user is a project member
//        get all the projects of the user of this user is part of
        return List.of();
    }
}
