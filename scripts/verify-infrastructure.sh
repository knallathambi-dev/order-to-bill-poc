#!/usr/bin/env sh
set -eu

COMPOSE="${COMPOSE:-docker compose --profile infra}"
CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
KAFKA_UI_URL="${KAFKA_UI_URL:-http://localhost:8085}"

printf '%s\n' 'Checking MongoDB replica set...'
$COMPOSE exec -T mongodb mongosh --quiet --eval 'rs.status().ok' | grep 1 >/dev/null

printf '%s\n' 'Checking Kafka topics...'
$COMPOSE exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list >/dev/null

printf '%s\n' 'Checking Kafka Connect...'
curl -fsS "$CONNECT_URL/connectors" >/dev/null

printf '%s\n' 'Checking Debezium connectors...'
curl -fsS "$CONNECT_URL/connectors/cood-outbox-connector/status" | grep -E '"state"[[:space:]]*:[[:space:]]*"(RUNNING|PAUSED)"' >/dev/null
curl -fsS "$CONNECT_URL/connectors/fallout-outbox-connector/status" | grep -E '"state"[[:space:]]*:[[:space:]]*"(RUNNING|PAUSED)"' >/dev/null

printf '%s\n' 'Checking Keycloak...'
curl -fsS "$KEYCLOAK_URL/realms/master" >/dev/null

printf '%s\n' 'Checking Kafka UI...'
curl -fsS "$KAFKA_UI_URL/actuator/health" | grep UP >/dev/null

printf '%s\n' 'Infrastructure verified.'

