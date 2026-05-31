#!/usr/bin/env sh
set -eu

check_health() {
  name="$1"
  url="$2"
  service="$3"

  printf 'Checking %s health at %s\n' "$name" "$url"

  attempts=0
  max_attempts="${VERIFY_MAX_ATTEMPTS:-45}"
  delay_seconds="${VERIFY_DELAY_SECONDS:-2}"
  curl_timeout_seconds="${VERIFY_CURL_TIMEOUT_SECONDS:-3}"
  container_id=$(docker compose --profile infra --profile core ps -q "$service" 2>/dev/null || true)

  if [ -z "$container_id" ]; then
    printf '%s container is not present. Did you run make core-up?\n' "$name" >&2
    return 1
  fi

  initial_restart_count=$(docker inspect "$container_id" --format '{{.RestartCount}}' 2>/dev/null || printf '0')

  while [ "$attempts" -lt "$max_attempts" ]; do
    status=$(docker inspect "$container_id" --format '{{.State.Status}}' 2>/dev/null || printf 'missing')
    restart_count=$(docker inspect "$container_id" --format '{{.RestartCount}}' 2>/dev/null || printf '0')

    case "$status" in
      exited|dead|restarting|missing)
        printf '%s container is %s before health became UP.\n' "$name" "$status" >&2
        docker compose --profile infra --profile core logs --tail=80 "$service" >&2 || true
        return 1
        ;;
    esac

    if [ "$restart_count" -gt "$initial_restart_count" ]; then
      printf '%s container restarted while waiting for health (%s -> %s).\n' "$name" "$initial_restart_count" "$restart_count" >&2
      docker compose --profile infra --profile core logs --tail=80 "$service" >&2 || true
      return 1
    fi

    if response=$(curl -fsS --connect-timeout "$curl_timeout_seconds" --max-time "$curl_timeout_seconds" "$url" 2>/dev/null); then
      if printf '%s' "$response" | grep UP >/dev/null; then
        return 0
      fi
    fi

    attempts=$((attempts + 1))
    sleep "$delay_seconds"
  done

  printf '%s health did not report UP after %s attempts.\n' "$name" "$max_attempts" >&2
  docker compose --profile infra --profile core logs --tail=80 "$service" >&2 || true
  return 1
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

check_health "auth-userrole" "http://localhost:${AUTH_USERROLE_MANAGEMENT_PORT:-19085}/actuator/health" "auth-userrole"
check_health "order-capture" "http://localhost:${ORDER_CAPTURE_MANAGEMENT_PORT:-19080}/actuator/health" "order-capture"
check_health "order-inventory" "http://localhost:${ORDER_INVENTORY_MANAGEMENT_PORT:-19081}/actuator/health" "order-inventory"
check_health "product-catalog" "http://localhost:${PRODUCT_CATALOG_MANAGEMENT_PORT:-19086}/actuator/health" "product-catalog"
check_health "product-specification" "http://localhost:${PRODUCT_SPECIFICATION_MANAGEMENT_PORT:-19087}/actuator/health" "product-specification"
check_health "product-offering" "http://localhost:${PRODUCT_OFFERING_MANAGEMENT_PORT:-19088}/actuator/health" "product-offering"
check_health "product-inventory" "http://localhost:${PRODUCT_INVENTORY_MANAGEMENT_PORT:-19089}/actuator/health" "product-inventory"
check_health "orchestration-delivery" "http://localhost:${ORCHESTRATION_DELIVERY_MANAGEMENT_PORT:-19082}/actuator/health" "orchestration-delivery"
check_health "orchestration-delivery-management" "http://localhost:${DELIVERY_MANAGEMENT_MANAGEMENT_PORT:-19083}/actuator/health" "orchestration-delivery-management"
check_health "orchestration-delivery-fallout" "http://localhost:${DELIVERY_FALLOUT_MANAGEMENT_PORT:-19084}/actuator/health" "orchestration-delivery-fallout"

check_openapi_if_available "auth-userrole" "http://localhost:${AUTH_USERROLE_PORT:-18085}/v3/api-docs"
check_openapi_if_available "order-inventory" "http://localhost:${ORDER_INVENTORY_PORT:-18081}/v3/api-docs"
check_openapi_if_available "orchestration-delivery" "http://localhost:${ORCHESTRATION_DELIVERY_PORT:-18082}/v3/api-docs"

printf '%s\n' 'Core Discobole service verification complete.'
