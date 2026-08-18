package com.PromptToApp.core.configuration;

import com.PromptToApp.core.advisors_and_tools.getFileContentAdvisorTool;
import com.PromptToApp.core.advisors_and_tools.projectTreeAdvisor;
import com.openai.core.Timeout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class aiConfiguration {

    private final projectTreeAdvisor projectTreeAdvisor;
    private final getFileContentAdvisorTool getFileContentAdvisorTool;


    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        log.info("I am in chat client");
        return builder
                .defaultAdvisors(projectTreeAdvisor) //registering the advisor , so that every llm call passes through this advisor and project file tree in system prompt context
                .defaultTools(getFileContentAdvisorTool)
                .build();
    }


    @Bean
    public OpenAiHttpClientBuilderCustomizer timeoutCustomizer() {
        return builder -> builder.timeout(
                Timeout.builder()
                        .connect(Duration.ofSeconds(30))
                        .read(Duration.ofMinutes(3))
                        .write(Duration.ofSeconds(30))
                        .request(Duration.ofMinutes(10))
                        .build()
        );
    }

//    @Bean
//    public SpringAiOpenAiHttpClient.Builder springAiOpenAiHttpClientBuilder() {
//        // Sets both connect and read timeouts for the underlying OpenAI SDK client
//        return SpringAiOpenAiHttpClient.builder()
//                .timeout(Duration.ofMinutes(3));
//    }

//    it is cofigured to 60 65 sec only , this 2 min is not

//    free models doing before 60 sec , no they were taking more than 60 sec sometimes

//    first undertsnd where is this error coming from

//    then where is this 2 min appliying

//    then how to make the timing to 3 4 min
}
