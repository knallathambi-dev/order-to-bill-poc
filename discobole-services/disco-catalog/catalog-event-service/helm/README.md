<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# event-service

A Helm chart for event service

**Homepage:** <https://gitlab.ow2.org/discobole>

## Source Code

* <https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/catalog-event-service.git>

## Usage

Declare the helm repository:

```bash
helm repo add discobole-stable-repo https://gitlab.ow2.org/api/v4/groups/752345/packages/helm/stable
helm repo update
```

Deploy a release on cluster:

```bash
helm install my-release disco-stable-repo/event-service
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
$ helm install my-release disco-stable-repo/event-service
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
| affinity | object | `{}` |  |
| autoscaling | object | `{"enabled":false,"maxReplicas":5,"minReplicas":1,"targetCPUUtilizationPercentage":80}` | Horizontal Pod Autoscaler configuration |
| dailyshutdown | string | `"enabled"` | Enable or disable application daily shutdown |
| fullnameOverride | string | `""` | Override fully qualified app name |
| image | object | `{"pullPolicy":"Always","repository":"${docker_repository}","tag":"${docker_tag}"}` | Docker image parameters |
| imagePullSecrets | list | `[]` | Specify pulling image secrets if your image repository requires it |
| ingress | object | `{"annotations":{},"enabled":false,"hosts":[{"host":"","paths":["/"]}]}` | Ingress configuration |
| monitoring | object | `{"application":"","domain":"","environment":"","productName":"","role":"","topology":""}` | Monitoring values |
| nameOverride | string | `""` | Override default fully qualified app name and chart name |
| nodeSelector | object | `{}` | Node selector for pod assignment |
| podAnnotations | object | `{}` | Enable monitoring and supervision of pods |
| podSecurityContext | object | `{}` | Security contexts |
| replicaCount | int | `1` | Application replica count |
| resources | object | `{"limits":{"cpu":"200m","memory":"512Mi"},"requests":{"cpu":"100m","memory":"128Mi"}}` | Application size and resource allocation |
| securityContext | object | `{}` | Container security context |
| service | object | `{"port":8080,"type":"ClusterIP"}` | Service configuration |
| serviceAccount | object | `{"annotations":{},"create":false,"name":""}` | Service account configuration if needed |
| services | object | `{"kafka":"kafka:9092","keycloak":"keycloak:80","keycloakrealm":"SpringBootKeycloak","keycloakroute":"keycloak","userroleretrieve":"auth-userrole:8080"}` | Service names used to connect to other services |
| tolerations | list | `[]` | Tolerations for node taints |

Specify each value using the `--set key=value[,key=value]` argument to `helm install`

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```console
$ helm install my-release -f values.yaml disco-stable-repo/event-service
```

## Docker image access

These values help you to configure access to the Docker image for this microservice.

- if you need to pull the Docker image from a private or protected registry, you have to create and specify an `imagePullSecret`
- if you wish to let the chart create the image pull secret for you, configure the `serviceAccount.imagePullSecret` section

In case you delegate the creation of an image pull secret, use your own credentials on the private or protected registry, as follows.

```bash
$ helm install my-release disco-stable-repo/event-service \
--set serviceAccount.imagePullSecret.create=true \
--set serviceAccount.imagePullSecret.registry=<your private or protected docker registry> \
--set serviceAccount.imagePullSecret.username=<you username> \
--set serviceAccount.imagePullSecret.password=<your access token>
```
