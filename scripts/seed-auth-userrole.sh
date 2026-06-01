#!/usr/bin/env sh
set -eu

AUTH_USERROLE_URL="${AUTH_USERROLE_URL:-http://localhost:18085}"
SEED_DIR="${SEED_DIR:-infrastructure/auth-userrole}"
BASE_URL="$AUTH_USERROLE_URL/userRolePermission/v1"

# Get Keycloak admin token
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
KEYCLOAK_AUTH_URL="${KEYCLOAK_AUTH_URL:-$KEYCLOAK_URL/realms/discobole/protocol/openid-connect/token}"
KEYCLOAK_CLIENT_ID="${KEYCLOAK_CLIENT_ID:-auth-userrole}"
KEYCLOAK_CLIENT_SECRET="${KEYCLOAK_CLIENT_SECRET:-change-me}"
KEYCLOAK_ADMIN_URL="${KEYCLOAK_ADMIN_URL:-$KEYCLOAK_URL/admin/realms/discobole}"
KEYCLOAK_MASTER_TOKEN_URL="${KEYCLOAK_MASTER_TOKEN_URL:-$KEYCLOAK_URL/realms/master/protocol/openid-connect/token}"
KEYCLOAK_ADMIN_USER="${KEYCLOAK_ADMIN_USER:-admin}"
KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-admin}"

# Get token from Keycloak using the auth-userrole client credentials
get_keycloak_token() {
  TOKEN=$(curl -fsS -X POST \
    "$KEYCLOAK_AUTH_URL" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=$KEYCLOAK_CLIENT_ID" \
    -d "client_secret=$KEYCLOAK_CLIENT_SECRET" \
    -d "grant_type=client_credentials" 2>/dev/null | jq -r .access_token)
  
  if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo "$TOKEN"
    return
  fi
  
  echo "Failed to get Keycloak token"
  exit 1
}

get_admin_token() {
  ADMIN_TOKEN=$(curl -fsS -X POST \
    "$KEYCLOAK_MASTER_TOKEN_URL" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=admin-cli" \
    -d "username=$KEYCLOAK_ADMIN_USER" \
    -d "password=$KEYCLOAK_ADMIN_PASSWORD" \
    -d "grant_type=password" 2>/dev/null | jq -r .access_token)

  if [ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "null" ]; then
    echo "$ADMIN_TOKEN"
    return
  fi

  echo "Failed to get Keycloak admin token"
  exit 1
}

ensure_auth_userrole_permissions() {
  ADMIN_TOKEN=$(get_admin_token)
  CLIENT_UUID=$(curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients?clientId=$KEYCLOAK_CLIENT_ID" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')
  SERVICE_ACCOUNT_ID=$(curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients/$CLIENT_UUID/service-account-user" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')
  REALM_MGMT_ID=$(curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients?clientId=realm-management" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')
  REALM_ADMIN_ROLE=$(curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients/$REALM_MGMT_ID/roles/realm-admin" \
    -H "Authorization: Bearer $ADMIN_TOKEN")

  curl -fsS -X POST \
    "$KEYCLOAK_ADMIN_URL/users/$SERVICE_ACCOUNT_ID/role-mappings/clients/$REALM_MGMT_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    --data "[$REALM_ADMIN_ROLE]" >/dev/null
}

ensure_service_account_client_role() {
  client_id="$1"
  role_name="$2"
  ADMIN_TOKEN=$(get_admin_token)
  CLIENT_UUID=$(curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients?clientId=$client_id" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')
  SERVICE_ACCOUNT_ID=$(curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients/$CLIENT_UUID/service-account-user" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')

  if ! curl -fsS \
    "$KEYCLOAK_ADMIN_URL/clients/$CLIENT_UUID/roles/$role_name" \
    -H "Authorization: Bearer $ADMIN_TOKEN" >/tmp/otb-poc-client-role.json 2>/dev/null; then
    curl -fsS -X POST \
      "$KEYCLOAK_ADMIN_URL/clients/$CLIENT_UUID/roles" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      --data "{\"name\":\"$role_name\"}" >/dev/null
    curl -fsS \
      "$KEYCLOAK_ADMIN_URL/clients/$CLIENT_UUID/roles/$role_name" \
      -H "Authorization: Bearer $ADMIN_TOKEN" >/tmp/otb-poc-client-role.json
  fi

  curl -fsS -X POST \
    "$KEYCLOAK_ADMIN_URL/users/$SERVICE_ACCOUNT_ID/role-mappings/clients/$CLIENT_UUID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    --data "[$(cat /tmp/otb-poc-client-role.json)]" >/dev/null || true
}

TOKEN=$(get_keycloak_token)
ensure_auth_userrole_permissions
ensure_service_account_client_role "order-capture" "OTB_CUSTOMER"
ensure_service_account_client_role "poc-gateway" "OTB_ORDER_OPERATOR"

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
      -H "Authorization: Bearer $TOKEN" \
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
  -H "Authorization: Bearer $TOKEN" \
  --data-binary "@$SEED_DIR/entitlements.json" \
  "$BASE_URL/entitlement" >/dev/null 2>&1 || true

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
