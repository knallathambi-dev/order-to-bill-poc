.PHONY: help verify-phase1 verify-phase2 list-discobole-images status

help:
	@printf '%s\n' 'Order-to-Bill POC commands'
	@printf '%s\n' ''
	@printf '%s\n' 'Available now:'
	@printf '%s\n' '  make verify-phase1  Verify the Phase 1 repository skeleton'
	@printf '%s\n' '  make verify-phase2  Verify copied Discobole source inventory'
	@printf '%s\n' '  make list-discobole-images  List local Discobole Docker image targets'
	@printf '%s\n' '  make status         Show git status'
	@printf '%s\n' ''
	@printf '%s\n' 'Runtime commands will be added in later phases.'

verify-phase1:
	@test -d infrastructure/keycloak
	@test -d infrastructure/kafka
	@test -d infrastructure/mongodb
	@test -d discobole-runtime
	@test -d discobole-services
	@test -d discobole-ui
	@test -d custom-services/qualification-service
	@test -d custom-services/activation-service
	@test -d custom-services/billing-service
	@test -d gateway
	@test -d ui-overrides
	@test -d scripts
	@test -d docs
	@test -f README.md
	@test -f .env.example
	@test -f .gitignore
	@test -f Makefile
	@grep -q 'KAFKA_IMAGE=apache/kafka:4.3.0' .env.example
	@grep -qi 'KRaft' README.md
	@printf '%s\n' 'Phase 1 skeleton verified.'

verify-phase2: verify-phase1
	@test -f docs/phase-2-discobole-runtime.md
	@test -x scripts/build-discobole-images.sh
	@test -f discobole-services/disco-order-management/order-capture/pom.xml
	@test -f discobole-services/disco-order-management/order-inventory/pom.xml
	@test -f discobole-services/disco-order-management/order-inventory-spec/pom.xml
	@test -f discobole-services/disco-order-orchestration/orchestration-delivery/pom.xml
	@test -f discobole-services/disco-order-orchestration/orchestration-delivery-management/pom.xml
	@test -f discobole-services/disco-order-orchestration/orchestration-delivery-fallout/pom.xml
	@test -f discobole-services/disco-order-orchestration/orchestration-delivery-commons/pom.xml
	@test -f discobole-services/disco-security/auth-userrole/pom.xml
	@test -f discobole-services/process-flow/pom.xml
	@test -f discobole-services/disco-catalog/catalog-product-catalog/pom.xml
	@test -f discobole-services/disco-catalog/catalog-product-specification/pom.xml
	@test -f discobole-services/disco-catalog/catalog-product-offering/pom.xml
	@test -f discobole-services/disco-product-inventory/product-inventory/pom.xml
	@test -f discobole-ui/selfcare-ui/package.json
	@test -f discobole-ui/selfcare-ui/server/package.json
	@test -f discobole-ui/disco-admin-ui/common-ui/package.json
	@test -f discobole-ui/disco-admin-ui/order-inventory-ui/package.json
	@test -f discobole-ui/disco-admin-ui/order-orchestration-ui/package.json
	@test -f discobole-ui/disco-admin-ui/hostmode-ui/package.json
	@if find discobole-services discobole-ui -type d \( -name .git -o -name target -o -name node_modules -o -name .m2 \) | grep .; then \
		printf '%s\n' 'Unexpected generated or nested repository folders found.'; \
		exit 1; \
	fi
	@printf '%s\n' 'Phase 2 Discobole source inventory verified.'

list-discobole-images:
	@scripts/build-discobole-images.sh --list

status:
	@git status --short
