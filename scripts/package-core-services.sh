#!/usr/bin/env sh
set -eu

MVN_ARGS="${MVN_ARGS:--DskipTests -Dmaven.javadoc.skip=true -Dlicense.skip=true}"

if [ -f "scripts/use-java17.sh" ]; then
  # Source the repo helper so JAVA_HOME/PATH are set for this shell and Maven.
  # shellcheck disable=SC1091
  . "scripts/use-java17.sh"
fi

java_version="$(java -version 2>&1 | sed -n 's/.* version "\([0-9][0-9]*\).*/\1/p' | head -n 1)"
if [ "$java_version" != "17" ]; then
  printf 'Java 17 is required to package copied Discobole core services; current Java major version is %s.\n' "${java_version:-unknown}" >&2
  printf '%s\n' 'Ensure scripts/use-java17.sh can resolve a JDK 17 installation, then retry make package-core-services.' >&2
  exit 1
fi

build_maven_module() {
  module="$1"
  printf 'Packaging %s\n' "$module"
  (cd "$module" && mvn -q clean install $MVN_ARGS)
}

build_maven_module "discobole-services/process-flow"
build_maven_module "discobole-services/disco-order-orchestration/orchestration-delivery-commons"
build_maven_module "discobole-services/disco-order-orchestration/orchestration-delivery-spec"
build_maven_module "discobole-services/disco-order-orchestration/orchestration-delivery-fallout-spec"
build_maven_module "discobole-services/disco-order-management/order-inventory-spec"
build_maven_module "discobole-services/disco-order-management/order-commons"
build_maven_module "discobole-services/disco-product-inventory/product-inventory-spec"
build_maven_module "discobole-services/disco-security/auth-userrole"
build_maven_module "discobole-services/disco-order-management/order-capture"
build_maven_module "discobole-services/disco-order-management/order-inventory"
build_maven_module "discobole-services/disco-catalog/catalog-product-catalog"
build_maven_module "discobole-services/disco-catalog/catalog-product-specification"
build_maven_module "discobole-services/disco-catalog/catalog-product-offering"
build_maven_module "discobole-services/disco-product-inventory/product-inventory"
build_maven_module "discobole-services/disco-order-orchestration/orchestration-delivery"
build_maven_module "discobole-services/disco-order-orchestration/orchestration-delivery-management"
build_maven_module "discobole-services/disco-order-orchestration/orchestration-delivery-fallout"

printf '%s\n' 'Core Discobole service packaging complete.'
