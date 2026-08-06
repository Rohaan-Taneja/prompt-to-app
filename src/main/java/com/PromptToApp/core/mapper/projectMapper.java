package com.PromptToApp.core.mapper;

import com.PromptToApp.core.Dto.Res.FileResDto;
import com.PromptToApp.core.Dto.Res.ProjectBasicDetailsResponseDto;
import com.PromptToApp.core.Entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface projectMapper {

    ProjectBasicDetailsResponseDto toProjectDto(Project project);
}
