# Phase 7 Reused Discobole UI Portals

## Scope

Phase 7 adapts the copied Discobole UI portals for the local broadband Order-to-Bill POC.

The phase uses:

- `discobole-ui/selfcare-ui` for customer offer selection, order capture, and tracking.
- `discobole-ui/selfcare-ui/server` as the temporary local BFF/proxy.
- `discobole-ui/disco-admin-ui/order-inventory-ui` as a standalone operator order view.
- `discobole-ui/disco-admin-ui/order-orchestration-ui` as a standalone orchestration and fallout view.

The standalone `gateway/` service remains deferred to Phase 8.

## Local URLs

| Component | URL |
| --- | --- |
| Selfcare BFF/proxy | `http://localhost:5010` |
| Customer selfcare UI | `http://localhost:3000` |
| Order inventory UI dev server | `http://localhost:3004` |
| Order orchestration UI dev server | `http://localhost:3006` |

## Bridge Routes

`selfcare-ui/server` forwards existing Discobole API paths without introducing a new API contract.

| Browser path prefix | Local target |
| --- | --- |
| `/productCatalogManagement` | `http://localhost:18086` |
| `/orderCapture` | `http://localhost:18080` |
| `/productOrderingManagement` | `http://localhost:18081` |
| `/productInventory` | `http://localhost:18089` |
| `/cood` | `http://localhost:18082`, with `/cood` stripped |
| `/fallout` | `http://localhost:18084`, with `/fallout` stripped |
| `/userRolePermission` | `http://localhost:18085` |

The bridge also keeps `/api/auth/*` local to the selfcare server for browser session, CSRF, login, refresh, and logout behavior.

## Run Order

Start the backend baseline:

```sh
make infra-up
make infra-bootstrap
make core-up
make security-seed-auth-userrole
make core-verify
```

Start the selfcare BFF:

```sh
cd discobole-ui/selfcare-ui/server
npm install
PORT=5010 \
EXPRESS_APP_KEYCLOAK_URL=http://localhost:8080 \
EXPRESS_APP_KEYCLOAK_REALM=discobole \
EXPRESS_APP_CLIENT_ID=admin-ui \
EXPRESS_APP_USERNAME=admin@otb.com \
EXPRESS_APP_PASSWORD=admin \
EXPRESS_APP_USER_CLIENT_ID=selfcare-ui \
npm start
```

The `admin-ui` password-grant fallback is intentionally local-only for Phase 7. It gives the temporary bridge a realm-role token when standalone admin portals call the backend before a dedicated Phase 8 gateway exists. If port `5010` is already owned by macOS Control Center/AirPlay Receiver, use `PORT=5010` and point `REACT_APP_PROXY_URL` / `VITE_GATEWAY_URL` to `http://localhost:5010`.

Start customer UI:

```sh
cd discobole-ui/selfcare-ui
npm install
REACT_APP_PROXY_URL=http://localhost:5010 npm start
```

Start operator UIs standalone:

```sh
cd discobole-ui/disco-admin-ui/common-ui
npm install
npm run build

cd ../order-inventory-ui
npm install
VITE_GATEWAY_URL=http://localhost:5010 VITE_STANDALONE_MODE=true npm run dev

cd ../order-orchestration-ui
npm install
VITE_GATEWAY_URL=http://localhost:5010 VITE_STANDALONE_MODE=true npm run dev
```

## Verification

Static implementation check:

```sh
make verify-phase7
```

Build check:

```sh
make phase7-ui-build
```

Runtime bridge smoke checks, after the backend and BFF are running:

```sh
curl -fsS http://localhost:5010/health
curl -fsS 'http://localhost:5010/productCatalogManagement/v1/productOffering?lifecycleStatus=active'
curl -fsS 'http://localhost:5010/productOrderingManagement/v1/productOrder?limit=1'
curl -fsS 'http://localhost:5010/cood/orchestrationPlan?limit=1'
curl -fsS 'http://localhost:5010/fallout/falloutIncident?limit=1'
```

Customer acceptance:

- Log in as `customer@otb.com` / `customer`.
- Confirm `Fiber Broadband 300 Mbps` and `Static IP Add-on` are visible.
- Select the Fiber Broadband offer and complete the existing Order Capture flow.
- Confirm the created ProductOrder id is stored and track-order reads Order Inventory.

Operator acceptance:

- Open `order-inventory-ui` and verify ProductOrder list/details.
- Verify the order-to-orchestration-plan link.
- Open `order-orchestration-ui` and verify plan list, details, node graph, timeline, and fallout incident pages.

## Verification Status

Status as of 2026-05-31:

- Phase 7 portal code is wired for the local BFF/proxy pattern.
- Admin UI packages use the copied local `common-ui` package instead of requiring private registry access.
- Customer offer loading includes a fallback for the Phase 6 seeded broadband offer names.
- Order Inventory local Compose entitlement defaults are aligned with the Phase 4 seed ids so admin/operator UI reads can authorize against `ENT_ORDER_READ_ALL`.
- Order Capture local Compose treats `OTB_CUSTOMER` as the ProcessFlow party-check bypass role for the selfcare POC because seeded Keycloak users do not carry a mapped `relatedPartyId` JWT claim.
- The selfcare BFF includes a temporary Phase 7 `/v1/queryProductConfiguration` fallback so the copied customer UI can render eligibility and plan setup without a standalone Product Configurator service.
- Runtime bridge smoke passed on alternate local port `5010` in the authoring environment because port `5010` was occupied by macOS Control Center.
- Bridge smoke covered health, Product Catalog broadband offers, Auth UserRole seeded roles, Order Inventory list authorization, COOD plan list, and Fallout incident list.
- Browser acceptance confirmed customer login, `Order Now`, eligibility configuration, and navigation to plan setup. Completing final order submission remains the next manual check.

## Notes

The Phase 6 local security bypasses remain in effect where already documented. Phase 7 does not harden service-to-service auth or replace the temporary bridge with the standalone Phase 8 gateway.
