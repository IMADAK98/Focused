package com.ai.spring_ai.api;

public record CheckInRequest(boolean success, Integer failedStageIndex, String frictionTag) {}
