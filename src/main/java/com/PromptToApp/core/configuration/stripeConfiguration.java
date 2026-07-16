package com.PromptToApp.core.configuration;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class stripeConfiguration {

    @Value("${stripe.api.key}")
    private String stripeSecret;

    @PostConstruct
    public void setupStripe() {
        Stripe.apiKey = stripeSecret;
    }
}
