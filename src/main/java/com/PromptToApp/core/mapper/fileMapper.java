package com.PromptToApp.core.mapper;

import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectFileDetailsResDto;
import com.PromptToApp.core.Entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface fileMapper {


    List<FileResDto> toFileResDtoList(List<ProjectFile> projectFiles);

    ProjectFileDetailsResDto toProjectFileDetailDto(ProjectFile file);
}
