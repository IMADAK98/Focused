package com.ai.spring_ai.api;

import com.ai.spring_ai.identity.IdentityService;
import com.ai.spring_ai.identity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "identity")
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping("/me")
    @Operation(summary = "Stub current User (no auth)")
    public User me() {
        return identityService.currentUser();
    }
}