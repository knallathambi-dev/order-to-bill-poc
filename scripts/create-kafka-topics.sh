#!/usr/bin/env sh
set -eu

COMPOSE="${COMPOSE:-docker compose --profile infra}"
TOPICS_FILE="${TOPICS_FILE:-infrastructure/kafka/topics.txt}"

if [ ! -f "$TOPICS_FILE" ]; then
  printf 'Topics file not found: %s\n' "$TOPICS_FILE" >&2
  exit 1
fi

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
done < "$TOPICS_FILE"

