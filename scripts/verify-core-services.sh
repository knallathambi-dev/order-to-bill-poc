#!/usr/bin/env sh
set -eu

check_health() {
  name="$1"
  url="$2"

  printf 'Checking %s health at %s\n' "$name" "$url"
  curl -fsS "$url" | grep UP >/dev/null
}

check_openapi_if_available() {
  name="$1"
  url="$2"

  printf 'Checking %s OpenAPI at %s\n' "$name" "$url"
  if curl -fsS "$url" >/dev/null 2>&1; then
    printf '%s OpenAPI endpoint available.\n' "$name"
  else
    printf '%s OpenAPI endpoint not available yet; continuing.\n' "$name"
  fi
}

check_health "auth-userrole" "http://localhost:${AUTH_USERROLE_MANAGEMENT_PORT:-19085}/actuator/health"
check_health "order-capture" "http://localhost:${ORDER_CAPTURE_MANAGEMENT_PORT:-19080}/actuator/health"
check_health "order-inventory" "http://localhost:${ORDER_INVENTORY_MANAGEMENT_PORT:-19081}/actuator/health"
check_health "product-catalog" "http://localhost:${PRODUCT_CATALOG_MANAGEMENT_PORT:-19086}/actuator/health"
check_health "product-specification" "http://localhost:${PRODUCT_SPECIFICATION_MANAGEMENT_PORT:-19087}/actuator/health"
check_health "product-offering" "http://localhost:${PRODUCT_OFFERING_MANAGEMENT_PORT:-19088}/actuator/health"
check_health "product-inventory" "http://localhost:${PRODUCT_INVENTORY_MANAGEMENT_PORT:-19089}/actuator/health"
check_health "orchestration-delivery" "http://localhost:${ORCHESTRATION_DELIVERY_MANAGEMENT_PORT:-19082}/actuator/health"
check_health "orchestration-delivery-management" "http://localhost:${DELIVERY_MANAGEMENT_MANAGEMENT_PORT:-19083}/actuator/health"
check_health "orchestration-delivery-fallout" "http://localhost:${DELIVERY_FALLOUT_MANAGEMENT_PORT:-19084}/actuator/health"

check_openapi_if_available "auth-userrole" "http://localhost:${AUTH_USERROLE_PORT:-18085}/v3/api-docs"
check_openapi_if_available "order-inventory" "http://localhost:${ORDER_INVENTORY_PORT:-18081}/v3/api-docs"
check_openapi_if_available "orchestration-delivery" "http://localhost:${ORCHESTRATION_DELIVERY_PORT:-18082}/v3/api-docs"

printf '%s\n' 'Core Discobole service verification complete.'

