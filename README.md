# Focused

Personal behaviour app backend (Routine Designer): **Design → Run → Signal → Adapt**.

## Run the POC

No Postgres, Flyway, or LLM key required. Default profile is `poc` (in-memory store).

```bash
./mvnw spring-boot:run
```

- Base URL: `http://localhost:8080`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

CORS is open for Angular at `http://localhost:4200`.

Seeded session for deep-links: `GET /api/v1/focus-areas/seed-morning-energy`

Optional live Gemini drafts:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--focused.ai.enabled=true --spring.ai.google.genai.api-key=YOUR_KEY"
```

Do not enable `focused.ai.enabled` without a real key. Controllers never call the model directly; `ai` adapters do.

## `/api/v1` endpoints

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/v1/me` | Stub User (no auth) |
| GET | `/api/v1/focus-area-catalog` | Catalog for FocusArea screen |
| GET | `/api/v1/focus-areas` | List sessions |
| POST | `/api/v1/focus-areas` | Create FocusArea session |
| GET | `/api/v1/focus-areas/{id}` | Full aggregate |
| GET/PUT | `/api/v1/focus-areas/{id}/intake` | Get / submit Intake |
| GET/PUT | `/api/v1/focus-areas/{id}/as-is` | Get / save As-Is Stages |
| POST | `/api/v1/focus-areas/{id}/as-is/draft` | AI As-Is draft (candidate Bottleneck only) |
| GET/POST | `/api/v1/focus-areas/{id}/calibration` | Human Calibration — confirm Bottleneck |
| GET/PUT | `/api/v1/focus-areas/{id}/to-be` | Get / save To-Be + Outcome |
| POST | `/api/v1/focus-areas/{id}/to-be/draft` | AI To-Be + Outcome draft |
| POST | `/api/v1/focus-areas/{id}/to-be/confirm` | Commit To-Be and open 7-day Run |
| GET | `/api/v1/focus-areas/{id}/run` | Run + DailyCheckIns |
| PUT | `/api/v1/focus-areas/{id}/run/check-ins/{day}` | Yes/No check-in for day 1–7 |
| GET | `/api/v1/focus-areas/{id}/adapt` | Thin Day-7 stub (Point 5 deferred) |

Existing Phase 1/2 draft endpoints remain at `/api/behaviour-loop/phase1` and `/phase2`.
