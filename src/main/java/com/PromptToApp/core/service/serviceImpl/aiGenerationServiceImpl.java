package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.CustomExceptionHandling.customBadRequestException;
import com.PromptToApp.core.Entity.SubscriptionPlan;
import com.PromptToApp.core.enums.SubscriptionStatus;
import com.PromptToApp.core.repository.usageLogsRepository;
import com.PromptToApp.core.repository.userSubscriptionRepository;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.service.aiGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class aiGenerationServiceImpl implements aiGenerationService {

    private final ChatClient chatClient;

    private final authUtilService authUtilService;

    private final userSubscriptionRepository userSubscriptionRepo;

    private final usageLogsRepository usageLogsRepo;


//    the user should be a project member of this project and should HAVE WRITE ACCESS

    /**
     *
     * @param projectId
     * @param chatMessage
     * @return we will get user object (project member)
     * we will get user subscription (we may need only max token)
     * we will fetch todays token usage
     * we will save chat
     * we will call ai with prompt + system prompt + file structure
     *
     * do onnext will collect the response , like ai assistant response , files complete data
     * we can give entity that each stream object will have a format ( type : assistant_response , message : "" ) and for files ( type : " file update" , file_path : "src/123" , message : "")
     *
     * so on do next we will collect the formatted data
     *
     * on do on complete , we will save the chat , updated ()
     *
     */
    @PreAuthorize("@securityAccessChecker.checkUserAccessAndEditToProject(#projectId)")
    public Flux<String> getChatResponse(UUID projectId, String chatMessage) {

        UUID userId = authUtilService.getUserId();
        SubscriptionPlan userSubscriptionPlan = userSubscriptionRepo.getByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("no active user subscription found"));
        Integer max_token_per_day = userSubscriptionPlan.getPlan().getMaxTokenPerDay();

        Integer user_today_token_usage = usageLogsRepo.getTokenUsedByUserToday(userId);


//        daily token quota check
        if (user_today_token_usage.equals(max_token_per_day)) {
            throw new customBadRequestException("token usage limit has reached for today");

        }

        StringBuilder chat_complete_response = new StringBuilder();


        System.out.println( "current thread running in jpa flow" + Thread.currentThread().getName());

        /**
         * chat client details
         * user prompt , system prompt , get propject tree
         */

        Flux<ChatResponse> chatResponseFlux = chatClient.prompt()
                .system("system_prompt")
                .user(chatMessage)
                .advisors()
                .stream()
                .chatResponse()
                .doOnNext(stream_message ->
                        {
                            System.out.println( "current thread running in rector flow , do on next" + Thread.currentThread().getName());
                            log.info("this is he chat stream data {}", stream_message.getResults());

//                            so here we can be collecting code and sort which is ai response and which is file
                            chat_complete_response.append(stream_message.getResult().getOutput().toString());
                        }

                )
                .doOnComplete(() -> {

                    System.out.println( "current thread running in rector flow , do on complete flow" + Thread.currentThread().getName());

//                    3 options
//                        1) run directly saveChanges (it will block the reactor )
//                        2) run using @async (will run by spring mvc executor thread )/ same as future callable
//                        3) run on scheduler (reactor thread pool)


//                we are using boundedElastic because it limits/bounds the no of threads and elastic measn that it can spin up new threads and if any thread is idle for 60 sec , it will dump
//                    Schedulers.boundedElastic().schedule(() -> {
//                       saveChatAndUpdatedFiles(chat_complete_response , projectId , userId );
//                    });

                    Mono.fromRunnable(() -> {
                        saveChatAndUpdatedFiles(chat_complete_response , projectId , userId );
                    }).subscribeOn(Schedulers.boundedElastic())
                            .doOnSubscribe( var -> log.info("saved the chats and files to db"))
                            .doOnError( e -> log.info("getting while saving the chats and file updates -> {}" , e.toString()))
                            .retry(3)
                            .subscribe();

//                    on complete , we can save the collected response , and updated file
//                    save the complete chat, usage log, generated files, and update token counts.
                })
                .doOnError(error -> log.error(
                        "we got error in chat stream of project {} this error {}",
                        projectId,
                        error.getLocalizedMessage()
                ));

//        call ai with prompt + system prompt

//        get token usage and we will update token used

        return chatResponseFlux.map(data -> data.getResult().getOutput().toString());

    }

        private boolean saveChatAndUpdatedFiles( StringBuilder ai_response , UUID projectId , UUID user_id){

        return true;

    }
}
