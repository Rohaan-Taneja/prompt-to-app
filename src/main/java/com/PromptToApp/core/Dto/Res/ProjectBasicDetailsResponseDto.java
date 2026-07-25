package com.PromptToApp.core.Dto.Res;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProjectBasicDetailsResponseDto(UUID project_id, String name, String description) {
}
