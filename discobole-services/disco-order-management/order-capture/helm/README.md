# order-capture

A Helm chart for order capture service.

## Source Code

* <https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/orchestration-delivery-management>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/5972/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release discobole-stable-repo/order-capture
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
$ helm install my-release discobole-stable-repo/order-capture
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
| api.contextPath | string | `"/api/v2/"` |  |
| autoscaling.enabled | bool | `false` | Enable Horizontal Pod Autoscaler |
| autoscaling.maxReplicas | int | `5` | Maximum number of pod replicas |
| autoscaling.minReplicas | int | `2` | Minimum number of pod replicas |
| autoscaling.targetCPUUtilizationPercentage | int | `70` | Target CPU utilization percentages for pod scaling |
| autoscaling.targetMemoryUtilizationPercentage | string | `""` | Target Memory utilization percentages for pod scaling |
| certManager.create | bool | `false` | specifies whether a certificate should be created with cert-manager |
| contextPath | string | `""` |  |
| fluentd.enabled | bool | `false` |  |
| fluentd.host | string | `"localhost"` |  |
| fluentd.port | string | `""` |  |
| fluentd.sidecar.enabled | bool | `false` |  |
| fluentd.sidecar.loki.extra_labels | string | `"\"env\":\"sandbox\", \"app\": \"disco\""` |  |
| fluentd.sidecar.loki.url | string | `""` |  |
| fluentd.tag | string | `""` |  |
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
| keycloak.enabled | bool | `false` | enable or disable Keycloak deployment |
| keycloak.url | string | `"https://keycloak"` | Keycloak URL (without protocol and port) |
| mongo.architecture | string | `"standalone"` | architecture and configuration for MongoDB deployment |
| mongo.auth.enabled | bool | `false` | enable or disable MongoDB root user |
| mongo.auth.rootPassword | string | `""` | MongoDB root user password |
| mongo.auth.rootUser | string | `""` | MongoDB root user name |
| mongo.commonLabels | object | `{"domain":"dbms","productname":"mongodb"}` | labels to specify on MongoDB pods and services (useful for monitoring) |
| mongo.directoryPerDB | bool | `true` | directory per database in MongoDB deployment |
| mongo.enabled | bool | `false` | enable MongoDB deployment |
| mongo.extraEnvVarsSecret | string | `""` | use extra env vars from secret |
| mongo.fullnameOverride | string | `""` | mongoDB fullname override |
| mongo.image.repository | string | `"bitnamilegacy/mongodb"` | mongoDB image repository |
| mongo.initdbScripts."init.js" | string | `"// app user\ndb.getSiblingDB(\"order_capture\")\nuse order_capture;\ndb.createCollection(\"dummy_table\")\ndb.dummy_table.insertOne({\"desc\" : \"create dummy row to create database\"})\nvar username='mongo_user';\nvar password=process.env[\"MONGO_DB_PWD\"];\nvar user=db.getUser(username);\nif (user == null) {\n  print('create user: ' + username);\n  db.createUser({user: 'mongo_user',pwd: password,roles: [ { role: 'readWrite', db: 'order_capture' } ]});\n} else {\n  print('update user: ' + username);\n  db.updateUser(username, {pwd: password});\n}\nprint('---- done for ' + username);\nquit(0);\n"` | database initialization scripts |
| mongo.metrics.enabled | bool | `true` | enable MongoDB exporter deployment |
| mongo.metrics.image.repository | string | `"bitnamilegacy/mongodb-exporter"` | mongoDB exporter image repository |
| mongo.podAnnotations | object | `{"prometheus.io/path":"/metrics","prometheus.io/port":"9216","prometheus.io/scrape":"true"}` | annotations to specify on MongoDB pods (useful for monitoring) |
| mongo.resources | object | `{"limits":{"memory":"1500Mi"}}` | default resource requests for MongoDB pods |
| mongo.service | object | `{"nameOverride":"","ports":{"mongodb":27017}}` | mongoDB  service configuration |
| mongo.useStatefulSet | bool | `true` | use StatefulSet for MongoDB deployment |
| monitoring.app | string | `""` |  |
| monitoring.application | string | `""` |  |
| monitoring.environment | string | `""` |  |
| monitoring.productName | string | `""` |  |
| nameOverride | string | `""` | Override default fully qualified app name and chart name |
| nodeSelector | object | `{}` | Node selectorfor pod assignment |
| services.adminUiUri | string | `"https://admin-ui"` | dependent microservice |
| services.authUserRole | string | `"auth-userrole:8080"` | dependent microservice |
| services.hostModeUiUri | string | `"https://hostmode-ui"` | dependent microservice |
| services.kafka | string | `"kafka:9092"` | Kafka connection options |
| services.mockServer | string | `"mock-server:8080"` | dependent microservice |
| oc.service.mxHeap | string | `""` | max heap size of the service |
| services.orderInventory | string | `"order-inventory:8080"` | dependent microservice |
| services.orderInventoryUiUri | string | `"https://order-inventory-ui"` | dependent microservice |
| services.productCatalog | string | `"catalog-product-catalog:8080"` | dependent microservice |
| services.productConfigurator | string | `"product-configurator:8080"` | dependent microservice |
| services.productInventory | string | `"product-inventory:8080"` | dependent microservice |
| services.selfcareUiUri | string | `"https://selfcare-ui"` | dependent microservice |
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
| podAnnotations | object | `{}` | Enable monitoring and supervision of pods |
| podSecurityContext | object | `{}` | Security contexts |
| registryCredentials.create | bool | `false` | specifies whether a secret should be created for pulling images from a private registry |
| registryCredentials.token | object | `{"password":"","userName":""}` | the access token of the private registry |
| registryCredentials.url | string | `""` | the URL of the private registry.  |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"600m","memory":"1Gi"}` | Default resource limits     |
| resources.requests | object | `{"cpu":"200m","memory":"500Mi"}` | Default resource requests  |
| scheduledshutdown | string | `"disabled"` | Enable or disable application daily shutdown |
| securityContext | object | `{}` | Security context for the containers |
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
$ helm install my-release -f values.yaml discobole-stable-repo/order-capture
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release discobole-stable-repo/order-capture \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
