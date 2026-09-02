package com.ai.spring_ai.service_impl;

import com.ai.spring_ai.dto.Phase1Request;
import com.ai.spring_ai.dto.Phase1Response;
import com.ai.spring_ai.service.DiagnosticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Phase 1 — As-Is diagnostic. Maps today's actual routine and surfaces a candidate
 * bottleneck. Diagnosis only; no fixes (invariants.md #36).
 */
@Service
public class DiagnosticServiceImpl implements DiagnosticService {

    private final ChatClient chatClient;
    private final PromptTemplate promptTemplate;
    private final BeanOutputConverter<Phase1Response> outputConverter =
            new BeanOutputConverter<>(Phase1Response.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DiagnosticServiceImpl(
            ChatClient.Builder chatClientBuilder,
            @Value("classpath:/prompts/phase1-prompt.st") Resource phase1PromptResource) {
        this.chatClient = chatClientBuilder.build();
        this.promptTemplate = PromptTemplate.builder()
                .resource(phase1PromptResource)
                .renderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .build();
    }

    @Override
    public Phase1Response diagnose(Phase1Request request) {
        Prompt prompt = promptTemplate.create(Map.of("inputJson", toJson(request)));
        String response = chatClient.prompt(prompt).call().content();
        return outputConverter.convert(response);
    }

    private String toJson(Phase1Request request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize Phase 1 request", e);
        }
    }
}
