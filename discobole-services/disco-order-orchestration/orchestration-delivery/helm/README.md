# orchestration-delivery

A Helm chart for orchestration delivery service.

**Homepage:** <https://discobole.ow2.io/doc>

## Source Code

* <https://gitlab.ow2.org/discobole/disco-order-orchestration/orchestration-delivery>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/752345/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release disco-stable-repo/orchestration-delivery
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
$ helm install my-release disco-stable-repo/orchestration-delivery
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
| affinity | object | `{}` | Node affinity for pod assignment |
| api.contextPath | string | `"/api/v2/"` | context path for the API |
| autoscaling.enabled | bool | `false` | Enable Horizontal Pod Autoscaler |
| autoscaling.maxReplicas | string | `"5"` | Maximum number of pod replicas |
| autoscaling.minReplicas | string | `"2"` | Minimum number of pod replicas |
| autoscaling.targetCPUUtilizationPercentage | string | `"70"` | Target CPU utilization percentages for pod scaling |
| autoscaling.targetMemoryUtilizationPercentage | string | `""` | Target Memory utilization percentages for pod scaling |
| certManager.create | bool | `false` | specify whether a certificate should be created for this service |
| contextPath | string | `""` | context path for the application. Defaults to root context. |
| event.deleteEventsThresholdDuration | string | `""` | threshhold duration to delete events |
| fluentd.enabled | bool | `false` | enable fluentd sidecar container |
| fluentd.host | string | `""` | hostname of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.tag | string | `""` | tag value being set on log events and used by fluentd to route logs |
| fullnameOverride | string | `""` | Override fully qualified app name |
| image | object | `{"pullPolicy":"Always","repository":"${docker_repository}","tag":"${docker_tag}"}` | Application container image configuration |
| imagePullSecrets | list | `[]` | Specify pulling image secrets if your image repository requires it |
| ingress.annotations | object | `{}` | Ingress annotations |
| ingress.enabled | bool | `false` | Enable ingress controller |
| ingress.hosts | list | `[{"host":"","paths":["/"]}]` | host names for the ingress controller |
| jdkJavaOptions | string | `""` | JDK options for application container |
| keyStoreP12 | string | `""` | Keystore in P12 format, base64 encoded |
| keycloak.clientId | string | `""` | client ID |
| keycloak.clientSecret | string | `""` | client secret |
| keycloak.enabled | bool | `false` | enable Keycloak integration |
| keycloak.issuerURI | string | `"https://keycloak/realms/SpringBootKeycloak"` | issuer URI to validate access tokens against |
| mongo.arbiter.enabled | bool | `false` | enable or disable MongoDB arbiter deployment. Default is false. |
| mongo.architecture | string | `"replicaset"` | architecture for MongoDB deployment |
| mongo.auth.enabled | bool | `false` | enable MongoDB authentication |
| mongo.auth.rootPassword | string | `""` | MongoDB root user password |
| mongo.auth.rootUser | string | `""` | MongoDB root user name |
| mongo.commonLabels | object | `{"domain":"dbms","productname":"mongodb"}` | labels to specify application monitoring  |
| mongo.directoryPerDB | bool | `true` | extra environment variables to be set on MongoDB pods, in case you want to customize your MongoDB deployment. |
| mongo.enabled | bool | `false` | enable MongoDB deployment |
| mongo.fullnameOverride | string | `""` | fullname override for MongoDB resources |
| mongo.global.storageClass | string | `""` | MongoDB data persistence configuration |
| mongo.image.repository | string | `"bitnamilegacy/mongodb"` | MongoDB image repository |
| mongo.initdbScripts."init.js" | string | `"// app user\ndb.getSiblingDB(\"orchestration_delivery\")\nuse orchestration_delivery;\ndb.createCollection(\"dummy_table\")\ndb.dummy_table.insertOne({\"desc\" : \"creare dummy row to create database\"})\nvar username='mongo_user';\nvar password=process.env[\"MONGO_DB_PWD\"];\nvar user=db.getUser(username);\nif (user == null) {\n  print('create user: ' + username);\n  db.createUser({user: 'mongo_user',pwd: password,roles: [ { role: 'readWrite', db: 'orchestration_delivery' }, { role: 'readWrite', db: 'catalog' } ]});\n} else {\n  print('update user: ' + username);\n  db.updateUser(username, {pwd: password});\n}\nprint('---- done for ' + username);\nquit(0);\n"` | database initialization scripts |
| mongo.metrics.enabled | bool | `false` | enable MongoDB exporter for monitoring |
| mongo.metrics.image.repository | string | `"bitnamilegacy/mongodb-exporter"` | MongoDB exporter image |
| mongo.podAnnotations | object | `{"prometheus.io/path":"/metrics","prometheus.io/port":"9216","prometheus.io/scrape":"true"}` | annotations to specify monitoring and supervision of MongoDB pods |
| mongo.replicaCount | int | `1` | MongoDB replica count |
| mongo.resources.limits.memory | string | `"1500Mi"` | memory limit for MongoDB container |
| mongo.service.nameOverride | string | `""` | override MongoDB service name |
| mongo.service.ports.mongodb | int | `27017` | MongoDB service port |
| mongo.useStatefulSet | bool | `true` | use StatefulSet for MongoDB deployment. Recommended for production environments |
| monitoring.app | string | `""` | label to specify application monitoring |
| monitoring.application | string | `""` | label to specify application monitoring |
| monitoring.environment | string | `""` | label for environment being deployed |
| monitoring.productName | string | `""` | label to specify product name (chart name by default) |
| nameOverride | string | `""` | Override default fully qualified app name and chart name |
| node.storeNodeStatisticsDuration | string | `""` | duration storage of node statistics |
| nodeSelector | object | `{}` | Node selectorfor pod assignment |
| services.cpib | string | `"product-inventory"` | product inventory service name |
| services.fallout | string | `"orchestration-delivery-fallout"` | orchestration fallout service name |
| services.kafka | string | `"kafka"` | service name of kafka |
| od.service.maxHeap | string | `"500M"` | max heap memory for application container |
| services.mock | string | `"mock-server"` | mock service name |
| services.poi | string | `"order-inventory"` | order inventory service name |
| services.productCatalog | string | `"catalog-product-catalog"` | product catalog service name |
| services.userRoleRetrieval | string | `"auth-userrole"` | user role and permission service name |
| otel.attributes.cluster | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.hostname | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.namespace | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.platform | string | `""` | resource attribute to be set on telemetry data |
| otel.attributes.region | string | `""` | resource attribute to be set on telemetry data |
| otel.enabled | bool | `false` | activate Open Telemetry instrumentation |
| otel.sidecar.enabled | bool | `false` | use collector agent sidecar |
| otel.sidecar.jaeger | object | `{"host":"","port":14250}` | main collector address |
| otel.sidecar.otlp.host | string | `""` |  |
| otel.sidecar.otlp.port | int | `4317` |  |
| plan.deletePlanThresholdDuration | string | `""` | threshhold duration to delete plans |
| plan.storePlanStatisticsDuration | string | `""` |  |
| podAnnotations | object | `{}` | Enable monitoring and supervision of pods |
| podSecurityContext | object | `{}` | Security context for the containers |
| registryCredentials.create | bool | `false` | specifies whether a secret should be created for pulling images from a private registry |
| registryCredentials.token | object | `{"password":"","userName":""}` | the access token of the private registry |
| registryCredentials.url | string | `""` | the URL of the private registry. |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"1","memory":"1Gi"}` | resource limits        |
| resources.requests | object | `{"cpu":"200m","memory":"256Mi"}` | resource requests |
| scheduledshutdown | string | `"disabled"` | Enable or disable application daily shutdown |
| securityContext | object | `{}` | Security context |
| service.annotations | object | `{}` | enable monitoring and supervision of service |
| service.port | int | `8080` |  |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.annotations | object | `{}` | annotations to add to the service account |
| serviceAccount.create | bool | `false` | specifies whether a service account should be created |
| serviceAccount.name | string | `""` | the name of the service account to use. If not set and create is true, a name is generated using the fullname template |
| tolerations | list | `[]` | Node tolerations for pod assignment |

Specify each value using the `--set key=value[,key=value]` argument to `helm install`

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```console
$ helm install my-release -f values.yaml disco-stable-repo/orchestration-delivery
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release disco-stable-repo/orchestration-delivery \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
