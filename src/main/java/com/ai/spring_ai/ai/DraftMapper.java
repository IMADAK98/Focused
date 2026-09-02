package com.ai.spring_ai.ai;

import com.ai.spring_ai.design.AsIsLoop;
import com.ai.spring_ai.design.Intake;
import com.ai.spring_ai.design.Outcome;
import com.ai.spring_ai.design.Stage;
import com.ai.spring_ai.design.ToBeLoop;
import com.ai.spring_ai.dto.AsIsStage;
import com.ai.spring_ai.dto.IntakeData;

import java.util.List;

public final class DraftMapper {

    private DraftMapper() {}

    public static IntakeData toIntakeData(Intake intake) {
        return new IntakeData(
                intake.selectedHabits(),
                intake.environmentalCues(),
                intake.failurePoints(),
                intake.customNotes());
    }

    public static AsIsLoop toDomain(com.ai.spring_ai.dto.AsIsLoop draft) {
        List<Stage> stages = draft.stages().stream()
                .map(stage -> new Stage(
                        stage.position(),
                        stage.title(),
                        stage.cue(),
                        stage.environment(),
                        stage.currentFriction(),
                        null))
                .toList();
        return new AsIsLoop(stages, draft.candidateBottleneckIndex(), draft.primaryFrictionAnalysis());
    }

    public static List<AsIsStage> toAsIsStages(List<Stage> stages) {
        return stages.stream()
                .map(stage -> new AsIsStage(
                        stage.position(),
                        stage.title(),
                        stage.cue(),
                        stage.environment(),
                        stage.currentFriction()))
                .toList();
    }

    public static ToBeLoop toDomain(String id, com.ai.spring_ai.dto.ToBeLoop draft) {
        List<Stage> stages = draft.stages().stream()
                .map(stage -> new Stage(
                        stage.position(),
                        stage.title(),
                        stage.cue(),
                        stage.environment(),
                        null,
                        stage.redesignIntervention()))
                .toList();
        return new ToBeLoop(id, stages, draft.bottleneckStageIndex(), draft.coreStrategy());
    }

    public static Outcome toDomain(com.ai.spring_ai.dto.Outcome draft) {
        return new Outcome(draft.statement(), draft.successCriteria());
    }
}