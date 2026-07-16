package com.PromptToApp.core.mapper;

import com.PromptToApp.core.Dto.Res.ProjectBasicDetailsResponseDto;
import com.PromptToApp.core.Entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface projectMapper {

    ProjectBasicDetailsResponseDto toProjectDto(Project project);
}
