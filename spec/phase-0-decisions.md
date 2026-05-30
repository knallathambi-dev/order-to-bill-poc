# Phase 0 Decisions

## Scope Confirmation

Phase 0 confirms this repository starts as a fresh, Discobole-first Order-to-Bill POC.

The POC must not implement a custom order-management platform. Discobole remains the authoritative platform for:

- order capture
- product order persistence and lifecycle
- orchestration plan generation and execution
- delivery state
- fallout and retry
- authentication and authorization integration
- customer and operator order visibility

Custom code is limited to local POC glue and telecom-specific simulators:

- POC gateway/proxy
- qualification-service
- activation-service
- billing-service
- small configuration or content adaptations for reused Discobole UI portals

## Repository Baseline

This repository is intentionally specs-only at the end of Phase 0. Implementation scaffolding begins in Phase 1.

Current source of truth documents:

- `spec/requirements.md`
- `spec/plan.md`
- `spec/architecture.md`
- `spec/auth-layer.md`
- `spec/tasks.md`

## Discobole Runtime Strategy

Decision: this repository will become the full POC mono-repo containing copied Discobole service and UI source code.

Source monorepo for the initial copy:

```text
/Users/knallathambi/Engineering/projects/java-apps/discoble-monorepo
```

Phase 2 must inventory the required Discobole services and UI portals from the source monorepo, then copy the selected code into this repository under dedicated mono-repo folders.

Target folders:

- `discobole-services/`
- `discobole-ui/`

After copy-in, required POC changes may be applied directly in this repository. The external Discobole monorepo remains the upstream source reference, not the runtime source of truth for this POC.

Rationale:

- gives the POC one self-contained repo for service code, UI code, configuration, scripts, simulators, and documentation
- allows required Discobole changes to be reviewed and versioned alongside POC-specific changes
- reduces dependency on a sibling checkout once Phase 2 copy-in is complete
- keeps Phase 2 responsible for deciding the exact copied modules and preserving attribution/upstream traceability

## Gateway Implementation Style

Decision: begin with the existing `selfcare-ui/server` pattern as the preferred gateway/session-proxy approach.

Fallback: create a lightweight Node/Express gateway if the reused portal server cannot cleanly proxy all required customer and operator UI traffic.

Spring Boot gateway is not the Phase 0 default.

Rationale:

- Discobole UI reuse is a core POC constraint
- `selfcare-ui/server` is already aligned with portal authentication/session concerns
- a lightweight Node gateway is easier to align with browser-facing proxy behavior if a standalone gateway is needed
- Spring Boot should be reserved for simulator services and backend integrations unless the gateway needs heavier Java service capabilities

## Phase 0 Acceptance

Phase 0 is complete when:

- the POC is explicitly documented as Discobole-first
- Discobole UI portals to reuse are identified
- this repository's specs-only baseline is confirmed
- the full mono-repo copy-in strategy is recorded
- the initial gateway strategy is recorded
- Phase 1 and later implementation tasks remain unstarted
