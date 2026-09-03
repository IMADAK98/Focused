package com.ai.spring_ai.run;

import com.ai.spring_ai.design.FocusArea;
import com.ai.spring_ai.repository.design.InMemoryFocusAreaStore;
import com.ai.spring_ai.design.PipelineStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RunService {

    static final String MISSED_CHECKIN = "MISSED_CHECKIN";

    private final InMemoryFocusAreaStore store;
    private final Clock clock;

    public RunService(InMemoryFocusAreaStore store, Clock clock) {
        this.store = store;
        this.clock = clock;
    }

    public Run createEmptyRun(String toBeLoopId) {
        LocalDate start = LocalDate.now(clock);
        String runId = UUID.randomUUID().toString();
        List<DailyCheckIn> checkIns = new ArrayList<>();
        for (int day = 1; day <= 7; day++) {
            LocalDate date = start.plusDays(day - 1);
            checkIns.add(new DailyCheckIn(UUID.randomUUID().toString(), runId, day, date, null, null, null, null));
        }
        return new Run(runId, toBeLoopId, start, start.plusDays(6), checkIns);
    }

    public Run get(String focusAreaId) {
        FocusArea focusArea = store.require(focusAreaId);
        Run run = requireRun(focusArea);
        Run withMisses = applyAutoMiss(run);
        if (withMisses != run) {
            store.save(focusArea.withRun(withMisses, PipelineStatus.RUN));
        }
        return withMisses;
    }

    public Run submitCheckIn(String focusAreaId, int day, boolean success, Integer failedStageIndex, String frictionTag) {
        if (day < 1 || day > 7) {
            throw new IllegalArgumentException("day must be 1-7");
        }
        FocusArea focusArea = store.require(focusAreaId);
        Run run = applyAutoMiss(requireRun(focusArea));
        DailyCheckIn current = run.dailyCheckIns().get(day - 1);
        if (!success && MISSED_CHECKIN.equals(frictionTag)) {
            throw new IllegalArgumentException("MISSED_CHECKIN is system-assigned, not a user friction tag");
        }
        DailyCheckIn updated = new DailyCheckIn(
                current.id(),
                run.id(),
                day,
                current.checkInDate(),
                Instant.now(clock),
                success,
                success ? null : failedStageIndex,
                success ? null : frictionTag);
        List<DailyCheckIn> next = new ArrayList<>(run.dailyCheckIns());
        next.set(day - 1, updated);
        Run saved = run.withCheckIns(next);
        store.save(focusArea.withRun(saved, PipelineStatus.RUN));
        return saved;
    }

    private Run applyAutoMiss(Run run) {
        // 24-hour grace for yesterday; older unlogged days become MISSED_CHECKIN.
        LocalDate today = LocalDate.now(clock);
        LocalDate graceCutoff = today.minusDays(1);
        boolean changed = false;
        List<DailyCheckIn> next = new ArrayList<>(run.dailyCheckIns().size());
        for (DailyCheckIn checkIn : run.dailyCheckIns()) {
            if (checkIn.success() == null && checkIn.checkInDate().isBefore(graceCutoff)) {
                next.add(new DailyCheckIn(
                        checkIn.id(),
                        checkIn.runId(),
                        checkIn.day(),
                        checkIn.checkInDate(),
                        Instant.now(clock),
                        false,
                        null,
                        MISSED_CHECKIN));
                changed = true;
            } else {
                next.add(checkIn);
            }
        }
        return changed ? run.withCheckIns(next) : run;
    }

    private static Run requireRun(FocusArea focusArea) {
        if (focusArea.run() == null) {
            throw new IllegalStateException("Run does not exist until To-Be is confirmed");
        }
        return focusArea.run();
    }
}