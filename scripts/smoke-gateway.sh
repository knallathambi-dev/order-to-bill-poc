#!/usr/bin/env bash
set -euo pipefail

GATEWAY_URL="${GATEWAY_URL:-http://localhost:${POC_GATEWAY_PORT:-8088}}"
INTERNAL_SECRET="${INTERNAL_SECRET:-${POC_GATEWAY_INTERNAL_SECRET:-local-gateway-secret}}"

printf 'Checking gateway health at %s\n' "$GATEWAY_URL"
curl -fsS "$GATEWAY_URL/health" | grep -q '"status":"healthy"'

printf 'Checking CSRF issuance\n'
curl -fsS -c /tmp/otb-poc-gateway-cookies.txt "$GATEWAY_URL/api/auth/csrf" | grep -q 'csrfToken'

printf 'Checking unauthenticated /me remains 401\n'
status="$(curl -sS -o /tmp/otb-poc-gateway-me.json -w '%{http_code}' "$GATEWAY_URL/api/auth/me")"
test "$status" = "401" || {
  printf 'Expected /api/auth/me to return 401, got %s\n' "$status" >&2
  cat /tmp/otb-poc-gateway-me.json >&2
  exit 1
}

printf 'Checking catalog proxy route\n'
curl -fsS "$GATEWAY_URL/api/productCatalogManagement/v1/productOffering?lifecycleStatus=active" >/dev/null

printf 'Checking internal smoke route for order inventory\n'
curl -fsS -H "x-internal-secret: $INTERNAL_SECRET" "$GATEWAY_URL/api/productOrderingManagement/v1/productOrder?limit=1" >/dev/null

printf '%s\n' 'Gateway smoke checks passed.'
