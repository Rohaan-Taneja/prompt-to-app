package com.PromptToApp.core.Dto.Res;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfileResponseDto(
        UUID id ,
        String name,
        String email ,
        String role
) {
}
