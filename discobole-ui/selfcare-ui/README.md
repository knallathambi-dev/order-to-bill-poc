<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# selfcare-ui

Selfcare UI is a non-commercial order-capture frontend developed with React. It provides an intuitive interface for
users to interact with the order capture process, enabling seamless data entry and management for demo purposes.

## Community

You can chat with the core team on ![](https://dcbadge.limes.pink/api/server/https://discord.gg/pzcwfgqns7?style=flat).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

1. Install `Node.js` (example for Linux).
   ```bash
   sudo apt install -y nodejs
   ```

2. Install  `npm` (example for Linux).

   ```bash
   sudo apt install -y npm
   ```

**Nota**: also `nvm`, the Node Version Manager, could be installed if you managed many `npm` versions:
see [here](https://www.linode.com/docs/guides/how-to-install-use-node-version-manager-nvm/#use-nvm-to-install-node).

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-ui-portals/selfcare-ui.git
```

Install project dependencies with:

```bash
npm install
```
Install server project dependencies with:

```bash
cd server && npm install
```
add the server .env variables following .env.example

### Running locally

Start the application with:

```bash
npm run dev
```

### Usage

To access to the application, open your web browser and navigate to [http://localhost:3000](http://localhost:3000).

## Installation manually using Helm

A chart helm is provided in order to deploy your application manually.

Please consult its [documentation](helm/chart/README.md).

## Installation  via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with

- Kubernetes 1.20+

**REMARKS** : some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to
expose your service outside the cluster:

- On *Openshift*, one must define an OpenShift Route
- On *Rancher* or *any Kubernetes cluster* running an Ingress controller, one may configure an Ingress

Sample custom value files for different environments are being provided with the chart to give you an example of what
needs to be done.

The application can be built, tested and deployed in an automated manner with a GitLab-CI pipeline. The
included [gitlab-ci.yml](.gitlab-ci.yml) file includes some templates provided
by [To Be Continuous](https://to-be-continuous.gitlab.io/doc/) for:

- Building the app (`docker` "to be continuous" template)
- Deploying the app (`helm` "to be continuous" template)
- Managing releases (`semantic-release` "to be continuous" template)

### Forking the repository and configuring the pipeline

You will first have to fork this project in a namespace where you have write access.

In order for the pipeline to execute properly, **some CI/CD variables need to be set**. The list of variables required
by each template is documented via comments in the [gitlab-ci.yml](.gitlab-ci.yml) file.

#### Docker images publication

The pipeline is configured to publish the Docker images produced by
the [Docker](https://gitlab.com/to-be-continuous/docker) "to be continuous" template to the GitLab project internal
registry. All template variables are configured by default to build and push your Docker images on the GitLab project
internal registry.

### Deployment with Helm

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part
is disabled but the Helm package is built and published in the GitLab project internal registry.

#### Versioning with semantic release

In order to let the [semantic release template](https://gitlab.com/to-be-continuous/semantic-release) adding the
necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

- :lock: `GITLAB_TOKEN` (gitlab access token with `api`, `read_repository` and `write` repository scopes and `Maintener`
  role)

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Built With

Add tools used to build your application.

- [git](https://git-scm.com) - Open source distributed version control system
- [node](https://nodejs.org/en) - Server-side Javascript runtime
- [npm](https://www.npmjs.com/) - Javascript Package Manager

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.