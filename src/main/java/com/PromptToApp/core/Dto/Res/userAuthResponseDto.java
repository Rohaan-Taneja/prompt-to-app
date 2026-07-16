package com.PromptToApp.core.Dto.Res;

import lombok.Builder;

@Builder
public record userAuthResponseDto(
        String accessToken,
        String refreshToken,
        UserProfileResponseDto userProfileResponse
) {
}
