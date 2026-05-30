---
title: Deploying DISCOBOLE Order Management
summary:
authors:
  - Hassene OULED SGHAIER
---

# Deploy your own DISCOBOLE Order Management Instance

!!! abstract "Decide where and how to build and deploy"
    - Manually (for local development)
      1. Build java services on your workstation
      1. Deploy databases (mongo)
      1. Run Kafka
      1. Run java services on a JVM on your workstation
    - Deploy manually on a local or remote kubernetes cluster using Helm
    - Automatically via CI/CD on a remote kubernetes cluster

## Manually

To deploy Order Management manually, you will have to deploy each component of Order Management:

- `order capture`
- `order inventory`
- `order follow-up`

??? info "Order Management components"
    ![component view](../img/software-view.png){.img-zoomable}

??? note "Build Java Services on your Workstation"
    a. Follow instructions of each component `README.md`.  
       - `order capture`[readme.md](../../architecture/detailed-architecture/order-capture/)  
       - `order follow-up`[readme.md](../../architecture/detailed-architecture/order-followup/)  
       - `order inventory`[readme.md](../../architecture/detailed-architecture/order-inventory/)  

??? "Deploy Databases (mongo)"
    b. The microservices use mongo as database.

??? "Run Kafka"
    c. The microservices use kafka as message broker.

??? "Run on a Local JVM or Deploy on a Kubernetes cluster"
    d. Follow instructions of each component `README.md`.

## Deploy Manually Using Helm

Please refer to the [DISCOBOLE Deployment Guide over GKE](https://discobole.ow2.io/doc/deployments/release-r9/deployment/) for installation and configuration details of this component.

## Automatically via CI/CD

To build and deploy the components automatically via CI/CD, you will need to fork the respective GitLab repository and configure the CI pipelines accordingly.
Details for configuration are being given in the `README.md` file for each Helm chart.

Alternatively, you may fork the respective GitLab repository and adjust the CI variables and custom values to match your target deployment. This is a way of deploying in a CI/CD manner.
