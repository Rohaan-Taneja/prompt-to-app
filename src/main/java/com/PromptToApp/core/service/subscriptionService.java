package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Res.subscriptionIntentResDto;
import com.PromptToApp.core.enums.paymentProcessorType;
import com.stripe.model.Event;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface subscriptionService {
    subscriptionIntentResDto createSubscriptionIntent(UUID planId, UUID userId , paymentProcessorType type);

    String paymentConfirmationWebHook(Event event , paymentProcessorType type);


    String getCustomerPortalLink(paymentProcessorType type);
}

