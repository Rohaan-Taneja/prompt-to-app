package com.PromptToApp.core.Dto.Res;

import lombok.Builder;

import java.util.List;

@Builder
public record projectDetailsResDto(String name , String description , UserProfileResponseDto owner) {
}
