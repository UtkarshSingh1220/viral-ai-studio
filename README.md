# Viral AI Studio

A semi-automated content studio for Hinglish AI and technology videos on Instagram Reels and YouTube Shorts.

## Current MVP

- React + TypeScript creator dashboard with sample content.
- Create short-form draft records and review hook, script, caption, and hashtags.
- Editorial states: IDEA, DRAFT, IN_REVIEW, APPROVED, REJECTED, PUBLISHED.
- Spring Boot REST API and PostgreSQL persistence with Flyway migration.
- Human approval required; approval does not automatically publish.
- Demo mode lets the UI be explored before the backend is running.

## Stack

- Java 21, Spring Boot 3.5.x, Spring Data JPA, Bean Validation
- PostgreSQL 16, Flyway, Maven
- React, TypeScript, Vite
- Docker Compose for local database

## Run locally

### 1. Start PostgreSQL

```bash
docker compose up -d postgres
```

Default local credentials are for development only. Override them before using a shared or deployed environment.

### 2. Run backend

Requires Java 21 and Maven.

```bash
mvn spring-boot:run
```

API starts at http://localhost:8080. Flyway creates the initial content table.

### 3. Run frontend

Requires Node.js 22 or newer.

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The UI uses sample data when the API is unavailable; draft changes made in demo mode are not durable.

## API

- `GET /api/content` — list content; optional `?status=DRAFT`
- `POST /api/content` — create draft
- `PATCH /api/content/{id}/status` — update editorial status and notes
- `DELETE /api/content/{id}` — delete a draft
- `GET /actuator/health` is not included yet; add Actuator before relying on health checks.

## Roadmap

See [docs/ROADMAP.md](docs/ROADMAP.md).

## Safety and quality

- Treat generated scripts as drafts, not verified facts.
- Verify factual claims and source links before approval.
- Never publish automatically without explicit approval.
- Never commit API keys or secrets. Use environment variables.
- Trend discovery, real LLM generation, social publishing, and analytics integrations are planned follow-ups, not implemented yet.
