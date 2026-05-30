<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Lifecycle Management Service

Lifecycle (Command), TMF-701, TMF-620 compliant service for catalog element governance and progression through defined states in the product lifecycle.

## Overview

The **Lifecycle Management** service enables the governance and progression of catalog elements—such as Product Specifications, Product Offerings, or Technical Entities—through their defined states in the product lifecycle. It ensures catalog items move from conception to production in a controlled, auditable, and standardized manner.

The lifecycle states provide clarity on where a catalog entity stands and determine the allowable actions for modification, publication, or retirement. This helps maintain catalog integrity, supports version control.

Lifecycle states are used in compliance with TM Forum standards (e.g., TMF620, TMF701) to ensure interoperability and governance across digital product catalogs.

### Purpose of Lifecycle Management

- **Govern Product Evolution**: Ensure products are tested, validated, and released in a structured and traceable manner
- **Enforce Change Control**: Restrict direct edits to live entities, requiring formal versioning and approvals
- **Track Product Availability**: Determine commercial visibility and operability for catalog items
- **Comply with TMF Standards**: Follow best practices outlined in TMF620 & TMF701 for catalog lifecycle governance

### Version Management Rules

- Lifecycle state **before `Active`** (i.e., `InStudy`, `InDesign`, `InTest`) allows **direct modification**
- Entities in state **`Rejected` or `Obsolete`** are **not editable**
- Once **`Active` or beyond**, changes require a **new version**

### Versioning Types

#### Minor Version (e.g., 1.0 → 1.1)

- For **backward-compatible changes** (e.g., adding enum value)
- Only the **latest version stays in `Active` or `Launched`**; old one is `Retired`
- Managed using `validFor` attribute

#### Major Version (e.g., 2.1 → 3.0)

- For **breaking changes** or redesigns
- Starts in `InStudy`
- Requires **migration or adaptation** of installed base if active

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- **Git**
- **Java 17**
- **Maven 3.8+**
- **MongoDB** (Axon Event Store)
- **Kafka** for Event Streaming

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

### Dependencies

- **Spring Boot** - Application framework
- **Axon Framework** - For CQRS & Event Sourcing
- **Kafka** - For Event Streaming
- **MongoDB** - Database (Axon Event Store)
- **Maven** - Dependency Management

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-catalog/catalog-product-lifecycle-management.git
```

If you haven't already done so, you will need to add the following lines in your local maven settings file:

```xml
<server>
  <id>gitlab-maven</id>
  <configuration>
    <httpHeaders>
      <property>
        <n>Private-Token</n>
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-catalog%2Fcatalog-product-lifecycle-management | jq .id
```

Build the application with:

```bash
mvn clean install
```

## Configuration

The default configuration is setup in `src/main/resources/config/application.yml` file.

## Running Locally

### Build & Run

```bash
cd catalog-product-lifecycle-management
mvn clean install

# Run with default port
java -jar target/catalog-product-lifecycle-management-{version}-exec.jar

# Run with custom port
java -Dserver.port=8086 -jar target/catalog-product-lifecycle-management-{version}-exec.jar
```

Or start the service with Spring Boot:

```bash
mvn spring-boot:run
```

Or start the service with your own configuration `application-<your profile name>.yml` overriding the default configuration:

```bash
mvn spring-boot:run -Pspring.profiles.active=<your profile name>
```

## Usage

Access Swagger UI at: `http://localhost:8086/swagger-ui.html`.

The service provides endpoints for:

- Managing product lifecycle states
- Version control and progression
- Enforcing change control policies
- TMF620 & TMF701 compliant operations

## Running the Tests

Run the unit tests with:

```bash
mvn test
```

## Deployment

### Manual Installation using Helm

A chart helm is provided in order to deploy your application manually. Please consult its documentation.

In order to deploy you will need helm and a kubernetes cluster:

```bash
helm install cpib-release ./helm -f ./helm/values-local.yaml
```

You will need to provide some values and pipeline has deploy step that has some of those for openshift environment.

### Installation via CI/CD with GitLab-CI and Helm

#### Prerequisites

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

#### Forking the Repository and Configuring the Pipeline

You will first have to fork this project in a namespace where you have write access. In order for the pipeline to execute properly, some CI/CD variables need to be set. The list of variables required by each template is documented via comments in the `gitlab-ci.yml` file.

#### Docker Images Publication

The pipeline is configured to publish the Docker images produced by the Docker "to be continuous" template to the GitLab project internal registry. All template variables are configured by default to build and push your Docker images on the GitLab project internal registry.

#### Deployment with Helm

Regarding deployments with the helm "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

#### Versioning with Semantic Release

In order to let the semantic release template add the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

🔒 **GITLAB_TOKEN** (gitlab access token with api, read_repository and write repository scopes and Maintainer role)

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Built With

- **Git** - Open source distributed version control system
- **Java 17** - Programming language
- **Maven** - Dependency Management
- **Spring Boot** - Application framework
- **Axon Framework** - CQRS & Event Sourcing
- **Kafka** - Event Streaming
- **MongoDB** - Database (Axon Event Store)

## Documentation

- [TMF723 Policy Management API Overview](https://www.tmforum.org/oda/open-apis/directory/policy-management-api-TMF723/v5.0.0)
- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [API User Guide](doc/api-getting-started.md)

## Contributing

We welcome contributions!

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.