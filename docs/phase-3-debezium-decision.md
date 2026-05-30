# Phase 3 Debezium Decision

## Question

Should Phase 3 include Kafka Connect and Debezium?

## Finding

Yes, but only because copied Discobole order orchestration code clearly uses a MongoDB outbox pattern.

Evidence:

- `discobole-services/disco-order-orchestration/orchestration-delivery-commons/common-kafka-config/src/main/java/com/orange/discobole/orderorchestration/outbox/EventPublisher.java`
  - Persists event records through `EventRepository`.
- `discobole-services/disco-order-orchestration/orchestration-delivery-commons/common-kafka-config/src/main/java/com/orange/discobole/orderorchestration/outbox/internal/EventEntity.java`
  - Stores outbox records in MongoDB collection `events`.
- `discobole-services/disco-order-orchestration/gitlab-profile/keycloak-kafka-mongo-setup/kafka/configs/orchestration_delivery.json`
  - Defines `io.debezium.connector.mongodb.MongoDbConnector`.
  - Uses `io.debezium.connector.mongodb.transforms.outbox.MongoEventRouter`.
  - Watches `orchestration_delivery.events`.
- `discobole-services/disco-order-orchestration/gitlab-profile/keycloak-kafka-mongo-setup/kafka/configs/fallout.json`
  - Defines the same Debezium Mongo outbox router for `falloutmanagement.events`.
- `discobole-services/disco-order-orchestration/doc/docs/architecture/detailed-architecture/orchestration-delivery.md`
  - Documents Debezium configuration for the COOD outbox.

Other copied services also use direct Kafka publishing patterns such as Spring Kafka or Spring Cloud Stream. Debezium is not required for every service, but it is required for the orchestration outbox path unless we deliberately rewrite that event publication mechanism.

## Decision

Phase 3 should include:

- Apache Kafka `4.3.0` in KRaft mode only
- no ZooKeeper
- MongoDB as a replica set, because Debezium MongoDB change streams require replica-set semantics
- Kafka Connect with Debezium MongoDB connector support
- connector configuration for the copied Discobole outbox collections:
  - `orchestration_delivery.events`
  - `falloutmanagement.events`

## Constraint

Do not reuse the copied upstream sample Compose as-is because it uses Debezium's ZooKeeper-based Kafka images. The POC Compose must keep Kafka KRaft-only.

## Open Phase 3 Follow-Up

Before finalizing Compose connector files, verify the exact runtime database names after local profiles are normalized. Current copied configs refer to variants including `COOD`, `orchestration_delivery`, `falloutManagement`, and `falloutmanagement`.

