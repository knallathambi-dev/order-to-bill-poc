#!/usr/bin/env sh
set -eu

IMAGE_PREFIX="${IMAGE_PREFIX:-otb-poc}"

MODULES="
discobole-services/disco-order-management/order-capture
discobole-services/disco-order-management/order-inventory
discobole-services/disco-order-orchestration/orchestration-delivery
discobole-services/disco-order-orchestration/orchestration-delivery-management
discobole-services/disco-order-orchestration/orchestration-delivery-fallout
discobole-services/disco-catalog/catalog-product-catalog
discobole-services/disco-catalog/catalog-product-specification
discobole-services/disco-catalog/catalog-product-offering
discobole-services/disco-catalog/catalog-product-offering-price
discobole-services/disco-catalog/catalog-product-category
discobole-services/disco-catalog/catalog-product-catalog-administration
discobole-services/disco-catalog/catalog-product-lifecycle-management
discobole-services/disco-catalog/catalog-event-service
discobole-services/disco-catalog/catalog-policy-rule
discobole-services/disco-product-inventory/product-inventory
discobole-services/disco-security/auth-userrole
discobole-ui/selfcare-ui
discobole-ui/disco-admin-ui/order-inventory-ui
discobole-ui/disco-admin-ui/order-orchestration-ui
discobole-ui/disco-admin-ui/product-inventory-ui
discobole-ui/disco-admin-ui/hostmode-ui
"

usage() {
  printf '%s\n' "Usage: $0 [--list]"
}

if [ "${1:-}" = "--help" ]; then
  usage
  exit 0
fi

if [ "${1:-}" = "--list" ]; then
  for module in $MODULES; do
    if [ -f "$module/Dockerfile" ]; then
      image_name="$IMAGE_PREFIX/$(basename "$module")"
      printf '%s -> %s\n' "$module" "$image_name"
    fi
  done
  exit 0
fi

for module in $MODULES; do
  if [ ! -f "$module/Dockerfile" ]; then
    printf 'Skipping %s: no Dockerfile\n' "$module"
    continue
  fi

  image_name="$IMAGE_PREFIX/$(basename "$module")"
  printf 'Building %s from %s\n' "$image_name" "$module"
  docker build -t "$image_name" "$module"
done

