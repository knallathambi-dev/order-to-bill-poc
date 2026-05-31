#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

require_file() {
  test -f "$ROOT_DIR/$1" || {
    printf 'Missing required file: %s\n' "$1" >&2
    exit 1
  }
}

require_grep() {
  local pattern="$1"
  local file="$2"
  grep -q "$pattern" "$ROOT_DIR/$file" || {
    printf 'Expected pattern not found in %s: %s\n' "$file" "$pattern" >&2
    exit 1
  }
}

require_file "gateway/package.json"
require_file "gateway/Dockerfile"
require_file "gateway/src/app.mjs"
require_file "gateway/src/authRouter.mjs"
require_file "gateway/src/proxyRouter.mjs"
require_file "gateway/src/routeRegistry.mjs"
require_file "gateway/src/productConfigurationFallback.mjs"
require_file "gateway/test/gateway.test.mjs"
require_file "scripts/smoke-gateway.sh"
require_file "docs/phase-8-poc-gateway.md"

test -x "$ROOT_DIR/scripts/smoke-gateway.sh" || {
  printf '%s\n' 'scripts/smoke-gateway.sh must be executable.' >&2
  exit 1
}

require_grep 'productCatalogManagement' "gateway/src/routeRegistry.mjs"
require_grep 'productOrderingManagement' "gateway/src/routeRegistry.mjs"
require_grep 'normalizePath' "gateway/src/routeRegistry.mjs"
require_grep 'PUBLIC_SERVICE' "gateway/src/routeRegistry.mjs"
require_grep 'USER_REQUIRED' "gateway/src/routeRegistry.mjs"
require_grep 'serviceSession: false' "gateway/src/authRouter.mjs"
require_grep 'queryProductConfiguration' "gateway/src/productConfigurationFallback.mjs"
require_grep 'poc-gateway:' "docker-compose.yml"
require_grep 'PRODUCT_CONFIGURATOR_SERVICE: "http://poc-gateway:8088"' "docker-compose.yml"
require_grep 'profiles: \["gateway"\]' "docker-compose.yml"
require_grep 'POC_GATEWAY_PORT=8088' ".env.example"
require_grep 'make gateway-test' "docs/phase-8-poc-gateway.md"
require_grep 'verify-phase8' "Makefile"
require_grep 'gateway-up' "Makefile"

printf '%s\n' 'Phase 8 gateway files verified.'
