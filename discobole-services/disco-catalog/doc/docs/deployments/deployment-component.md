---
title: Deploying the Product Catalog
summary: Deploying the Product Catalog
author:
  - Sumit
---

# Deploy your Product Catalog instance

!!! abstract "Decide where and how to build and deploy"
    - Manually (for local development)
      1. Build java services on your workstation
      1. Optionally deploy databases (mariadb, mongo), by default the microservices run in-memory databases
      1. Run java services on a JVM on your workstation
      1. Build and run web front-end on your workstation
    - Deploy manually on a local or remote kubernetes cluster using Helm
    - Automatically via CI/CD on a remote kubernetes cluster

## Manually

To deploy Product Catalog manually, you will have to deploy each application of it.

??? note "a. build java services on your workstation"
     - First needs to clone catalog projects from git location: `https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog`
     - Click on clone button
     - After clicking on clone, copied URL from Clone with HTTPS
     - Open Command Prompt (cmd.exe) and locate directory where you want to clone and clone all catalog projects repositories as follows:
       ```bash
       cd {{git repository name}}
       git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-catalog/{{git repo name}}.git
       ```

??? note "b. deploy databases (mariadb, mongo)"
    this step is optional, as by default for development purpose, the microservices use in-memory database instances.

??? note "c. run on a local JVM or deploy on a kubernetes cluster"
     - Import projects in IDE. Here we are using Eclipse IDE.
     - We need to build process flow first as it is used in all catalog projects.
     - Take clone of processFlow: `git clone https://gitlab.ow2.org/discobole/process-flow.git`.
     - So, let's import process flow and then the same steps will be followed for other projects.
     - Click on Files -> import -> Select Existing Maven Project -> Next -> Browse the location where you have cloned process flow library/project -> Finish.
     - After that need to execute maven update. Right click on project-> select Maven-> update Project.
     - After completion of project update , need to build the project. Steps : Right click on Project -> Show in Terminal -> write "mvn clean install".
     - Now need to set up catalog projects in same way ,as we have done setup for process flow project.
     - After importing all the projects successfully, we need to up kafka service, mongoDB.
     - After "mvn clean install" : run services by click right on project main application file and run as java application.
     - Install mongodb and Kafka on local machine and before running the application start those services.

??? note "d. build and run web front-end on your workstation"
    - First needs to clone catalog ui from git location: `git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-ui-portals/product-catalog-ui`
    - After taking clone open terminal in that repository.
    - In terminal run the following command npm install env-cmd and npm start.
    - The React UI will start working on your local machine.

## Deploy manually using Helm

Please refer to the [DISCOBOLE Deployment Guide over GKE](https://discobole.ow2.io/doc/deployments/release-r9/deployment/) for installation and configuration details of this component.

## Automatically via CI/CD

To build and deploy the DISCOBOLE Product Catalog component automatically via CI/CD, you will need to fork the respective GitLab repository and configure the CI pipelines accodingly.

Details for configuration are being given in the `README.md` file for each repository.

Altenatively, you may fork the respective DISCOBOLE Product catalog repository and adjust the CI variables and custom values to match your target deployment. This is a way of deploying in a CI/CD manner.
