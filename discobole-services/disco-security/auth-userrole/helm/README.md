# auth-userrole

A Helm chart for user role and permission service.

**Homepage:** <https://gitlab.ow2.org/discobole>

## Source Code

* <https://gitlab.ow2.org/discobole/disco-oda-components/disco-security/auth-userrole/>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/752345/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release disco-stable-repo/auth-userrole
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
$ helm install my-release disco-stable-repo/auth-userrole
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
| autoscaling.enabled | bool | `false` | Enable Horizontal Pod Autoscaler |
| autoscaling.maxReplicas | string | `""` | Maximum number of pod replicas |
| autoscaling.minReplicas | string | `""` | Minimum number of pod replicas |
| autoscaling.targetCPUUtilizationPercentage | string | `""` | Target CPU utilization percentages for pod scaling |
| autoscaling.targetMemoryUtilizationPercentage | string | `""` | Target Memory utilization percentages for pod scaling |
| contextPath | string | `""` | Context path for the application |
| dailyshutdown | string | `"enabled"` | Enable or disable application daily shutdown |
| database.enabled | bool | `true` | enable or disable authentication |
| database.rootPassword | string | `""` | database root user credentials |
| database.rootUser | string | `"mongo_user"` |  |
| fluentd.enabled | bool | `false` | enable fluentd sidecar container |
| fluentd.host | string | `""` | hostname of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.tag | string | `""` | tag value being set on log events and used by fluentd to route logs |
| fullnameOverride | string | `""` | Override fully qualified app name |
| gateway | object | `{"id":""}` | DISCOBOLE gateway configuration |
| gateway.id | string | `""` | Keycloak client ID |
| image | object | `{"pullPolicy":"Always","repository":"${docker_repository}","tag":"${docker_tag}"}` | Application container image configuration |
| imagePullSecrets | list | `[]` | Specify pulling image secrets if your image repository requires it |
| ingress.annotations | object | `{}` | Ingress annotations |
| ingress.enabled | bool | `false` | Enable ingress controller |
| ingress.hosts | list | `[{"host":"","paths":["/"]}]` | host names for the ingress controller |
| keyStoreP12 | string | `""` | Keystore in P12 format, base64 encoded |
| keycloak | object | `{"clientSecret":"","enabled":false,"issuerURI":""}` | Keycloak configuration |
| keycloak.clientSecret | string | `""` | keycloak client id |
| keycloak.enabled | bool | `false` | enable or disable keycloak integration |
| keycloak.issuerURI | string | `""` | keycloak url |
| mongo.auth.enabled | bool | `false` | enable or disable mongoDB authentication |
| mongo.auth.rootPassword | string | `""` | mongoDB root password |
| mongo.auth.rootUser | string | `"mongo_user"` | mongoDB root user |
| mongo.enabled | bool | `true` | enable or disable mongoDB deployment |
| mongo.extraEnvVarsSecret | string | `"auth-userrole-mongo-secret"` | use extra env vars from secret |
| mongo.fullnameOverride | string | `"auth-userrole-mongo"` | mongoDB fullname override |
| mongo.hidden | object | `{"persistence":{"size":"4Gi"}}` | hidden mongoDB persistence configuration |
| mongo.initdbScripts | object | `{"init.js":"// app user\ndb.getSiblingDB(\"userRoleManagement\")\nuse userRoleManagement;\nvar username='mongo_user';\nvar password=process.env[\"MONGO_DB_PWD\"];\nvar user=db.getUser(username);\nif (user == null) {\n  print('create user: ' + username);\n  db.createUser({user: 'mongo_user',pwd: password,roles: [ { role: 'readWrite', db: 'userRoleManagement' } ]});\n} else {\n  print('update user: ' + username);\n  db.updateUser(username, {pwd: password});\n}\nprint('---- done for ' + username);\nquit(0);\n"}` | database initialization scripts |
| mongo.persistence | object | `{"enabled":true,"size":"4Gi"}` | mongoDB persistence configuration |
| mongo.service | object | `{"ports":{"mongodb":27017}}` | mongoDB  service configuration |
| monitoring.application | string | `""` | label to specify application monitoring |
| monitoring.domain | string | `""` |  |
| monitoring.environment | string | `""` | label for environment being deployed |
| monitoring.productName | string | `""` | label to specify product name (chart name by default) |
| monitoring.role | string | `""` | label to specify role |
| monitoring.topology | string | `""` | label to specify topology |
| nameOverride | string | `""` | Override default fully qualified app name and chart name |
| nodeSelector | object | `{}` | Node selectorfor pod assignment |
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
| podSecurityContext | object | `{}` | Security context for the containers |
| promtail | object | `{"enabled":false}` | Enable Promtail sidecar for Loki log aggregation |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"200m","memory":"1024Mi"}` | resource limits       |
| resources.requests | object | `{"cpu":"100m","memory":"128Mi"}` | resource requests |
| securityContext | object | `{}` | Security context |
| service.annotations | object | `{}` | enable monitoring and supervision of service |
| service.port | int | `8080` |  |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.annotations | object | `{}` | annotations to add to the service account |
| serviceAccount.create | bool | `false` | specifies whether a service account should be created |
| serviceAccount.name | string | `""` | the name of the service account to use. If not set and create is true, a name is generated using the fullname template |
| services.authuserroleroute | string | `"https://auth-userrole"` | auth-userrole service route |
| services.catalogui | string | `"catalog-ui:80"` |  |
| services.category | string | `"catalog-product-category:8080"` | dependent microservice |
| services.dbname | string | `"userRoleManagement"` | Mongo database name |
| services.gateway | string | `"disco-gateway:8080"` | dependent microservice |
| services.kafka | string | `"kafka:9092"` | Kafka connection options |
| services.keycloak | string | `"keycloak:80"` |  |
| services.keycloakrealm | string | `"SpringBootKeycloak"` | Keycloak realm |
| services.keycloakroute | string | `"keycloak"` | keycloak service route |
| services.keycloakserviceroute | string | `"https://keycloak"` |  |
| services.lifecycle | string | `"catalog-product-lifecycle-management:8080"` | dependent microservice |
| services.mockservice | string | `"mock-server:80"` | dependent microservice |
| services.mongodb | string | `"auth-userrole-mongo"` | hostnames and ports of dependent services |
| services.offering | string | `"catalog-product-offering:8080"` | dependent microservice |
| services.offeringprice | string | `"catalog-product-offering-price:8080"` | dependent microservice |
| services.productcatalog | string | `"catalog-product-catalog:8080"` | dependent microservice |
| services.specification | string | `"catalog-product-specification:8080"` | dependent microservice |
| tolerations | list | `[]` | Node tolerations for pod assignment |

Specify each value using the `--set key=value[,key=value]` argument to `helm install`

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```console
$ helm install my-release -f values.yaml disco-stable-repo/auth-userrole
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release disco-stable-repo/auth-userrole \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
