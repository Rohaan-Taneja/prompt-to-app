package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.CustomExceptionHandling.*;
import com.PromptToApp.core.Dto.Res.subscriptionIntentResDto;
import com.PromptToApp.core.Entity.Plan;
import com.PromptToApp.core.Entity.ProcessedStripeEvent;
import com.PromptToApp.core.Entity.SubscriptionPlan;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.enums.SubscriptionStatus;
import com.PromptToApp.core.repository.planRepository;
import com.PromptToApp.core.repository.processedStripeEventRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.repository.userSubscriptionRepository;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.service.paymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.billingportal.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.billingportal.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service("STRIPE")
@Slf4j
@RequiredArgsConstructor
@Transactional
public class stripePaymentServiceImpl implements paymentProcessor {


    @Value("${payment_success_url}")
    private String paymentSuccessUrl;

    @Value("${payment_fail_url}")
    private String paymentFailsUrl;

    private final userSubscriptionRepository userSubscriptionRepo;

    private final planRepository planRepo;

    private final userRepository userRepo;

    private final processedStripeEventRepository processedStripeEventRepo;

    private final authUtilService auth_util_service;

    @Override
    public String getCustomerPortal() {


        User user = auth_util_service.getUser();

        SubscriptionPlan user_subscription = getUseCurrentSubscriptionPlan(user.getId());

        String customerStripeId = user_subscription.getStripeCustomerId();

        if (customerStripeId == null) {
            throw new ResourceNotFoundException("you have not subscribed to any subscription");
        }


        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCustomer(customerStripeId)
                        .setReturnUrl("https://example.com/account")
                        .build();

        try {
            Session session = Session.create(params);

            return session.getUrl();

        } catch (Exception ex) {

            throw new customInternalServerError(ex.getLocalizedMessage());


        }
    }

    /**
     *
     * @param planId
     * @param userId
     * @return //        user id wants to buy a subscription
     * //        load the user current user subscription object and check current plan < purchasing plan
     * //        then load purchasing plan also
     * //        check purchasing plan greater that current plan
     * <p>
     * //        we will create stripe user in stripe
     * //        so we will do something in stripe create subscription object with stripe customer id
     * //       and save customer id in db for future payments
     * <p>
     * //        create session object to get payment url
     * <p>
     * //        and return payment url
     */
    public subscriptionIntentResDto createSubscriptionIntent(UUID planId, UUID userId) {

        log.info("we are in stripe service.creaet subscription intent");
//        getting user current subscription plan
        SubscriptionPlan userCurrentSubscription = userSubscriptionRepo.getByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("user subscription does not exist"));

//      to purchase plan
        Plan toSubscribePlan = planRepo.findById(planId).orElseThrow(() -> new ResourceNotFoundException("plan with this id doesn't exist"));

        log.info("this is to subscribe plan that user currently has {}", userCurrentSubscription.getPlan());
        log.info("this is to subscribe plan that user want to purchase{}", planId);

        User currentUser = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("no user found "));


//        getting user stripe customer id (if present)
//        for first time payer , stripe id will be null
//        String stripeCustomerId = userCurrentPlan.getStripeCustomerId();
        String stripeCustomerId = userCurrentSubscription.getStripeCustomerId();


//        check for customer id , if not then create a stripe customer id
        if (stripeCustomerId == null) {
            try {
//                creating customer and saving in user subscription table
                stripeCustomerId = createCustomerId(currentUser);
                userCurrentSubscription.setStripeCustomerId(stripeCustomerId);
                userSubscriptionRepo.save(userCurrentSubscription);


            } catch (StripeException ex) {

                throw new StripeCustomException(ex.getLocalizedMessage());
            } catch (Exception ex) {
                throw new customBadRequestException(ex.getLocalizedMessage());
            }
        }

        String planPriceId = toSubscribePlan.getStripePlanPriceId();


        // create subscription object
        com.stripe.param.checkout.SessionCreateParams params = com.stripe.param.checkout.SessionCreateParams.builder()
                .setMode(com.stripe.param.checkout.SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(stripeCustomerId)
                .addLineItem(com.stripe.param.checkout.SessionCreateParams.LineItem.builder().setQuantity(1L).setPrice(planPriceId).build()).setSubscriptionData(com.stripe.param.checkout.SessionCreateParams.SubscriptionData.builder().putMetadata("user_id", userId.toString()).putMetadata("plan_id", toSubscribePlan.getId().toString()).build()).setSuccessUrl(paymentSuccessUrl + "?session_id={CHECKOUT_SESSION_ID}").setCancelUrl(paymentFailsUrl).build();


        try {
            log.info("we are here just before stripe call");

//            creating subscription object in stripe
            com.stripe.model.checkout.Session paymentSubscription = com.stripe.model.checkout.Session.create(params);
            log.info("this is the session object {}", paymentSubscription);

            return subscriptionIntentResDto.builder().paymentUrl(paymentSubscription.getUrl()).build();

        } catch (StripeException ex) {
            throw new StripeCustomException(ex.getLocalizedMessage());
        }
    }


    /**
     *
     * @param user
     * @return we will be creating a stripe customer , so that we can refer this user by this id
     * @throws StripeException
     */
    private String createCustomerId(User user) throws StripeException {

        CustomerCreateParams params = CustomerCreateParams.builder().setEmail(user.getEmail()).setName(user.getName()).build();

        Customer customer = Customer.create(params);

        return customer.getId();


    }


    /**
     *
     * @param event can be any stripe event ( with id , type and data.object as general)
     *              data.object is Event object is abstract type , it knows data.object is present but exactly what it dont knw
     *              so we will try to get the data.object
     *              we will deserialize the event
     *              we will check if this event is not already handled , if already handled then return , else
     *              handle it via switch case
     * @return
     */
    public String paymentConfirmationWebHook(Event event) {

//        trying to deserialize the event.data.obejct into appropriate object on the the basis of event.type
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

//        the actual event.data.object which contains event all details
//        this is like a base class and further event are extension of it , liek animal class and dog , cat are extensions
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {

            // Deserialization failed, probably due to an API version mismatch.
            throw new StripeCustomException("stripe event object cannot be deserialized");
        }


//        we will try to  directly save every incoming event
//        if its a new event , we will be able to save and handle
//        if repetitive or parallel same event , then
//        1 will be executed and repetitive will throw duplicate exception and stop here only , no further going
        try {
            processedStripeEventRepo.save(ProcessedStripeEvent.builder().eventId(event.getId()).build());

        } catch (Exception ex) {
            log.info("event already executed {}", ex.getLocalizedMessage());
            throw new customBadRequestException("duplicate event event already executed");
        }

        switch (event.getType()) {

            case "customer.subscription.updated": {
                Subscription subscription = (Subscription) stripeObject;

                log.info("we are in customer subscription updated");
                handleSubscriptionUpdate(subscription);

                break;
            }

            case "invoice.paid": {
                Invoice paidInvoice = (Invoice) stripeObject;
                log.info("we are in invoice paid event {}", paidInvoice);

                handleInvoicePaid(paidInvoice);

                break;
            }

            default:
                log.info("this is the other events we get {}", event.getType());
        }

        return "thank uou";

    }


    /**
     *
     * @param invoice -> invoice object containing all the objects
     *                what we doing ?
     *                we will get invoice details
     *                we will get user id from meta data (remain same for user , even if it cancels or upgrade plan)
     *                we will get stripe product id, then we will get plan id linked to this product from db
     *                then we will update user start/end/plan data accordingly.
     */
    private void handleInvoicePaid(Invoice invoice) {
        String type = "invoice.paid";

//        get user metadata (plan id , user id )
//        get user current subscription active plan ...
//        just set plan
//        then we will update expiry of the user plan , taken from stripe
//        then save

        log.info("we are in invoice paid");
//        getting metadata
        Map<String, String> metaData = invoice.getParent().getSubscriptionDetails().getMetadata();
        UUID user_id = UUID.fromString(metaData.get("user_id"));

        log.info("this is the meta date {}", metaData);


//        getting product id from the stripe
//        then this stripe product id is linked with our plan id , so we will find which plan has user purchased
        String stripe_productId = invoice.getLines().getData().get(0).getPricing().getPriceDetails().getProduct();

        Plan plan_details = planRepo.findByStripePlanId(stripe_productId).orElseThrow(() -> new ResourceNotFoundException("plan with this id does not exist => " + stripe_productId));

        UUID purchased_plan_id = plan_details.getId();

        log.info("this is the plan id from product {}", stripe_productId);

        SubscriptionPlan userCurrentSubscription = getUseCurrentSubscriptionPlan(user_id);

        log.info("we are after user current subscription found");

//        what user paid for
        Plan userToBeSubscribedPlan = planRepo.findById(purchased_plan_id).orElseThrow(() -> new ResourceNotFoundException("no plan id found for this subscription"));

        log.info("we are after user paid subscription found");

        String stripeCustomerId = invoice.getCustomer();

        log.info("this is the customer_id {} ", stripeCustomerId);

        if (userCurrentSubscription.getStripeCustomerId() == null && stripeCustomerId != null) {
            userCurrentSubscription.setStripeCustomerId(stripeCustomerId);
        }


        String stripeCustomerSubscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        log.info("this is the customer subscription id  {} ", stripeCustomerSubscriptionId);

        if (userCurrentSubscription.getStripeCustomerId() == null && stripeCustomerSubscriptionId != null) {
            userCurrentSubscription.setStripeSubscriptionId(stripeCustomerSubscriptionId);
        }

//        we will update start date and plan only when the new subscription is purchased
        if ("subscription_create".equals(invoice.getBillingReason())) {

//            adding start date to user subscription
            userCurrentSubscription.setSubscriptionStartDate(LocalDateTime.now());

            //setting the plan user paid for
            userCurrentSubscription.setPlan(userToBeSubscribedPlan);

            log.info("we are after updating the date and updte plan ");
        }

        userCurrentSubscription.setSubscriptionPlanEndDate(LocalDateTime.now().plusDays(30));

        userSubscriptionRepo.save(userCurrentSubscription);

        return;
    }


    /**
     *
     * this will called by stripe so , no free plan to be considered ,
     * will be called when payment is done
     *
     * @param subscription -> invoice object containing all the objects
     *                     what we doing ?
     *                     we will get updated subscription things  , like pause , cancel , plan update ,
     *                     cancelled_at will tell if cancel happens or not , if yes , then got for cancel function
     *                     for upgrade/degrade , if plan is changed , we will update plan id and start date
     *                     and also cancel_at is set to false (plan is upgrade/active)
     *                     then we will update those things in th user subscription object
     */
    private void handleSubscriptionUpdate(Subscription subscription) {
        String type = "customer.subscription.updated";

        log.info("we are in update customer subscription updated");


//        when user try to update plan , after cancel , so in that cancel logic cance_at_period will be set to false in stripe subscription
//        boolean cancel_at_period_end = subscription.getCancelAtPeriodEnd();
        Long will_cancel_on = subscription.getCanceledAt();


        log.info("this is the cancel_at_period_end value {}", will_cancel_on);

//        IT means cancel is scheduled
        if (will_cancel_on != null) {

            log.info("we are in cancel subscription check statement in update func");

            handleSubscriptionCancelled(subscription);

            return;
        }

//        getting metadata
        Map<String, String> metaData = subscription.getMetadata();

        UUID user_id = UUID.fromString(metaData.get("user_id"));


        //        getting product id from the stripe
//        then this stripe product id is linked with our plan id , so we will find which plan has user purchased
        String stripe_productId = subscription.getItems().getData().get(0).getPrice().getProduct();

        Plan plan_details = planRepo.findByStripePlanId(stripe_productId).orElseThrow(() -> new ResourceNotFoundException("plan with this id does not exist => " + stripe_productId));

        UUID purchased_plan_id = plan_details.getId();


        SubscriptionPlan userCurrentSubscription = getUseCurrentSubscriptionPlan(user_id);

        log.info("we are after user current subscription found");

//        what user paid for(can be a new plan)
        Plan userToBeSubscribedPlan = planRepo.findById(purchased_plan_id).orElseThrow(() -> new ResourceNotFoundException("no plan id found for this subscription"));

        log.info("we are after user paid subscription found");

//        if new plan != current plan , so it measn it is a upgrade/downgrade
//        So we will update plan id , start date
        if (!userCurrentSubscription.getPlan().getId().equals(userToBeSubscribedPlan.getId())) {
            userCurrentSubscription.setSubscriptionStartDate(LocalDateTime.now());

            //setting the plan user paid/upgraded to
            userCurrentSubscription.setPlan(userToBeSubscribedPlan);


        }


//        syncing with the current cancel value in the stripe
//        if user try to upgrade after cancel , so update flow will run
//        stripe is updated with cancel = false , but db is not updated , so on every update we are just syncing with stripe
        userCurrentSubscription.setCancelAtPeriodEnd(false);

        log.info("we are after updating the date and updte plan ");

        log.info("this is the start date  {}", userCurrentSubscription.getSubscriptionStartDate());


        userSubscriptionRepo.save(userCurrentSubscription);

        return;


    }


    /**
     *
     * @param subscription we will get user current active subscription and mark cancel at period end to true , so we can show it in frontend
     *                     nothing else to update , let subscription run till end date then stripe wont charge
     */
    private void handleSubscriptionCancelled(Subscription subscription) {
        String type = "customer.subscription.updated";

        log.info("we are in cancel subscription function ");


//        getting metadata
        Map<String, String> metaData = subscription.getMetadata();
        UUID user_id = UUID.fromString(metaData.get("user_id"));


        log.info("this is the meta date {}", metaData);

        SubscriptionPlan userCurrentSubscription = getUseCurrentSubscriptionPlan(user_id);

        //setting the plan user paid/upgraded to
        userCurrentSubscription.setCancelAtPeriodEnd(true);

        log.info("we are after updating the cancel at period end");


        userSubscriptionRepo.save(userCurrentSubscription);

        return;


    }


    /**
     *
     * @param user_id
     * @return get user current plan , it can be active , past due or paused , but that would be the current plan
     * user can have previous subscription plan also , but current is this onlu
     */
    private SubscriptionPlan getUseCurrentSubscriptionPlan(UUID user_id) {

        log.info("finding user subscription details");

        try {

            return userSubscriptionRepo.getByUserIdAndStatusIn(user_id, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.PAUSED)).orElseThrow(() -> new ResourceNotFoundException("no current plan for user exist"));

        } catch (Exception ex) {

            log.info("this is the user subscription not found exception {}", ex.getLocalizedMessage());
            throw new ResourceNotFoundException(ex.getLocalizedMessage());

        }


    }


}

