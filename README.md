# Order-to-Bill POC

Discobole-first proof of concept for a telecom broadband Order-to-Bill flow.

The target customer journey is:

```text
Fiber Broadband 300 Mbps
+
Static IP Add-on
```

Discobole is the source of truth for order capture, order inventory, orchestration, delivery state, fallout, authorization, and UI visibility. This repository is the full POC mono-repo for copied Discobole services, copied Discobole UI portals, POC-specific services, gateway glue, infrastructure, scripts, seed data, and documentation.

## Phase Status

Current implementation scope:

- Phase 0: complete
- Phase 1: repository structure and fresh-start setup files
- Phase 2: copied Discobole service/UI source and runtime strategy
- Phase 3: local infrastructure stack
- Phase 4: security seed data

Later phases will add security seed data, catalog data, UI adaptations, gateway implementation, simulator services, event integration, fallout/retry flows, and tests.

## Repository Layout

```text
.
|-- custom-services/
|   |-- activation-service/
|   |-- billing-service/
|   `-- qualification-service/
|-- discobole-runtime/
|-- discobole-services/
|-- discobole-ui/
|-- docs/
|-- gateway/
|-- infrastructure/
|   |-- kafka/
|   |-- keycloak/
|   `-- mongodb/
|-- scripts/
|-- spec/
`-- ui-overrides/
```

## Discobole Source

The initial copy source is:

```text
/Users/knallathambi/Engineering/projects/java-apps/discoble-monorepo
```

Phase 2 will copy the selected Discobole services into `discobole-services/` and selected Discobole UI portals into `discobole-ui/`. After that copy, this repository is the runtime and modification source of truth for the POC. The external monorepo remains an upstream reference.

## Kafka Baseline

Use Apache Kafka `4.3.0` with KRaft mode and no ZooKeeper.

Default local image:

```text
apache/kafka:4.3.0
```

Kafka Compose and topic bootstrap scripts belong to Phase 3. Any future Kafka configuration in this repo must stay KRaft-only unless the specs are deliberately changed.

Kafka Connect and Debezium are required for the copied Discobole order-orchestration outbox path. See `docs/phase-3-debezium-decision.md`.

Reference:

- [Apache Kafka downloads](https://kafka.apache.org/community/downloads/)
- [Apache Kafka 4.3.0 release announcement](https://kafka.apache.org/blog/2026/05/22/apache-kafka-4.3.0-release-announcement/)

## Local Setup Assumptions

Expected local tools for later phases:

- Docker Desktop or compatible Docker Engine with Compose v2
- Java 21 or the version required by the selected Discobole modules
- Node.js LTS for reused Discobole UI portals and gateway/session proxy work
- Maven or Gradle, matching the Discobole module build system
- `make`

Copy `.env.example` to `.env` before running future local workflows.

```sh
cp .env.example .env
```

## Commands

```sh
make help
make verify-phase1
make verify-phase2
make verify-phase3
make verify-phase4
make list-discobole-images
make infra-up
make infra-bootstrap
make infra-verify
make infra-down
make security-verify-keycloak
```

Most runtime commands are placeholders until the owning implementation phases add Compose files, service projects, and tests.
