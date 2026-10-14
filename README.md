# Viral AI Studio

A semi-automated content studio for Hinglish AI and technology videos on Instagram Reels and YouTube Shorts.

## Current MVP

- React + TypeScript dashboard with content cards, search, status filters, draft creation, and editorial review.
- Java 21 + Spring Boot REST API with PostgreSQL persistence and Flyway migrations.
- AI-powered script generation through the OpenAI Responses API with schema-constrained JSON output and optional live web research.
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
- `POST /api/content/generate` — generate a Hinglish draft; body: `{"topic":"RAG for Java developers","platform":"BOTH"}`
- `GET /api/trends` — research current AI/technology video ideas with source URLs
- `PATCH /api/content/{id}/status` — update editorial status and notes
- `DELETE /api/content/{id}` — delete a content item
- `POST /api/analytics/{contentId}` — store a performance snapshot. Example body: `{"platform":"YOUTUBE_SHORTS","views":1200,"likes":85,"comments":12,"shares":20,"saves":30,"followersGained":8,"averageWatchSeconds":18.5}`
- `GET /api/analytics/dashboard` — aggregate the latest snapshot per content/platform, top performers, and recommendations

Engagement rate is calculated as `(likes + comments + shares + saves) / views * 100`. Dashboard totals use the latest snapshot for each content/platform pair to avoid double-counting historical snapshots. Recommendations are transparent heuristics, not causal claims or machine-learned predictions.

## Important limitations

- Generated content is a draft, not a verified fact-check. Verify claims and current information before publishing.
- Analytics snapshots are currently entered through the API; official Instagram/YouTube analytics ingestion is not connected.
- Social publishing is not automatic; official platform API credentials, permissions, and account eligibility are required.
- Never commit API keys or credentials.
