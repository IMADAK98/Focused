package com.ai.spring_ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI focusedOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Focused Routine Designer API")
                        .version("v1")
                        .description("POC in-memory API for Design → Run → Signal → Adapt. AI drafts never auto-commit a Bottleneck."))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local POC")));
    }
}