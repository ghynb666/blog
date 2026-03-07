# Architecture Evolution Roadmap

## Stage 1 (Implemented in current patch)
- API version compatibility: both `/api/**` and `/api/v1/**` are available.
- Standard response envelope: `code`, `message`, `data`, `requestId`, `timestamp`.
- Unified business error model with typed error codes.
- Domain events introduced (in-process):
  - `ArticlePublishedEvent`
  - `ArticleUpdatedEvent`
  - `ArticleViewedEvent`
- Visit statistics are decoupled through asynchronous event listener.

## Stage 2 (Next iterations)
- Add audit logging pipeline using the same event infrastructure.
- Add rate limit and anti-abuse strategy for write endpoints.
- Introduce queue-backed event transport (Redis Stream or RabbitMQ) with retry and dead-letter handling.
- Define domain-owned data boundaries and forbid cross-domain mapper usage.

## Stage 3 (Only after trigger conditions)
- Split `analytics-service` and `media-service` first.
- Keep content and auth inside the monolith until independent scaling pressure appears.
- Introduce gateway, service discovery, distributed tracing, and central alerting.

## Trigger Conditions for Service Split
- Multi-team release conflicts continue for 2+ iterations.
- Analytics/media consume >40% sustained resources with clear isolated scaling gain.
- Lead time remains >2x target after stage 2 improvements.
