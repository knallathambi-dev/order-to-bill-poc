#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

SERVICES=(
  "qualification-service:otb-poc/qualification-service"
  "activation-service:otb-poc/activation-service"
  "billing-service:otb-poc/billing-service"
)

if [[ "${1:-}" == "--list" ]]; then
  for service in "${SERVICES[@]}"; do
    printf '%s\n' "${service#*:}"
  done
  exit 0
fi

scripts/package-simulator-services.sh

for service in "${SERVICES[@]}"; do
  name="${service%%:*}"
  image="${service#*:}"
  docker build -t "$image" "custom-services/$name"
done
