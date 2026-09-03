package com.ai.spring_ai.dto.ai;

import java.util.List;

public record IntakeData(
        List<String> selectedHabits,
        List<String> environmentalCues,
        List<String> failurePoints,
        String customNotes) {}
