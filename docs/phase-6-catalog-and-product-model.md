# Phase 6 Catalog and Product Model

## Scope

Phase 6 seeds the broadband product model that the reused Discobole services will validate and orchestrate.

This phase adds the catalog content needed for the target order:

- `Fiber Broadband 300 Mbps`
- `Static IP Add-on`

It also captures the product/spec relationships needed for downstream COOD decomposition:

- root broadband bundle
- static IP dependency on broadband activation
- billing dependency after service activation

## Seed Artifacts

The catalog seed bundle is stored in the product-inventory Bruno collection:

- `discobole-services/disco-product-inventory/product-inventory/bruno/CPIB collection/catalog/phase-6-create-broadband-spec.bru`
- `discobole-services/disco-product-inventory/product-inventory/bruno/CPIB collection/catalog/phase-6-create-static-ip-spec.bru`
- `discobole-services/disco-product-inventory/product-inventory/bruno/CPIB collection/catalog/phase-6-create-billing-spec.bru`
- `discobole-services/disco-product-inventory/product-inventory/bruno/CPIB collection/catalog/phase-6-create-fiber-offer.bru`
- `discobole-services/disco-product-inventory/product-inventory/bruno/CPIB collection/catalog/phase-6-create-static-ip-offer.bru`
- `discobole-services/disco-product-inventory/product-inventory/bruno/CPIB collection/catalog/phase-6-link-offering-relationships.bru`

These requests are shaped from the upstream catalog examples already present in the collection and are intended to be executed against a running local `product-inventory`/catalog stack.

## Seed Model

### Product specifications

- `Broadband Service`
- `Static IP Service`
- `Billing Initiation`

### Product offerings

- `Fiber Broadband 300 Mbps`
- `Static IP Add-on`

### Relationships

- `Fiber Broadband 300 Mbps` is the root bundled offering.
- `Static IP Add-on` is a bundled child and depends on broadband activation.
- `Billing Initiation` is modeled as a product node so COOD can place billing after service activation when the orchestration plan is decomposed.

## Usage

1. Start the infrastructure and core services.
2. Seed auth-userrole and the Phase 6 catalog requests.
3. Verify the broadband offers are visible through the catalog endpoints and are consumable by the customer UI and order capture flow.

## Verification Status

Status as of 2026-05-31:

- Catalog seed data is present and readable from the local catalog API.
  - `GET http://localhost:18086/productCatalogManagement/v1/productSpecification/6a1c07a1cae7bc7afad1a7bb` returned `200` for `Broadband Service`.
  - Seeded offers found in catalog:
    - `Fiber Broadband 300 Mbps` (`6a1c07b3323b73d984d1a7bb`)
    - `Static IP Add-on` (`6a1c07b865b5db7406d1a7bb`)
- Order Capture accepts the seeded offer structure.
  - Verified the `selectOfferOrContract` task with the Fiber Broadband offer.
  - The flow advanced to `OrderCapture.confirmConfiguration`.
  - The request must include `channel`; otherwise ProcessFlow cannot populate `CHANNEL_ID`.
- Accepted ProductOrder events produce a COOD orchestration plan.
  - Verified accepted ProductOrder `phase6-1780226407`.
  - COOD created orchestration plan `0920ab23-f4f3-4271-91af-8a47099bcf5f`.
  - The plan reached `Acknowledged` and contains three nodes for broadband, static IP, and billing.
- Core service verification passed with `./scripts/verify-core-services.sh`.
- Customer UI visibility remains pending and should be completed with Phase 7 UI work.

## POC Runtime Changes

To keep the POC moving, local inter-service authentication was bypassed for the backend verification path:

- Product Catalog runs with `APP_SECURITY_DISABLED=true`.
- COOD runs with the `no-security` profile for local verification.
- COOD Fallout runs with the `no-security` profile; its default security configuration is disabled when that profile is active.

COOD was also made tolerant of seeded product specifications that omit `relatedResource`; without that null-safe check, plan initialization failed before node creation completed.

## Notes

The Phase 6 seed content intentionally stays close to the upstream Discobole catalog shapes. If the catalog service requires a stricter payload variant in this environment, update the Bruno requests rather than duplicating a second seed format.

The authentication bypass is POC-only and should be removed or replaced with proper service-account token propagation before hardening this flow.
