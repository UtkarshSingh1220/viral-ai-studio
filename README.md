# Viral AI Studio

A semi-automated content studio for Hinglish AI and technology videos on Instagram Reels and YouTube Shorts.

## Current MVP

- React + TypeScript dashboard with content cards, search, status filters, draft creation, and editorial review.
- Java 21 + Spring Boot REST API with PostgreSQL persistence and Flyway migrations.
- AI-powered production package generation through the OpenAI Responses API: hook, Hinglish script, caption, hashtags, CTA, estimated duration, scene-by-scene storyboard, source links, and verification notes.
- Trend research endpoint for current AI/technology video ideas with source URLs and audience-specific Hinglish angles.
- Persistent per-post/per-platform performance snapshots and a dashboard with engagement rate, top performers, and rule-based content recommendations.
- Editorial states: IDEA, DRAFT, IN_REVIEW, APPROVED, REJECTED, PUBLISHED.
- Human approval required; approval does not automatically publish.

## Run locally

1. Set `OPENAI_API_KEY` in your shell environment. Never commit the key.
2. Start PostgreSQL: `docker compose up -d postgres`
3. Run backend from repository root: `mvn test` then `mvn spring-boot:run`
4. In another terminal: `cd frontend && npm install && npm run build && npm run dev`
5. Open http://localhost:5173.

Backend uses `OPENAI_API_KEY` and optional `OPENAI_MODEL` (defaults to `gpt-4o-mini`). Database variables: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`. The compose file's database credentials are for local development only.

## API

- `GET /api/content` — list content; optional `?status=DRAFT`
- `POST /api/content` — save a manually authored draft
- `POST /api/content/generate` — generate and persist a complete Hinglish production package; body: `{"topic":"RAG for Java developers","platform":"BOTH"}`. The response includes `cta`, `durationSeconds`, and `storyboard` (a JSON string containing timecoded visual directions, voiceover, and on-screen text for each scene).
- `GET /api/trends` — research current AI/technology video ideas with source URLs
- `PATCH /api/content/{id}/status` — update editorial status and notes
- `DELETE /api/content/{id}` — delete a content item
- `POST /api/analytics/{contentId}` — store a performance snapshot. Example body: `{"platform":"YOUTUBE_SHORTS","views":1200,"likes":85,"comments":12,"shares":20,"saves":30,"followersGained":8,"averageWatchSeconds":18.5}`
- `GET /api/analytics/dashboard` — aggregate the latest snapshot per content/platform, top performers, and recommendations

Engagement rate is calculated as `(likes + comments + shares + saves) / views * 100`. Dashboard totals use the latest snapshot for each content/platform pair to avoid double-counting historical snapshots. Recommendations are transparent heuristics, not causal claims or machine-learned predictions.

## Important limitations

- Generated content is a draft, not a verified fact-check. Web-research URLs and verification notes are review aids, not proof that every claim is accurate. Verify claims and current information before publishing.
- Analytics snapshots are currently entered through the API; official Instagram/YouTube analytics ingestion is not connected.
- Social publishing is not automatic; official platform API credentials, permissions, and account eligibility are required.
- Never commit API keys or credentials.

## API access token

The backend now requires a shared API token on `/api/**` requests. Set `APP_API_TOKEN` to a long, randomly generated secret before starting the backend; if it is missing, API routes fail closed with HTTP 503. In the dashboard, enter the same token in the API access token field and select Connect. The token is stored in `sessionStorage` for the current browser tab session and sent in the `X-App-Token` header. Use HTTPS outside local development, rotate the token if exposed, and never commit the real value. This shared-token gate is intended for a single-creator/private deployment; it is not a substitute for per-user accounts, roles, or identity management.

## Production-readiness status

This repository is an MVP and is **not yet safe to expose directly to the public internet**. Before a public or multi-user deployment, complete and verify these gates:

- [ ] Add authentication and authorization to every content, trend, and analytics API; do not rely on the frontend to enforce access.
- [ ] Add request validation, request-size limits, rate limits, and safe error responses for AI generation and research endpoints.
- [ ] Configure production CORS to allow only the deployed frontend origin; do not use wildcard origins with credentials.
- [ ] Store OpenAI and database credentials in the hosting provider's secret manager. Never use the example/local database credentials in production.
- [ ] Use managed PostgreSQL with TLS, automated backups, and a tested restore procedure.
- [ ] Add application health/readiness checks, structured logs, error monitoring, and alerts; redact prompts, tokens, and secrets where appropriate.
- [ ] Run the backend and frontend builds/tests in CI and verify a deployed smoke test against a non-production database and a replaceable API key.
- [ ] Confirm AI-generated source links and claims manually before publishing; publishing and platform analytics ingestion are not implemented.

### Local vs production configuration

Copy .env.example to a local environment file only for development, keep real secrets out of Git, and use environment/secret-manager injection in hosted environments. DATABASE_PASSWORD=change-me and the credentials in docker-compose.yml are local-development values, not production credentials. The UI may show sample content when the API is unavailable; in preview mode, edits are temporary and disappear on refresh. A failed status update while the API is connected is now surfaced instead of being silently treated as a local success.
