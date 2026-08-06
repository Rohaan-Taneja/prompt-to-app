package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.CustomExceptionHandling.customInternalServerError;
import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.Entity.Project;
import com.PromptToApp.core.Entity.ProjectFile;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.mapper.fileMapper;
import com.PromptToApp.core.repository.projectFileRepository;
import com.PromptToApp.core.repository.projectRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.service.fileService;
import com.PromptToApp.core.service.minioService;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class fileServiceImpl implements fileService {

    private final projectFileRepository projectFileRepo;

    private final minioService minioService;

    private final projectRepository projectRepo;
    private final projectFileRepository fileRepo;
    private final userRepository userRepo;

    private final fileMapper fileMapper;


    @Value("${react.template.starter}")
    public String reactTemplateStartsWith;


    /**
     * we will give project id and normal file tree
     */
    public List<FileResDto> getProjectFilesTree(UUID projectId) {

        List<ProjectFile> projectAllFiles = projectFileRepo.getFileTree(projectId);
        return fileMapper.toFileResDtoList(projectAllFiles);

    }

    public ProjectFileDetailsResDto getProjectFileContent(UUID fileId) {

        ProjectFile file = projectFileRepo.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("project with file id = " + fileId + " does not exist"));

        try {

            String fileContent = minioService.getFile(file.getMinIoObjectKey());

            ProjectFileDetailsResDto fileDetailsResDto = fileMapper.toProjectFileDetailDto(file);

            fileDetailsResDto.setContent(fileContent);
            return fileDetailsResDto;


        } catch (Exception e) {
            throw new ResourceNotFoundException(e.toString());
        }

    }

    @Override
    public ProjectFileDetailsResDto updatedProjectFileContent(UUID userId, UUID projectId, UUID fileId) {
        return null;
    }


    /**
     * we will save file content in minio(for new , new object si created ,for already present , it
     * will be overwritten).
     * <p>
     * and then we will find file in db , if present then okay , if not then create file object
     * and update late updated by and save file in db
     * file path = "projectiD/src/file.js"
     * name =file.js
     */
    @Override
    public void addOrUpdateFile(UUID projectId, String filePath, String fileContent, UUID userId) {

        log.info("we are in add or update file service {} {} {} {}", projectId, filePath, fileContent, userId);

        String[] fileArray = filePath.split("/");

        String fileName = fileArray[fileArray.length - 1];

        String minioObjectKey = "projects/" + projectId + "/" + filePath;

        InputStream stream = new ByteArrayInputStream(
                fileContent.getBytes(StandardCharsets.UTF_8)
        );

        Project project = projectRepo.getReferenceById(projectId);

        User user = userRepo.getReferenceById(userId);


        /**
         * uploading file to minio , if new then also okay , if already existing then it will be over written by new content
         */
        try {
            minioService.uploadFile(minioObjectKey, stream, getFileType(fileName));

        } catch (Exception e) {
            throw new customInternalServerError("error while upload file to minio" + e.toString());
        }


//        if file is present then this db file
//        else or else get , we will construct a new projectFile object and return
        ProjectFile file = projectFileRepo.findByProjectIdAndPath(projectId, filePath)
                .orElseGet(() ->
//                        or else create new project file
                                ProjectFile.builder()
                                        .name(fileName)
                                        .project(project)
                                        .path(filePath) // projectId/src/file
                                        .minIoObjectKey(minioObjectKey) // projectId/src/file
                                        .build()

                );

//        updating last updated by
        file.setLastUpdatedBy(user);
        projectFileRepo.save(file);

    }


    /**
     * we will get file name (file.js /css /ts / json etc)
     * so we w ill split file on the basis of . and get the (css , js , ts , json etc)
     * and mark file type as different file types
     */
    private String getFileType(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return "application/octet-stream";
        }

        /**
         * rohan.component.jsx./ component.js
         */
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {

            // JavaScript / TypeScript
            case "js", "jsx" -> "application/javascript";
            case "ts", "tsx" -> "application/typescript";

            // Web
            case "html", "htm" -> "text/html";
            case "css" -> "text/css";
            case "scss" -> "text/x-scss";

            // Data
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            case "md" -> "text/markdown";
            case "txt" -> "text/plain";

            // Images
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "svg" -> "image/svg+xml";

            // Fonts
            case "ttf" -> "font/ttf";
            case "otf" -> "font/otf";
            case "woff" -> "font/woff";
            case "woff2" -> "font/woff2";

            // Videos
            case "mp4" -> "video/mp4";
            case "webm" -> "video/webm";
            case "mov" -> "video/quicktime";

            // Audio
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "ogg" -> "audio/ogg";

            // Archives
            case "zip" -> "application/zip";
            case "tar" -> "application/x-tar";
            case "gz" -> "application/gzip";

            default -> "application/octet-stream";
        };
    }


    /**
     *
     * @param projectId new project just created
     *                  we will be having a React template , so we need to copy those all files for this projectId
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

            log.info("no of files {}" , templateFiles);

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
