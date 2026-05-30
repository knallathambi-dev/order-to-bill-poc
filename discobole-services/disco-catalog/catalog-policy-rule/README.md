<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Policy Rule Service

This Spring Boot application implements the TMF723 standard for Policy Rule. As of now, no API specifications have been defined for this implementation; you can refer to the  `catalog-policy-rule-spec/src/main/resources/policy-rule-api-spec.yaml` Swagger documentation for more details once they are available.

## Overview

Policy API is designed from SID Policy description. The Policy API is defined from Event-Condition-Action ECA definition:

- The Event part specifies the signal that triggers the invocation of the rule
- The Condition part is a logical test that, if satisfied or evaluates to true, causes the action to be carried out
- The Action part consists of updates or invocations on the local data

### Functions

#### Create a policy event

Pre-condition: An event requiring a policy rule is identified. It could be an event from other TMF open API or a time event. The event could be precise to specific sub case like a specific attribute change from “attribute value change” generic event.

Post condition: The event is created

Error: Incomplete data provided – the event is not created

#### Create a policy condition

Pre-condition: A policy condition to ‘validate’ the execution of a policy rule is identified. It must be described as a <<variable>> + operation + <<value>>. A policy condition could be defined as a composition of policy condition.

Post condition: The policy condition is created

Error: Incomplete/incorrect data provided – the policy condition is not created

#### Create a policy action

Pre-condition: A policy action to be applied once a policy rule condition is verified is identified. It could be a specific action like raising an alarm or an event but also it could be a resource creation or an existing resource update.

Post condition: The policy action is created

Error: Incomplete data provided – the policy action is not created

#### Create a policy rule

Pre-condition: policy event, policy condition or composition of policy condition (a set of policy condition linked with AND/OR conjunction) and policy action or composition of policy action are existing and related to a requirement to define a new policy rule

Post condition: The policy rule is created

Error: Incomplete/incorrect data provided – the policy rule is not created

#### Search policy rule/action/condition/event

Pre-condition: an user wishes to retrieve a set of policy rule, policy event, policy condition and policy action from a set of search criteria

Post condition: A list of resource is retrieved (could be empty)

Error: Search criteria too vague so too many records retrieved

#### Retrieve policy rule/action/condition/event by id

Pre-condition: an user wishes to retrieve a policy rule, policy event, policy condition and policy action from an id.

Post condition: A resource representation is retrieved

Error: id did not match existing and not record are retrieved

#### Modify policy rule/action/condition/event

Pre-condition: an user wishes to modify a set of policy rule, policy event, policy condition and policy action from a set of search criteria

Post condition: A list of resource is modified.

Error: id did not match existing or wrong attribute update required

#### Delete policy rule/action/condition/event by id

Pre-condition: an user wishes to delete a policy rule, policy event, policy condition and policy action from an id.

Post condition: A resource is deleted

Error: id did not match existing resource, or the resource is still used from another active resource

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 17
- [Maven](https://maven.apache.org/) 3.8

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

Your Maven repository may require authentication credentials to publish artifacts in unstable and stable maven repositories. To handle authentication, please read the [to-be-continuous maven template documentation](https://to-be-continuous.gitlab.io/doc/ref/maven/#maven-repository-authentication) for more information.

### Dependencies

- **Spring Data Mongo DB**
- **Actuator and Micrometer**: For monitoring purposes(to generate application metrics)
- **lombok**: for Object mapping between Java beans.
- **oauth2**: for authorization.

## Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-catalog/catalog-policy-rule.git
```

If you haven't already done so, add the following lines in your local Maven settings file:

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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-catalog%2Fcatalog-policy-rule | jq .id
```

Build the application with:

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
Or start the service with your own configuration `application-<your profile>.yml` overriding the default configuration:

```bash
mvn spring-boot:run -Pspring.profiles.active=<your profile>
```

### Usage

Once running, refer to the [Swagger documentation](http://localhost:8080) for available endpoints and usage details (to be published).

## Running the tests

Run the unit tests with:

```bash
mvn tests
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

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Documentation

- [TMF723 Policy Management API Overview](https://www.tmforum.org/oda/open-apis/directory/policy-management-api-TMF723/v5.0.0)
- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [API User Guide](doc/api-getting-started.md)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
