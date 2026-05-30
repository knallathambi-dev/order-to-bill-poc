<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Product Catalog Administration Service

The Product Catalog Administration API provides management capabilities which includes CRUD operations for entities that are referred in product catalog like catalog constants. These may be currencies, units of measures, frequencyTypes etc. The API is not a TMF prescribed API and supports POST, PATCH, PUT, and DELETE operations.

## Overview

Product Catalog Administration is designed for admin operations like unit, frequency, currency, cpib-config.

- **Unit** can be GB, TB, MB etc
- **Currency** can be INR, FR, EURO etc
- **Frequency** can be DAY, MONTH etc

### Functions

#### Create a Unit

**Pre-condition:** unitOfMeasure attribute in payload is mandatory. 
**Post condition:** Unit is created. 
**Error:** Missing Body Field UnitOfMeasure

#### Create a Currency

**Pre-condition:** Label and Code should be mandatory. Code should be 3 letters and in uppercase.
**Post condition:** Currency is created
**Error:** 

1. Please provide mandatory fields code and label
2. Please mention 3 letter code for currency compliant with ISO4217

#### Create a Frequency

**Pre-condition:** frequencyCode and frequencyLabel should be mandatory. frequencyLabel should be same with code and will fill automatically. 
**Post condition:** Frequency is created 
**Error:** Missing Body Field Frequency Label

#### Retrieve unit/frequency/currency by id

**Pre-condition:** A user wishes to retrieve a set of unit, currency, frequency from an id.
**Post condition:** A resource representation is retrieved 
**Error:** id did not match existing and resource not found

#### Modify unit/frequency/currency by id

**Pre-condition:** A user wishes to modify a set of unit, currency, frequency from a set of search criteria 
**Post condition:** A list of resource is modified. 
**Error:** id did not match existing or wrong attribute update required

#### Delete unit/frequency/currency by id

**Pre-condition:** A user wishes to delete set of unit, currency, frequency from an id.
**Post condition:** A resource is deleted 
**Error:** id did not match existing resource, or the resource is still used from another active resource

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- **Git**
- **Java 17**
- **Maven 3.8**
- **MongoDB**

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

### Dependencies

- **Spring Data MongoDB** - For database operations
- **Actuator and Micrometer** - For monitoring purposes (to generate application metrics)
- **Lombok** - For object mapping between Java beans
- **OAuth2** - For authorization

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-catalog/catalog-product-catalog-administration.git
```

If you haven't already done so, you will need to add the following lines in your local maven settings file:

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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-catalog%2Fcatalog-product-catalog-administration | jq .id
```

Build the application with:

```bash
mvn clean install
```

## Configuration

The default configuration is setup in `src/main/resources/config/application.yml` file.

## Tools

### OpenAPI

This repository contains a service divided into two modules:

#### The API REST Specification Module

This module is responsible for defining contracts and specifications for the service. It includes the following:

- **Contracts:** This module defines the APIs, request-response models, and data structures used by the service.
- **Swagger YAML:** The `product-catalog-administration-spec/src/main/resources/swagger.yaml` file documents the service's API specification. It outlines all available endpoints, request parameters, and response formats.
- **Code Generation:** The module utilizes OpenAPI 3 code generation to create DTOs (Data Transfer Objects) and client code based on the Swagger specification.

#### The API REST Service Module

This module implements the service using the generated code from the API REST Specification module. 

It includes the actual service logic with business operations, and uses the DTOs and client code generated by the API REST Specification module to interact with the defined API.

## Running Locally

To launch the service on your local machine using Spring Boot dev tools:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,plaintextconsole
```

Notice that you need to set the profiles above when you run to avoid having problems and to show the logs properly.

Or start the service with your own configuration `application-<your profile name>.yml` overriding the default configuration:

```bash
mvn spring-boot:run -Pspring.profiles.active=<your profile name>
```

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
helm install catalog-product-catalog-administration ./helm -f ./helm/values.yaml
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

## Built With

- **Git** - Open source distributed version control system
- **Java 17** - Programming language
- **Maven** - Dependency Management
- **Spring Boot** - Application framework
- **MongoDB** - Database
- **OpenAPI 3** - API specification

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [API User Guide](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-catalog-administration/-/blob/main/doc/api-getting-started.md)

## Contributing

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-catalog-administration/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-catalog-administration/-/tags).

## Authors

See the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-catalog-administration/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-product-catalog-administration/-/blob/main/LICENSE.txt) file for details.