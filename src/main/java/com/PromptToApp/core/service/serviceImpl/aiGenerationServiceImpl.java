package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.CustomExceptionHandling.customBadRequestException;
import com.PromptToApp.core.CustomExceptionHandling.customUnauthorizedException;
import com.PromptToApp.core.Entity.SubscriptionPlan;
import com.PromptToApp.core.enums.ChatBy;
import com.PromptToApp.core.enums.SubscriptionStatus;
import com.PromptToApp.core.repository.userSubscriptionRepository;
import com.PromptToApp.core.security.authUtilService;
import com.PromptToApp.core.security.securityAccessCheck;
import com.PromptToApp.core.service.aiGenerationService;
import com.PromptToApp.core.service.chatService;
import com.PromptToApp.core.service.fileService;
import com.PromptToApp.core.service.usageLogsService;
import com.PromptToApp.core.utils.SystemPrompt;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class aiGenerationServiceImpl implements aiGenerationService {

    private final ChatClient chatClient;

    private final authUtilService authUtilService;

    private final userSubscriptionRepository userSubscriptionRepo;

    private final usageLogsService usageLogsService;

    private final chatService chatService;

    private final fileService fileService;

    private final securityAccessCheck security_access_check;


    private final Pattern chatPattern = Pattern.compile("<chat>(.*?)</chat>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private final Pattern filePattern = Pattern.compile("<file\\s+path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL); // dotall is for multiline matcher


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
     * <p>
     * do onnext will collect the response , like ai assistant response , files complete data
     * we can give entity that each stream object will have a format ( type : assistant_response , message : "" ) and for files ( type : " file update" , file_path : "src/123" , message : "")
     * <p>
     * so on do next we will collect the formatted data
     * <p>
     * on do on complete , we will save the chat , updated ()
     *
     */
    @PreAuthorize("@securityAccessChecker.checkUserAccessAndEditToProject(#projectId)")
    public Flux<String> getChatResponse(UUID projectId, String chatMessage) {

        log.info("I am starting the ai respons service");
        UUID userId = authUtilService.getUserId();
        log.info("got user from auth utils service");
        SubscriptionPlan userSubscriptionPlan = userSubscriptionRepo.getByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("no active user subscription found"));
        Integer max_token_per_day = userSubscriptionPlan.getPlan().getMaxTokenPerDay();

        log.info("sub plan and max token found");

        Integer user_today_token_usage = usageLogsService.getTokenUsedByUser(userId);

        log.info("found token quota on the user {}", user_today_token_usage);

//        daily token quota check
        if (user_today_token_usage.equals(max_token_per_day) || user_today_token_usage > max_token_per_day) {
            throw new customBadRequestException("token usage limit has reached for today");

        }

//        creating object , so that can be used/updated in lambda function
        StringBuilder chat_complete_response = new StringBuilder();
        AtomicInteger totalTokenUsed = new AtomicInteger();


        System.out.println("current thread running in jpa flow" + Thread.currentThread().getName());

        /**
         * chat client details
         * user prompt , system prompt , get project tree
         */

        Flux<ChatResponse> chatResponseFlux = chatClient.prompt()
                .user(chatMessage)
                .advisors(a -> a.param("projectId" , projectId)) // from here we can do task between user prompt given adn llm receiving the prompt
                .stream()
                .chatResponse()
                .doOnNext(stream_message ->
                        {
//                            System.out.println("current thread running in rector flow , do on next" + Thread.currentThread().getName());
//                            log.info("this is he chat stream data {}", stream_message.getResults());

                            /**
                             * we will get the response chunks and see as we get </chat> , we will save and send this chat response to client
                             * we will then collect file data and wait for </file> as we get it , we will save and send to the frontend the file and path
                             * so instead of sending chunks , ui will also wait for complete <chat></chat> then only it will show the data
                             * so instaed we will do it here only and send it to the ui
                             */


//                            so here we can be collecting code and sort which is ai response and which is file.
                            chat_complete_response.append(stream_message.getResult().getOutput().getText());

//                            in this llm , only the last message chat response contains the token consumed
                            if (stream_message.getMetadata().getUsage().getTotalTokens() != 0) {
                                totalTokenUsed.addAndGet(stream_message.getMetadata().getUsage().getTotalTokens());
                                System.out.println(totalTokenUsed);
                            }
                        }

                )
                .doOnComplete(() -> {

                    System.out.println("current thread running in rector flow , do on complete flow" + Thread.currentThread().getName());

//                    3 options
//                        1) run directly saveChanges (it will block the reactor )
//                        2) run using @async (will run by spring mvc executor thread )/ same as future callable
//                        3) run on scheduler (reactor thread pool)

//                    this tasks will happen in background , the client has gotten its response and stream is closed
                    Mono.fromRunnable(() -> {
                                saveChatsAndUpdatedFilesAndAddTokenTokensLogs(chat_complete_response, projectId, userId, totalTokenUsed.get(), chatMessage);
                            }).subscribeOn(Schedulers.boundedElastic())
                            .doOnSubscribe(var -> log.info("saved the chats and files to db"))
                            .doOnError(e -> log.info("getting while saving the chats and file updates -> {}", e.toString()))
                            .retry(0)
                            .subscribe();

                })
                .doOnError(error -> log.error(
                        "we got error in chat stream of project {} this error {}",
                        projectId,
                        error.getLocalizedMessage()
                ));


        return chatResponseFlux.map(data -> data.getResult().getOutput().getText());

    }


    /**
     *
     * @param ai_response complete string that ai generated
     * @param projectId
     * @param user_id     why we are using delimiter(<chat></chat> , <file></file>) format and not json becasue if we use json , so in frontend json parser get chota chota non json chunks which cannt be parsed by
     *                    json parser , so if we wait completed for data to come and then parse it(then only complete json will come) then no point of streaming
     *                    we will get <chat> </chat> as it completes we will show this in ui
     * @return we will get the whole string taht ai has generated , then we will
     * 1) parse the response , get assistance response , complete code file , file path
     * 2) will same assistance response + user prompt in chat entity
     * 3) will save file to min.io
     *
     */
    private void saveChatsAndUpdatedFilesAndAddTokenTokensLogs(StringBuilder ai_response, UUID projectId, UUID user_id, Integer tokenUsed, String userPrompt) {

        /**
         * so the ai_response we will get it
         *
         * <chat> hello i am ai assistance response , save me in chat of project by thi user</chat>
         * <file file_path = "comple file path of the file which got edited"> hello I am the file content , parse me and save me in db</file>

         */

        log.info("so we are trying to save files and chatsx");
        String aiStringResponse = ai_response.toString();

//        this will do both chat saving(assistant response + user prompt) and updating token used by the user prompt
        saveChatsAndUsageLogs(aiStringResponse, userPrompt, projectId, user_id, tokenUsed);

        saveFile(aiStringResponse, projectId, user_id);
    }


    /**
     * we will save file changed by ai assistant
     */
    private void saveFile(String aiStringResponse, UUID projectId, UUID user_id) {

        log.info("I am in file save function {}", aiStringResponse);
//        find file and save it in db
        Matcher fileMatched = filePattern.matcher(aiStringResponse);

//        everytime it finds a match to <file path ="">...</file> , it will return us the data
        while (fileMatched.find()) {
            log.info("we found file matched");
//            getting the file path and file content (at group = 0 is complete <file></file> thing)
            String filePath = fileMatched.group(1);
            String fileContent = fileMatched.group(2).trim();

//            save the file to the project file
            fileService.addOrUpdateFile(projectId, filePath, fileContent ,  user_id);
        }


    }


    /**
     * we will save ai assistant + user prompt chat replied to user message and call/save total token used in usage logs
     */
    private void saveChatsAndUsageLogs(String aiStringResponse, String userPrompt, UUID projectId, UUID user_id, Integer totalTokenUsed) {

        log.info("i am in chat save function {}", aiStringResponse);
//        find file and save it in db
        Matcher chatMatched = chatPattern.matcher(aiStringResponse);

        log.info("I am just after the chat matched {}", chatMatched.toString());

//        everytime it finds a match to <chat>...</chat> , it will return us the data
        while (chatMatched.find()) {
            log.info("found 1 chat");
            String chatMessage = chatMatched.group(1);

//            save chat/ai response
            chatService.saveChat(ChatBy.AI, projectId, chatMessage, totalTokenUsed, user_id);


        }

//        saving user message/prompt
        chatService.saveChat(ChatBy.USER, projectId, userPrompt, totalTokenUsed, user_id);

        //           save token used by user prompt
        usageLogsService.addUserUsageLogs(projectId, user_id, totalTokenUsed, userPrompt);


    }

}
