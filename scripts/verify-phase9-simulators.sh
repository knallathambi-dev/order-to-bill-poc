#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

required_files=(
  "custom-services/pom.xml"
  "custom-services/simulator-common/pom.xml"
  "custom-services/qualification-service/pom.xml"
  "custom-services/activation-service/pom.xml"
  "custom-services/billing-service/pom.xml"
  "custom-services/qualification-service/Dockerfile"
  "custom-services/activation-service/Dockerfile"
  "custom-services/billing-service/Dockerfile"
  "docs/phase-9-simulator-services.md"
  "scripts/package-simulator-services.sh"
  "scripts/build-simulator-service-images.sh"
  "scripts/smoke-simulators.sh"
)

for file in "${required_files[@]}"; do
  [[ -f "$file" ]] || {
    printf 'Missing required Phase 9 file: %s\n' "$file" >&2
    exit 1
  }
done

grep -q 'qualification-service:' docker-compose.yml
grep -q 'activation-service:' docker-compose.yml
grep -q 'billing-service:' docker-compose.yml
grep -q 'profiles: \["simulators' docker-compose.yml
grep -q 'disco.service-order-management.serviceOrderStateChange-event' infrastructure/kafka/topics.txt
grep -q 'disco.delivery-management.deliveryOrder-event' infrastructure/kafka/topics.txt
grep -q 'disco.delivery-management.deliveryOrderItemStatus-event' infrastructure/kafka/topics.txt

if grep -R "spring-boot-starter-security\|keycloak\|oauth2-resource-server" custom-services \
    --include pom.xml --include '*.java' --include '*.yml'; then
  printf '%s\n' 'Unexpected auth/security dependency or configuration found in custom simulator services.' >&2
  exit 1
fi

printf '%s\n' 'Phase 9 simulator service files verified.'
