package com.ai.spring_ai.design;

import java.util.List;

public record Intake(
        List<String> directionChoices,
        List<String> successCriteriaChoices,
        List<String> selectedHabits,
        List<String> environmentalCues,
        List<String> failurePoints,
        String customNotes,
        List<IntakeChip> chips) {

    public Intake {
        directionChoices = copy(directionChoices);
        successCriteriaChoices = copy(successCriteriaChoices);
        selectedHabits = copy(selectedHabits);
        environmentalCues = copy(environmentalCues);
        failurePoints = copy(failurePoints);
        chips = chips == null ? List.of() : List.copyOf(chips);
    }

    private static List<String> copy(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}