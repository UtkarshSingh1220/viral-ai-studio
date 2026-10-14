# Roadmap

## Milestone 1 — Content workspace
- [x] Java/Spring Boot and React/TypeScript skeleton.
- [x] Draft list, search, status filters, review modal, and draft creation.
- [x] PostgreSQL schema migration and content CRUD/status endpoints.
- [ ] Run build/test verification in a Java 21 + Node environment.
- [ ] Add CI workflow.

## Milestone 2 — Real AI generation
- Add a provider interface and configurable LLM implementation.
- Use structured output for topic angle, hook, timed script beats, CTA, caption, hashtags, and source links.
- Validate outputs and preserve provider/model metadata.
- Treat generated claims as unverified until reviewed.

## Milestone 3 — Research and quality
- Trend-source adapters with timestamped source URLs.
- Claim/source checklist and reviewer notes.
- Editorial scoring for hook clarity, novelty, audience fit, retention structure, and production effort.
- Never invent trend metrics or present predictions as facts.

## Milestone 4 — Publishing and analytics
- Manual publishing checklist first.
- Official platform APIs only, after credentials, permissions, and account eligibility are configured.
- Record views, retention, likes, comments, shares, saves, and observation date.
- Use analytics to inform suggestions, not guarantee virality.

## Non-goals for MVP
- No automatic posting without explicit approval.
- No scraping behind authentication or bypassing platform limits.
- No Kafka, vector database, or multi-agent orchestration until required.
