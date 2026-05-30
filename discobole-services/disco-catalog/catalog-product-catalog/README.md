<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Product Catalog Service

TMF-620, Catalog(Query), Catalog Projection(View).

Catalog, a subset of ODACA, which is as a whole is for product order fulfillment, serves the data according to the TMF620 which will be used for product offers' projection or view, and overall can be treated as the product catalog, comprising of Product Specifications,Product Offerings,Product Offering Price and Categories.

In the context of the DISCOBOLE (Digital & Innovative openSource platform for Core cOmmerce), a product catalog refers to a centralized database or repository that contains information about the products or services offered by a company. It is a core component of ODA Core Commerce Management, which is an implementation of an e-commerce system.

The product catalog in DISCOBOLE serves as a comprehensive and structured collection of product data.The catalog acts as a single source of truth for all product-related data within the DISCOBOLE ecosystem.

## Overview

The product catalog in the Digital & Innovative openSource platform for Core cOmmerce (DISCOBOLE) is a vital component of ODA Core Commerce Management. It provides centralized management, enables efficient search and navigation, facilitates inventory management, powers omnichannel commerce, aids marketing efforts, and integrates with other systems, ultimately enhancing the overall commerce capabilities of a business.

TMF620 refers to the Product Catalog Management API, which is a standard defined by the TM Forum. It provides a set of interfaces and operations for managing product catalog information within a digital ecosystem. The API enables efficient creation, retrieval, updating, and deletion of product catalog data, supporting various functionalities related to product offerings.

The Product Catalog Management API defined by TMF620 typically includes the following information:

1. **Product Specifications**: This includes detailed information about the products or services offered, such as their names, descriptions, features, and attributes. Product specifications define the characteristics and behavior of the products, serving as a blueprint for creating instances of products in the catalog.

2. **Product Offerings**: Product offerings represent specific instances of products available for sale or distribution. They may include additional information such as pricing, promotions, bundles, and availability. Product offerings are created based on the product specifications and can be associated with various commercial and operational attributes.

3. **Product Catalog Structure**: The API provides methods to define and manage the structure of the product catalog. This includes organizing products into categories, hierarchies, or collections, enabling efficient navigation and search capabilities. The catalog structure helps in organizing and presenting products in a logical manner to facilitate user engagement.

4. **Relationships and Associations**: The API allows the establishment of relationships and associations between products or between products and other entities in the ecosystem. This can include cross-selling, up-selling, dependencies, compatibility, and other connections that enhance the customer experience and enable effective product management.

5. **Lifecycle Management**: The Product Catalog Management API supports lifecycle management operations for products and their offerings. This includes handling product versioning, publication, retirement, and archival. Lifecycle management ensures that only relevant and up-to-date product information is available to consumers.

6. **Search and Query**: The API includes search and query functionalities to retrieve products and their associated information based on specific criteria. This allows consumers, applications, and systems to efficiently search and retrieve relevant products from the catalog.

7. **Export Feature**: This feature exports the data into excel file.


By implementing the TMF620 Product Catalog Management API, organizations can effectively manage their product catalog information, streamline product creation and management processes, enhance customer experiences, and support interoperability within a digital ecosystem.

### Basic Structure

The application is divided into 2 parts which provide command and query functionality for CQRS.

One is the Controller side which is Query funcationality i.e.

* ServiceSpecController
* ProductSpecController
* ProductOfferingController
* ProductOfferingPriceController
* StockItemController
* CategoryController

It is used the query the data related to specific APi's like product specifications, product offerings, etc. 

And Other part is Command Functionality in which Handlers are used to save event data in product catalog. The handlers are: 

* ServiceSpecEventHandler
* ProductSpecEventHandler
* ProductOfferingEventHandler
* ProductOfferingPriceEventHandler
* StockItemEventHandler
* CategoryEventHandler

### Working

Catalog is the processflow-cqrs based query part of ODACAT i.e. service to query the product specifications, atomic product offerings, bundled product offerings with filters, sorting order, etc. 
So if you need to fetch some data related to product specification or product offerings, you can query it to the catalog which will get you from the database.

Above url defines that we need to fetch all the product offering defined in the system. So for that we need to use that url.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the Product Catalog service up and running, follow these simple steps.

### Prerequisites

* [Git](https://git-scm.com/)
* [Java 17](https://jdk.java.net/17/)
* [Maven 3.8](https://maven.apache.org/)
* [MongoDB](https://www.mongodb.com/)
* [Kafka](https://kafka.apache.org/)

Ensure your local Maven settings file (`$HOME/.m2/settings.xml`) specifies your local repository directory.

### Installing

Clone the project:

```bash
git clone https://gitlab.ow2.org/discobole/disco-catalog/catalog-product-catalog.git
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-catalog%2Fcatalog-product-catalog | jq .id
```

Build the application:

```bash
mvn clean install
```

### Configuration

The default configuration is set in `src/main/resources/config/application.yml`.

### Running locally

Navigate to the service directory:

```bash
cd catalog-product-catalog
```

Start the catalog service:

```bash
java -jar target/catalog-product-catalog-{{version}}-exec.jar
```

To use a custom context root:

```bash
java -Dserver.servlet.context-path=/cat-catalog -jar target/catalog-product-catalog-{{version}}-exec.jar
```

To run on a different port:

```bash
java -Dserver.port=8081 -jar target/catalog-product-catalog-{{version}}-exec.jar
```

Access the Swagger UI after service startup.

## Running the tests

Run the test suite with:

```bash
mvn test
```

To run using Spring Boot dev tools:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,plaintextconsole
```

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

#### Docker images publication

The pipeline is configured to publish the Docker images produced by the [Docker](https://gitlab.com/to-be-continuous/docker) "to be continuous" template to the GitLab project internal registry. All template variables are configured by default to build and push your Docker images on the GitLab project internal registry.

### Deployment with Helm

Regarding deployments with the [helm](https://gitlab.com/to-be-continuous/helm) "to be continuous" template, this part is disabled but the Helm package is built and published in the GitLab project internal registry.

#### Versioning with semantic release

In order to let the [semantic release template](https://gitlab.com/to-be-continuous/semantic-release) adding the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:

- :lock: `GITLAB_TOKEN` (gitlab access token with `api`, `read_repository` and `write` repository scopes and `Maintener` role)

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Built With

* [Git](https://git-scm.com/)
* [Java 17](https://jdk.java.net/)
* [Maven](https://maven.apache.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [MongoDB](https://www.mongodb.com/)
* [Apache Kafka](https://kafka.apache.org/)

## Documentation

The **Catalog Product Catalog** service is the query layer of the CQRS-based ODACAT system. It enables querying of:

* Product Specifications
* Atomic Product Offerings
* Bundled Product Offerings

For more information, please consult:

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [API User Guide](doc/api-getting-started.md)
- [API TMF Compliancy](doc/api-compliance-report)
- [API TMF Conformance](doc/CTK-report.html)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
