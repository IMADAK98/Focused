package com.ai.spring_ai.dto.run;

public record CheckInRequest(boolean success, Integer failedStageIndex, String frictionTag) {}
