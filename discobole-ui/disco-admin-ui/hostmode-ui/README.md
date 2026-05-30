<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# DISCOBOLE Admin UI – Host Application

The **host application** for the DISCO Admin platform. Built with **Vite**, **React**, and **Module Federation**, it loads and orchestrates all Micro Frontends (MFEs) at runtime, providing the global layout, authentication bootstrap, and routing delegation.

The `Host Application` provides:

- **MFE orchestration**: dynamically loads and manages Order Inventory, Product Inventory, and Order Orchestration MFEs
- **global layout**: provides consistent header, navigation, and tab-based layout across all MFEs
- **authentication bootstrap**: initializes Keycloak authentication and shares it with MFEs via `@discobole/common-ui`
- **federation context**: exposes MFE configuration to child modules for cross-MFE awareness

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Node.js](https://nodejs.org/) 18+
- [npm](https://www.npmjs.com/) 8+

**Note**: `nvm` (Node Version Manager) can also be used to manage multiple Node.js versions:
see [here](https://www.linode.com/docs/guides/how-to-install-use-node-version-manager-nvm/#use-nvm-to-install-node).

Access to the internal NPM registry (`.npmrc` required):

```text
@discobole:registry=https://gitlab.ow2.org/api/v4/projects/<COMMON_UI_PROJECT_ID>/packages/npm/
//gitlab.ow2.org/api/v4/projects/<COMMON_UI_PROJECT_ID>/packages/npm/:_authToken=[GITLAB_PAT_HERE]
```

> Replace `<COMMON_UI_PROJECT_ID>` with the actual GitLab project ID for `common-ui`.

For CI/CD pipelines, the `.npmrc` uses environment variables automatically:

```text
@discobole:registry=${CI_API_V4_URL}/projects/${COMMON_UI_PROJECT_ID}/packages/npm/
//${CI_SERVER_HOST}/api/v4/projects/${COMMON_UI_PROJECT_ID}/packages/npm/:_authToken=${CI_JOB_TOKEN}
```

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-ui-portals/disco-admin-ui/hostmode-ui.git
```

To build and deploy the application, the following environment variables need to be defined and exported in your terminal:

| Name                 | Description                                                  | Value                                                        |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| :lock: `GITLAB_TOKEN` | The GitLab project access token defined with the `api` scope and the `maintainer` role. | _none_ |
| `COMMON_UI_PROJECT_ID` | The ID of the GitLab common-ui project for the internal npm registry (see the project general settings).| _none_ |

:information_source: If you don't have access to the project general settings, the project ID may be retrieved as follows:

```bash
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-ui-portals%2F/disco-admin-ui%2Fhostmode-ui | jq .id
```

Install project dependencies with:

```bash
npm install
```

### Configuration

The default configuration is set up in the `env.local.js` file. The application can be configured through environment variables at runtime.

| Variable                        | Default                 | Description                                |
|---------------------------------|-------------------------|--------------------------------------------|
| `GATEWAY_URL`                   | `http://localhost:8686` | Backend gateway URL                        |
| `FEATURE_ORDER_MANAGEMENT`      | `true`                  | Enable Order Inventory MFE                 |
| `FEATURE_PRODUCT_INVENTORY`     | `true`                  | Enable Product Inventory MFE               |
| `FEATURE_ORDER_ORCHESTRATION`   | `true`                  | Enable Order Orchestration MFE             |
| `ORDER_INVENTORY_UI_URL`        | `http://localhost:3001` | Order Inventory MFE URL                    |
| `ORDER_ORCHESTRATION_UI_URL`    | `http://localhost:3002` | Order Orchestration MFE URL                |
| `PRODUCT_INVENTORY_UI_URL`      | `http://localhost:3003` | Product Inventory MFE URL                  |
| `PROXY_URL`                     | `http://localhost:8686` | API proxy URL                              |

### Local Environment

Create `env.local.js` at the project root to override defaults:

```js
export const ENV_CONFIG = {
    GATEWAY_URL: 'http://localhost:8686',
    FEATURE_ORDER_MANAGEMENT: 'true',
    FEATURE_PRODUCT_INVENTORY: 'true',
    FEATURE_ORDER_ORCHESTRATION: 'true',
    ORDER_INVENTORY_UI_URL: 'http://localhost:3001',
    ORDER_ORCHESTRATION_UI_URL: 'http://localhost:3002',
    PRODUCT_INVENTORY_UI_URL: 'http://localhost:3003',
};
```

This file is git-ignored and never committed.

### Running locally

Start the application with:

```bash
npm run dev
```

| Mode    | Port   | URL                     |
|---------|--------|-------------------------|
| Dev     | `5173` | http://localhost:5173   |

## What It Does

- **Provides the global layout**: header, tab navigation, and consistent styling across all MFEs
- **Dynamically loads MFEs**: Order Inventory, Product Inventory, Order Orchestration based on environment configuration
- **Bootstraps authentication**: initializes Keycloak and shares it with MFEs via `@discobole/common-ui`
- **Manages MFE availability**: shows fallback UI when a module is disabled or unreachable
- **Shares common dependencies**: `react`, `react-dom`, `react-router-dom`, `@discobole/common-ui` as singletons
- **Exposes federation context**: provides MFE configuration to child modules via `FederationContext`

## Architecture

```
hostmode-ui/
├── src/
│   ├── App.jsx                                # Main application component
│   ├── components/
│   │   └── layoutWrapper.jsx                  # Main layout, routing, MFE orchestration
│   ├── remotes/
│   │   ├── RemoteLoader.jsx                   # Shared MFE loader with error/retry handling
│   │   ├── RemoteRegistry.js                  # Dynamic import registry with caching
│   │   ├── RemoteErrorBoundary.jsx            # Runtime crash boundary
│   │   ├── RemoteWrapperOrderInventory.jsx    # Order Inventory MFE wrapper
│   │   ├── RemoteWrapperOrderOrchestration.jsx # Order Orchestration MFE wrapper
│   │   └── RemoteWrapperProductInventory.jsx  # Product Inventory MFE wrapper
│   └── utils/
│       └── env-helper.js                      # Runtime environment config
├── env.local.js                               # Local environment overrides (git-ignored)
├── vite.config.js                             # Vite configuration and Module Federation setup
├── package.json                               # Project dependencies and scripts
└── index.html                                 # HTML entry point
```

## MFE Configuration

MFE availability is controlled via environment variables:

| Variable        | Default | Effect when `'true'`         |
|-----------------|---------|------------------------------|
| `FEATURE_ORDER_MANAGEMENT`   | `true`  | Enables Order Inventory     |
| `FEATURE_PRODUCT_INVENTORY`   | `true`  | Enables Product Inventory   |
| `FEATURE_ORDER_ORCHESTRATION` | `true`  | Enables Order Orchestration |

The default MFE (loaded on `/`) follows this priority: **Order Inventory → Product Inventory → Order Orchestration**.
The first enabled MFE wins.

Remote URLs are configured per environment:

| Variable                     | Default                 |
|------------------------------|-------------------------|
| `ORDER_INVENTORY_UI_URL`     | `http://localhost:3001` |
| `ORDER_ORCHESTRATION_UI_URL` | `http://localhost:3002` |
| `PRODUCT_INVENTORY_UI_URL`   | `http://localhost:3003` |
| `PROXY_URL`                  | `http://localhost:8686` |

## Module Federation

The host application consumes MFEs as federated modules:

```js
federation({
    name: "host",
    remotes: {
        order_inventory: "http://localhost:3001/assets/remoteEntry.js",
        order_orchestration: "http://localhost:3002/assets/remoteEntry.js",
        product_inventory: "http://localhost:3003/assets/remoteEntry.js"
    },
    shared: {
        "react": {singleton: true, requiredVersion: "^18.0.0"},
        "react-dom": {singleton: true, requiredVersion: "^18.0.0"},
        "react-router-dom": {singleton: true, requiredVersion: "^6.0.0"},
        "@discobole/common-ui": {singleton: true, requiredVersion: "^1.0.0"}
    }
});
```

## Build

```bash
npm run build
```

Output: `dist/` — served by Nginx in Docker.

The build includes code splitting and optimization via Vite for minimal bundle sizes.

## Linting

Run ESLint to check code quality:

```bash
npm run lint
```

## Running the tests

Run the unit tests with:

```bash
npm run test
```

## Proxy

The Vite dev server proxies API calls to the backend gateway. Proxied paths include `/orderCapture`,
`/productOrderingManagement`, `/productInventory`, `/cood`, `/fallout`, and `/userRolePermission`.

## Docker

Build and run the application in a Docker container:

```bash
docker build -t hostmode-ui .
docker run -p 8080:8080 hostmode-ui
```

The Dockerfile uses multi-stage build to optimize the final image size. API requests are proxied through Nginx.

## Installation manually using Helm

A Helm chart is provided in order to deploy your application manually.

Please consult its [documentation](helm/chart/README.md).

## Installation via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with:

- Kubernetes 1.20+

**REMARKS**: some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to
expose your service outside the cluster:

- On *OpenShift*, one must define an OpenShift Route
- On *Rancher* or *any Kubernetes cluster* running an Ingress controller, one may configure an Ingress

The application can be built, tested and deployed in an automated manner with a GitLab-CI pipeline. The
included [gitlab-ci.yml](.gitlab-ci.yml) file includes templates provided
by [To Be Continuous](https://to-be-continuous.gitlab.io/doc/) for:

- Building the app (`docker` template)
- Deploying the app (`helm` template)
- Managing releases (`semantic-release` template)

### Forking the repository and configuring the pipeline

You will first have to fork this project in a namespace where you have write access.

In order for the pipeline to execute properly, **some CI/CD variables need to be set**. The list of variables required
by each template is documented via comments in the [gitlab-ci.yml](.gitlab-ci.yml) file.

#### Docker images publication

The pipeline is configured to publish the Docker images produced by the [Docker](https://gitlab.com/to-be-continuous/docker) "to be continuous" template to the GitLab project internal registry. All template variables are configured by default to build and push your Docker images on the GitLab project internal registry.

#### Helm deployment

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

#### Versioning with semantic release

In order to let the [semantic release template](https://gitlab.com/to-be-continuous/semantic-release) adding the
necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

- :lock: `GITLAB_TOKEN` (GitLab access token with `api`, `read_repository` and `write_repository` scopes and `Maintainer` role)

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Built With

- [git](https://git-scm.com/) - Open source distributed version control system
- [Node.js](https://nodejs.org/en/) - JavaScript runtime
- [npm](https://www.npmjs.com/) - JavaScript Package Manager
- [Vite](https://vitejs.dev/) - Frontend build tool and dev server
- [React](https://react.dev/) - UI framework
- [@originjs/vite-plugin-federation](https://github.com/nicedoc/vite-plugin-federation) - Module Federation for Vite
- [@discobole/common-ui](https://discobole.ow2.io/doc) - Shared component library
- [React Router](https://reactrouter.com/) - Client-side routing
- [Keycloak](https://www.keycloak.org/) - Authentication and authorization

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

- Never hardcode MFE URLs — all URLs come from environment config
- Each MFE must remain independently deployable
- Shared components belong in `@discobole/common-ui`, not in the host
- Keep the host thin — business logic belongs in MFEs
- Run `npm run lint && npm run test` before committing

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE.txt) file for details.