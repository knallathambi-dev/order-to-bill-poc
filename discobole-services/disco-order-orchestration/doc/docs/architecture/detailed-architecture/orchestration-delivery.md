<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Customer Order Orchestration Delivery (COOD)

This Spring Boot application implements the TMFC003 standard for Product Order Delivery Orchestration and Management.

## Overview

This Spring Boot application implements the orchestration of the delivery of Product Orders based on the Product specification level of information available in the Product Catalog. it determines in which order the product specification level order items need to be delivered, and to which CFS.

It achieves this by constructing an orchestration plan composed of dependent nodes, where the dependencies are determined by the order items. These order items are sourced from order management events, specifically the ProductOrderStateChangeEvent.

It is responsible for the following:

- **Informing by events the Order Management system** about the delivery status of order items
- **Notifying the Product Inventory system** about product status changes and realizing services through patch requests as defined in TMF637 Product Inventory API Specification
- **Handling events from the Production factories** (delivery backend) to initiate the delivery of an item.
- **Initiate the delivery** of any node by publishing message to disco.delivery-management.deliveryStart-event to be consumed by Delivery Management after that.

As of now, non API REST specifications have been defined by TMForum for this implementation; you can refer to the Swagger documentation [here](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-spec/-/blob/main/orchestration-delivery-restapi-spec/src/main/resources/orchestration-delivery-spec.yaml?ref_type=heads) for more details once they are available.

The events are specified [here](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-spec/-/blob/main/orchestration-delivery-asyncapi-spec/src/main/resources/orchestration-delivery-asyncapi-spec.yaml?ref_type=heads).

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

To get a local copy of the project up and running, follow these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 17
- [Maven](https://maven.apache.org/) 3.8
- MongoDB
- Apache Kafka
- kafka connect

Your local Maven settings file (`$HOME/.m2/settings.xml`) must specify your local repository directory.

Your Maven repository may require authentication credentials to publish artifacts in unstable and stable maven repositories. To handle the authentication, please read the [to-be-continuous maven template documentation](https://to-be-continuous.gitlab.io/doc/ref/maven/#maven-repository-authentication) for more information.

### Installing

Clone the project with:

```bash
git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery
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
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery | jq .id
```

Run the `docker-compose/docker-compose.yaml` docker compose file.

```bash
docker-compose -up docker-compose/docker-compose.yaml
```

Do POST request in `docker-compose/config/Debezium.postman_collection.json` to configure Debezuim.

Install the project with maven command

```bash
mvn clean install
```

### Configuration

The default configuration is setup in `src/main/resources/config/application.yml` file.

### Running Locally

Run the application with maven springboot:run command and define needed variables.

```bash
KEYCLOAK_CLIENT_ID=cood 
KEYCLOAK_CLIENT_SECRET=<keycloak-secret> 
KEYCLOAK_URI=<keycloak-url> 
mvn spring-boot:run -Dspring-boot.run.profiles=dev,plaintextconsole
```

> **Note:**
> - You need to set the profiles above when you run to avoid having problems and to show the logs properly
> - The `dev` profile assumes you have [Order Management Mock Server](https://gitlab.ow2.org/discobole/disco-order-management/mock-server) application running at port 8082

You will need other services to complete the orchestration and delivery process:

- [Order Orchestration Delivery Management service](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management)
- [Order Orchestration Delivery Fallout service](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout)

### Debezium Configuration

To integrate Debezium for capturing changes in MongoDB, use the following configuration:

```json
{
    "name": "outbox-connector",
    "config": {
        "connector.class": "io.debezium.connector.mongodb.MongoDbConnector",
        "tasks.max": "1",
        "topic.prefix": "dbserver1",
        "mongodb.connection.string": "mongodb://host.docker.internal:27017/?replicaSet=rs0",
        "database.include.list": "cood",
        "collection.include.list": "cood.events",
        "database.history.kafka.bootstrap.servers": "kafka:9092",
        "transforms": "outbox",
        "transforms.outbox.type": "io.debezium.connector.mongodb.transforms.outbox.MongoEventRouter",
        "transforms.outbox.route.topic.replacement": "${routedByValue}",
        "transforms.outbox.collection.expand.json.payload": "true",
        "transforms.outbox.collection.field.event.timestamp": "timestamp",
        "transforms.outbox.collection.fields.additional.placement": "type:header:eventType,headers:header:headers,traceparent:header:traceparent",
        "transforms.outbox.route.by.field": "type",
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "value.converter": "org.apache.kafka.connect.storage.StringConverter",
        "include.schema.changes": "false",
        "errors.tolerance": "all",
        "errors.log.enable": "true",
        "errors.log.include.messages": "true"
    }
}
```

This configuration sets up a MongoDB connector with Debezium to monitor changes in the `COOD` database, particularly the `cood.events` collection, and routes events based on the specified fields.

## Running The Tests

- Add the following line to 

  ```bash
  vim etc/hosts
  127.0.0.1       docker1`
  ```

- Run tests using maven command, this will run unit tests, integration tests, BDD tests.

  ```bash 
  mvn test
  ```

## Usage

Access to the OpenAPI specification of the API at `{{deployed-url}}/swagger-ui/index.html`.

Access to the AsyncAPI specification of the API at `{{deployed-url}}/async-api/index.html`.

## Built With

Add tools used to build your application.

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please read [CONTRIBUTING.md](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery/-/blob/main/CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery/-/tags).

## Authors

See also the list of [contributors](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery/-/blob/main/CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery/-/blob/main/LICENSE.txt) file for details.