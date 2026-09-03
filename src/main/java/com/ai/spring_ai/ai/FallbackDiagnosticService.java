package com.ai.spring_ai.ai;

import com.ai.spring_ai.dto.ai.AsIsLoop;
import com.ai.spring_ai.dto.ai.AsIsStage;
import com.ai.spring_ai.dto.ai.IntakeData;
import com.ai.spring_ai.dto.ai.Phase1Request;
import com.ai.spring_ai.dto.ai.Phase1Response;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "focused.ai.enabled", havingValue = "false", matchIfMissing = true)
public class FallbackDiagnosticService implements DiagnosticService {

    @Override
    public Phase1Response diagnose(Phase1Request request) {
        IntakeData intake = request.intakeData();
        List<String> habits = labels(intake == null ? null : intake.selectedHabits());
        if (habits.size() < 2) {
            habits = List.of("Start the current routine", "Stall before the intended next step");
        }
        List<String> cues = labels(intake == null ? null : intake.environmentalCues());
        List<String> failures = labels(intake == null ? null : intake.failurePoints());

        List<AsIsStage> stages = new ArrayList<>();
        for (int i = 0; i < habits.size(); i++) {
            String friction = i < failures.size() ? failures.get(i) : null;
            String cue = i < cues.size() ? cues.get(i) : (cues.isEmpty() ? null : cues.get(i % cues.size()));
            stages.add(new AsIsStage(i, habits.get(i), cue, null, friction));
        }

        int candidate = 0;
        for (int i = 0; i < stages.size(); i++) {
            if (stages.get(i).currentFriction() != null && !stages.get(i).currentFriction().isBlank()) {
                candidate = i;
                break;
            }
        }
        String analysis = stages.get(candidate).currentFriction() == null
                ? "The routine loses momentum at '" + stages.get(candidate).title() + "'."
                : "The routine stalls at '" + stages.get(candidate).title() + "': "
                        + stages.get(candidate).currentFriction();
        return new Phase1Response(new AsIsLoop(stages, candidate, analysis));
    }

    private static List<String> labels(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream().filter(value -> value != null && !value.isBlank()).toList();
    }
}