#!/usr/bin/env sh
set -eu

AUTH_USERROLE_URL="${AUTH_USERROLE_URL:-http://localhost:18085}"
TOKEN="${AUTH_USERROLE_TOKEN:-local-seed-token}"
SEED_DIR="${SEED_DIR:-infrastructure/auth-userrole}"
BASE_URL="$AUTH_USERROLE_URL/userRolePermission/v1"

post_each() {
  endpoint="$1"
  file="$2"

  node -e '
const fs = require("fs");
const data = JSON.parse(fs.readFileSync(process.argv[1], "utf8"));
for (const item of data) console.log(JSON.stringify(item));
' "$file" | while IFS= read -r payload; do
    curl -fsS -X POST \
      -H 'Content-Type: application/json' \
      -H "Token: $TOKEN" \
      --data "$payload" \
      "$BASE_URL/$endpoint" >/dev/null || true
  done
}

printf '%s\n' 'Seeding auth-userrole component configuration...'
post_each "componentConfiguration" "$SEED_DIR/component-configurations.json"

printf '%s\n' 'Seeding auth-userrole function configuration...'
post_each "functionConfiguration" "$SEED_DIR/function-configurations.json"

printf '%s\n' 'Seeding auth-userrole entitlements...'
curl -fsS -X POST \
  -H 'Content-Type: application/json' \
  -H "Token: $TOKEN" \
  --data-binary "@$SEED_DIR/entitlements.json" \
  "$BASE_URL/entitlement" >/dev/null || true

printf '%s\n' 'Seeding auth-userrole user roles...'
node -e '
const fs = require("fs");
const data = JSON.parse(fs.readFileSync(process.argv[1], "utf8"));
for (const item of data) console.log(JSON.stringify(item));
' "$SEED_DIR/user-roles.json" | while IFS= read -r payload; do
  curl -fsS -X POST \
    -H 'Content-Type: application/json' \
    -H "Authorization: Bearer $TOKEN" \
    --data "$payload" \
    "$BASE_URL/userRole" >/dev/null || true
done

printf '%s\n' 'auth-userrole seed request completed.'

