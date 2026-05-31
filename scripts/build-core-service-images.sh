#!/usr/bin/env sh
set -eu

IMAGE_PREFIX="${IMAGE_PREFIX:-otb-poc}"

MODULES="
discobole-services/disco-security/auth-userrole
discobole-services/disco-order-management/order-capture
discobole-services/disco-order-management/order-inventory
discobole-services/disco-catalog/catalog-product-catalog
discobole-services/disco-catalog/catalog-product-specification
discobole-services/disco-catalog/catalog-product-offering
discobole-services/disco-product-inventory/product-inventory
discobole-services/disco-order-orchestration/orchestration-delivery
discobole-services/disco-order-orchestration/orchestration-delivery-management
discobole-services/disco-order-orchestration/orchestration-delivery-fallout
"

if [ "${1:-}" = "--list" ]; then
  for module in $MODULES; do
    printf '%s -> %s/%s\n' "$module" "$IMAGE_PREFIX" "$(basename "$module")"
  done
  exit 0
fi

for module in $MODULES; do
  image_name="$IMAGE_PREFIX/$(basename "$module")"
  printf 'Building %s from %s\n' "$image_name" "$module"
  docker build -t "$image_name" "$module"
done

printf '%s\n' 'Core Discobole service image build complete.'

