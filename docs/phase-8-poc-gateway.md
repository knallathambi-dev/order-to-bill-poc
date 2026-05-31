# Phase 8 POC Gateway

## Scope

Phase 8 adds the standalone Node/Express `gateway/` service. It replaces the temporary Phase 7 `selfcare-ui/server` bridge for browser-facing API traffic while keeping the same Selfcare UI path contract.

The gateway is intentionally glue only. Discobole services remain authoritative for catalog, order capture, order inventory, orchestration, fallout, product inventory, and authorization data.

## Local URL

| Component | URL |
| --- | --- |
| POC gateway | `http://localhost:8088` |
| Customer selfcare UI | `http://localhost:3000` |
| Order inventory UI dev server | `http://localhost:3004` |
| Order orchestration UI dev server | `http://localhost:3006` |

Start Selfcare against the gateway:

```sh
cd discobole-ui/selfcare-ui
REACT_APP_PROXY_URL=http://localhost:8088 npm start
```

The copied Selfcare env file also sets `REACT_APP_PROXY_URL=http://localhost:8088`, and `src/setupProxy.js` falls back to the Phase 8 gateway. This avoids accidentally sending `/api/auth/*` calls to the old Phase 7 bridge or macOS Control Center on port `5000`.

Selfcare keeps its existing API env paths:

```text
REACT_APP_PRODUCT_CATALOG_URL=/productCatalogManagement/v1/productOffering
REACT_APP_ORDER_CAPTURE_URL=/orderCapture/processManagement/v1/processFlow
REACT_APP_ORDER_INVENTORY_URL=/productOrderingManagement/v1/productOrder
REACT_APP_PRODUCT_INVENTORY_URL=/productInventory/productInventoryManagement/v1/product
REACT_APP_PRODUCT_CONFIGURATOR_URL=/v1/queryProductConfiguration
REACT_APP_POQ_URL=/productOfferingQualification/v1/productOfferingQualification
```

## Route Behavior

The gateway normalizes optional `/api` prefixes before routing. For example, `/api/productCatalogManagement/v1/productOffering` and `/productCatalogManagement/v1/productOffering` are equivalent.

| Browser path prefix | Local target | Policy |
| --- | --- | --- |
| `/auth`, `/api/auth` | Gateway auth/session routes | Browser session |
| `/productCatalogManagement` | Product Catalog | Public read through gateway service token |
| `/orderCapture` | Order Capture | User token required |
| `/productOrderingManagement` | Order Inventory | User token required, internal smoke allowed |
| `/productInventory` | Product Inventory | User token required |
| `/cood` | COOD, with `/cood` stripped | User token or internal secret |
| `/fallout` | Fallout, with `/fallout` stripped | User token or internal secret |
| `/userRolePermission` | Auth UserRole | User token or internal secret |
| `/productOfferingQualification` | Order Capture | Public GET/POST through gateway service token |
| `/processManagement` | Order Capture | User token required |
| `/v1/queryProductConfiguration` | Local POC fallback | Public local fallback |

Order Capture is wired to `PRODUCT_CONFIGURATOR_SERVICE=http://poc-gateway:8088` in the POC Docker profile. That keeps the browser's local configuration fallback and Order Capture's confirm-configuration validation pointed at the same temporary Product Configurator source until a real Product Configurator service is available.

## Auth Rules

- `/auth/me` only returns a real user session. It returns `401` when logged out and never creates a service session for the browser.
- `/auth/refresh` refreshes only user sessions. It returns `FULL_SESSION_EXPIRED` when the user refresh token is missing or expired.
- Gateway service tokens are used only internally for configured public reads and internal-secret smoke calls. They are never saved in the browser session.
- Mutating auth routes require the `XSRF-TOKEN` cookie and matching `X-CSRF-Token` header.
- CORS defaults to `http://localhost:3000`, `http://localhost:3004`, and `http://localhost:3006`.
- Local Docker runs over plain HTTP, so `POC_GATEWAY_SECURE_COOKIES=false` is required. Set it to `true` only behind HTTPS.

## Run Order

```sh
make infra-up
make infra-bootstrap
make core-up
make security-seed-auth-userrole
make core-verify
make gateway-install
make gateway-test
make gateway-up
make gateway-verify
```

For local development without Docker:

```sh
cd gateway
npm install
PORT=8088 \
KEYCLOAK_URL=http://localhost:8080 \
KEYCLOAK_REALM=discobole \
GATEWAY_CLIENT_ID=poc-gateway \
GATEWAY_CLIENT_SECRET=change-me \
SELFCARE_CLIENT_ID=selfcare-ui \
npm start
```

## Verification

Static and unit checks:

```sh
make verify-phase8
make gateway-test
```

Runtime smoke checks:

```sh
curl -fsS http://localhost:8088/health
curl -fsS http://localhost:8088/api/auth/csrf
curl -fsS 'http://localhost:8088/api/productCatalogManagement/v1/productOffering?lifecycleStatus=active'
curl -fsS -H 'x-internal-secret: local-gateway-secret' \
  'http://localhost:8088/api/productOrderingManagement/v1/productOrder?limit=1'
```

## Phase 8 Status

Implemented gateway code, tests, Compose wiring, Make targets, and smoke scripts. Browser component testing for Phase 7 remains deferred until the gateway is running and stable.
