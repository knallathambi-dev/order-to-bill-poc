.PHONY: help verify-phase1 status

help:
	@printf '%s\n' 'Order-to-Bill POC commands'
	@printf '%s\n' ''
	@printf '%s\n' 'Available now:'
	@printf '%s\n' '  make verify-phase1  Verify the Phase 1 repository skeleton'
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

status:
	@git status --short
