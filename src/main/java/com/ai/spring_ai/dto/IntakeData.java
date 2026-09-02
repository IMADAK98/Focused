package com.ai.spring_ai.dto;

import java.util.List;

public record IntakeData(
        List<String> selectedHabits,
        List<String> environmentalCues,
        List<String> failurePoints,
        String customNotes) {}
