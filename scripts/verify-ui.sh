#!/usr/bin/env bash
set -euo pipefail

SELFCARE_UI_URL="${SELFCARE_UI_URL:-http://localhost:${SELFCARE_UI_PORT:-3000}}"
ORDER_INVENTORY_UI_URL="${ORDER_INVENTORY_UI_URL:-http://localhost:${ORDER_INVENTORY_UI_PORT:-3004}}"
ORDER_ORCHESTRATION_UI_URL="${ORDER_ORCHESTRATION_UI_URL:-http://localhost:${ORDER_ORCHESTRATION_UI_PORT:-3006}}"

wait_for_url() {
  local name="$1"
  local url="$2"
  local pattern="${3:-}"
  local attempts=0
  local max_attempts="${VERIFY_MAX_ATTEMPTS:-60}"
  local delay_seconds="${VERIFY_DELAY_SECONDS:-2}"

  printf 'Checking %s at %s\n' "$name" "$url"
  while [ "$attempts" -lt "$max_attempts" ]; do
    if response="$(curl -fsS "$url" 2>/dev/null)"; then
      if [ -z "$pattern" ] || printf '%s' "$response" | grep -q "$pattern"; then
        return 0
      fi
    fi

    attempts=$((attempts + 1))
    sleep "$delay_seconds"
  done

  printf '%s did not become ready at %s after %s attempts.\n' "$name" "$url" "$max_attempts" >&2
  return 1
}

wait_for_url "selfcare-ui health" "$SELFCARE_UI_URL/health" '"status":"healthy"'
wait_for_url "order-inventory-ui" "$ORDER_INVENTORY_UI_URL/"
wait_for_url "order-orchestration-ui" "$ORDER_ORCHESTRATION_UI_URL/"

printf '%s\n' 'UI portals verified.'
