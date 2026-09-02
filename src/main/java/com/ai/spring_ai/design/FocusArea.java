package com.ai.spring_ai.design;

import com.ai.spring_ai.run.Run;

public record FocusArea(
        String id,
        String userId,
        String name,
        String description,
        PipelineStatus status,
        Intake intake,
        AsIsLoop asIsLoop,
        Bottleneck bottleneck,
        Outcome outcome,
        ToBeLoop toBeLoop,
        Run run) {

    public FocusArea withStatus(PipelineStatus next) {
        return new FocusArea(id, userId, name, description, next, intake, asIsLoop, bottleneck, outcome, toBeLoop, run);
    }

    public FocusArea withIntake(Intake next) {
        return new FocusArea(id, userId, name, description, status, next, asIsLoop, bottleneck, outcome, toBeLoop, run);
    }

    public FocusArea withAsIs(AsIsLoop next, Bottleneck nextBottleneck, PipelineStatus nextStatus) {
        return new FocusArea(id, userId, name, description, nextStatus, intake, next, nextBottleneck, outcome, toBeLoop, run);
    }

    public FocusArea withBottleneck(Bottleneck next, AsIsLoop nextAsIs, PipelineStatus nextStatus) {
        return new FocusArea(id, userId, name, description, nextStatus, intake, nextAsIs, next, outcome, toBeLoop, run);
    }

    public FocusArea withToBe(ToBeLoop nextToBe, Outcome nextOutcome, PipelineStatus nextStatus) {
        return new FocusArea(id, userId, name, description, nextStatus, intake, asIsLoop, bottleneck, nextOutcome, nextToBe, run);
    }

    public FocusArea withRun(Run next, PipelineStatus nextStatus) {
        return new FocusArea(id, userId, name, description, nextStatus, intake, asIsLoop, bottleneck, outcome, toBeLoop, next);
    }
}