<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Common UI

DISCOBOLE Common UI is the **shared component library** for the DISCOBOLE platform. Built with **Vite**, **React**, and **Boosted** (Orange's Bootstrap), it provides reusable components, services, and utilities consumed as a singleton by the host shell and all Micro Frontends via Module Federation.

The `Common UI` library provides:

- **shared components**: reusable UI elements (Header, Modal, Pagination, NavBar, etc.) for building consistent interfaces
- **authentication & authorization**: centralized AuthService for managing user sessions and entitlements
- **federation context**: FederationProvider and useFederationConfig hook for cross-MFE awareness
- **styling & theming**: built on Boosted (Orange's Bootstrap), ensuring visual consistency across all applications
- **utility functions**: common helpers and services used across the platform

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Node.js](https://nodejs.org/) 18+ (LTS recommended, Node.js 22 used in CI)
- [npm](https://www.npmjs.com/) 9+

**Note**: `nvm` (Node Version Manager) can also be used to manage multiple Node.js versions:
see [here](https://www.linode.com/docs/guides/how-to-install-use-node-version-manager-nvm/#use-nvm-to-install-node).

Access to the internal NPM registry (`.npmrc` required):

```text
@discobole:registry=https://gitlab.ow2.org/api/v4/projects/<COMMON_UI_PROJECT_ID>/packages/npm/
//gitlab.ow2.org/api/v4/projects/<COMMON_UI_PROJECT_ID>/packages/npm/:_authToken=<GITLAB_PAT>
```

> Replace `<COMMON_UI_PROJECT_ID>` with the actual GitLab project ID for `common-ui`.

For CI/CD pipelines, the `.npmrc` uses environment variables automatically:

```text
@discobole:registry=${CI_API_V4_URL}/projects/${CI_PROJECT_ID}/packages/npm/
//${GITLAB_REGISTRY_HOST}/projects/${CI_PROJECT_ID}/packages/npm/:_authToken=${CI_JOB_TOKEN}
```

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/pks/common-ui.git
```

To build and publish the library, the following environment variables need to be defined and exported in your terminal:

| Name                   | Description                                                  | Value                                                        |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| :lock: `CI_JOB_TOKEN`  | The GitLab CI job token for authenticating npm package publication. | Automatically provided by GitLab CI (*required to publish the npm package*). |
| `CI_API_V4_URL`        | The GitLab API v4 root URL.                                  | `https://gitlab.ow2.org/api/v4` (*automatically provided by GitLab CI*). |
| `CI_PROJECT_ID`        | The ID of the GitLab project.                                | Automatically provided by GitLab CI (*required to publish the npm package*). |
| `GITLAB_REGISTRY_HOST` | The host name for the GitLab registry.                       | `gitlab.ow2.org` (*automatically provided by GitLab CI*). |

:information_source: If you don't have access to the project general settings, the project ID may be retrieved as follows:

```bash
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fpks%2Fcommon-ui | jq .id
```

Install project dependencies with:

```bash
npm install
```

### Configuration

The library uses Vite for bundling and building. Configuration is defined in `vite.config.js`.

Components and exports are defined in `src/` and exposed via `src/index.js`.

### Running locally

To build the library locally:

```bash
npm run build
```

To test changes in a consuming project:

1. Link the library locally:

```bash
npm link
```

2. In the consuming project, link common-ui:

```bash
npm link @discobole/common-ui
```

3. After testing, unlink from both projects:

```bash
npm unlink @discobole/common-ui
```

### Usage

The library is consumed by importing components, services, and utilities:

```jsx
import {
  Header,
  AuthService,
  FederationProvider,
  useFederationConfig,
  AppLayout,
  Modal,
  Pagination,
  NavBar,
  ErrorMessageBox,
  LoadingIndicator,
  Unauthorized,
  PageNotFound,
} from "@discobole/common-ui";
```

#### Authentication Example

```jsx
// Check user entitlements
const hasAccess = AuthService.hasEntitlement("PRODUCT_ORDER_ADMIN");

// Get current user
const user = AuthService.getUserInfo();

// Logout
AuthService.logout();
```

#### Module Federation Example

Wrap your application with `FederationProvider`:

```jsx
<FederationProvider config={{omEnabled: true, piEnabled: false}}>
  <App />
</FederationProvider>
```

Check MFE availability:

```jsx
const { piEnabled, coodEnabled } = useFederationConfig();

if (piEnabled) {
  return <ProductsModule />;
}
```

## What It Does

- **Provides reusable UI components**: Header, Modal, Pagination, NavBar, LoadingIndicator, ErrorMessageBox, and more
- **Manages authentication**: centralized AuthService for Keycloak integration, user sessions, and entitlements
- **Enables cross-MFE awareness**: FederationProvider and useFederationConfig hook for detecting available MFEs
- **Ensures visual consistency**: built on Boosted (Orange's Bootstrap) for consistent styling across the platform
- **Exports utility functions**: common helpers used across all DISCOBOLE applications
- **Provides static pages**: 404, Unauthorized, and error handler pages

## Architecture

```
common-ui/
├── src/
│   ├── components/              # Reusable React components
│   │   ├── Header/
│   │   ├── Modal/
│   │   ├── Pagination/
│   │   ├── NavBar/
│   │   ├── ErrorMessageBox/
│   │   └── LoadingIndicator/
│   ├── context/                 # React contexts & providers
│   │   └── FederationContext/
│   ├── hooks/                   # Custom React hooks
│   │   └── useFederationConfig/
│   ├── layouts/                 # Layout components
│   │   └── AppLayout/
│   ├── service/                 # Services (authentication, HTTP)
│   │   └── AuthService/
│   ├── static/                  # Static pages (404, unauthorized, error handler)
│   │   ├── PageNotFound/
│   │   └── Unauthorized/
│   ├── utils/                   # Utilities & helper functions
│   ├── assets/                  # Fonts, icons, images
│   ├── index.css                # Global styles
│   └── index.js                 # Public API exports
├── vite.config.js               # Vite configuration
├── package.json                 # Project dependencies and scripts
└── eslint.config.js             # ESLint configuration
```

## Module Federation Configuration

This library is configured as a **singleton** shared across the host and all Micro Frontends. Configure in your `vite.config.js`:

```js
shared: {
  "@discobole/common-ui": {
    singleton: true,
    requiredVersion: "1.0.0"
  }
}
```

This ensures a single instance of contexts and services across module boundaries.

## Build

Build the library for production:

```bash
npm run build
```

Output: `dist/` — contains the bundled library ready for npm publication.

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

## Installation via CI/CD with GitLab-CI and npm

### Prerequisites

You will need access to the GitLab project with write permissions for publishing npm packages.

The application can be built, tested and published in an automated manner with a GitLab-CI pipeline. The included [.gitlab-ci.yml](.gitlab-ci.yml) file includes:

- Building the library (`npm run build`)
- Running linter checks (`npm run lint`)
- Publishing the npm package
- Managing releases with semantic versioning

### Forking the repository and configuring the pipeline

You will first have to fork this project in a namespace where you have write access.

In order for the pipeline to execute properly, **some CI/CD variables need to be set**. The list of variables required is documented via comments in the [.gitlab-ci.yml](.gitlab-ci.yml) file.

#### npm package publication

The pipeline is configured to publish the npm package to the GitLab project internal registry.

#### Versioning with semantic release

The npm package is published automatically by the GitLab CI pipeline when a version tag is created (e.g., `v1.0.0`). The required environment variables are automatically provided by GitLab CI:

- `CI_JOB_TOKEN` — GitLab CI job token (automatic)
- `CI_API_V4_URL` — GitLab API URL (automatic)
- `CI_PROJECT_ID` — Project ID (automatic)
- `GITLAB_REGISTRY_HOST` — Registry host (automatic)

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Built With

- [git](https://git-scm.com/) - Open source distributed version control system
- [Node.js](https://nodejs.org/en/) - JavaScript runtime
- [npm](https://www.npmjs.com/) - JavaScript Package Manager
- [Vite](https://vitejs.dev/) - Build tool and module bundler
- [React](https://react.dev/) - UI library
- [Boosted](https://boosted.orange.com/) - Orange's Bootstrap variant

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/pks/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

This library is **shared across all DISCOBOLE projects** — changes affect everything. Please:

- ✅ Export only what multiple projects need
- ✅ Keep the API surface small and stable
- ✅ Document new exports in this README
- ✅ Run `npm run lint` before committing
- ❌ Don't add MFE-specific business logic
- ❌ Don't make breaking changes without team coordination

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

Versioning follows these rules:

- **MAJOR** — Breaking API changes (requires team coordination)
- **MINOR** — New backward-compatible exports
- **PATCH** — Bug fixes only

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed under the MIT License — see the [LICENSE.txt](LICENSE.txt) file for details.
