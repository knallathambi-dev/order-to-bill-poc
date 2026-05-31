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
  curl -fsS "$url/actuator/health" >/dev/null
  printf '%s\n' "$name health OK"
}

post_order() {
  local name="$1"
  local url="$2"
  local spec_id="$3"
  local response
  response="$(payload_for_spec "$spec_id" | curl -fsS -H 'Content-Type: application/json' -d @- "$url/serviceOrdering/v1/serviceOrder")"
  printf '%s\n' "$response" | grep -q '"state":"Completed"'
  printf '%s\n' "$name service order OK"
}

check_health qualification-service "$QUALIFICATION_MANAGEMENT_URL"
check_health activation-service "$ACTIVATION_MANAGEMENT_URL"
check_health billing-service "$BILLING_MANAGEMENT_URL"

post_order qualification-service "$QUALIFICATION_URL" "fiber-broadband-service"
post_order activation-service "$ACTIVATION_URL" "fiber-broadband-service"
post_order activation-service-static-ip "$ACTIVATION_URL" "static-ip-service"
post_order billing-service "$BILLING_URL" "billing-initiation-service"

printf '%s\n' 'Simulator smoke checks passed.'
