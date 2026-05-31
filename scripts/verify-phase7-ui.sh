#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_BUILDS=false

if [[ "${1:-}" == "--build" ]]; then
  RUN_BUILDS=true
fi

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

require_file "docs/phase-7-reused-ui-portals.md"
require_file "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_file "discobole-ui/selfcare-ui/server/app.mjs"
require_file "discobole-ui/selfcare-ui/server/authRouter.mjs"
require_file "discobole-ui/selfcare-ui/src/setupProxy.js"
require_file "discobole-ui/selfcare-ui/src/pages/Home/services/fetchOffers.js"
require_file "discobole-ui/selfcare-ui/src/utlis/constants.js"
require_file "discobole-ui/disco-admin-ui/order-inventory-ui/package.json"
require_file "discobole-ui/disco-admin-ui/order-orchestration-ui/package.json"

require_grep "DEFAULT_ROUTE_TARGETS" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "http://localhost:18086" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "http://localhost:18080" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "http://localhost:18081" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "http://localhost:18082" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "http://localhost:18084" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "http://localhost:18085" "discobole-ui/selfcare-ui/server/proxyRouter.mjs"
require_grep "/api/auth" "discobole-ui/selfcare-ui/server/app.mjs"
require_grep "queryProductConfiguration" "discobole-ui/selfcare-ui/server/app.mjs"
require_grep "/status" "discobole-ui/selfcare-ui/server/authRouter.mjs"
require_grep "http://localhost:5000" "discobole-ui/selfcare-ui/src/setupProxy.js"
require_grep "Fiber Broadband 300 Mbps" "discobole-ui/selfcare-ui/src/pages/Home/services/fetchOffers.js"
require_grep "Static IP Add-on" "discobole-ui/selfcare-ui/src/pages/Home/services/fetchOffers.js"
require_grep "Broadband activation" "discobole-ui/selfcare-ui/src/utlis/constants.js"
require_grep "Static IP activation" "discobole-ui/selfcare-ui/src/utlis/constants.js"
require_grep "file:../common-ui" "discobole-ui/disco-admin-ui/order-inventory-ui/package.json"
require_grep "file:../common-ui" "discobole-ui/disco-admin-ui/order-orchestration-ui/package.json"
require_grep "APP_SECURITY_DISCOADMINROLE" "docker-compose.yml"
require_grep "PROCESS_FLOW_ROLE_ADMIN: \"OTB_CUSTOMER\"" "docker-compose.yml"

if [[ "$RUN_BUILDS" == "true" ]]; then
  (
    cd "$ROOT_DIR/discobole-ui/selfcare-ui/server"
    npm install
  )
  (
    cd "$ROOT_DIR/discobole-ui/selfcare-ui"
    npm install
    npm run build
  )
  (
    cd "$ROOT_DIR/discobole-ui/disco-admin-ui/common-ui"
    npm install
    npm run build
  )
  (
    cd "$ROOT_DIR/discobole-ui/disco-admin-ui/order-inventory-ui"
    npm install
    npm run build
  )
  (
    cd "$ROOT_DIR/discobole-ui/disco-admin-ui/order-orchestration-ui"
    npm install
    npm run build
  )
fi

printf '%s\n' 'Phase 7 UI portal files verified.'
