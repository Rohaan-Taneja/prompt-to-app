package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.Entity.ProjectFile;
import com.PromptToApp.core.repository.projectFileRepository;
import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class fileServiceImpl implements fileService {

    private final projectFileRepository projectFileRepo;


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


    /**
     *we will check if file path exist , if it exist then we will replace the file content with incoming
     * else if new file , then we will create new file in project id and save it
     */
    @Override
    public void addOrUpdateFile(UUID projectId, String filePath, String fileContent) {

        Optional<ProjectFile> isFile = projectFileRepo.findByProjectIdAndPath(projectId , filePath);

//        if file is present , then we will update content and return
//        else we will create a new file and save it
        if(isFile.isPresent()){
            ProjectFile file = isFile.get();

//            we will get minio id ,
//            replace content with this updated content

//            file.get
            return;
        }

//        else
//        we will create new file object and save it in min.io and save file object


    }


}
