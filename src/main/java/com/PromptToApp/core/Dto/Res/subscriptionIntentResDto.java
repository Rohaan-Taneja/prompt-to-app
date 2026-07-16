package com.PromptToApp.core.Dto.Res;

import lombok.Builder;

@Builder
public record subscriptionIntentResDto(String paymentUrl) {
}
