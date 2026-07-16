package com.PromptToApp.core.controller;

import com.PromptToApp.core.CustomExceptionHandling.customBadRequestException;
import com.PromptToApp.core.CustomExceptionHandling.customUnauthorizedException;
import com.PromptToApp.core.Dto.Res.subscriptionIntentResDto;
import com.PromptToApp.core.enums.paymentProcessorType;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.service.serviceImpl.stripePaymentServiceImpl;
import com.PromptToApp.core.service.subscriptionService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("payment")
public class subscriptionController {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    private final subscriptionService subscription_service;
    private final authUtilService auth_util_service;
    private final stripePaymentServiceImpl stripe_payment_service;


    @PostMapping("create-subscription-intent/{planId}/{type}")
    public ResponseEntity<subscriptionIntentResDto> createSubscriptionIntent(@PathVariable("planId") UUID planId ,  @PathVariable("type") paymentProcessorType type ){
        UUID userId = auth_util_service.getUserId();


        log.info("we are here in create intent controller");
        return ResponseEntity.ok(subscription_service.createSubscriptionIntent(planId , userId , type));
    }

    /**
     * stripe payment webhook , it will public route , anybody can call it
     * @return
     * so stripe will call with secret
     * we will verify secret and construct payload to event object
     * and then we will call service
     */
    @PostMapping("/webhook/{type}")
    public ResponseEntity<String> paymentWebhook(@RequestBody String payload , @RequestHeader("Stripe-Signature") String signature , @PathVariable("type") paymentProcessorType type ){

        log.info("this is the secret signature from headers webhook {}" , signature);
        log.info("this is the event {}" , payload);

        Event event = null;

        try {
//            this will verify the secret and return us the event object
            event = Webhook.constructEvent( payload ,signature , stripeWebhookSecret);

        }catch (SignatureVerificationException ex){
            log.warn("Invalid Stripe webhook signature: {}", ex.getMessage());
            throw new customUnauthorizedException("webhook cannot be validated");

        }
        catch (Exception ex){
            log.warn("malformed payload");
            throw new customUnauthorizedException(ex.getMessage());
        }

        try {

            return ResponseEntity.ok(subscription_service.paymentConfirmationWebHook(event , type));

        }
        catch (Exception ex){

            log.info("this is the error from controller {}" , ex.getLocalizedMessage());
            throw new customBadRequestException(ex.getLocalizedMessage());

        }

        /**
         * so in webhook we will get webhook secret
         * which we can handle by pre handle (secret verification)
         *
         * then we can redirect to strip service function , which will handle switch on the basis of different event that we want to handle
         */



    }



    @GetMapping("/get-customer-portal/{type}")
    public ResponseEntity<String> getCustomerPortal(@PathVariable("type") paymentProcessorType type ){

        log.info("we are into getting customer portal");
        return ResponseEntity.ok(subscription_service.getCustomerPortalLink(type));

    }
}
