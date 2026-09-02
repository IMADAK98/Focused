package com.ai.spring_ai.api;

import com.ai.spring_ai.adapt.AdaptPreview;
import com.ai.spring_ai.adapt.AdaptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/focus-areas/{id}/adapt")
@Tag(name = "adapt")
public class AdaptController {

    private final AdaptService adaptService;

    public AdaptController(AdaptService adaptService) {
        this.adaptService = adaptService;
    }

    @GetMapping
    @Operation(summary = "Thin Day-7 Signal→Adapt stub (Point 5 deferred)")
    public AdaptPreview preview(@PathVariable String id) {
        return adaptService.preview(id);
    }
}