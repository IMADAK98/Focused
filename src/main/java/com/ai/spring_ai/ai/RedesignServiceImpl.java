package com.ai.spring_ai.ai;

import com.ai.spring_ai.dto.ai.Phase2Request;
import com.ai.spring_ai.dto.ai.Phase2Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Phase 2 — To-Be redesign + Outcome. Redesigns around the human-confirmed bottleneck
 * (invariants.md #25, #36). Runs only after Human Calibration.
 */
@Service
@ConditionalOnProperty(name = "focused.ai.enabled", havingValue = "true")
public class RedesignServiceImpl implements RedesignService {

    private final ChatClient chatClient;
    private final PromptTemplate promptTemplate;
    private final BeanOutputConverter<Phase2Response> outputConverter =
            new BeanOutputConverter<>(Phase2Response.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedesignServiceImpl(
            ChatClient.Builder chatClientBuilder,
            @Value("classpath:/prompts/phase2-prompt.st") Resource phase2PromptResource) {
        this.chatClient = chatClientBuilder.build();
        this.promptTemplate = PromptTemplate.builder()
                .resource(phase2PromptResource)
                .renderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .build();
    }

    @Override
    public Phase2Response redesign(Phase2Request request) {
        Prompt prompt = promptTemplate.create(Map.of("inputJson", toJson(request)));
        String response = chatClient.prompt(prompt).call().content();
        return outputConverter.convert(response);
    }

    private String toJson(Phase2Request request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize Phase 2 request", e);
        }
    }
}
