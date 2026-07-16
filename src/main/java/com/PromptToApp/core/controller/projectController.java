package com.PromptToApp.core.controller;

import com.PromptToApp.core.Dto.Req.newProjectReqDto;
import com.PromptToApp.core.Dto.Res.ProjectBasicDetailsResponseDto;
import com.PromptToApp.core.Dto.Res.projectDetailsResDto;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.service.projectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class projectController {


    private final projectService project_service;

    private final authUtilService auth_util_service;

    @PostMapping("/create")
    public ResponseEntity<ProjectBasicDetailsResponseDto> createNewProject(@RequestBody() newProjectReqDto newProjectDetails){

        UUID userId = auth_util_service.getUserId();

        return ResponseEntity.ok(project_service.createNewProject(userId , newProjectDetails));



    }


    @GetMapping("/get-project/{project-id}")
    public ResponseEntity<projectDetailsResDto> getProjectDetails(@PathVariable("project-id") UUID projectId){

        UUID userId = auth_util_service.getUserId();

        return ResponseEntity.ok(project_service.getProjectDetails(userId , projectId));
    }


    @GetMapping("/get-all-projects")
    public ResponseEntity<List<ProjectBasicDetailsResponseDto>> getUserAllProjects(@RequestBody() newProjectReqDto newProjectDetails){

        UUID userId = auth_util_service.getUserId();

        return ResponseEntity.ok(project_service.getUserAllProjects(userId));



    }
}
