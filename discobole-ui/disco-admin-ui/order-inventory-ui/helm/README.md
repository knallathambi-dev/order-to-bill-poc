<!--
SPDX-FileCopyrightText: 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# order-inventory-ui

A Helm chart for order inventory ui portal.

## Source Code

* <https://gitlab.ow2.org/discobole/disco-oda-components/disco-ui-portals/disco-microfrontend/order-inventory-ui>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/5972/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release discobole-stable-repo/order-inventory-ui
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
$ helm install my-release discobole-stable-repo/order-inventory-ui
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
| certManager.create | bool | `false` | specify whether a certificate should be created for this service |
| image | object | `{"pullPolicy":"Always","repository":"${docker_repository}","tag":"${docker_tag}"}` | Application container image configuration |
| imagePullSecrets | list | `[]` | Specify pulling image secrets if your image repository requires it |
| ingress.annotations | object | `{}` | Ingress annotations |
| ingress.enabled | bool | `false` | Enable ingress controller |
| ingress.hosts | list | `[{"host":"","paths":["/"]}]` | host names for the ingress controller |
| monitoring.app | string | `""` | label to specify application monitoring |
| monitoring.application | string | `""` | label to specify application monitoring |
| monitoring.domain | string | `"appli"` | label to specify domain |
| monitoring.environment | string | `""` | label for environment being deployed |
| monitoring.productName | string | `""` | label to specify product name (chart name by default) |
| monitoring.role | string | `""` | label to specify role |
| monitoring.topology | string | `""` | label to specify topology |
| nodeSelector | object | `{}` | Node selectorfor pod assignment |
| orderinventoryui.service.gatewayUrl | string | `""` | gateway URL |
| orderinventoryui.service.standaloneMode | bool | `false` | enable or disable standalone mode |
| podAnnotations | object | `{}` | Enable monitoring and supervision of pods |
| podSecurityContext | object | `{}` | Security context for the containers |
| registryCredentials.create | bool | `false` | specifies whether a secret should be created for pulling images from a private registry |
| registryCredentials.token | object | `{"password":"","userName":""}` | the access token of the private registry |
| registryCredentials.url | string | `""` | the URL of the private registry. |
| replicaCount | int | `1` | Application replica count |
| resources.limits | object | `{"cpu":"120m","memory":"500Mi"}` | resource limits      |
| resources.requests | object | `{"cpu":"60m","memory":"128Mi"}` | resource requests |
| scheduledshutdown | string | `"disabled"` | Enable or disable application daily shutdown |
| securityContext | object | `{}` | Security context |
| service.annotations | object | `{}` | enable monitoring and supervision of service |
| service.port | int | `80` | Specify the port number for the service |
| service.targetPort | int | `8080` | Specify the target port for the service |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.annotations | object | `{}` | annotations to add to the service account |
| serviceAccount.create | bool | `false` | specifies whether a service account should be created |
| serviceAccount.name | string | `""` | the name of the service account to use. If not set and create is true, a name is generated using the fullname template |
| tolerations | list | `[]` | Node tolerations for pod assignment |

Specify each value using the `--set key=value[,key=value]` argument to `helm install`

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```console
$ helm install my-release -f values.yaml discobole-stable-repo/order-inventory-ui
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release discobole-stable-repo/order-inventory-ui \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
