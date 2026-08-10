package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;

import java.util.List;
import java.util.UUID;

public interface fileService {


    List<FileResDto> getProjectFilesTree(UUID projectId);

    List<ProjectFileDetailsResDto> getProjectFileContent(List<UUID> fileIds);


    ProjectFileDetailsResDto updatedProjectFileContent(UUID userId, UUID projectId, UUID fileId);

    void addOrUpdateFile(UUID projectId , String filePath , String fileContent , UUID userId);

    boolean copyReactTemplateToMinio();
}
