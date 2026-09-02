package com.ai.spring_ai.api;

import com.ai.spring_ai.run.Run;
import com.ai.spring_ai.run.RunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/focus-areas/{id}/run")
@Tag(name = "run")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    @GetMapping
    @Operation(summary = "Get the 7-day Run and DailyCheckIns")
    public Run get(@PathVariable String id) {
        return runService.get(id);
    }

    @GetMapping("/check-ins")
    @Operation(summary = "List DailyCheckIns for days 1-7")
    public Run listCheckIns(@PathVariable String id) {
        return runService.get(id);
    }

    @PutMapping("/check-ins/{day}")
    @Operation(summary = "Submit a Yes/No DailyCheckIn for day 1-7")
    public Run submit(@PathVariable String id, @PathVariable int day, @RequestBody CheckInRequest request) {
        return runService.submitCheckIn(id, day, request.success(), request.failedStageIndex(), request.frictionTag());
    }
}