package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.CustomExceptionHandling.customBadRequestException;
import com.PromptToApp.core.Dto.Res.subscriptionIntentResDto;
import com.PromptToApp.core.enums.paymentProcessorType;
import com.PromptToApp.core.repository.userSubscriptionRepository;
import com.PromptToApp.core.service.paymentProcessor;
import com.PromptToApp.core.service.subscriptionService;
import com.stripe.model.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class subscriptionServiceImpl implements subscriptionService {

    private final userSubscriptionRepository subscriptionPlanRepo;

//    since currently we have 1 payment method , that's why no autowired , else bean injection will be done
//    based on at runtime , function call condition
    private paymentProcessor payment_processor;

    private final Map<String , paymentProcessor> processors ;



    /**
     *
     * @param planId
     * @param userId
     * @param type of payment processor user , for now it is stripe only
     * @return  we will also specify payment method via a enum value , so bsed on that we will update paymentprocessor
     * and call function
     */
    public subscriptionIntentResDto createSubscriptionIntent(UUID planId, UUID userId , paymentProcessorType type) {

//        we will be getting a var for payment gateway used (stripe etc)
//        on the basis of that we will use our strategy

        log.info("we are here in subscription service");
        payment_processor = processors.get(type.name());

        log.info("this is the payment processor {}" , payment_processor);

        if(payment_processor == null){
            throw new ResourceNotFoundException("not a valid payment provider");
        }

        return payment_processor.createSubscriptionIntent(planId , userId);
    }



//    it will not be here , it will beon auth free route , so that stripe can call
    public String paymentConfirmationWebHook(Event event , paymentProcessorType type) {

        if (type == null){
            throw new customBadRequestException("no payment processor of this type exist");
        }
        payment_processor = processors.get(type.name());

        payment_processor.paymentConfirmationWebHook(event);



        return "hello";


    }


    public String getCustomerPortalLink(paymentProcessorType type) {
        if (type == null){
            throw new customBadRequestException("no payment processor of this type exist");
        }
        payment_processor = processors.get(type.name());

        return payment_processor.getCustomerPortal();

    }


}




