<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# order-inventory-spec

The Product Ordering Management API provides a standardized mechanism for placing a product order with all the necessary order parameters. A product order is created based on a product offering that is defined in a catalog. The product offering identifies the product or set of products that are available to a customer and includes characteristics such as pricing, product options and market.

This API has two specifications built as maven artifacts:

- **Product Ordering Management REST API Specification artifact** - This artifact consists of the data model and a simple set of operation interfaces to manage product offerings.

- **Product Ordering AsyncAPI Specification artifact** - This artifact consists of the event data model and a set of event-driven producer and consumer interfaces. Events are published in case the product order state is changed and in case product order attribute value change event; also. Events are received to update product ordering state and attributes when products are delivered and durinng the product ordering capture process.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 17
- [Maven](https://maven.apache.org/) 3.8

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

Your Maven repository may require authentication credentials to publish artifacts in unstable and stable maven repositories. To handle the authentication, please read the [to-be-continuous maven template documentation](https://to-be-continuous.gitlab.io/doc/ref/maven/#maven-repository-authentication) for more information.

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-order-management/order-inventory-spec.git
```

If you haven't already done so, you will need to add the following lines in your local maven `.m2/settings.xml` file.

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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-management%2Forder-inventory-spec | jq .id
```

Build the application with your local maven settings:

```bash
mvn clean install 
```

### Usage of the API REST Specification

Copy and paste this inside your pom.xml dependencies block.

```xml
<dependency>
  <groupId>com.orange.discobole.ordermanagement</groupId>
  <artifactId>discobole-ordermanagement-order-inventory-restapi-spec</artifactId>
  <version>1.8.0</version>
</dependency>
```

### Usage of the API AsyncAPI Specification

Copy and paste this inside your pom.xml dependencies block.

```xml
<dependency>
  <groupId>com.orange.discobole.ordermanagement</groupId>
  <artifactId>discobole-ordermanagement-order-inventory-async-spec</artifactId>
  <version>1.8.0</version>
</dependency>
```

#### Registry Setup

If you haven't already done so, you will need to add the below to your pom.xml file.

```xml
<repositories>
  <repository>
    <id>gitlab-maven</id>
    <url>${CI_API_V4_URL}/projects/{$GROUP_ID}/packages/maven</url>
  </repository>
</repositories>
```

## Built With

Add tools used to build your application.

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/discobole/disco-oda-components/disco-order-management/doc)
- [API User Guide](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/blob/main/doc/api-getting-started.md)
- [API TMF Compliancy](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/blob/main/doc/api-compliance-report)
- [API TMF Conformance](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/blob/main/doc/CTK-report.html)

## Contributing

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/tags).

## Authors

See the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-inventory-spec/-/blob/main/LICENSE.txt) file for details.
