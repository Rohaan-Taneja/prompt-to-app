package com.PromptToApp.core.service.serviceImpl;


import com.PromptToApp.core.Entity.Project;
import com.PromptToApp.core.Entity.ProjectFile;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.repository.projectFileRepository;
import com.PromptToApp.core.repository.projectRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.service.initializeReactTemplateForNewProject;
import com.PromptToApp.core.service.minioService;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class initializeReactTemplateForNewProjectImpl implements initializeReactTemplateForNewProject {

    @Value("${react.template.starter}")
    public String reactTemplateStartsWith;

    private final projectRepository projectRepo;
    private final projectFileRepository fileRepo;
    private final userRepository userRepo;
    private final minioService minioService;


    /**
     *
     * @param projectId new project just created
     *                  we will be having a React template , so we need to copy those all files for this
     *                  projectId(from minio to minio under new project id)
     *                  1) for minio
     *                  2) and after copying and pasting all files , we Will save all projectFile OBJECT TO DB
     */
    @Async("background-task-executor-thread-pool")
    public void copyReactTemplateForNewProject(UUID projectId, String projectName, UUID userId) {

        List<ProjectFile> filesToSave = new ArrayList<>();

        Project project = projectRepo.getReferenceById(projectId);

        User user = userRepo.getReferenceById(userId);

        try {
            log.info("this is the file saving background thread {}", Thread.currentThread().getName());

//            getting all template files
            Iterable<Result<Item>> templateFiles = minioService.getAllFileStartsWith(reactTemplateStartsWith);

            log.info("no of files {}", templateFiles);

            for (Result<Item> file : templateFiles) {

//                filepath -> react-templa
                String source = file.get().objectName();

//                we will replace react-template/ with projects/project_id


                String reactFile = source.split(reactTemplateStartsWith + "/")[1];
                String destination = "projects/" + projectId + "/" + reactFile;

//              copy pasting the file from template to /for user new project
                minioService.copyObject(source, destination);

//                on every minio me addition we will save files to this array
                filesToSave.add(
                        ProjectFile.builder().name(projectName).project(project).path(reactFile).minIoObjectKey(destination).createdBy(user).lastUpdatedBy(user).build()
                );


            }

            fileRepo.saveAll(filesToSave);

        } catch (Exception exception) {
            throw new RuntimeException("exception");
        }

    }


}
