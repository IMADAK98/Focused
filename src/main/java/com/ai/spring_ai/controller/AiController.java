package com.ai.spring_ai.controller;

import com.ai.spring_ai.dto.ai.TestOutput;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ConditionalOnProperty(name = "focused.ai.enabled", havingValue = "true")
public class AiController {

    private final ChatModel chatModel;
    private final ChatClient chatClient;

    // ponytail: Spring AI auto-configures ChatClient.Builder, not ChatClient
    public AiController(ChatModel chatModel, ChatClient.Builder chatClientBuilder) {
        this.chatModel = chatModel;
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a fun fact") String message) {
        return chatModel.call(message);
    }

    @GetMapping("/ai/test")
    public List<TestOutput> test(@RequestParam(value = "message") String userInput) {
        return chatClient.prompt()
                .user(u-> u.text("Recommend 2 morning habits for a person with profession {profession}").param("profession", userInput))
                .call()
                .entity(new ParameterizedTypeReference<List<TestOutput>>(){});
    }
}
