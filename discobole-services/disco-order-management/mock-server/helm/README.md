# mock-server

A Helm chart for [order management]/[order capture mock server] service

**Homepage:** <https://discobole.ow2.io/doc>

## Source Code

* <https://gitlab.ow2.org/discobole/disco-order-management/mock-server>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/5972/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release discobole-stable-repo/mock-server
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
$ helm install my-release discobole-stable-repo/mock-server
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
| fluentd.enabled | bool | `false` | enable fluentd sidecar container |
| fluentd.host | string | `""` | hostname of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.port | string | `""` | port of the fluentd instance this application should send logs to. Defaults to localhost |
| fluentd.sidecar.enabled | bool | `false` | use to enable fluentd sidecar container running logging agent |
| fluentd.sidecar.loki.extra_labels | string | `"\"env\":\"sandbox\", \"app\": \"disco\""` | extra labels that the fluentd sidecar will set on log events for loki |
| fluentd.sidecar.loki.url | string | `""` | URL of the Loki instance that the sidecar will output logs to |
| fluentd.tag | string | `""` | tag value being set on log events and used by fluentd to route logs |
| fullnameOverride | string | `""` | Override fully qualified app name |
| image.pullPolicy | string | `"Always"` | image pull policy |
| image.repository | string | `"${docker_repository}"` |  |
| image.tag | string | `"${docker_tag}"` |  |
| ingress.annotations | object | `{}` | Ingress annotations |
| ingress.enabled | bool | `false` | Enable ingress controller |
| ingress.hosts | list | `[{"host":"","paths":["/"]}]` | Hosts configuration     |
| ingress.hostsFiber[0].host | string | `""` |  |
| ingress.hostsFiber[0].paths[0] | string | `"/"` |  |
| ingress.hostsMobile[0].host | string | `""` |  |
| ingress.hostsMobile[0].paths[0] | string | `"/"` |  |
| ingress.hostsPartner[0].host | string | `""` |  |
| ingress.hostsPartner[0].paths[0] | string | `"/"` |  |
| istio.enabled | bool | `false` | enable istio sidecar injection |
| jdkJavaOptions | string | `""` | Extra Java options to be set on the java command line, for instance to enable specific GC or set system properties. Default is blank. |
| keyStoreP12 | string | `""` | Keystore in P12 format, base64 encoded |
| mockServer.service.mxHeap | string | `""` | max heap size of the mock server service |
| monitoring.app | string | `""` | label to application name |
| monitoring.application | string | `""` | label for exporter autodetection |
| monitoring.environment | string | `""` | label for environment being deployed |
| monitoring.productName | string | `""` | label to specify product name (chart name by default) |
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
| podAnnotations | object | `{}` |  |
| podSecurityContext | object | `{}` |  |
| registryCredentials.create | bool | `false` | specifies whether a registry secret should be created |
| registryCredentials.token | object | `{"password":"","userName":""}` | the name of the secret to create if registryCredentials.create is true. |
| registryCredentials.url | string | `""` | docker registry server, for instance "https://index.docker.io/v1/" for docker hub. |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"500m","memory":"1024Mi"}` | Default resource limits     |
| resources.requests | object | `{"cpu":"200m","memory":"500Mi"}` | Default resource requests  |
| scheduledshutdown | bool | `true` | Enable or disable application daily shutdown |
| securityContext | object | `{}` |  |
| service.port | int | `8080` |  |
| service.type | string | `"ClusterIP"` |  |
| tolerations | list | `[]` | Node tolerations for pod assignment |

Specify each value using the `--set key=value[,key=value]` argument to `helm install`

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```console
$ helm install my-release -f values.yaml discobole-stable-repo/mock-server
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release discobole-stable-repo/mock-server \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
