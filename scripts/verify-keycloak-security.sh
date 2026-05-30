#!/usr/bin/env sh
set -eu

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
REALM="${KEYCLOAK_REALM:-discobole}"

token_endpoint="$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token"

check_password_grant() {
  username="$1"
  password="$2"
  client_id="$3"

  printf 'Checking token issuance for %s through %s\n' "$username" "$client_id"
  curl -fsS \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode "grant_type=password" \
    --data-urlencode "client_id=$client_id" \
    --data-urlencode "username=$username" \
    --data-urlencode "password=$password" \
    "$token_endpoint" | grep '"access_token"' >/dev/null
}

check_client_credentials() {
  client_id="$1"
  secret="${2:-change-me}"

  printf 'Checking service token issuance for %s\n' "$client_id"
  curl -fsS \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode "grant_type=client_credentials" \
    --data-urlencode "client_id=$client_id" \
    --data-urlencode "client_secret=$secret" \
    "$token_endpoint" | grep '"access_token"' >/dev/null
}

curl -fsS "$KEYCLOAK_URL/realms/$REALM/.well-known/openid-configuration" >/dev/null
check_password_grant "customer@otb.com" "customer" "selfcare-ui"
check_password_grant "operator@otb.com" "operator" "admin-ui"
check_password_grant "admin@otb.com" "admin" "admin-ui"
check_client_credentials "poc-gateway"
check_client_credentials "order-capture"
check_client_credentials "order-inventory"
check_client_credentials "orchestration-delivery"
check_client_credentials "orchestration-delivery-management"
check_client_credentials "orchestration-delivery-fallout"
check_client_credentials "auth-userrole"
check_client_credentials "product-catalog"
check_client_credentials "qualification-service"
check_client_credentials "activation-service"
check_client_credentials "billing-service"

printf '%s\n' 'Keycloak security seed verified.'

