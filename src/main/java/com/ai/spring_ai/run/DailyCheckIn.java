package com.ai.spring_ai.run;

import java.time.Instant;
import java.time.LocalDate;

public record DailyCheckIn(
        String id,
        String runId,
        int day,
        LocalDate checkInDate,
        Instant timestamp,
        Boolean success,
        Integer failedStageIndex,
        String frictionTag) {}
