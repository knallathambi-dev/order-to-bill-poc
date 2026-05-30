# Phase 3 Infrastructure

## Scope

Phase 3 adds the local infrastructure stack only. Discobole services, UI portals, custom simulator services, gateway code, catalog seed data, and security seed data are implemented in later phases.

## Services

| Service | Port | Purpose |
| --- | --- | --- |
| MongoDB | `27017` | Local persistence and Debezium outbox source |
| Kafka | `9092` | Event broker, Apache Kafka `4.3.0` in KRaft mode |
| Kafka controller | `9093` | KRaft controller listener |
| Kafka UI | `8085` | Local topic and connector visibility |
| Kafka Connect/Debezium | `8083` | Mongo outbox connector runtime |
| Keycloak | `8080` | Local identity provider shell |

All long-running infrastructure services use `restart: unless-stopped` and local resource caps. The one-shot `mongo-init` helper uses `restart: "no"` so failures are visible.

The stack uses the named Compose network `otb-poc-infra`. Application service dependencies will be added in the later phases that introduce those services.

## Kafka

Kafka runs with KRaft only. ZooKeeper is intentionally absent.

Configured image:

```text
apache/kafka:4.3.0
```

Kafka uses two plaintext listeners:

- `PLAINTEXT://kafka:9092` for containers on the Compose network
- `PLAINTEXT_HOST://localhost:9092` for host tools

The host port `9092` maps to container port `19092` intentionally, because `19092` is the dedicated host listener. Mapping host `9092` directly to container `9092` would cause host clients to receive `kafka:9092` metadata, which is not resolvable outside the Compose network.

The Kafka healthcheck verifies that `/opt/kafka/bin/kafka-topics.sh` exists before using it.

## MongoDB

MongoDB runs as a single-node replica set named `rs0`. The official `mongo:7` image provides `mongosh`; the legacy `mongo` shell is not available in that image line.

The `mongo-init` helper has a bounded retry loop and fails after 60 attempts.

## Debezium

Debezium is included because copied Discobole order-orchestration services persist outbox records to MongoDB `events` collections. Kafka Connect registers MongoDB outbox connectors for:

- `orchestration_delivery.events`
- `falloutmanagement.events`

The connector database names may need adjustment after Phase 5 normalizes local service profiles. The decision is recorded in `docs/phase-3-debezium-decision.md`.

## Commands

```sh
cp .env.example .env
make infra-up
make infra-bootstrap
make infra-verify
make infra-down
```

## URLs

- Kafka UI: `http://localhost:8085`
- Kafka Connect: `http://localhost:8083`
- Keycloak: `http://localhost:8080`
- MongoDB: `mongodb://localhost:27017/?replicaSet=rs0`
- Kafka bootstrap: `localhost:9092`
