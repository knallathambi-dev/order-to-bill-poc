#!/usr/bin/env sh
set -eu

CONNECT_URL="${CONNECT_URL:-http://localhost:8083}"
CONNECTOR_DIR="${CONNECTOR_DIR:-infrastructure/kafka/connectors}"

for connector in "$CONNECTOR_DIR"/*.json; do
  [ -f "$connector" ] || continue
  name="$(sed -n 's/.*"name"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' "$connector" | head -n 1)"

  if [ -z "$name" ]; then
    printf 'Could not find connector name in %s\n' "$connector" >&2
    exit 1
  fi

  printf 'Registering Debezium connector %s\n' "$name"
  curl -fsS -X DELETE "$CONNECT_URL/connectors/$name" >/dev/null 2>&1 || true
  curl -fsS -X POST \
    -H 'Content-Type: application/json' \
    --data-binary "@$connector" \
    "$CONNECT_URL/connectors" >/dev/null
done

printf '%s\n' 'Debezium connector registration complete.'
