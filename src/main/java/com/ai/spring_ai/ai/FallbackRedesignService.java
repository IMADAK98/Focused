package com.ai.spring_ai.ai;

import com.ai.spring_ai.dto.AsIsStage;
import com.ai.spring_ai.dto.ConfirmedAsIsLoop;
import com.ai.spring_ai.dto.Outcome;
import com.ai.spring_ai.dto.Phase2Request;
import com.ai.spring_ai.dto.Phase2Response;
import com.ai.spring_ai.dto.ToBeLoop;
import com.ai.spring_ai.dto.ToBeStage;
import com.ai.spring_ai.service.RedesignService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "focused.ai.enabled", havingValue = "false", matchIfMissing = true)
public class FallbackRedesignService implements RedesignService {

    @Override
    public Phase2Response redesign(Phase2Request request) {
        ConfirmedAsIsLoop confirmed = request.confirmedAsIsLoop();
        List<AsIsStage> baseline = confirmed == null || confirmed.stages() == null ? List.of() : confirmed.stages();
        if (baseline.size() < 2) {
            throw new IllegalArgumentException("Confirmed As-Is needs at least two Stages");
        }
        int bottleneck = request.confirmedBottleneckIndex();
        List<ToBeStage> stages = new ArrayList<>();
        for (AsIsStage stage : baseline) {
            boolean isBottleneck = stage.position() == bottleneck;
            String intervention = isBottleneck
                    ? "Change the environment around this bottleneck so the stall is harder to repeat."
                    : "Keep this step, and make the next action obvious from the current environment.";
            stages.add(new ToBeStage(
                    stage.position(),
                    stage.title(),
                    stage.cue(),
                    stage.environment(),
                    intervention));
        }
        List<String> criteria = request.successCriteriaChoices() == null || request.successCriteriaChoices().isEmpty()
                ? List.of("Complete the redesigned loop without the old stall", "Notice less friction at the bottleneck")
                : request.successCriteriaChoices().stream().limit(4).toList();
        String statement = request.directionChoices() == null || request.directionChoices().isEmpty()
                ? "A calmer loop that does not stall at the confirmed bottleneck."
                : request.directionChoices().getFirst();
        Outcome outcome = new Outcome(statement, criteria);
        ToBeLoop toBeLoop = new ToBeLoop(
                stages,
                bottleneck,
                "Redesign the environment at stage " + bottleneck + " rather than adding willpower.");
        return new Phase2Response(outcome, toBeLoop);
    }
}