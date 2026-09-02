package com.ai.spring_ai.design;

import com.ai.spring_ai.identity.IdentityService;
import com.ai.spring_ai.run.DailyCheckIn;
import com.ai.spring_ai.run.Run;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

final class MorningEnergySeed {

    static final String ID = "seed-morning-energy";

    private MorningEnergySeed() {}

    static FocusArea create(LocalDate startDate) {
        Intake intake = new Intake(
                List.of(
                        "Wake up with more energy",
                        "Stop hitting snooze repeatedly",
                        "Have a calmer, less rushed start to the day"),
                List.of(
                        "Out of bed within 10 minutes of the first alarm",
                        "No phone scrolling before getting out of bed",
                        "Leave the house feeling unrushed"),
                List.of(
                        "Hit snooze multiple times",
                        "Check phone immediately after waking",
                        "Decide what to wear at the last minute",
                        "Skip breakfast most days"),
                List.of(
                        "Phone alarm is within arm's reach",
                        "Room stays dark until curtains are opened",
                        "Clothes are not laid out the night before"),
                List.of(
                        "Falling back asleep after snoozing",
                        "Losing time scrolling social media in bed",
                        "Rushing out the door without breakfast"),
                "Mornings feel worse on days I go to bed late the night before.",
                List.of(
                        new IntakeChip("1", "Waking up groggy", true),
                        new IntakeChip("2", "Scattered mornings", true),
                        new IntakeChip("3", "Phone first thing", true),
                        new IntakeChip("4", "No breakfast routine", false),
                        new IntakeChip("5", "Late to meetings", false)));

        AsIsLoop asIsLoop = new AsIsLoop(
                List.of(
                        new Stage(0, "Alarm goes off", "Phone alarm sound", "In bed, room still dark", null, null),
                        new Stage(1, "Snooze the alarm", "Alarm within arm's reach", "In bed",
                                "Presses snooze multiple times before getting up", null),
                        new Stage(2, "Check phone in bed", "Phone already in hand after snoozing", "In bed, room dark",
                                "Loses track of time scrolling", null),
                        new Stage(3, "Get out of bed", "Running out of buffer time", "Bedroom", null, null),
                        new Stage(4, "Decide what to wear", "Standing in front of closet", "Bedroom, no clothes prepared",
                                "Takes long deciding last minute", null),
                        new Stage(5, "Leave without breakfast", "Already running late", "Kitchen / front door",
                                "Skips eating to save time", null)),
                2,
                "Phone already in hand after snoozing turns wake-up into a scroll stall before any physical momentum starts.");

        Bottleneck bottleneck = new Bottleneck(2, asIsLoop.primaryFrictionAnalysis(), 2);

        Outcome outcome = new Outcome(
                "Start the day upright and unrushed, without a phone-in-bed stall.",
                List.of(
                        "Out of bed within 10 minutes of the first alarm",
                        "No phone scrolling before getting out of bed",
                        "Leave the house feeling unrushed"));

        String toBeLoopId = "seed-morning-energy-to-be";
        ToBeLoop toBeLoop = new ToBeLoop(
                toBeLoopId,
                List.of(
                        new Stage(0, "Alarm goes off", "Phone alarm across the room", "Bedroom, curtains cracked", null,
                                "Move the charger out of arm's reach the night before."),
                        new Stage(1, "Stand up on first alarm", "Having to walk to silence the alarm", "Bedroom floor", null,
                                "Keep a glass of water next to the alarm so standing has an immediate next action."),
                        new Stage(2, "Leave the phone face down", "Phone stays at the charger", "Away from the bed", null,
                                "Do not pick up the phone until after leaving the bedroom."),
                        new Stage(3, "Open curtains and get dressed", "Daylight + clothes laid out", "Bedroom", null,
                                "Lay out clothes the night before so dress is not a decision."),
                        new Stage(4, "Eat a simple breakfast", "Kettle or toaster as the kitchen cue", "Kitchen", null,
                                "Keep one default breakfast option visible on the counter."),
                        new Stage(5, "Leave with buffer", "Bag packed by the door", "Front door", null,
                                "Pack bag the night before so leaving is not a scramble.")),
                2,
                "Break the in-bed phone loop by forcing a stand-up to silence the alarm, then keep the phone at the charger.");

        String runId = "seed-morning-energy-run";
        List<DailyCheckIn> checkIns = List.of(
                emptyCheckIn(runId, 1, startDate),
                emptyCheckIn(runId, 2, startDate.plusDays(1)),
                emptyCheckIn(runId, 3, startDate.plusDays(2)),
                emptyCheckIn(runId, 4, startDate.plusDays(3)),
                emptyCheckIn(runId, 5, startDate.plusDays(4)),
                emptyCheckIn(runId, 6, startDate.plusDays(5)),
                emptyCheckIn(runId, 7, startDate.plusDays(6)));
        Run run = new Run(runId, toBeLoopId, startDate, startDate.plusDays(6), checkIns);

        return new FocusArea(
                ID,
                IdentityService.STUB_USER.id(),
                "Morning Energy",
                "Build sustainable morning momentum",
                PipelineStatus.RUN,
                intake,
                asIsLoop,
                bottleneck,
                outcome,
                toBeLoop,
                run);
    }

    private static DailyCheckIn emptyCheckIn(String runId, int day, LocalDate date) {
        return new DailyCheckIn(UUID.randomUUID().toString(), runId, day, date, null, null, null, null);
    }
}