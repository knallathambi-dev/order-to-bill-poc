<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# product-inventory

This project implements the ODA Product Inventory component by exposing the TMF637 Product Inventory Management API REST Specification.  

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Prerequisites

You will need the following installed on your local environment:

- [git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 17
- [Maven](https://maven.apache.org/) 3.8
- mongodb
- docker

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

Your Maven repository may require authentication credentials to publish artifacts in unstable and stable maven repositories. To handle the authentication, please read the [to-be-continuous maven template documentation](https://to-be-continuous.gitlab.io/doc/ref/maven/#maven-repository-authentication) for more information.

### Installing

Clone the project with:

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-product-inventory/product-inventory.git
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-product-inventory%2Fproduct-inventory | jq .id
```

Build the application with:

```bash
cd product-inventory
mvn clean install
```

### Running locally

Before running the application locally, 

- You may need to add your own certificate to the Java certificate store.
- You will need to provide `KEYCLOAK_CLIENT_SECRET` in environment variable to be able to run. This is the service account client secret that is needed to access catalog APIs.
- You will need also mongodb and kafka servies.

### Docker-compose file to run mongodb and kafka

There is a docker-compose file to run both mongodb and kafka to make it easier to start with development.

```bash
docker-compose -f docker/mongodb-kafka.yml up -d
docker-compose -f docker/minio.yml up -d # for s3
```

Or if using newer versions of docker

```bash
docker compose -f docker/mongodb-kafka.yml up -d
docker compose -f docker/minio.yml up -d # for s3
```

### Running the app itself

Before running the project, make sure the following environment variables are set:

| Variable Name             | Description                                 | Example Value              |
|---------------------------|---------------------------------------------|----------------------------|
| `BUCKET_HOST`             | Hostname or IP of the object storage bucket | `localhost`                |
| `BUCKET_PORT`             | Port used to access the bucket              | `9000`                     |
| `BUCKET_NAME`             | Name of the bucket                          | `product-bucket`           |
| `BUCKET_REGION`           | Region of the bucket                        | `us-east-1`                |
| `ACCESS_KEY_ID`           | Access key for authentication               | `minioadmin`               |
| `SECRET_ACCESS_KEY`       | Secret key for authentication               | `minioadmin`               |
| `LINK_EXPIRATION_MINUTES` | Link expiry time in minutes                 | `15`                       |

To run using spring boot dev tools:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,plaintextconsole
```

Notice that you need to set the profiles above when you run to avoid having problems.

To use your favorite IDE, you can run `product-inventory-service/src/main/java/com/orange/disco/productinventory/ProductInventoryApplication.java` file and it would start the application.

## Running tests

```bash
mvn test
```

Tests use test containers and it needs docker to be running to work.

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

Add tools used to build your application.

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-product-inventory/doc/)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
