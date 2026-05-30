<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Order Orchestration UI

DISCOBOLE Order Orchestration Micro Frontend is a standalone microfrontend for **Order Orchestration** within the DISCOBOLE Admin
platform. Built with **Vite**, **React**, and **Module Federation**, it implements orchestration monitoring and management
capabilities and can run independently or be consumed by the host shell at runtime.

The `Order Orchestration UI` MFE provides:

- **plan monitoring**: displays real-time status and progress of orchestration plans with interactive dashboards
- **workflow visualization**: provides graphical representations of order orchestration workflows and their execution states
- **delivery management**: handles delivery tracking, timeline visualization, and incident reporting
- **cross-MFE navigation**: seamless navigation to related services like Order Inventory and Product Inventory when available
- **reporting**: delivers comprehensive statistics and reports on contract fulfillment, product delivery, and historical performance

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Node.js](https://nodejs.org/en/) 18+
- [npm](https://www.npmjs.com/) 9+ or [yarn](https://yarnpkg.com/)

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
//${GITLAB_REGISTRY_HOST}/projects/${COMMON_UI_PROJECT_ID}/packages/npm/:_authToken=${CI_JOB_TOKEN}
```

### Installing

```bash
git clone git@gitlab.ow2.org:disco/disco-oda-components/disco-ui-portals/disco-admin-ui/order-orchestration-ui.git
```

Install project dependencies with:

```bash
npm install
```

### Configuration

The default configuration is set up in the `env/env.template.js` file and can be overridden with environment variables at runtime. For development, Vite-specific variables can be set:

| Variable                    | Default             | Description                                          |
|-----------------------------|---------------------|------------------------------------------------------|
| :lock: `GITLAB_TOKEN` | The GitLab project access token defined with the `api` scope and the `maintainer` role. | _none_ |
| `COMMON_UI_PROJECT_ID` | The ID of the GitLab common-ui project for the internal npm registry (see the project general settings).| _none_ |


Override locally with `.env.local`:

```bash
VITE_GATEWAY_URL=http://localhost:8080
VITE_STANDALONE_MODE=false
```

### Running locally

To launch the application on your local machine:

1. Ensure environment variables are properly configured (see Configuration section above)

2. Start the development server:

```bash
npm run dev
```

| Mode    | Port   | URL                                  |
|---------|--------|--------------------------------------|
| Dev     | `3006` | http://localhost:3006                |
| Preview | `3003` | http://localhost:3003                |
| Remote  |        | http://localhost:3003/assets/remoteEntry.js |

The application will automatically proxy API calls to the configured `VITE_GATEWAY_URL`.

**Note:** ensure that the Order Orchestration backend API is running and accessible for the UI to function properly.

## What It Does

- **Monitors orchestration plans**: displays real-time status and progress with interactive dashboards
- **Visualizes workflows**: provides graphical representations of order orchestration workflows and execution states
- **Manages deliveries**: handles delivery tracking, timeline visualization, and incident reporting
- **Generates reports**: delivers comprehensive statistics on contract fulfillment and historical performance
- **Cross-navigates to other MFEs**: provides navigation links to Order Inventory MFE and Product Inventory MFE when available via federation awareness
- **Exposes Module Federation entry point**: can be consumed by the host shell for runtime composition

## Architecture

```bash
order-orchestration-ui/
├── src/
│   ├── App.jsx                    # Main application component
│   ├── bootstrap.jsx              # Module Federation entry point
│   ├── main.jsx                   # React DOM render entry
│   ├── components/                # Reusable UI components
│   │   ├── Dashboard/             # Dashboard components
│   │   ├── Plans/                 # Plan monitoring components
│   │   ├── Workflows/             # Workflow visualization components
│   │   └── Reports/               # Reporting components
│   ├── layout/                    # Layout wrapper components
│   ├── service/                   # API layer and business logic
│   ├── context/                   # React contexts (Auth, etc.)
│   ├── routes/                    # Routing configuration
│   ├── utils/                     # Utility functions and constants
│   └── Views/                     # Page-level view components
├── env/
│   └── env.template.js            # Environment configuration template
├── public/                        # Static assets
├── vite.config.js                 # Vite configuration and Module Federation setup
├── package.json                   # Project dependencies and scripts
├── eslint.config.js               # ESLint configuration
└── index.html                     # HTML entry point
```

## Module Federation

This MFE exposes its entry point as a federated module:

```js
federation({
    name: "order_orchestration",
    filename: "remoteEntry.js",
    exposes: {
        "./App": "./src/App.jsx"
    },
    shared: {
        "react": {singleton: true, requiredVersion: "^18.0.0"},
        "react-dom": {singleton: true, requiredVersion: "^18.0.0"},
        "react-router-dom": {singleton: true, requiredVersion: "^6.0.0"},
        "@discobole/common-ui": {singleton: true, requiredVersion: "^1.0.0"}
    }
});
```

The host loads this at: `http://<domain>/assets/remoteEntry.js`

## Cross-MFE Awareness

This MFE uses `useFederationConfig()` from `@discobole/common-ui` to detect which other MFEs are available:

```jsx
import {useFederationConfig} from "@discobole/common-ui";

const {omEnabled, piEnabled} = useFederationConfig();

// Action buttons are hidden when target MFE is disabled
{
    omEnabled && <button onClick={handleOrderClick}>View Orders</button>
}
{
    piEnabled && <button onClick={handleProductClick}>View Products</button>
}
```

When running standalone (without the host), all MFEs default to disabled — buttons are hidden gracefully.

## Build

Build the application for production:

```bash
npm run build
```

Output: `dist/` — contains `assets/remoteEntry.js` and all chunks. Served by Nginx in Docker.

The build includes code splitting and optimization via Vite for minimal bundle sizes.

## Linting

Run ESLint to check code quality:

```bash
npm run lint
```

ESLint is configured via `eslint.config.js` and checks for code style issues and best practices.

## Running the tests

Run the unit tests with:

```bash
npm run test                # Run tests once
npm run test:watch         # Watch mode for development
```

Coverage output: `coverage/`

The project uses Vitest for unit testing with support for React Testing Library for component testing.

## Docker

Build and run the application in a Docker container:

```bash
docker build -t order-orchestration-ui .
docker run -p 3003:8080 order-orchestration-ui
```

The Dockerfile uses multi-stage build to optimize the final image size. API requests are proxied through Nginx.

## Installation manually using Helm

A Helm chart is provided in order to deploy your application manually.

Please consult its [documentation](helm/README.md).

## Installation via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with:

- Kubernetes 1.20+

**REMARKS**: some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to
expose your service outside the cluster:

- On *OpenShift*, one must define an OpenShift Route
- On *Rancher* or *any Kubernetes cluster* running an Ingress controller, one may configure an Ingress

Sample custom value files for different environments are being provided with the chart to give you an example of what
needs to be done.

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

The pipeline is configured to publish the Docker images produced by the [Docker](https://gitlab.com/to-be-continuous/docker)
"to be continuous" template to the GitLab project internal registry. All template variables are configured by default to
build and push your Docker images on the GitLab project internal registry.

#### Helm deployment

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part is
disabled but the Helm package is built and published in the GitLab project internal registry.

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
- [Module Federation](https://webpack.js.org/concepts/module-federation/) - Micro frontend architecture
- [@discobole/common-ui](https://discobole.ow2.io/doc) - Shared component library
- [React Router](https://reactrouter.com/) - Client-side routing
- [Axios](https://axios-http.com/) - HTTP client

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-order-management/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

- Keep business logic inside this MFE — never put it in the host
- Shared components belong in `@discobole/common-ui`
- Use the service layer for all API calls — never use raw `fetch` or standalone `axios`
- Run `npm run lint && npm run test` before committing

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE.txt) file for details.