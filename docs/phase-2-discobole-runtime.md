# Phase 2 Discobole Runtime Strategy

## Decision

This repository is the full Order-to-Bill POC mono-repo. Selected Discobole services and UI portals have been copied from:

```text
/Users/knallathambi/Engineering/projects/java-apps/discoble-monorepo
```

The copied source in this repository is now the POC runtime and modification source of truth. The external monorepo remains an upstream reference.

## Copied Service Source

Copied into `discobole-services/`:

| Source family | Included modules | POC role |
| --- | --- | --- |
| `disco-order-management` | `order-capture`, `order-inventory`, `order-inventory-spec`, `order-commons`, `order-followup`, `mock-server` | Order capture and ProductOrder lifecycle |
| `disco-order-orchestration` | `orchestration-delivery`, `orchestration-delivery-management`, `orchestration-delivery-fallout`, specs, commons, docs/tests/config | COOD, delivery dispatch, fallout/retry |
| `disco-catalog` | product catalog, offering, offering price, specification, category, lifecycle, admin, event, policy modules | Product-driven decomposition and catalog seed support |
| `disco-security` | `auth-userrole` | TMF672 roles, permissions, and entitlements |
| `process-flow` | full module | TMF701 process/fallout support |
| `disco-product-inventory` | `product-inventory`, `product-inventory-spec` | Operator UI and orchestration/product visibility support |

Copy excludes generated or local-only folders:

- `.git`
- `target`
- `.m2`
- `node_modules`
- `dist`
- `build`

## Copied UI Source

Copied into `discobole-ui/`:

| Source | POC role |
| --- | --- |
| `selfcare-ui` | Customer broadband offer selection, order capture, and order tracking |
| `disco-admin-ui/order-inventory-ui` | Operator ProductOrder monitoring |
| `disco-admin-ui/order-orchestration-ui` | Operator orchestration and fallout monitoring |
| `disco-admin-ui/common-ui` | Shared admin UI package |
| `disco-admin-ui/hostmode-ui` | Optional admin shell for composed operator experience |
| `disco-admin-ui/product-inventory-ui` | Supporting product inventory view used by the admin shell |

## Local Build Strategy

All Discobole images should be built from copied source in this repository.

Initial image build helper:

```sh
scripts/build-discobole-images.sh --list
scripts/build-discobole-images.sh
```

The script builds Docker images only for copied modules that already provide Dockerfiles. It does not replace Maven or npm dependency resolution; later phases can add root-level aggregation, pinned build profiles, and CI tasks after the exact runtime set is proven.

## Preliminary Build Order

The copied modules are still independent Discobole projects, so the initial build order preserves their upstream dependency shape:

1. `discobole-services/process-flow`
2. `discobole-services/disco-order-orchestration/orchestration-delivery-commons`
3. `discobole-services/disco-order-orchestration/orchestration-delivery-spec`
4. `discobole-services/disco-order-orchestration/orchestration-delivery-fallout-spec`
5. `discobole-services/disco-order-management/order-inventory-spec`
6. `discobole-services/disco-order-management/order-commons`
7. `discobole-services/disco-catalog/*`
8. `discobole-services/disco-product-inventory/*`
9. `discobole-services/disco-security/auth-userrole`
10. `discobole-services/disco-order-management/order-capture`
11. `discobole-services/disco-order-management/order-inventory`
12. `discobole-services/disco-order-orchestration/orchestration-delivery`
13. `discobole-services/disco-order-orchestration/orchestration-delivery-management`
14. `discobole-services/disco-order-orchestration/orchestration-delivery-fallout`
15. `discobole-ui/selfcare-ui`
16. `discobole-ui/disco-admin-ui/common-ui`
17. `discobole-ui/disco-admin-ui/order-inventory-ui`
18. `discobole-ui/disco-admin-ui/order-orchestration-ui`
19. `discobole-ui/disco-admin-ui/product-inventory-ui`
20. `discobole-ui/disco-admin-ui/hostmode-ui`

## Phase 2 Boundaries

Phase 2 copies and inventories source code, documents the runtime strategy, and adds build helpers.

Phase 2 does not:

- create the root Docker Compose stack
- change Kafka, MongoDB, or Keycloak runtime configuration
- modify Discobole service behavior
- adapt UI screens to broadband content
- scaffold custom simulator services

