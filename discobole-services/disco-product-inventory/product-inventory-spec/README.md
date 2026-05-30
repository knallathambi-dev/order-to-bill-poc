<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# product-inventory-spec

Product Inventory API REST and Async specifications.

## Overview

This project is responsible for defining contracts and specifications for the service. It includes the following:

- The [OpenAPI specification](https://gitlab.ow2.org/discobole/disco-oda-components/disco-product-inventory/product-inventory-spec/-/tree/main/product-inventory-restapi-spec/src/main/resources) documents the service's API specification. It outlines all available endpoints, request parameters, and response formats.

- The [AsynAPI specification](https://gitlab.ow2.org/discobole/disco-oda-components/disco-product-inventory/product-inventory-spec/-/tree/main/product-inventory-asyncapi-spec/src/main/resources/asyncapi-files) documents the events sent and received by the Product Inventory component.

It utilizes OpenAPI 3 code generation to create DTOs (Data Transfer Objects) and client code based on the Swagger specification.

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
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-product-inventory/product-inventory-spec.git
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-product-inventory%2Fproduct-inventory-spec | jq .id
```

Build the application with:

```bash
mvn clean install 
```

### Usage of the API REST Specification

Copy and paste this inside your `pom.xml` dependencies block.

```xml
<!-- for rest spec which is defined in the swagger file -->
<dependency>
    <groupId>com.orange.discobole.productinventory</groupId>
    <artifactId>discobole-productinventory-product-inventory-restapi-spec</artifactId>
    <version>1.5.0</version>
</dependency>
```

### Usage of the API AsyncAPI Specification

Copy and paste this inside your `pom.xml` dependencies block.

```xml
<!-- for async api spec which is defined in the async-api file -->
<dependency>
    <groupId>com.orange.discobole.productinventory</groupId>
    <artifactId>discobole-productinventory-product-inventory-asyncapi-spec</artifactId>
    <version>1.5.0</version>
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
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-product-inventory/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
