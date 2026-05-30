<!--
Software Name: orchestration-delivery-management
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Orchestration Delivery Management

This Spring Boot application externalize the delivery process to be outside Orchestration Delivery component to be able to developed and extendable based on the delivery factories and affiliates.

## Overview

This service is in charge of the delivery process of product order items either it is tangible or CFS (Customer Facing Service) items.

It achieves this by initiating the delivery factories based on the item/node type, it selects the correct delivery factory to start the delivery process and receive the delivery response then notify order-orchestration with it.

It consume and publish events specified by the [Orchestration Delivery AsyncAPI Specification](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-spec).

### Consumed Events

The Orchestration Delivery Management is triggered by the following event types:

- **`disco.delivery-management.deliveryStart-event`**:
  - Source: Order Orchestration
  - Description: event generated from Orchestration microservice to let Delivery microservice start the delivery process.

- **`disco.service-order-management.serviceOrderStateChange-event`**:
  - Source: Production System (Delivery Backend for CFS nodes)
  - Description: notify delivery management about the delivery status of items(CFS).

- **`disco.shipping-order-management.shippingOrderStateChange-event`**:
  - Source: Production System (Delivery Backend for Stock nodes that needs shipment)
  - Description: notify delivery management about the delivery status of shipment items.

### Triggered Events

The Orchestration Delivery Management throws the following event type:

**`disco.delivery-management.deliveryStatus-event`**:
  - Source: Orchestration Delivery Management
  - Description: event generated from Orchestration Delivery Management after finalize the delivery process and receieve the delivery response from the delivery factory.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

### Installing

Clone the project with:

```bash
git clone https://gitlab.com/Orange-OpenSource/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery-management | jq .id
```

Install the project with maven command.

```bash
mvn clean install
```

## Prerequisites

You will need the following installed on your local environment:

- GIT
- Java 17
- Maven 3.8
- MongoDB
- Apache Kafka

## Run Commands

To run using spring boot dev tools

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,plaintextconsole
```

> **Note:**
> - You need to set the profiles above when you run to avoid having problems and to show the logs properly
> - The `dev` profile assumes you have [Order Management Mock Server](https://gitlab.ow2.org/discobole/disco-order-management/mock-server) application running at port 8082

To run tests

```bash
mvn test
```

## Usage

There is no TMF API REST Specfication for this service but it contains APIs for configuration and testing purposes:

- `POST /api/cood/test/enable-service-order-state-change-job` to enableServiceOrderStateChangeServiceMock
- `POST /api/cood/test/disable-service-order-state-change-job` to disableServiceOrderStateChangeServiceMock
- `POST /api/cood/test/sendServiceOrder/serviceOrder` to publish ServiceOrder event
- `POST /api/cood/test/toggle-shipping-order-state-change-job` to enableShippingOrderStateChangeServiceMock

## Installation manually using Helm

A chart helm is provided in order to deploy your application manually. 

Please consult its [documentation](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/blob/main/helm/chart/README.md).

## Installation  via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with

- Kubernetes 1.20+

**REMARKS** : some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to expose your service outside the cluster:

- On *Openshift*, one must define an OpenShift Route
- On *Rancher* or *any Kubernetes cluster* running an Ingress controller, one may configure an Ingress

Sample custom value files for different environments are being provided with the chart to give you an example of what needs to be done.

The application can be built, tested and deployed in an automated manner with a GitLab-CI pipeline. The included [gitlab-ci.yml](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/blob/main/.gitlab-ci.yml) file includes some templates provided by [To Be Continuous](https://to-be-continuous.gitlab.io/doc/) for:

- Building the app (`docker` "to be continuous" template)
- Deploying the app (`helm` "to be continuous" template)
- Managing releases (`semantic-release` "to be continuous" template)

### Forking the repository and configuring the pipeline

You will first have to fork this project in a namespace where you have write access.

In order for the pipeline to execute properly, **some CI/CD variables need to be set**. The list of variables required by each template is documented via comments in the [gitlab-ci.yml](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/blob/main/.gitlab-ci.yml) file.

#### Docker images publication

The pipeline is configured to publish the Docker images produced by the [Docker](https://gitlab.com/to-be-continuous/docker) "to be continuous" template to the GitLab project internal registry. All template variables are configured by default to build and push your Docker images on the GitLab project internal registry.

### Deployment with Helm

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

#### Versioning with semantic release

In order to let the [semantic release template](https://gitlab.com/to-be-continuous/semantic-release) adding the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

- :lock: `GITLAB_TOKEN` (gitlab access token with `api`, `read_repository` and `write` repository scopes and `Maintener` role)

## Built With

Add tools used to build your application.

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-order-orchestration/doc)

## Contributing

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/tags).

## Authors

See the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/-/blob/main/LICENSE.txt) file for details.
