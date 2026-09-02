package com.ai.spring_ai.api;

import com.ai.spring_ai.dto.Phase1Request;
import com.ai.spring_ai.dto.Phase1Response;
import com.ai.spring_ai.dto.Phase2Request;
import com.ai.spring_ai.dto.Phase2Response;
import com.ai.spring_ai.service.DiagnosticService;
import com.ai.spring_ai.service.RedesignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/behaviour-loop")
@Tag(name = "ai-drafts")
public class BehaviourLoopController {

    private final DiagnosticService diagnosticService;
    private final RedesignService redesignService;

    public BehaviourLoopController(DiagnosticService diagnosticService, RedesignService redesignService) {
        this.diagnosticService = diagnosticService;
        this.redesignService = redesignService;
    }

    @PostMapping("/phase1")
    public Phase1Response phase1(@RequestBody Phase1Request request) {
        return diagnosticService.diagnose(request);
    }

    @PostMapping("/phase2")
    public Phase2Response phase2(@RequestBody Phase2Request request) {
        return redesignService.redesign(request);
    }
}