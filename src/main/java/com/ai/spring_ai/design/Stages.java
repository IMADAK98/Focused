package com.ai.spring_ai.design;

import java.util.ArrayList;
import java.util.List;

public final class Stages {

    private Stages() {}

    public static List<Stage> contiguous(List<Stage> stages) {
        if (stages == null || stages.isEmpty()) {
            return List.of();
        }
        List<Stage> reindexed = new ArrayList<>(stages.size());
        for (int i = 0; i < stages.size(); i++) {
            Stage stage = stages.get(i);
            reindexed.add(new Stage(
                    i,
                    stage.title(),
                    stage.cue(),
                    stage.environment(),
                    stage.currentFriction(),
                    stage.redesignIntervention()));
        }
        return List.copyOf(reindexed);
    }

    public static void requireAtLeastTwo(List<Stage> stages) {
        if (stages == null || stages.size() < 2) {
            throw new IllegalArgumentException("A BehaviourLoop needs at least two Stages");
        }
    }

    public static void requireIndexInRange(int index, List<Stage> stages, String fieldName) {
        if (index < 0 || index >= stages.size()) {
            throw new IllegalArgumentException(fieldName + " must point at a Stage in this loop");
        }
    }
}