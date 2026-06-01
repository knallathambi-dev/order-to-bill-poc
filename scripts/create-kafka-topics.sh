#!/usr/bin/env sh
set -eu

COMPOSE="${COMPOSE:-docker compose --profile infra}"
TOPICS_FILE="${TOPICS_FILE:-infrastructure/kafka/topics.txt}"

if [ ! -f "$TOPICS_FILE" ]; then
  printf 'Topics file not found: %s\n' "$TOPICS_FILE" >&2
  exit 1
fi

printf '%s\n' 'Waiting for Kafka broker...'
for attempt in $(seq 1 60); do
  if $COMPOSE exec -T kafka /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server kafka:9092 \
    --list >/dev/null 2>&1; then
    break
  fi

  if [ "$attempt" -eq 60 ]; then
    printf '%s\n' 'Kafka broker did not become ready after 60 attempts.' >&2
    exit 1
  fi

  sleep 2
done

while IFS=: read -r topic partitions replication; do
  case "$topic" in
    ""|\#*) continue ;;
  esac

  printf 'Creating Kafka topic %s\n' "$topic"
  $COMPOSE exec -T kafka /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server kafka:9092 \
    --create \
    --if-not-exists \
    --topic "$topic" \
    --partitions "$partitions" \
    --replication-factor "$replication"

  case "$topic" in
    connect-configs|connect-offsets|connect-status)
      $COMPOSE exec -T kafka /opt/kafka/bin/kafka-configs.sh \
        --bootstrap-server kafka:9092 \
        --alter \
        --entity-type topics \
        --entity-name "$topic" \
        --add-config cleanup.policy=compact >/dev/null
      ;;
  esac

  actual_partitions="$($COMPOSE exec -T kafka /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server kafka:9092 \
    --describe \
    --topic "$topic" | sed -n 's/.*PartitionCount: \([0-9][0-9]*\).*/\1/p' | head -n 1)"

  if [ "$actual_partitions" != "$partitions" ]; then
    printf 'Kafka topic %s already exists with %s partitions, expected %s.\n' "$topic" "$actual_partitions" "$partitions" >&2
    printf '%s\n' 'Delete/recreate the topic or reset the infra volume before bootstrapping.' >&2
    exit 1
  fi
done < "$TOPICS_FILE"
