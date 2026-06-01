#!/usr/bin/env sh
set -eu

COMPOSE="${COMPOSE:-docker compose --profile infra}"
CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
KAFKA_UI_URL="${KAFKA_UI_URL:-http://localhost:8085}"

wait_for_http() {
  name="$1"
  url="$2"

  attempts=0
  max_attempts="${VERIFY_MAX_ATTEMPTS:-60}"
  delay_seconds="${VERIFY_DELAY_SECONDS:-2}"

  while [ "$attempts" -lt "$max_attempts" ]; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      return 0
    fi

    attempts=$((attempts + 1))
    sleep "$delay_seconds"
  done

  printf '%s did not become ready at %s after %s attempts.\n' "$name" "$url" "$max_attempts" >&2
  return 1
}

printf '%s\n' 'Checking MongoDB replica set...'
$COMPOSE exec -T mongodb mongosh --quiet --eval 'rs.status().ok' | grep 1 >/dev/null

printf '%s\n' 'Checking Kafka topics...'
$COMPOSE exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list >/dev/null

printf '%s\n' 'Checking Kafka Connect...'
wait_for_http "Kafka Connect" "$CONNECT_URL/connectors"

printf '%s\n' 'Checking Debezium connectors...'
if ! curl -fsS "$CONNECT_URL/connectors/cood-outbox-connector/status" | grep -E '"state"[[:space:]]*:[[:space:]]*"(RUNNING|PAUSED)"' >/dev/null; then
  printf '%s\n' 'cood-outbox-connector is not registered or not running. Run make infra-bootstrap.' >&2
  exit 1
fi
if ! curl -fsS "$CONNECT_URL/connectors/fallout-outbox-connector/status" | grep -E '"state"[[:space:]]*:[[:space:]]*"(RUNNING|PAUSED)"' >/dev/null; then
  printf '%s\n' 'fallout-outbox-connector is not registered or not running. Run make infra-bootstrap.' >&2
  exit 1
fi

printf '%s\n' 'Checking Keycloak...'
wait_for_http "Keycloak" "$KEYCLOAK_URL/realms/master"

printf '%s\n' 'Checking Kafka UI...'
attempts=0
while [ "$attempts" -lt "${VERIFY_MAX_ATTEMPTS:-60}" ]; do
  if curl -fsS "$KAFKA_UI_URL/actuator/health" 2>/dev/null | grep UP >/dev/null; then
    printf '%s\n' 'Infrastructure verified.'
    exit 0
  fi

  attempts=$((attempts + 1))
  sleep "${VERIFY_DELAY_SECONDS:-2}"
done

printf 'Kafka UI did not become healthy at %s after %s attempts.\n' "$KAFKA_UI_URL/actuator/health" "${VERIFY_MAX_ATTEMPTS:-60}" >&2
exit 1
