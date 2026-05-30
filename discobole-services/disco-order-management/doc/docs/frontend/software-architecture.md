---
title: Software Architecture
summary: Microfrontend architecture of the admin-ui platform, covering the federated structure, module federation implementation, routing, deployment, and technology stack modernization.
authors:
  - Mohamed Amine MACHERKI
---

# Software Architecture

!!! info "Architecture Context"

    This document describes the frontend architecture of the **order-inventory-ui** microfrontend (OM) within the broader **admin-ui** federated platform. It covers the target architecture, key design decisions, deployment topology, and the technology stack adopted during the migration from the monolithic React application.

---

## Project Overview

The administration UI was historically a single React application managing three distinct business domains: **Order Inventory**, **Product Inventory**, and **Order Orchestration**. This monolithic structure created tight coupling between unrelated business functions and introduced deployment bottlenecks that affected the entire application on every release.

The adopted solution is a **domain-driven decomposition** into a federated Microfrontend Architecture, where each domain — including **Order Management (OM)** — becomes an independently buildable, testable, and deployable unit.

![Microfrontends Hub](../img/frontend/ui-software-architecture-mfe-hub.png){ width="850" align="center" .img-zoomable }

---

## Target Architecture

The architecture relies on a **host shell** (`hostmode-ui`) that orchestrates independently deployed microfrontends at runtime via Module Federation. Each microfrontend owns a single business domain and is fully autonomous — it can be built, tested, and deployed without coordinating with other teams.

The `@discobole/common-ui` shared library centralizes cross-cutting concerns shared by all microfrontends: authentication, shared components, hooks, and the federation context.

![Microfrontend Composition](../img/frontend/ui-software-architecture-mfe-composition.png){ width="850" align="center" .img-zoomable }

| Component | Type | Purpose |
| :--- | :--- | :--- |
| `hostmode-ui` | Host Application | Main container orchestrating all MFEs at runtime |
| **`order-inventory-ui`** | **Microfrontend** | **Order management domain (OM)** |
| `product-inventory-ui` | Microfrontend | Product inventory domain |
| `order-orchestration-ui` | Microfrontend | Workflow orchestration domain |
| `common-ui` | Shared Library | Shared components, authentication, hooks, and utilities |

![GitLab Group Overview](../img/frontend/ui-software-architecture-gitlab-overview.png){ width="850" align="center" .img-zoomable }

---

## Module Federation

Module Federation is the core enabler of the architecture. It allows the host shell to load remote MFE bundles at runtime without any build-time dependency between repositories, and ensures that singleton shared dependencies (React, React Router, `@discobole/common-ui`) are instantiated only once across all microfrontends.

Each MFE exposes its root component via a `remoteEntry.js` manifest file, which the host shell fetches dynamically at runtime:

```
http://<domain>/assets/remoteEntry.js
```

The federation configuration for `order-inventory-ui` is as follows:

```js
// vite.config.js — order-inventory-ui
federation({
    name: "order_inventory",
    filename: "remoteEntry.js",
    exposes: {
        "./App": "./src/bootstrap.jsx"
    },
    shared: {
        "react":                { singleton: true, requiredVersion: "^19.1.1" },
        "react-dom":            { singleton: true, requiredVersion: "^19.1.1" },
        "react-router-dom":     { singleton: true, requiredVersion: "^7.9.1", eager: true },
        "@discobole/common-ui": { singleton: true, requiredVersion: "^1.0.4" }
    }
});
```

---

## Dual-Mode Operation

`order-inventory-ui` supports two distinct operational modes, dynamically resolved at boot time via the `VITE_STANDALONE_MODE` environment variable. This design allows the same codebase to run both as a fully autonomous application during development and as a federated module inside the host shell in production.

![Execution Mode Decision Tree](../img/frontend/ui-software-architecture-execution-mode-decision-tree.png){ width="650" align="center" .img-zoomable }

**Standalone Mode** (`VITE_STANDALONE_MODE=true`)

The MFE bootstraps as a fully autonomous application. It initializes its own Keycloak authentication session, mounts a `BrowserRouter`, and renders independently. This mode is used during local development and isolated testing.

**Federated Mode** (`VITE_STANDALONE_MODE=false`)

The MFE is consumed by `hostmode-ui`. It skips authentication initialization — delegating entirely to the host's Keycloak session — and returns a bare App component for the host to mount within its own routing and provider tree.

![Dual-Mode Bootstrapping Flow](../img/frontend/ui-software-architecture-dual-mode-bootstrap.png){ width="650" align="center" .img-zoomable }

---

## Routing Architecture

Routing responsibilities are split between the host application and `order-inventory-ui`. The host maintains top-level navigation control while the MFE manages its own internal page structure under its base path.

**Host Application Routing**

`hostmode-ui` defines a top-level wildcard route that delegates all `/order-inventory/*` traffic to the OM MFE bundle at runtime:

```jsx
<Route path="/order-inventory/*" element={<OrderInventoryMFE />} />
```

**Order Inventory Internal Routing**

Once the host hands off control, the MFE's internal `<Routes>` take over. The MFE defines a `LayoutWrapper` scoped to `/order-inventory` and manages its own page components:

| Route | Component |
| :--- | :--- |
| `/order-inventory/monitoring/orders` | `OrdersMonitoring` — orders list, filter, and search view |
| `/order-inventory/orders-details-page/:id` | `OrdersDetailsPage` — full detail view of a single order |

![OM Routing](../img/frontend/ui-software-architecture-routing-om.png){ align="center" .img-zoomable }

---

## Cross-MFE Awareness

To maintain strict decoupling while enabling seamless cross-domain navigation, microfrontends do not import from each other directly. Instead, they rely on the `useFederationConfig()` hook provided by `@discobole/common-ui`, which reads from a shared `FederationContext` populated at host level.

A concrete example involving OM: the **Order Inventory MFE** conditionally renders navigation buttons to jump to related **CPIB products** or the **COOD orchestration plan** depending on whether `product-inventory-ui` or `order-orchestration-ui` are currently registered and available in the host environment. If a target MFE is not loaded, the corresponding button is hidden gracefully — no broken links, no runtime errors.

![Cross-MFE Awareness](../img/frontend/ui-software-architecture-cross-mfe-awareness.png){ align="center" .img-zoomable }

---

## Technology Stack

The migration was paired with a full modernization of the frontend toolchain. The choices below reflect a deliberate alignment with modern standards and the specific requirements of a Module Federation setup.

![Technology Stack Evolution](../img/frontend/ui-software-architecture-tech-stack-evolution.png){ width="850" align="center" .img-zoomable }

### Build Tool: Webpack → Vite

Vite replaces Create React App's Webpack pipeline. By leveraging native ES Modules in the browser during development, Vite eliminates the upfront bundling step that caused slow cold starts. In production, it uses Rollup under the hood for optimized output. For a Module Federation setup specifically, Vite's architecture is a significantly better fit than Webpack's CRA abstraction, which does not natively support federation.

| Metric | Webpack (CRA) | Vite | Expected Improvement |
| :--- | :--- | :--- | :--- |
| **Dev Server Start** | 25–35 seconds | 2–4 seconds | ~8× faster |
| **Hot Reload** | 3–8 seconds | 200–800 ms | ~10× faster |
| **Production Build** | 45–60 seconds | 15–25 seconds | ~2.5× faster |

!!! warning "On Performance Figures"

    The metrics above are derived from official and community benchmarks comparing Webpack and Vite at scale. They represent expected gains based on the toolchain difference and should be validated through measurements on the actual codebase once sufficient production data is available.

### Framework Versions

| Package | Previous | Current | Notable Change |
| :--- | :--- | :--- | :--- |
| `react` / `react-dom` | 18.2.0 | 19.1.1 | Concurrent features, improved rendering |
| `react-router-dom` | 6.15.0 | 7.9.1 | Revised data routing API |
| `react-scripts` | 5.0.1 | — | Removed; replaced entirely by Vite |

### Testing Framework: Jest → Vitest

Testing was migrated from Jest to Vitest to align with the Vite ecosystem. The migration required minimal syntax changes and maintained full test coverage. The primary motivation was native ES Module support — Jest's CommonJS-based transform pipeline creates friction in a Vite/ESM project and complicates Module Federation test setups.

| Metric | Jest | Vitest |
| :--- | :--- | :--- |
| **Test Startup** | 5–10 seconds | 1–2 seconds |
| **ES Modules** | Requires transform workarounds | Native support |
| **Vite Integration** | External, config duplication | Built-in, single config |

---

## Deployment & CI/CD

`order-inventory-ui` is deployed independently through its own GitLab CI/CD pipeline. No coordination with other MFE pipelines is required — a push to `order-inventory-ui` triggers only its own pipeline.

![Automated CI/CD](../img/frontend/ui-software-architecture-cicd-pipeline.png){ width="850" align="center" .img-zoomable }

The pipeline follows three sequential stages:

**1. Quality Gate**

```
npm run lint  →  npm run test (Vitest + coverage)  →  npm run build (Vite)
```

All three steps must pass before the pipeline proceeds. A failing test or linting error blocks the release.

**2. Semantic Release**

The `semantic-release` bot analyzes commit messages (following Conventional Commits), increments the version automatically, generates the CHANGELOG, and publishes artefacts to:

- **NPM Registry** — for `common-ui` package releases
- **Docker Image Registry** — containerized build of the MFE
- **Helm Package Registry** — Helm chart for Kubernetes deployment

**3. Cluster Deployment**

The versioned Helm chart is deployed to the OpenShift/Rancher Kubernetes cluster.

!!! success "Deployment Benefits"

    - **Independent Releases:** The OM team controls its own deployment schedule with no dependency on other MFE release cycles.
    - **Rollback Safety:** A failed `order-inventory-ui` deployment does not affect `product-inventory-ui`, `order-orchestration-ui`, or the host shell.
    - **Reduced Blast Radius:** Smaller, focused deployments limit the scope of any given change.

---

## Containerized Deployment Architecture

In production, `order-inventory-ui` runs as a containerized Nginx pod. Client requests enter through the OpenShift Ingress controller, are routed to the `hostmode-ui` pod, which then fetches the OM `remoteEntry.js` at runtime to dynamically compose the application.

API requests from the OM frontend are proxied directly through the `order-inventory-ui` Nginx container to the Backend API Gateway — the host shell does not intermediate API traffic.

![Containerized Deployment Architecture](../img/frontend/ui-software-architecture-containerized-deployment.png){ width="850" align="center" .img-zoomable }

??? info "API Proxy Configuration: Development vs. Production"

    **Local Development (`VITE_STANDALONE_MODE=true`):**
    The Vite Dev Server intercepts all API calls and proxies them to the configured backend via `vite.config.js`. This replaces the legacy `setupProxy.js` Webpack middleware from the monolithic app and provides enhanced debugging via `proxyReq` event hooks that log every proxied request to the console.

    **Production:**
    The `order-inventory-ui` Nginx container uses `proxy_pass` directives to forward API requests to the Backend API Gateway at `GATEWAY_URL`. The Vite Dev Server is not present in production — the built static assets are served directly by Nginx.

    ![API Proxy vs. Production Gateway — OM](../img/frontend/ui-software-architecture-api-proxy-vs-gateway-om.png){ width="650" align="center" .img-zoomable }

---

## Runtime Loading Strategy

The transition to a federated architecture fundamentally changes how the application loads in the browser. The monolithic approach required the browser to fetch, parse, and compile a single 15 MB bundle — including the entirety of OM, CPIB, and COOD — before rendering anything. The federated approach uses a **progressive, on-demand loading model**: only the host shell and `common-ui` are fetched on first load; `order-inventory-ui` is lazy-loaded only when the user navigates to an order inventory route.

![Runtime Loading Strategy](../img/frontend/ui-software-architecture-runtime-loading-strategy.png){ width="850" align="center" .img-zoomable }

| Phase | Monolithic (Old) | Federated (New) |
| :--- | :--- | :--- |
| **Initial Fetch** | 15 MB mega-bundle (all domains) | `hostmode-ui` + `common-ui` shells only |
| **First Paint** | After full bundle parse & compile | After shell render & authentication |
| **OM Load** | Always loaded, even when unused | Lazy-loaded on first `/order-inventory` navigation |
| **Cache Granularity** | Entire bundle invalidated on any change | Only the `order-inventory-ui` chunk is invalidated |

---

## Source Code Organization

### Repository Structure

Each MFE lives in its own GitLab repository under the `disco-admin-ui` group. There are no shared build pipelines or cross-repository build-time dependencies.

| Repository | Role | Purpose |
| :--- | :--- | :--- |
| `hostmode-ui` | Host Shell | Main container orchestrating all MFEs at runtime |
| **`order-inventory-ui`** | **Microfrontend** | **Order management domain (OM)** |
| `product-inventory-ui` | Microfrontend | Product inventory domain |
| `order-orchestration-ui` | Microfrontend | Workflow orchestration domain |
| `common-ui` | Shared Library | Shared components, authentication, hooks, and utilities |

### order-inventory-ui Project Structure

```
order-inventory-ui/
├── src/
│   ├── App.jsx                 # Root component exposed via Module Federation
│   ├── bootstrap.jsx           # MFE entry point — dual-mode initialization logic
│   ├── components/             # Reusable UI components (OM-specific)
│   ├── service/                # API layer — all backend calls go through here
│   ├── routes/                 # Routing configuration
│   ├── views/                  # Page-level components (OrdersMonitoring, OrdersDetailsPage)
│   └── hooks/                  # Custom React hooks
├── env/
│   └── env.template.js         # Runtime environment variable template
├── vite.config.js              # Vite + Module Federation configuration
├── Dockerfile                  # Multi-stage containerized build
└── package.json
```
