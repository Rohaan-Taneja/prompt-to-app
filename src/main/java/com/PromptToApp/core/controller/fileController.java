package com.PromptToApp.core.controller;
import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project/{project-id}/file")
public class fileController {

    private final fileService file_service;


    @GetMapping("/all-project-file-tree")
    public ResponseEntity<List<FileResDto>> getProjectFilesTree(@PathVariable("project-id") UUID projectId ){

        UUID userId = UUID.randomUUID();

        return ResponseEntity.ok(file_service.getProjectFilesTree(userId , projectId));

    }


    @GetMapping("file-content/{file-id}")
    public ResponseEntity<ProjectFileDetailsResDto> getProjectFileContent(@PathVariable("project-id") UUID projectId , @PathVariable("file-id") UUID fileId ){

        UUID userId = UUID.randomUUID();

        return ResponseEntity.ok(file_service.getProjectFileContent(userId , projectId , fileId));

    }


    @PatchMapping("update-file-content/{file-id}")
    public ResponseEntity<ProjectFileDetailsResDto> updateProjectFileContent(@PathVariable("project-id") UUID projectId , @PathVariable("file-id") UUID fileId  , @RequestBody() String updatedFile){

        UUID userId = UUID.randomUUID();

        return ResponseEntity.ok(file_service.updatedProjectFileContent(userId ,projectId , fileId));

    }

}
