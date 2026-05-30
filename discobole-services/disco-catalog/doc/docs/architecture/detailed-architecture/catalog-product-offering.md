<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Product Offering Service

Product Offering, Catalog(Command), TMF-701, TMF-620.

## Overview

Product offerings represent specific instances of products available for sale or distribution. They may include additional information such as pricing, promotions, bundles, and availability. Product offerings are created based on the product specifications and can be associated with various commercial and operational attributes.
The purpose of a product specification is to provide a detailed and standardized description of a products. 

The Catalog component follows the CQRS pattern. The Product Offering handles only the command part and manages stages like creation of PO and modification of PO.The PO is Envelope dependent i.e. based on TMF 701 API and uses Event Store (AxonDB) to save the data.

### Brief description of Sequence diagram:

1. ProductOfferingUserAction will send data to service class which is needed to perform that task
2. ProductOfferingService will process the data and sends a command and wait for a result either synchronously, with a timeout or asynchronously.
3. ProductOfferingAggregate - As an aggregate will handle commands that are targeted to a specific aggregate instance, we need to specify the identifier with the AggregateIdentifier annotation.
In ProductOfferingAggregate ,business logic is written for the creation and the updation tasks and events are published to specific event handler.
It's automatically save the event into axon database.
4. ProductOfferingEventHandler : In this ProductOfferingEventHandler, we are calling handle method in which we pass the event and this method internally call the ProductOfferingProjector for publishing the events.'
5. ProductOfferingProjector - will send the events to kafka stream.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

* [Git](https://git-scm.com/)
* [Java 17](https://jdk.java.net/17/)
* [Maven 3.8](https://maven.apache.org/)

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-catalog/catalog-product-offering.git
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-catalog%2Fcatalog-product-offering | jq .id
```

Build the application:

```bash
mvn clean install
```

### Configuration

The default configuration is set up in the `src/main/resources/config/application.yml` file.

### Running locally

To launch the service on your local machine:

```bash
mvn spring-boot:run
```

Or to start the service with another context root:

```bash
java -Dserver.servlet.context-path=/product-offering -jar target/product-offering-{{version}}-exec.jar
```

To start the service on another port:

```bash
java -Dserver.port=8081 -jar target/product-offering-{{version}}-exec.jar
```

### Usage

Access the API specification via Swagger UI at:

[http://localhost:8080](http://localhost:8080)

## Running the tests

Run the unit tests with:

```bash
mvn test
```

## Installation manually using Helm

A Helm chart is provided in order to deploy your application manually.

Please consult its [documentation](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/helm/chart/README.md).

## Installation via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with:

* Kubernetes 1.20+

**Remarks**: Some deployment parameters depend on the target infrastructure.

For example:

* On *OpenShift*, define an OpenShift Route
* On *Rancher* or clusters with Ingress controllers, configure an Ingress

Sample custom value files for different environments are provided with the chart.

The application can be built, tested, and deployed in an automated manner using GitLab-CI. The provided `.gitlab-ci.yml` includes [To Be Continuous](https://to-be-continuous.gitlab.io/doc/) templates for:

* Building the app (`docker`)
* Deploying the app (`helm`)
* Managing releases (`semantic-release`)

### Forking the repository and configuring the pipeline

Fork this project into a namespace where you have write access.

**CI/CD Variables Required:**
Check the `.gitlab-ci.yml` comments for a list of variables required by each template.

#### Docker images publication

The pipeline publishes Docker images using the [Docker](https://gitlab.com/to-be-continuous/docker) template to the GitLab project internal registry.

### Deployment with Helm

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

#### Versioning with semantic release

In order to let the [semantic release template](https://gitlab.com/to-be-continuous/semantic-release) adding the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

- :lock: `GITLAB_TOKEN` (gitlab access token with `api`, `read_repository` and `write` repository scopes and `Maintener` role)

## Built With

* [Git](https://git-scm.com/) – Open source distributed version control system
* [Java 17](https://jdk.java.net/)
* [Maven](https://maven.apache.org/) – Dependency Management
* [Spring Boot](https://spring.io/projects/spring-boot) – Microservice framework

## Documentation

* [API User Guide](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/doc/api-getting-started.md)
* [TMF Compliancy](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/doc/api-compliance-report)
* [TMF Conformance](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/doc/CTK-report.html)

## Contributing

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests.

## Versioning

We use [Semantic Versioning (SemVer)](http://semver.org/).
See the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/tags) for available versions.

## Authors

See the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed under the [MIT License](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-offering/-/blob/main/LICENSE.txt). See the license file for details.