---
title: Live Environments
summary: Product Inventory live environments.
authors:
  - Mostafa ABDELWAHAB
---

# Live Environments

???+ abstract "Live Environment Overview"
    Live environments are deployed by CI/CD (Continuous Integration / Continuous Deployment). Here is a description of the deployment pipelines. The deployment pipelines are described in GitLab `.gitlab-ci.yml` files which are stored along the source code. On each change on the git repository - on each `git push` - GitLab is starting a new pipeline that follows 4 essential stages:

    - build stage to fetch the source code and build the binary (`jar` file for java, `zip` file for Angular)
    - test stage to run automatic tests such as _Sonar_, _Security Dashboard_, DefectDojo and _Mimirsbrunn_ 
    - publish stage to publish libraries and docker images to _Artifactory_
    - deploy stage to create _Kubernetes_ objects on _OpenShift_ and notify _Mattermost_ channels

    Once GitLab CI/CD hand over to OpenShift, OpenShift can instantiate CPIB microservice and its eventual database. All theses components are monitored and tracked by tools such as _Jaeger_, _Grafana_, _Fluentd_, _Loki_ and _Prometheus_.

## BUILD

### Continuous integration

???+ info "GitLab CI Pipelines"
    - [Product Inventory pipelines](https://gitlab.ow2.org/discobole/disco-oda-components/disco-product-inventory/product-inventory/-/pipelines).
