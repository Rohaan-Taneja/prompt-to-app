package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class fileServiceImpl implements fileService {


    public List<FileResDto> getProjectFilesTree(UUID userId, UUID projectId) {
        return List.of();
    }

    public ProjectFileDetailsResDto getProjectFileContent(UUID userId, UUID projectId, UUID fileId) {
        return null;
    }

    @Override
    public ProjectFileDetailsResDto updatedProjectFileContent(UUID userId, UUID projectId, UUID fileId) {
        return null;
    }

}
