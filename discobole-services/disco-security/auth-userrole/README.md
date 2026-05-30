<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# User Role & Permission Management

## Overview

The User role & permission management is a key component responsible for managing user entitlements and their mappings to roles within an authentication and authorization framework. It works in conjunction with the Keycloak server to ensure secure and managed access to resources.

1. Role-Based Access Control (RBAC) Implementation:
     * Authorization is implemented based on Role-Based Access Control (RBAC) in ODACAT.
     * RBAC restricts system access to authorized users based on their roles.
     * Controls what end-users can do at both broad and granular levels.
     * Users are designated as administrators, specialist users, or end-users, aligning roles with access  permissions.
2. User and Role Management:
     * Users are created and persisted in the auth-server (Keycloak).
     * Roles are mapped to specific users.
3. DISCO-AUTH Server:
     * Roles are not created manually on the auth-server (Keycloak).
     * A new service, DISCO-AUTH server, is created for this purpose.
     * DISCO-AUTH server handles the creation of roles with entitlements mapping.
4. Entitlements and Role Creation:
     * Entitlements are saved in the DISCO-AUTH server database and Keycloak.
     * After creating roles and entitlements mapping on DISCO-AUTH server, the role is automatically created  on Keycloak.

## Usage

To use this service, follow these steps:

1. Build and run the authorization-userrole-creation module to generate the necessary code from the Swagger specification.
2. Build and run the authorization-userrole-creation module to start the service, which will use the generated code to handle API requests and business operations.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

### Prerequisites

You will need the following installed on your local environment:

- [git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 17
- [Maven](https://maven.apache.org/) 3.8

### Installing

Clone the project with:

```bash
git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-security/auth-userrole.git
```

If you haven't already done so, you will need to add the following lines in your local maven settings file.

```xml
<server>
  <id>gitlab-maven</id>
  <configuration>
    <httpHeaders>
      <property>
        <name>Private-Token</name>
        <value>${GITLAB_TOKEN}</value>
      </property>
    </httpHeaders>
  </configuration>
</server> 
```

To build and deploy the application, the following environment variables need to be defined and exported in your terminal.

| Name                 | Description                                                  | Value                                                        |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| :lock: `GITLAB_TOKEN` | The GitLab private access token defined with the `api` scope. | See your [preferences](https://gitlab.ow2.org/-/user_settings/personal_access_tokens?page=1&state=active&sort=expires_asc) (*required to deploy the project artifacts*). |
| `CI_API_V4_URL`      | The GitLab API v4 root URL.                                  | `https://gitlab.ow2.org/api/v4` (*required to pull & deploy the project artifacts*). |
| `CI_PROJECT_ID`      | The ID of the GitLab project. This ID is part of the GitLab project package registry URL. | See the project general settings (*required to deploy the maven artifacts*). |
| `GROUP_ID`           | The ID of the GitLab group visible in the [project general settings](https://gitlab.ow2.org/groups/discobole/-/edit). This ID is part of the GitLab group package registry URL. | `5972` (*required only if the project depends on `DISCOBOLE` maven artifac*t). |

:information_source: If you don't have access to the project general settings, the project ID may be retrieved as follows: 

```bash
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-security%2Fauth-userrole | jq .id
```

Build the application.

```bash
mvn clean install
```

Run locally.

```bash
mvn clean install 
```

Play with the API via Swagger UI.

## Installation manually using Helm

A chart helm is provided in order to deploy your application manually. 

Please consult its [documentation](helm/chart/README.md).

## Installation  via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with

- Kubernetes 1.20+

**REMARKS** : some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to expose your service outside the cluster:

- On *Openshift*, one must define an OpenShift Route
- On *Rancher* or *any Kubernetes cluster* running an Ingress controller, one may configure an Ingress

Sample custom value files for different environments are being provided with the chart to give you an example of what needs to be done.

The application can be built, tested and deployed in an automated manner with a GitLab-CI pipeline. The included [gitlab-ci.yml](.gitlab-ci.yml) file includes some templates provided by [To Be Continuous](https://to-be-continuous.gitlab.io/doc/) for:

- Building the app (`docker` "to be continuous" template)
- Deploying the app (`helm` "to be continuous" template)
- Managing releases (`semantic-release` "to be continuous" template)

### Forking the repository and configuring the pipeline

You will first have to fork this project in a namespace where you have write access.

In order for the pipeline to execute properly, **some CI/CD variables need to be set**. The list of variables required by each template is documented via comments in the [gitlab-ci.yml](.gitlab-ci.yml) file.

### Docker images publication

The pipeline is configured to publish the Docker images produced by the [Docker](https://gitlab.com/to-be-continuous/docker) "to be continuous" template to the GitLab project internal registry. All template variables are configured by default to build and push your Docker images on the GitLab project internal registry.

### Deployment with Helm

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

### Versioning with semantic release

In order to let the [semantic release template](https://gitlab.com/to-be-continuous/semantic-release) adding the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

- :lock: `GITLAB_TOKEN` (gitlab access token with `api`, `read_repository` and `write` repository scopes and `Maintener` role)

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Built With

Add tools used to build your application.

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-security/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.


