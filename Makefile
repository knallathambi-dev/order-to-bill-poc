.PHONY: help verify-phase1 verify-phase2 verify-phase3 verify-phase4 verify-phase5 verify-phase7 verify-phase8 verify-phase9 phase7-ui-build build-ui-images list-ui-images ui-up gateway-install gateway-test gateway-up gateway-verify list-discobole-images package-core-services build-core-service-images list-core-service-images package-simulator-services build-simulator-service-images list-simulator-service-images simulator-up simulator-verify infra-up infra-bootstrap infra-verify infra-down core-up core-verify core-down security-verify-keycloak security-seed-auth-userrole status

help:
	@printf '%s\n' 'Order-to-Bill POC commands'
	@printf '%s\n' ''
	@printf '%s\n' 'Available now:'
	@printf '%s\n' '  make verify-phase1  Verify the Phase 1 repository skeleton'
	@printf '%s\n' '  make verify-phase2  Verify copied Discobole source inventory'
	@printf '%s\n' '  make verify-phase3  Verify infrastructure files and scripts'
	@printf '%s\n' '  make verify-phase4  Verify security seed files and scripts'
	@printf '%s\n' '  make verify-phase5  Verify core service Compose/build files'
	@printf '%s\n' '  make verify-phase7  Verify Phase 7 UI portal implementation files'
	@printf '%s\n' '  make verify-phase8  Verify Phase 8 POC gateway files'
	@printf '%s\n' '  make verify-phase9  Verify Phase 9 simulator service files'
	@printf '%s\n' '  make phase7-ui-build Build Phase 7 UI packages where possible'
	@printf '%s\n' '  make build-ui-images Build selfcare and admin UI Docker images'
	@printf '%s\n' '  make list-ui-images  List local UI Docker image targets'
	@printf '%s\n' '  make ui-up           Start Dockerized UI portals'
	@printf '%s\n' '  make gateway-install Install POC gateway Node dependencies'
	@printf '%s\n' '  make gateway-test    Run POC gateway unit tests'
	@printf '%s\n' '  make gateway-up      Start POC gateway with infra/core profiles'
	@printf '%s\n' '  make gateway-verify  Run POC gateway runtime smoke checks'
	@printf '%s\n' '  make list-discobole-images  List local Discobole Docker image targets'
	@printf '%s\n' '  make package-core-services  Package copied Discobole core services'
	@printf '%s\n' '  make build-core-service-images Build copied Discobole core images'
	@printf '%s\n' '  make list-core-service-images List copied Discobole core image targets'
	@printf '%s\n' '  make package-simulator-services Package Phase 9 simulator services'
	@printf '%s\n' '  make build-simulator-service-images Build Phase 9 simulator images'
	@printf '%s\n' '  make list-simulator-service-images List Phase 9 simulator image targets'
	@printf '%s\n' '  make simulator-up    Start Phase 9 simulator services'
	@printf '%s\n' '  make simulator-verify Run Phase 9 simulator runtime smoke checks'
	@printf '%s\n' '  make infra-up        Start Phase 3 infrastructure'
	@printf '%s\n' '  make infra-bootstrap Create topics and register Debezium connectors'
	@printf '%s\n' '  make infra-verify    Verify running infrastructure'
	@printf '%s\n' '  make infra-down      Stop Phase 3 infrastructure'
	@printf '%s\n' '  make core-up         Start Phase 5 Discobole core services'
	@printf '%s\n' '  make core-verify     Verify running Discobole core services'
	@printf '%s\n' '  make core-down       Stop Phase 5 Discobole core services'
	@printf '%s\n' '  make security-verify-keycloak  Verify Keycloak realm token issuance'
	@printf '%s\n' '  make security-seed-auth-userrole Seed auth-userrole once service is running'
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
	@if find discobole-services discobole-ui -type d \( -name .git -o -name node_modules -o -name .m2 \) | grep .; then \
		printf '%s\n' 'Unexpected generated or nested repository folders found.'; \
		exit 1; \
	fi
	@printf '%s\n' 'Phase 2 Discobole source inventory verified.'

list-discobole-images:
	@scripts/build-discobole-images.sh --list

package-core-services:
	@scripts/package-core-services.sh

build-core-service-images:
	@scripts/build-core-service-images.sh

list-core-service-images:
	@scripts/build-core-service-images.sh --list

verify-phase3: verify-phase2
	@test -f docker-compose.yml
	@test -f docs/phase-3-infrastructure.md
	@test -f docs/phase-3-debezium-decision.md
	@test -f infrastructure/mongodb/init-replica-set.js
	@test -f infrastructure/kafka/topics.txt
	@test -f infrastructure/kafka/connectors/orchestration-delivery-outbox.json
	@test -f infrastructure/kafka/connectors/fallout-outbox.json
	@test -d infrastructure/keycloak/import
	@test -x scripts/create-kafka-topics.sh
	@test -x scripts/register-debezium-connectors.sh
	@test -x scripts/verify-infrastructure.sh
	@grep -q 'apache/kafka:4.3.0' .env.example
	@grep -q 'KAFKA_PROCESS_ROLES' docker-compose.yml
	@grep -q 'CONTROLLER' docker-compose.yml
	@grep -q 'quay.io/debezium/connect:3.0' .env.example
	@grep -q 'MongoEventRouter' infrastructure/kafka/connectors/orchestration-delivery-outbox.json
	@grep -q 'MongoEventRouter' infrastructure/kafka/connectors/fallout-outbox.json
	@if grep -R 'zookeeper\|ZOOKEEPER\|ZooKeeper' docker-compose.yml infrastructure/kafka infrastructure/mongodb; then \
		printf '%s\n' 'ZooKeeper reference found in Phase 3 runtime files.'; \
		exit 1; \
	fi
	@printf '%s\n' 'Phase 3 infrastructure files verified.'

infra-up:
	@docker compose --profile infra up -d

infra-bootstrap:
	@scripts/create-kafka-topics.sh
	@scripts/register-debezium-connectors.sh

infra-verify:
	@scripts/verify-infrastructure.sh

infra-down:
	@docker compose --profile infra down

verify-phase4: verify-phase3
	@test -f docs/phase-4-security-seed.md
	@test -f infrastructure/keycloak/import/discobole-realm.json
	@test -f infrastructure/auth-userrole/component-configurations.json
	@test -f infrastructure/auth-userrole/function-configurations.json
	@test -f infrastructure/auth-userrole/entitlements.json
	@test -f infrastructure/auth-userrole/user-roles.json
	@test -x scripts/verify-keycloak-security.sh
	@test -x scripts/seed-auth-userrole.sh
	@grep -q '"realm": "discobole"' infrastructure/keycloak/import/discobole-realm.json
	@grep -q '"clientId": "selfcare-ui"' infrastructure/keycloak/import/discobole-realm.json
	@grep -q '"clientId": "billing-service"' infrastructure/keycloak/import/discobole-realm.json
	@grep -q 'customer@otb.com' infrastructure/keycloak/import/discobole-realm.json
	@grep -q 'operator@otb.com' infrastructure/keycloak/import/discobole-realm.json
	@grep -q 'admin@otb.com' infrastructure/keycloak/import/discobole-realm.json
	@grep -q 'OTB_CUSTOMER' infrastructure/auth-userrole/user-roles.json
	@grep -q 'OTB_ADMIN' infrastructure/auth-userrole/user-roles.json
	@printf '%s\n' 'Phase 4 security seed files verified.'

security-verify-keycloak:
	@scripts/verify-keycloak-security.sh

security-seed-auth-userrole:
	@scripts/seed-auth-userrole.sh

verify-phase5: verify-phase4
	@test -f docs/phase-5-core-services.md
	@test -x scripts/package-core-services.sh
	@test -x scripts/build-core-service-images.sh
	@test -x scripts/verify-core-services.sh
	@grep -q 'profiles: \["core"\]' docker-compose.yml
	@grep -q 'auth-userrole:' docker-compose.yml
	@grep -q 'order-capture:' docker-compose.yml
	@grep -q 'order-inventory:' docker-compose.yml
	@grep -q 'product-catalog:' docker-compose.yml
	@grep -q 'product-specification:' docker-compose.yml
	@grep -q 'product-offering:' docker-compose.yml
	@grep -q 'product-inventory:' docker-compose.yml
	@grep -q 'orchestration-delivery:' docker-compose.yml
	@grep -q 'orchestration-delivery-management:' docker-compose.yml
	@grep -q 'orchestration-delivery-fallout:' docker-compose.yml
	@grep -q 'SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka:9092"' docker-compose.yml
	@grep -q 'http://keycloak:8080/realms/discobole' docker-compose.yml
	@grep -q 'mongodb://mongodb:27017' docker-compose.yml
	@grep -Eq 'RUN (addgroup java && adduser -D javauser java|groupadd java && useradd -r -g java javauser)' discobole-services/disco-security/auth-userrole/Dockerfile
	@printf '%s\n' 'Phase 5 core service files verified.'

core-up:
	@docker compose --profile infra --profile core up -d

core-verify:
	@scripts/verify-core-services.sh

core-down:
	@docker compose --profile infra --profile core stop auth-userrole order-capture order-inventory product-catalog product-specification product-offering product-inventory orchestration-delivery orchestration-delivery-management orchestration-delivery-fallout

verify-phase7: verify-phase5
	@scripts/verify-phase7-ui.sh

phase7-ui-build:
	@scripts/verify-phase7-ui.sh --build

build-ui-images:
	@scripts/build-ui-images.sh

list-ui-images:
	@scripts/build-ui-images.sh --list

ui-up:
	@docker compose --profile infra --profile core --profile gateway --profile ui up -d selfcare-ui order-inventory-ui order-orchestration-ui

verify-phase8:
	@scripts/verify-phase8-gateway.sh

gateway-install:
	@cd gateway && npm install

gateway-test:
	@cd gateway && npm test

gateway-up:
	@docker compose --profile infra --profile core --profile gateway up -d poc-gateway

gateway-verify:
	@scripts/smoke-gateway.sh

verify-phase9: verify-phase8
	@scripts/verify-phase9-simulators.sh

package-simulator-services:
	@scripts/package-simulator-services.sh

build-simulator-service-images:
	@scripts/build-simulator-service-images.sh

list-simulator-service-images:
	@scripts/build-simulator-service-images.sh --list

simulator-up:
	@docker compose --profile infra --profile simulators up -d qualification-service activation-service billing-service

simulator-verify:
	@scripts/smoke-simulators.sh

status:
	@git status --short
