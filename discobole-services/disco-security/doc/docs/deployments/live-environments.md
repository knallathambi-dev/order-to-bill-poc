---
title: Live Environments
summary: Live Environments.
authors:
  - Sumit
---

# Live environments

Feel free to __interact__ with User Role & Permission Management , check the dashboards for integration environment staging environment and much more.

???+ abstract "Live Environment Overview"
    Live environments are deployed by CI/CD (Continuous Integration / Continuous Deployment). Here is a description of the deployment pipelines. The deployment pipelines are described in GitLab `.gitlab-ci.yml` files which are stored along the source code. On each change on the git repository - on each `git push` - GitLab is starting a new pipeline that follows 4 essential stages:

    - build stage to fetch the source code and build the binary (`jar` file for java, `zip` file for Angular)
    - test stage to run automatic tests such as _Sonar_, _Security Dashboard_ and _Mimirsbrunn_
    - publish stage to publish libraries and docker images to _Artifactory_.
    - deploy stage to create _Kubernetes_ objects on _OpenShift_ and notify _Mattermost_ channels

## BUILD

### Continuous integration

???+ info "GitLab-CI"
      - `gitlab`: [User Role & Permission management website pipeline](https://gitlab.ow2.org/discobole/disco-oda-components/disco-security/security-authorization-website/-/pipelines).
