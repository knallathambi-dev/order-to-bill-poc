#!/usr/bin/env bash
set -euo pipefail

QUALIFICATION_URL="${QUALIFICATION_URL:-http://localhost:${QUALIFICATION_SERVICE_PORT:-18101}}"
QUALIFICATION_MANAGEMENT_URL="${QUALIFICATION_MANAGEMENT_URL:-http://localhost:${QUALIFICATION_SERVICE_MANAGEMENT_PORT:-19101}}"
ACTIVATION_URL="${ACTIVATION_URL:-http://localhost:${ACTIVATION_SERVICE_PORT:-18102}}"
ACTIVATION_MANAGEMENT_URL="${ACTIVATION_MANAGEMENT_URL:-http://localhost:${ACTIVATION_SERVICE_MANAGEMENT_PORT:-19102}}"
BILLING_URL="${BILLING_URL:-http://localhost:${BILLING_SERVICE_PORT:-18103}}"
BILLING_MANAGEMENT_URL="${BILLING_MANAGEMENT_URL:-http://localhost:${BILLING_SERVICE_MANAGEMENT_PORT:-19103}}"

payload_for_spec() {
  local spec_id="$1"
  cat <<JSON
{
  "id": "smoke-${spec_id}",
  "serviceOrderItem": [
    {
      "id": "item-${spec_id}",
      "quantity": 1,
      "action": "add",
      "service": {
        "serviceType": "CFS",
        "serviceSpecification": {
          "id": "${spec_id}"
        },
        "serviceCharacteristic": []
      }
    }
  ]
}
JSON
}

check_health() {
  local name="$1"
  local url="$2"
  local attempts=0
  local max_attempts="${VERIFY_MAX_ATTEMPTS:-60}"
  local delay_seconds="${VERIFY_DELAY_SECONDS:-2}"

  while [ "$attempts" -lt "$max_attempts" ]; do
    if curl -fsS "$url/actuator/health" >/dev/null 2>&1; then
      printf '%s\n' "$name health OK"
      return 0
    fi

    attempts=$((attempts + 1))
    sleep "$delay_seconds"
  done

  printf '%s health did not become ready at %s after %s attempts.\n' "$name" "$url/actuator/health" "$max_attempts" >&2
  return 1
}

post_order() {
  local name="$1"
  local url="$2"
  local spec_id="$3"
  local response
  local attempts=0
  local max_attempts="${VERIFY_MAX_ATTEMPTS:-60}"
  local delay_seconds="${VERIFY_DELAY_SECONDS:-2}"

  while [ "$attempts" -lt "$max_attempts" ]; do
    if response="$(payload_for_spec "$spec_id" | curl -fsS -H 'Content-Type: application/json' -d @- "$url/serviceOrdering/v1/serviceOrder" 2>/dev/null)" &&
      printf '%s\n' "$response" | grep -q '"state":"Completed"'; then
      printf '%s\n' "$name service order OK"
      return 0
    fi

    attempts=$((attempts + 1))
    sleep "$delay_seconds"
  done

  printf '%s service order did not complete at %s after %s attempts.\n' "$name" "$url/serviceOrdering/v1/serviceOrder" "$max_attempts" >&2
  return 1
}

check_health qualification-service "$QUALIFICATION_MANAGEMENT_URL"
check_health activation-service "$ACTIVATION_MANAGEMENT_URL"
check_health billing-service "$BILLING_MANAGEMENT_URL"

post_order qualification-service "$QUALIFICATION_URL" "fiber-broadband-service"
post_order activation-service "$ACTIVATION_URL" "fiber-broadband-service"
post_order activation-service-static-ip "$ACTIVATION_URL" "static-ip-service"
post_order billing-service "$BILLING_URL" "billing-initiation-service"

printf '%s\n' 'Simulator smoke checks passed.'
