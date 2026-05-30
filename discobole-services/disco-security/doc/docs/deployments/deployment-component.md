---
title: Deploying User Role & Permission Management
summary:
authors:
  - DISCOBOLE Team
---

# Deploy your own Permission Management Instance

!!! abstract "Decide where and how to build and deploy"
    - Manually (for local development)
      1. Build java services on your workstation
      1. Deploy databases (mongo)
      1. Run Kafka
      1. Run java services on a JVM on your workstation
    - Deploy manually on a local or remote kubernetes cluster using Helm
    - Automatically via CI/CD on a remote kubernetes cluster

## Manually

To deploy the component manually, you will have to deploy each servie of the component:

- `auth-userrole`

??? note "Build Java Services on your Workstation"
    a. Follow instructions of each component `README.md`.  
       - `auth-userrole` [readme.md](../../architecture/detailed-architecture/auth-userrole/)  

??? "Deploy Databases (mongo)"
    b. The microservices use mongo as database.

??? "Run on a Local JVM or Deploy on a Kubernetes cluster"
    d. Follow instructions of each component `README.md`.

## Deploy Manually Using Helm

Please refer to the [DISCOBOLE Deployment Guide over GKE](https://discobole.ow2.io/doc/deployments/release-r9/deployment/) for installation and configuration details of this component.

## Automatically via CI/CD

To build and deploy the components automatically via CI/CD, you will need to fork the respective GitLab repository and configure the CI pipelines accordingly.

Details for configuration are being given in the `README.md` file for each Helm chart.

Alternatively, you may fork the respective GitLab repository and adjust the CI variables and custom values to match your target deployment. This is a way of deploying in a CI/CD manner.
