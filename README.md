# Viral AI Studio

A semi-automated content studio for Hinglish AI and technology videos on Instagram Reels and YouTube Shorts.

## Current MVP

- React + TypeScript dashboard with content cards, search, status filters, draft creation, and editorial review.
- Java 21 + Spring Boot REST API with PostgreSQL persistence and Flyway migrations.
- AI-powered script generation through the OpenAI Responses API with schema-constrained JSON output and optional live web research.
- Live AI/technology trend research endpoint that returns source URLs and audience-specific Hinglish angles.
- Editorial states: IDEA, DRAFT, IN_REVIEW, APPROVED, REJECTED, PUBLISHED.
- Human approval required; approval does not automatically publish.
- Demo content is shown if the API cannot be reached; AI generation requires the backend and an API key.

## Run locally

1. Set `OPENAI_API_KEY` in your shell environment. Never commit the key.
2. Start PostgreSQL: `docker compose up -d postgres`
3. Run backend from repository root: `mvn spring-boot:run`
4. In another terminal: `cd frontend && npm install && npm run dev`
5. Open http://localhost:5173.

Backend uses `OPENAI_API_KEY` and optional `OPENAI_MODEL` (defaults to `gpt-4o-mini`). Database variables: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`. The compose file's database credentials are for local development only.

## API

- `GET /api/content` — list content; optional `?status=DRAFT`
- `POST /api/content` — save a manually authored draft
- `POST /api/content/generate` — research and generate a draft; body: `{"topic":"RAG for Java developers","platform":"BOTH"}`
- `GET /api/trends` — research five current AI/technology video ideas with source URLs
- `PATCH /api/content/{id}/status` — update editorial status and notes
- `DELETE /api/content/{id}` — delete a content item

## Limitations

- Generated content is a draft, not a verified fact-check. Verify claims and current information before publishing.
- Trend discovery, external source retrieval, platform publishing, and performance analytics are planned follow-ups, not implemented yet.
- Social publishing is not automatic; official platform API credentials, permissions, and account eligibility are required.
- Never commit API keys or credentials.
