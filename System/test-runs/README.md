# Test Runs — Morning Routine & Fitness

Four files, each a bare JSON object matching the `INPUT DATA` fields declared in `You are an expert behavioural diagnostic.dart` — no wrapper keys, no API envelope, no model/roles.

| File | Pairs with | Fields |
|------|-----------|--------|
| `morning-routine.phase1.json` | Phase 1 prompt | `focusArea`, `directionChoices`, `successCriteriaChoices`, `intakeData` |
| `morning-routine.phase2.json` | Phase 2 prompt | `confirmedAsIsLoop`, `confirmedBottleneckIndex`, `directionChoices`, `successCriteriaChoices` |
| `fitness.phase1.json` | Phase 1 prompt | same shape as above |
| `fitness.phase2.json` | Phase 2 prompt | same shape as above |

The `confirmedAsIsLoop` in each `*.phase2.json` is a hand-authored, plausible Phase 1 result — so Phase 2 can be tested standalone, without a live Phase 1 call first.

## How to use

Pair the file's contents with the matching prompt text from `You are an expert behavioural diagnostic.dart`, using whatever mechanism you're testing with (Spring AI, a script, a playground). No request envelope is prescribed here — that's a client concern, not part of the prompt contract.

## Invariant checklist while reviewing output

- [ ] Stage `position` values are `0..n-1`, no gaps
- [ ] No invented clock times unless present in `customNotes`
- [ ] No invented brands/activities beyond what was selected
- [ ] Phase 1: no fixes/interventions suggested, only diagnosis
- [ ] Phase 1: exactly one `candidateBottleneckIndex`, with a 1-sentence `primaryFrictionAnalysis`
- [ ] Phase 2: `toBeLoop` stage count/order roughly mirrors the confirmed baseline
- [ ] Phase 2: `redesignIntervention` present on every stage, most specific at the bottleneck
- [ ] Point 5 / Phase 3 is **not** exercised by these payloads — deferred per `invariants.md`
