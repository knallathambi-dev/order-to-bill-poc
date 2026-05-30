<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Orchestration Delivery Fallout

During the order orchestration and delivery process, some errors may occur and would impact the progress of the execution. The errors could be related either to the decomposition of the order or the delivery activities done by the delivery factories.

To handle the different errors,  the [Process Flow library](https://gitlab.ow2.org/discobole/disco-oda-components/process-flow) that supports the TMF701 Process Flow Management API REST Specification is used to handle the fallout process.

Then the creation and update of fallout incidents are implemented by the [Orchestration Fallout Management API REST Specification](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout-spec/-/tree/main/orchestration-delivery-fallout-restapi-spec/src/main/resources?ref_type=heads) while the status updates of  fallout incidents are published based on the [Orchestration Fallout Management API Async Specification](https://gitlab.ow2.orgdisco/disco-oda-components/disco-order-orchestration/orchestration-delivery-spec/-/tree/main/orchestration-delivery-asyncapi-spec/src/main/resources/asyncapi-files?ref_type=heads) towards the [Orchestration Delivery API](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout-spec).

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 17
- [Maven](https://maven.apache.org/) 3.8
- MongoDB
- docker

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

Your Maven repository may require authentication credentials to publish artifacts in unstable and stable maven repositories. To handle the authentication, please read the [to-be-continuous maven template documentation](https://to-be-continuous.gitlab.io/doc/ref/maven/#maven-repository-authentication) for more information.

### Installing

Clone the project with:

```bash
git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery-fallout | jq .id
```

Build the application with:

```bash
mvn clean install
```

### Configuration

The default configuration is setup in `src/main/resources/config/application.yml` file.

### Running locally

To run using spring boot dev tools

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev,plaintextconsole
```

> **Note:**
> - You need to set the profiles above when you run to avoid having problems and to show the logs properly
> - The `dev` profile assumes you have [Order Management Mock Server](https://gitlab.ow2.org/discobole/disco-order-management/mock-server) application running at port 8082

## Running the tests

Run the unit tests with:

```bash
mvn tests
```
## Usage

Access to the OpenAPI specification of the API at `{{deployed-url}}/swagger-ui/index.html`.

Access to the AsyncAPI specification of the API at `{{deployed-url}}/async-api/index.html`.

## Installation manually using Helm

A chart helm is provided in order to deploy your application manually.

Please consult its [documentation](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/blob/main/helm/chart/README.md).

## Installation  via CI/CD with GitLab-CI and Helm

### Prerequisites

You will need access to a remote Kubernetes cluster (OpenShift, Rancher, GKE, Vanilla Kubernetes, ...) with

- Kubernetes 1.20+

**REMARKS** : some parameters depend on the target infrastructure (Openshift, Rancher, ...). For instance, in order to expose your service outside the cluster:

- On *Openshift*, one must define an OpenShift Route
- On *Rancher* or *any Kubernetes cluster* running an Ingress controller, one may configure an Ingress

Sample custom value files for different environments are being provided with the chart to give you an example of what needs to be done.

The application can be built, tested and deployed in an automated manner with a GitLab-CI pipeline. The included [gitlab-ci.yml](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/blob/main/.gitlab-ci.yml) file includes some templates provided by [To Be Continuous](https://to-be-continuous.gitlab.io/doc/) for:

- Building the app (`docker` "to be continuous" template)
- Deploying the app (`helm` "to be continuous" template)
- Managing releases (`semantic-release` "to be continuous" template)

### Forking the repository and configuring the pipeline

You will first have to fork this project in a namespace where you have write access.

In order for the pipeline to execute properly, **some CI/CD variables need to be set**. The list of variables required by each template is documented via comments in the [gitlab-ci.yml](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/blob/main/.gitlab-ci.yml) file.

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

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/tags).

## Authors

See the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/-/blob/main/LICENSE.txt) file for details.