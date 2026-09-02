# Domain Invariants — Personal Behaviour Systems

**Status:** Locked through Daily Run (Points 1–4). Point 5 deferred.  
**Last updated:** 2026-08-23  
**Depends on:** `ubiquitous-language.md`, `system-architecture.md`

Invariants are rules that must stay true. If a feature breaks one, either the feature is wrong or the invariant must be explicitly revised.

---

## A. Scope

1. The product redesigns **one FocusArea at a time**. The primary flow assumes one active focus.
2. The main user-facing object is a **BehaviourLoop** as an editable flow of **Stages** (interactive blocks) — not a knowledge graph of nouns.
3. Users **choose and confirm**; blank-page writing is never required to complete onboarding.
4. Architecture is **Design → Run → Signal → Adapt**:
   - Phase 1: As-Is Diagnostic
   - Human Calibration
   - Phase 2: To-Be Redesign
   - Daily Run (7-day cycle)
   - Phase 3 Adaptation is **not locked** (see Point 5).

---

## B. FocusArea

5. A FocusArea belongs to exactly one User.
6. A FocusArea has a name/identity chosen from a catalog or equivalent simple selection.
7. **Single active To-Be loop (Point 1, locked):** a User has strictly **one active `ToBeLoop`** at a time.

---

## C. Outcome

8. Outcome is the **definition of the aim** — not today’s success score.
9. Outcome has a **statement** (one primary desired result) and **successCriteria** (2–4 observable items).
10. Outcome must be **user-confirmable**. It need not be user-authored.
11. **Generation order (supersedes earlier draft):** Outcome is synthesized in **Phase 2**, after the As-Is loop and bottleneck are human-confirmed. Intake direction/criteria chips are *inputs* to that synthesis, not a pre-loop Outcome commit.
12. Outcome does **not** store a live success percentage, weight, or daily grade.
13. UI may hide the word “Outcome”; plain copy is allowed.

---

## D. BehaviourLoop & Stage

14. A BehaviourLoop is an **ordered** sequence of Stages.
15. A BehaviourLoop has at least **two** Stages.
16. **Ordering:** each Stage has an integer `position` unique within its loop. Next Stage is `position + 1`. No edge table in POC.
17. **Contiguous indexing:** after any edit, positions are `0, 1, 2, ..., n-1` with no gaps.
18. A Stage is one interactive block. **Required:** title, loop membership, position. **Optional:** cue, environment. Friction / intervention fields depend on loop role (below).
19. **Two loop roles, one Stage component:**
    - **AsIsLoop** (Phase 1): current baseline. Stage may have `currentFriction`.
    - **ToBeLoop** (Phase 2): redesigned target. Stage has `redesignIntervention`.
20. A **decision point is a Stage**. Branching is out of POC; linear order only.
21. No hard Cue/Environment constraints by Stage type. Templates may suggest; the domain does not require them.

---

## E. Bottleneck

22. A loop has **at most one** current Bottleneck (forced prioritization).
23. When set, Bottleneck **points at one Stage** in that same loop (by contiguous index).
24. Phase 1 produces a **candidate** (`candidateBottleneckIndex` + `primaryFrictionAnalysis`).
25. **Human Calibration** is required before Phase 2: the user confirms or adjusts the bottleneck index. Phase 2 receives `confirmedBottleneckIndex`.
26. Phase 1 friction analysis evaluates **where energy/time stutters in today’s current loop**, not failure against a future ideal.

---

## F. Measurement — Daily Run (Points 2–4, locked)

27. Daily degree of success lives on **DailyCheckIn** (the first Signal), not on Outcome.
28. **Point 2 — Binary check-in:** one tap, **Yes** (success) or **No** (failure). If No, capture `failedStageIndex` and `frictionTag`.
29. **DailyCheckIn** fields: `id`, `runId`, `checkInDate`, `timestamp`, `success`, `failedStageIndex`, `frictionTag`.
30. **Point 3 — Fixed 7-day calendar cycle:** one Run window covers 7 days (weekday + weekend rhythm).
31. **Point 4 — Catch-up & auto-miss:** 24-hour grace to log yesterday. After that, unlogged days default to `success = false`, `frictionTag = "MISSED_CHECKIN"`.
32. `MISSED_CHECKIN` counts against adherence. It is **not** a stage-level friction observation (`failedStageIndex` is absent).

---

## G. AI / prompt rules

33. AI **proposes**; the user (or explicit confirm) **commits**.
34. Drafts map to domain objects (AsIsLoop / Outcome + ToBeLoop), not a generic node/relationship graph.
35. **Zero-assumption / strict grounding (all phases that exist):**
    - Never invent clock times unless the user wrote them in custom notes.
    - Never invent unselected activities, brands, or items.
    - Keep titles, cues, and environments generalized to intake / confirmed baseline.
36. Phase 1 must not suggest fixes. Phase 2 redesigns around the confirmed bottleneck.
37. LLM does not continuously grade Outcome as pass/fail.

---

## H. Point 5 — Day 7 evaluation (deferred)

**Not locked.** Do not implement Phase 3 trigger, `HABIT_STABILIZED`, or `ADAPTATION_REQUIRED` until confirmed.

Held draft (for later discussion only):

- ≥ 80% adherence (≥ 6/7) → stabilize / switch focus
- < 80% (≤ 5/7) → adaptation required → Phase 3 micro-adjustments

---

## I. Creation pipeline (intake → draft → confirm)

38. The user does not blank-build Stages from scratch.
39. Onboarding order:
    1. FocusArea (tap)
    2. Direction / success-criteria chips (intake inputs, not a committed Outcome yet)
    3. Current-day / behaviour intake (chips; optional note)
    4. **Phase 1** generates AsIsLoop + candidate bottleneck
    5. **Human Calibration** — confirm/adjust bottleneck (and edit As-Is blocks)
    6. **Phase 2** generates Outcome + ToBeLoop
    7. User edits To-Be blocks → confirm → this becomes the single active loop
    8. Daily check-ins for the 7-day Run
40. Stage generation input for Phase 1 = FocusArea + direction/criteria choices + behaviour intake. Not a committed Outcome.
41. After To-Be confirm, edits are normal Stage edits unless the user regenerates a draft.

```text
[ Intake: area, direction chips, day/behaviour chips ]
                    │
                    ▼
[ Phase 1: As-Is Diagnostic ] ──► asIsLoop + candidateBottleneckIndex
                    │
                    ▼
[ Human Calibration ]         ──► confirmed bottleneck + edited baseline
                    │
                    ▼
[ Phase 2: To-Be Redesign ]   ──► outcome + toBeLoop
                    │
                    ▼
[ Daily Run ]                 ──► 7-day Yes/No check-ins
                    │
                    ▼
[ Day 7 Evaluation ]          ──► deferred (Point 5)
```

---

## J. Explicitly out of invariants (for now)

- Multiple active ToBeLoops per user
- Cross-FocusArea graphs
- Formal Intervention / Experiment aggregates beyond `redesignIntervention` on a Stage
- Top-level Constraint / Resource entities
- Required free-text authorship
- Branching / non-linear Stage graphs
- Locked Phase 3 / Point 5 state machine

---

## Document history

| Date | Change |
|------|--------|
| 2026-07-30 | Initial invariants from UL + Outcome UX |
| 2026-07-30 | Loop/Stage ordering, contiguity, optional fields |
| 2026-07-30 | Onboarding → draft → confirm |
| 2026-08-23 | Two-phase As-Is / To-Be pipeline; Outcome moves to Phase 2; Daily Run Points 1–4 locked; Point 5 deferred |
