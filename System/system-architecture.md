# System Architecture & Handover

**Status:** Locked through Daily Run (Points 1–4). Point 5 deferred.  
**Last updated:** 2026-08-23

---

## Core stack

| Layer | Choice |
|-------|--------|
| Backend | Java 21, Spring Boot 3, Spring AI (`ChatClient`, `BeanOutputConverter`, `PromptTemplate`) |
| Frontend | Angular (latest), Signals, `@angular/cdk/drag-drop` for Stage reordering |
| Pattern | 2-Phase Diagnostic & Redesign + Daily Execution loop: Design → Run → Signal → Adapt |

---

## Pipeline

```text
[ Intake Data ]
       │
       ▼
[ Phase 1: As-Is Diagnostic ] ──► baseline loop + candidate bottleneck
       │
       ▼
[ Human Calibration ]         ──► user confirms or adjusts bottleneck index
       │
       ▼
[ Phase 2: To-Be Redesign ]   ──► target routine + environment interventions + Outcome
       │
       ▼
[ Daily Run ]                 ──► 7-day calendar, binary Yes/No check-ins
       │
       ▼
[ Day 7 Evaluation ]          ──► deferred (Point 5) — do not implement yet
```

---

## Prompt constraints (Phase 1 and Phase 2)

1. **Strict grounding / zero assumption**
   - Never invent clock times unless the user wrote them in custom notes.
   - Never invent unselected activities, brands, or items.
   - Keep titles, cues, and environments generalized to user inputs / confirmed baseline.

2. **Phase 1 is diagnostic only**
   - `primaryFrictionAnalysis` evaluates stutter in *today’s* loop, not failure against a future ideal.
   - No fixes or interventions in Phase 1.

3. **Contiguous indexing**
   - Stage positions are `0, 1, 2, ..., n-1`.

---

## Phase 1 response (`AsIsLoopResponse`)

```json
{
  "asIsLoop": {
    "stages": [
      {
        "position": 0,
        "title": "string",
        "cue": "string or null",
        "environment": "string or null",
        "currentFriction": "string or null"
      }
    ],
    "candidateBottleneckIndex": 0,
    "primaryFrictionAnalysis": "string"
  }
}
```

---

## Phase 2 response (`ToBeLoopResponse`)

```json
{
  "outcome": {
    "statement": "string",
    "successCriteria": ["string"]
  },
  "toBeLoop": {
    "stages": [
      {
        "position": 0,
        "title": "string",
        "cue": "string or null",
        "environment": "string or null",
        "redesignIntervention": "string"
      }
    ],
    "bottleneckStageIndex": 0,
    "coreStrategy": "string"
  }
}
```

Phase 2 inputs include `confirmedAsIsLoop` and `confirmedBottleneckIndex` from Human Calibration.

---

## Run module

### Point 1 — locked: single active loop

One active `ToBeLoop` per user at a time.

### Point 2 — locked: binary daily check-in

- Yes / No.
- If No: `failedStageIndex`, `frictionTag`.
- Entity `DailyCheckIn`: `id`, `runId`, `checkInDate`, `timestamp`, `success`, `failedStageIndex`, `frictionTag`.

### Point 3 — locked: 7-day calendar

Fixed 7-day window (weekday + weekend rhythm).

### Point 4 — locked: catch-up and auto-miss

- 24-hour grace to log yesterday.
- After that: `success = false`, `frictionTag = "MISSED_CHECKIN"`.
- Missed check-in is not a stage-level friction observation.

### Point 5 — deferred

Do **not** lock or implement Day 7 evaluation, `HABIT_STABILIZED`, `ADAPTATION_REQUIRED`, or Phase 3 until discussed.

Held draft only:

- ≥ 80% (≥ 6/7) → stabilize / switch focus
- < 80% (≤ 5/7) → adaptation required → Phase 3

---

## Next implementation steps (when building)

1. Discuss and lock Point 5 later.
2. Spring Boot records for Phase 1 & Phase 2 + `BeanOutputConverter` services.
3. Angular Signal store and CDK drag-drop UI for As-Is verification / calibration.

Prompt text for Phase 1 and Phase 2 lives in `You are an expert behavioural diagnostic.dart`.
