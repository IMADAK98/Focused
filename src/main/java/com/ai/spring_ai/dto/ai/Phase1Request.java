package com.ai.spring_ai.dto.ai;

import java.util.List;

public record Phase1Request(
        String focusArea,
        List<String> directionChoices,
        List<String> successCriteriaChoices,
        IntakeData intakeData) {}
