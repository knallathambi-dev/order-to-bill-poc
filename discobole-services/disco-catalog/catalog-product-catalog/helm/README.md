<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# catalog-product-catalog

A Helm chart for product catalog  service

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/752345/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release disco-stable-repo/catalog-product-catalog
```

## Prerequisites Details

You will need access to a Kubernetes cluster, local or distant (OpenShift, Rancher, GKE, Vanilla Kubernetes, Minikube, MicroK8s, ...) with

- Kubernetes 1.20+

You will need the following installed and configured on your local environment:

- kubectl
- helm 3

Configure your local environment to have `kubectl` access to the target cluster.
For instance, if you wish to deploy to a project named `myproject` on `OpenShift`:

```bash
# first login to OpenShift cluster using your personal OpenShift token
$ oc login
# select the target project
$ oc project myproject
```

## Installing the Chart

To install the chart with the release name `my-release`:

```bash
$ helm install my-release disco-stable-repo/catalog-product-catalog
```

The command deploys the application on the Kubernetes cluster in the **default** configuration. The [Parameters](#parameters) section lists the parameters that can be configured during installation.

> **Tip**: List all releases using `helm list`

## Uninstalling the Chart

To uninstall/delete the `my-release` deployment:

```console
$ helm uninstall my-release
```

The command removes all the Kubernetes components associated with the chart and deletes the release.

The following table lists the configurable values of the chart.

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` | Pod affinity |
| autoscaling.enabled | bool | `false` | enable autoscaling |
| autoscaling.maxReplicas | int | `5` | maximum number of replicas |
| autoscaling.minReplicas | int | `1` | minimum number of replicas |
| autoscaling.targetCPUUtilizationPercentage | int | `80` | target CPU utilization percentages for pod scaling |
| contextPath | string | `""` | Context path for the application |
| dailyshutdown | string | `"enabled"` | Enable or disable application daily shutdown |
| database.enabled | bool | `true` |  |
| database.rootPassword | string | `""` |  |
| database.rootUser | string | `"mongo_user"` |  |
| fluentd.enabled | bool | `false` | enable fluentd sidecar container |
| fluentd.host | string | `""` | hostname of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.tag | string | `""` | tag value being set on log events and used by fluentd to route logs |
| fullnameOverride | string | `""` | Override fully qualified app name |
| image.pullPolicy | string | `"Always"` | image pull policy |
| image.repository | string | `"${docker_repository}"` |  |
| image.tag | string | `"${docker_tag}"` |  |
| imagePullSecrets | list | `[]` | Specify pulling image secrets if your image repository requires it |
| ingress.annotations | object | `{}` | ingress  annotations |
| ingress.enabled | bool | `false` | enable ingress controller |
| ingress.hosts | list | `[{"host":"","paths":["/"]}]` | hosts configuration     |
| keyStoreP12 | string | `""` |  |
| mongo.architecture | string | `"standalone"` | MongoDB deployment |
| mongo.auth.enabled | bool | `false` | enable or disable mongodb root user |
| mongo.auth.rootPassword | string | `""` | mongodb root user password |
| mongo.auth.rootUser | string | `"mongo_user"` | mongodb root user name |
| mongo.enabled | bool | `true` | enable mongodb chart |
| mongo.extraEnvVarsSecret | string | `""` |  |
| mongo.fullnameOverride | string | `"catalog-mongo"` | override mongodb chart name |
| mongo.hidden.persistence.size | string | `"4Gi"` |  |
| mongo.image.registry | string | `"registry.hub.docker.com"` | docker image registry |
| mongo.image.repository | string | `"bitnamilegacy/mongodb"` | mongodb docker image repository |
| mongo.initdbScripts | object | `{"init.js":"// app user\ndb.getSiblingDB(\"catalog\")\nuse catalog;\nvar username='mongo_user';\nvar password=process.env[\"MONGO_DB_PWD\"];\nvar user=db.getUser(username);\nif (user == null) {\n  print('create user: ' + username);\n  db.createUser({user: 'mongo_user',pwd: password,roles: [ { role: 'readWrite', db: 'catalog' } ]});\n} else {\n  print('update user: ' + username);\n  db.updateUser(username, {pwd: password});\n}\nprint('---- done for ' + username);\nquit(0);\n"}` | init scripts to create app user |
| mongo.persistence.enabled | bool | `true` | enable persistence for mongodb |
| mongo.persistence.size | string | `"4Gi"` | size of mongodb persistence volume |
| mongo.service.nameOverride | string | `"catalog-mongo"` | override mongodb service name |
| mongo.service.ports.mongodb | int | `27017` | mongodb service port |
| mongo.useStatefulSet | bool | `true` |  |
| monitoring.application | string | `""` | label for application monitoring |
| monitoring.domain | string | `""` | label for exporter autodetection |
| monitoring.environment | string | `""` | label for environment being deployed |
| monitoring.productName | string | `""` | label to specify product name (chart name by default) |
| monitoring.role | string | `""` | label to specify role |
| monitoring.topology | string | `""` | label to specify topology |
| nameOverride | string | `""` | Override default fully qualified app name and chart name |
| nodeSelector | object | `{}` | Node affinity, tolerations and nodeSelector |
| otel.attributes.cluster | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.hostname | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.namespace | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.platform | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.region | string | `""` | resource attribute to be set on telemetry data |
| otel.enabled | bool | `false` |  |
| otel.sidecar.enabled | bool | `false` | use collector agent sidecar |
| otel.sidecar.jaeger | object | `{"host":"","port":14250}` | main collector address |
| otel.sidecar.otlp | object | `{"host":"","port":4317}` | main receiver address |
| podAnnotations | object | `{}` |  |
| podSecurityContext | object | `{}` |  |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"200m","memory":"1024Mi"}` | default resource limits |
| resources.requests | object | `{"cpu":"100m","memory":"128Mi"}` | default resource requests |
| securityContext | object | `{}` |  |
| service.annotations | object | `{}` |  |
| service.port | int | `8080` |  |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.annotations | object | `{}` | annotations to add to the service account |
| serviceAccount.create | bool | `false` | specifies whether a service account should be created |
| serviceAccount.name | string | `""` | name of the service account |
| services.catalogui | string | `"catalog-ui:80"` | catalog-ui service |
| services.category | string | `"catalog-product-category:8080"` | dependent microservice   |
| services.dbname | string | `"catalog"` | Mongo database name |
| services.gateway | string | `"disco-gateway:8080"` | dependent microservice   |
| services.kafka | string | `"kafka:9092"` | hostname and port of kafka |
| services.keycloak | string | `"keycloak:80"` | hostname and port of keycloak |
| services.keycloakrealm | string | `"SpringBootKeycloak"` | Keycloak realm |
| services.keycloakroute | string | `"keycloak"` | dependent microservice   |
| services.lifecycle | string | `"catalog-product-lifecycle-management:8080"` | dependent microservice   |
| services.mockservice | string | `"mock-server:80"` | dependent microservice   |
| services.mongodb | string | `"catalog-mongo"` | hostname and port of mongo |
| services.offering | string | `"catalog-product-offering:8080"` | dependent microservice   |
| services.offeringprice | string | `"catalog-product-offering-price:8080"` | dependent microservice   |
| services.productcatalog | string | `"catalog-product-catalog:8080"` | dependent microservice |
| services.specification | string | `"catalog-product-specification:8080"` | dependent microservice   |
| services.userroleretrieve | string | `"auth-userrole:8080"` | dependent microservice   |
| tolerations | list | `[]` | Pod tolerations |

Specify each value using the `--set key=value[,key=value]` argument to `helm install`

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```console
$ helm install my-release -f values.yaml disco-stable-repo/catalog-product-catalog
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release disco-stable-repo/catalog-product-catalog \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
