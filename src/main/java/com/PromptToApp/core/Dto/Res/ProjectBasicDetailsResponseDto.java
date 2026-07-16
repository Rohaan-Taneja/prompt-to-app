package com.PromptToApp.core.Dto.Res;

import java.util.UUID;

public record ProjectBasicDetailsResponseDto(UUID project_id , String name , String description) {
}
