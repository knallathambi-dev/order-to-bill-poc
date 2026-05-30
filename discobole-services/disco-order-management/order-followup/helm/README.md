# order-follow-up

A Helm chart for order follow up service.

## Source Code

* <https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-capture>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/5972/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release discobole-stable-repo/order-follow-up
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
$ helm install my-release discobole-stable-repo/order-follow-up
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
| api.contextPath | string | `"/api/v2/"` | Context path for the API endpoint |
| autoscaling.enabled | bool | `false` | Enable Horizontal Pod Autoscaler |
| autoscaling.maxReplicas | int | `5` | Maximum number of pod replicas |
| autoscaling.minReplicas | int | `2` | Minimum number of pod replicas |
| autoscaling.targetCPUUtilizationPercentage | int | `70` | Target CPU utilization percentages for pod scaling |
| autoscaling.targetMemoryUtilizationPercentage | string | `""` | Target Memory utilization percentages for pod scaling |
| certManager.create | bool | `false` | specify whether a certificate should be created for this service |
| contextPath | string | `""` | Set the context path in the API endpoint, for instance /order-follow-up. Default is blank. |
| fluentd.enabled | bool | `false` | enable fluentd sidecar container |
| fluentd.host | string | `""` | hostname of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.port | string | `""` | port of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.sidecar.enabled | bool | `false` | use to enable fluentd sidecar container running logging agent |
| fluentd.sidecar.loki.extra_labels | string | `"\"env\":\"sandbox\", \"app\": \"disco\""` | extra labels that the fluentd sidecar will set on log events for loki |
| fluentd.sidecar.loki.url | string | `""` | URL of the Loki instance that the sidecar will output logs to |
| fluentd.tag | string | `""` | tag value being set on log events and used by fluentd to route logs |
| fullnameOverride | string | `""` | Override fully qualified app name |
| image.pullPolicy | string | `"Always"` | image pull policy |
| image.repository | string | `"${docker_repository}"` | docker image repository |
| image.tag | string | `"${docker_tag}"` | overrides the image tag whose default is the chart appVersion. |
| imagePullSecrets | list | `[]` | Specify pulling image secrets if your image repository requires it |
| ingress.annotations | object | `{}` | Ingress annotations |
| ingress.enabled | bool | `false` | Enable ingress controller |
| ingress.hosts[0].host | string | `""` |  |
| ingress.hosts[0].paths[0] | string | `"/"` |  |
| jdkJavaOptions | string | `""` | jdk options to be set on the application container |
| keyStoreP12 | string | `""` | Keystore in P12 format, base64 encoded |
| keycloak.clientId | string | `""` | Keycloak client ID |
| keycloak.clientSecret | string | `""` | Keycloak client secret |
| keycloak.enabled | bool | `false` | enable or disable Keycloak integration |
| keycloak.url | string | `"https://keycloak"` | Keycloak URL |
| mongo.arbiter.enabled | bool | `false` | enable or disable MongoDB arbiter deployment. Default is false. |
| mongo.architecture | string | `"standalone"` | Use standalone architecture for MongoDB deployment, which is recommended for development and testing environments, or replica set for production environments. Default is standalone. |
| mongo.auth.enabled | bool | `false` | enable or disable MongoDB root user. Default is false. |
| mongo.auth.rootPassword | string | `""` | MongoDB root user password. Default is blank. |
| mongo.auth.rootUser | string | `""` | MongoDB root user name. Default is blank. |
| mongo.commonLabels | object | `{"domain":"dbms","productname":"mongodb"}` | labels to be set on MongoDB pods and services (useful for monitoring) |
| mongo.directoryPerDB | bool | `true` | Use directory per database in MongoDB deployment. Default is true. |
| mongo.enabled | bool | `true` | enable or disable MongoDB deployment |
| mongo.extraEnvVarsSecret | string | `""` | Use extra env vars from secret. Default is false. |
| mongo.fullnameOverride | string | `""` | mongoDB fullname override |
| mongo.global.storageClass | string | `""` | MongoDB data persistence configuration |
| mongo.image.repository | string | `"bitnamilegacy/mongodb"` | docker image repository for MongoDB |
| mongo.initdbScripts."init.js" | string | `"// app user\ndb.getSiblingDB(\"order_follow_up\")\nuse order_follow_up;\ndb.createCollection(\"dummy_table\")\ndb.dummy_table.insertOne({\"desc\" : \"create dummy row to create database\"})\nvar username='mongo_user';\nvar password=process.env[\"MONGO_DB_PWD\"];\nvar user=db.getUser(username);\nif (user == null) {\n  print('create user: ' + username);\n  db.createUser({user: 'mongo_user',pwd: password,roles: [ { role: 'readWrite', db: 'order_follow_up' } ]});\n} else {\n  print('update user: ' + username);\n  db.updateUser(username, {pwd: password});\n}\nprint('---- done for ' + username);\nquit(0);\n"` | database initialization scripts |
| mongo.metrics.enabled | bool | `false` | enable or disable MongoDB exporter deployment |
| mongo.metrics.image.repository | string | `"bitnamilegacy/mongodb-exporter"` | docker image repository for MongoDB exporter |
| mongo.podAnnotations | object | `{"prometheus.io/path":"/metrics","prometheus.io/port":"9216","prometheus.io/scrape":"true"}` | annotations to be set on MongoDB pods (useful for monitoring) |
| mongo.resources.limits.memory | string | `"1500Mi"` | Memory limit for MongoDB container |
| mongo.service.nameOverride | string | `""` | override MongoDB service |
| mongo.service.ports.mongodb | int | `27017` | MongoDB service port |
| mongo.useStatefulSet | bool | `true` | Use StatefulSet for MongoDB deployment, which is recommended for production environments, or Deployment for simpler setups. Default is StatefulSet. |
| monitoring.app | string | `""` | label to specify application name |
| monitoring.application | string | `""` | label to specify application name |
| monitoring.environment | string | `""` | label for environment being deployed |
| monitoring.productName | string | `""` | label to specify product name (chart name by default) |
| nameOverride | string | `""` | Override default fully qualified app name and chart name |
| nodeSelector | object | `{}` | Node selectorfor pod assignment |
| services.kafka | string | `"kafka"` | service name of kafka service |
| of.service.mxHeap | string | `""` | max heap memory to be set on the application container |
| services.productInventory | string | `"product-inventory"` | service name of product inventory service |
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
| podAnnotations | object | `{}` |  |
| podSecurityContext | object | `{}` |  |
| registryCredentials.create | bool | `true` | specifies whether a secret should be created for pulling images from a private registry |
| registryCredentials.token | object | `{"password":"","userName":""}` | the access token of the private registry |
| registryCredentials.url | string | `""` | the URL of the private registry. |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"1","memory":"1000Mi"}` | Default resource limits |
| resources.requests | object | `{"cpu":"200m","memory":"256Mi"}` | Default resource requests  |
| scheduledshutdown | bool | `false` | Enable or disable application daily shutdown |
| securityContext | object | `{}` |  |
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
$ helm install my-release -f values.yaml discobole-stable-repo/order-follow-up
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release discobole-stable-repo/order-follow-up \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
