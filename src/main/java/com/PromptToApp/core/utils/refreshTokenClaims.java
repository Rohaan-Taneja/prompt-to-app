package com.PromptToApp.core.utils;

import lombok.Builder;

import java.util.UUID;

@Builder
public record refreshTokenClaims(UUID user_id, UUID jti) {
}
