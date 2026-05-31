#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SELFCARE_IMAGE="${SELFCARE_UI_IMAGE:-otb-poc/selfcare-ui}"
ORDER_INVENTORY_UI_IMAGE="${ORDER_INVENTORY_UI_IMAGE:-otb-poc/order-inventory-ui}"
ORDER_ORCHESTRATION_UI_IMAGE="${ORDER_ORCHESTRATION_UI_IMAGE:-otb-poc/order-orchestration-ui}"

if [[ "${1:-}" == "--list" ]]; then
  printf '%s\n' "$SELFCARE_IMAGE"
  printf '%s\n' "$ORDER_INVENTORY_UI_IMAGE"
  printf '%s\n' "$ORDER_ORCHESTRATION_UI_IMAGE"
  exit 0
fi

npm --prefix discobole-ui/disco-admin-ui/common-ui run build
npm --prefix discobole-ui/disco-admin-ui/order-inventory-ui run build
npm --prefix discobole-ui/disco-admin-ui/order-orchestration-ui run build

docker build -t "$SELFCARE_IMAGE" discobole-ui/selfcare-ui
docker build -t "$ORDER_INVENTORY_UI_IMAGE" discobole-ui/disco-admin-ui/order-inventory-ui
docker build -t "$ORDER_ORCHESTRATION_UI_IMAGE" discobole-ui/disco-admin-ui/order-orchestration-ui

printf '%s\n' 'UI image build complete.'
