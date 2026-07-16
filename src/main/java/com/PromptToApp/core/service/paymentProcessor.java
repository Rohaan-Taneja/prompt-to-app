package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Res.subscriptionIntentResDto;
import com.stripe.model.Event;

import java.util.UUID;

public interface paymentProcessor {

    public String getCustomerPortal();

    public subscriptionIntentResDto createSubscriptionIntent(UUID planId, UUID userId);


    public String paymentConfirmationWebHook(Event event);
    }
