<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Product Order Delivery Orchestation Component for Helm

This project implements a GitLab CI/CD template to build your Helm Charts and/or deploy your component to a Kubernetes platform using Helm.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-catalog/orchestration-delivery-component.git
```

To release your project, the following environment variables need to be defined.

| Name                 | Description                                                  | Value                                                        |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| :lock: `GITLAB_TOKEN` | The GitLab private access token defined with the `api` scope. | See your [preferences](https://gitlab.ow2.org/-/user_settings/personal_access_tokens?page=1&state=active&sort=expires_asc) (*required to deploy the project artifacts*). |

## Installation manually using Helm

A chart helm is provided in order to deploy your application manually.

Please consult its [documentation](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-component/-/blob/main/helm/chart/README.md).

## Installation via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with:

- **Kubernetes 1.20+**

**REMARKS:** Some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to expose your service outside the cluster:

- On Openshift, one must define an OpenShift Route
- On Rancher or any Kubernetes cluster running an Ingress controller, one may configure an Ingress

Sample custom value files for different environments are being provided with the chart to give you an example of what needs to be done.

The application can be built, tested and deployed in an automated manner with a GitLab-CI pipeline. The included `gitlab-ci.yml` file includes some templates provided by To Be Continuous for:

- Building the app (docker "to be continuous" template)
- Deploying the app (helm "to be continuous" template)
- Managing releases (semantic-release "to be continuous" template)

### Forking the Repository and Configuring the Pipeline

You will first have to fork this project in a namespace where you have write access. In order for the pipeline to execute properly, some CI/CD variables need to be set. The list of variables required by each template is documented via comments in the `gitlab-ci.yml` file.

### Deployment with Helm

Regarding deployments with the helm "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

### Versioning with Semantic Release

In order to let the semantic release template add the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

🔒 **GITLAB_TOKEN** (gitlab access token with api, read_repository and write repository scopes and Maintainer role)

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-order-orchestration/doc/)

## Contributing

We welcome contributions!

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-component/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-component/-/tags).

## Authors

See the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-component/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-component/-/blob/main/LICENSE.txt) file for details.