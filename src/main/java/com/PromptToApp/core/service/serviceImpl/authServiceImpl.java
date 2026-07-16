package com.PromptToApp.core.service.serviceImpl;


import com.PromptToApp.core.CustomExceptionHandling.ResourceAlreadyPresent;
import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.Dto.Req.SignUpReqDto;
import com.PromptToApp.core.Dto.Req.loginReqDto;
import com.PromptToApp.core.Dto.Res.UserProfileResponseDto;
import com.PromptToApp.core.Dto.Res.userAuthResponseDto;
import com.PromptToApp.core.Entity.Plan;
import com.PromptToApp.core.Entity.SubscriptionPlan;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.enums.SubscriptionStatus;
import com.PromptToApp.core.repository.planRepository;
import com.PromptToApp.core.repository.userRepository;
import com.PromptToApp.core.repository.userSubscriptionRepository;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.service.authService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class authServiceImpl implements authService {


    @Value("${free.plan.id}")
    private UUID freePlanId;

    private final AuthenticationManager authenticationManager;

    private final userRepository user_repo;
    private final planRepository plan_repo;

    private final userSubscriptionRepository subscription_repo;

    private final authUtilService auth_util_service;

    private final PasswordEncoder passwordEncoder;


    /**
     *
     * we will get user and password
     * we will check it this is new
     * if new then we will creaet new user object with hashed password and save it
     * creaet acees adn refresh token and done
     *
     */
    public userAuthResponseDto signUp(SignUpReqDto signUpUserData) {

//        todo
//        when new user signs up , we will create a plan for user (free tier plan)

        user_repo.findByEmail(signUpUserData.email()).ifPresent(user -> {

            throw new ResourceAlreadyPresent("user already present , please login instead");

        });


        String hashedPassword = passwordEncoder.encode(signUpUserData.password());

        User newUser = User.builder()
                .name(signUpUserData.name())
                .email(signUpUserData.email())
                .password(hashedPassword)
                .jti(null)
                .build();


        UUID jti = UUID.randomUUID();

//        storing refresh token id for later verification of refresh token
        newUser.setJti(jti);

        User savedUser = user_repo.save(newUser);

//        creating user subscription object for new sign up
        createUserSubscription(savedUser);

//        create user subscription object for new user , by default free plan will be added


        String accessToken = auth_util_service.createAccessToken(newUser.getId());
        String refreshToken = auth_util_service.createRefreshToken(newUser.getId(), jti);

        log.info("new user created {}", savedUser);


        UserProfileResponseDto userProfileResponseDto = UserProfileResponseDto.builder().id(savedUser.getId()).email(savedUser.getEmail()).name(savedUser.getName()).role("admin").build();

        return userAuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).userProfileResponse(userProfileResponseDto).build();
    }


    /**
     *
     * we are using spring security to authenticate user
     * then we will generate access and refresh token and return the response
     */
    public userAuthResponseDto login(loginReqDto userData) {

//        it will authenticate user
        Authentication authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userData.email(), userData.password()));

        log.info("authentication doe with auth manager");

        if (!authResult.isAuthenticated()) {
            throw new ResourceNotFoundException("email or password is wrong");
        }

        User authUser = (User) authResult.getPrincipal();

        String accessToken = auth_util_service.createAccessToken(authUser.getId());

        log.info("access token creaetd");

//      refresh token will store user id and jwt token Id(refresh token id) also for /refresh token verification Part
        UUID jti = UUID.randomUUID();
        String refreshToken = auth_util_service.createRefreshToken(authUser.getId(), jti);

        authUser.setJti(jti);

        user_repo.save(authUser);

        UserProfileResponseDto userProfileResponseDto = UserProfileResponseDto.builder().id(authUser.getId()).email(authUser.getEmail()).name(authUser.getName()).role("admin").build();

        return userAuthResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).userProfileResponse(userProfileResponseDto).build();

    }


    /**
     *
     * @param user
     * adding user subscription with free lan to each new user sign up
     */
    public void createUserSubscription(User user) {

        Plan freePlan = plan_repo.findById(freePlanId).orElseThrow(() -> new ResourceNotFoundException("no free plan exist"));
        SubscriptionPlan userSubscription=  SubscriptionPlan.builder()
                .plan(freePlan)
                .user(user)
                .subscriptionStartDate(LocalDateTime.now())
                .subscriptionPlanEndDate(LocalDateTime.now().plusDays(365))
                .status(SubscriptionStatus.ACTIVE)
                .build();


        subscription_repo.save(userSubscription);

    }
}
