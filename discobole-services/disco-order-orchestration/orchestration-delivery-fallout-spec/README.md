<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# orchestration-delivery-spec

Expose materials for an implementation of orchestration fallout management functionality (it contains the classes to consume REST API and to send/receive events with an AsyncAPI).

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
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout-spec.git
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery-fallout-spec | jq .id
```

Build the application with your local maven settings:

```bash
mvn clean install 
```

### Usage of the API REST Specification

Copy and paste this inside your pom.xml dependencies block.

```xml
<dependency>
  <groupId>com.orange.discobole.orderorchestration</groupId>
  <artifactId>discobole-orderorchestration-orchestration-delivery-fallout-restapi-spec</artifactId>
  <version>1.1.0-SNAPSHOT</version>
</dependency>
```

### Usage of the API AsyncAPI Specification

Copy and paste this inside your pom.xml dependencies block.

```xml
<dependency>
  <groupId>com.orange.discobole.orderorchestration</groupId>
  <artifactId>discobole-orderorchestration-orchestration-delivery-fallout-asyncapi-spec</artifactId>
  <version>1.1.0-SNAPSHOT</version>
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
- [Component documentation](https://discobole.ow2.io/discobole/disco-oda-components/disco-order-orchestration/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
