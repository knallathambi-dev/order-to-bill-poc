<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Order Management Automated Tests

This guide walks you through the process of setting up automated tests using Robot Framework in the 
[DISCOBOLE Order Management project](https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-management/order-test).

In this Git project, the CI/CD pipeline is configured to support the following features:

- Execute automated tests for various accessories and packages
- Perform a GitLeaks scan to detect potential secrets in the code
- Generate test reports and artifacts for each test stage

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

### How to use it?

1. Fork or clone this project and copy the relevant files into your own project.
2. Review the CI/CD pipeline configuration in the `.gitlab-ci.yml` file.
3. Start developing your tests using Robot Framework!

## Getting Started

To play with the project, get a local copy of the project up and running by following these simple steps.

### Prerequisites

- [git](https://git-scm.com/)
- [Python](https://www.python.org/downloads/) (with Robot Framework installed) (Recommended 3.9)
- [Docker](https://www.docker.com/get-started)
- [Robot Framework](https://robotframework.org/?tab=1#getting-started)

1. Install [git](https://git-scm.com/):

    ```bash
    sudo apt update && sudo apt upgrade -y
    sudo apt install gitlab-ee
    ```

2. Install [Python](https://www.python.org/downloads/):

    ```bash
    sudo apt update
    sudo apt install python
    sudo apt install python3.9-pip
    ```

    Nota: the version of Python recommended is `v3.9`.

3. Install [Robot Framework](https://robotframework.org/?tab=1#getting-started):

    ```bash
    pip install robotframework
    ```
    Nota: the version of Robot Framework recommended is `v6.1.1`.

4. Install additionnal libraries

    ```bash
    pip install --upgrade robotframework-selenium2library
	pip install rpaframework
    pip install robotframework-excellib
    ```
    

5. Clone the project

```bash
git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-order-management/order-test.git
```

## Running Tests Locally
To run the tests suites locally u have to follow some steps.

### Add env variables locally

First you have to add the environment variable locally on the file **Manage_Token.robot** because the git lab use his own variables:

```bash
${EndPoint_Om_Oc}                              https://om-order-capture-integration.apps.fr01.paas.tech.orange
${EndPoint_POI}                                https://poi-integration.apps.fr01.paas.tech.orange
${EndPoint_PI}                                 https://cpib-integration.apps.fr01.paas.tech.orange
${EndPoint_Product_Configurator}               https://product-configurator-integration-disco.apps.fr01.paas.tech.orange
${end-point-catalog}                           https://catalog-product-catalog-integration-disco.apps.fr01.paas.tech.orange/productCatalogManagement/v1/productSpecification/
${Api_POST_PF}                                 /processManagement/v1/processFlow
${Api_Setting}                                 /v1/setting
${KC_URL_Value}                                 https://keycloak-integration-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak
${KC_Username_Value}                            om-user@test.com
${KC_Password_Value}                            om_password
${EndPoint_Catalog}                             https://catalog-product-catalog-integration-disco.apps.fr01.paas.tech.orange
${EndPoint_Setting}                             https://admin-ui-integration-disco.apps.fr01.paas.tech.orange

${KEYCLOAK_URL}                                 %{KEYCLOAK_URL=${KC_URL_Value}}
${USERNAME}                                     %{OM_USER=${KC_Username_Value}}
${PASSWORD}                                     %{OM_PASSWORD=${KC_Password_Value}}
${CLIENT_ID}                                    %{AUTH_CLIENT_ID=admin-ui}
```
### Run tests
To run the tests locally, use the Robot Framework command:

```bash
robot <test_suite>.robot
```

Replace `<test_suite>` with the name of your test suite file.


Example:

``` bash
robot BDD_Nominal_case_Max_Plus/Scenarios/Acquisition_prepaid_mobile_offer_Max_Plus.robot
```

## Running Tests in CI/CD

### CI/CD Pipeline Configuration

The CI/CD pipeline is structured into several stages, each dedicated to specific test scenarios or tasks. The stages are dynamically defined in the pipeline configuration to allow easy expansion for new cases. Below are the key stages in the pipeline:

- **gitleaks_scan** : Scans the repository for secrets.
- **OM-Accessories-```<Component>```** : Tests for acquisition of different accessories, such as `Buds`, `Case`, `VRHeadset`, etc.
- **OM-Package-```<PackageType>```**: Tests for various package acquisitions and modifications, such as `Basic`, `Comfort`, `MAX`, etc..

Each stage is defined to handle the specific components and tasks associated with the respective testing scenarios. New stages can be added by following this naming convention:

- **OM-Accessories-```<Component>```**: for accessories test cases
- **OM-Package-```<PackageType>```**: for package test cases.

### Configure CI variables

Check the CI variables values if you want to run the e2e tests remotely with the GitLab-CI pipeline.
Expand the Settings > CI/CD > Variables on GitLab.
Here are the variables you can change (or overwrite while running the pipeline), and for each one an example of their value or possible values (if they are enum):

| Variable Name                      | Value                                                                                                    |
|-------------------------------------|----------------------------------------------------------------------------------------------------------|
| `${CATALOG_URL}`                    | `https://catalog-product-catalog-integration-disco.apps.fr01.paas.tech.orange`                           |
| `${CATALOG_URL_STAGING}`            | `https://catalog-product-catalog-integration-disco.apps.fr01.paas.tech.orange`                           |
| `${CONFIG_URL}`                     | `https://product-configurator-integration-disco.apps.fr01.paas.tech.orange`                              |
| `${CONFIG_URL_STAGING}`             | `https://product-configurator-integration-disco.apps.fr01.paas.tech.orange`                              |
| `${KC_CLIENT}`                      | `admin-ui`                                                                                 |
| `${KC_PASSWORD}`                    | `om_password`                                                                               |
| `${KC_USERNAME}`                    | `om-user@test.com`                                                                                        |
| `${KEYCLOAK_VAR}`                   | `https://keycloak-integration-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak`                |
| `${KEYCLOAK_VAR_STAGING}`           | `https://keycloak-integration-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak`                |
| `${OM_END_POINT}`                   | `https://om-order-capture-integration.apps.fr01.paas.tech.orange`                                       |
| `${OM_END_POINT_STAGING}`           | `https://om-order-capture-integration.apps.fr01.paas.tech.orange`                                       |
| `${PI}`                             | `https://cpib-integration.apps.fr01.paas.tech.orange`                                                   |
| `${PI_STAGING}`                     | `https://cpib-integration.apps.fr01.paas.tech.orange`                                                   |
| `${POI}`                            | `https://poi-integration.apps.fr01.paas.tech.orange`                                                   |
| `${POI_STAGING}`                    | `https://poi-integration.apps.fr01.paas.tech.orange`                                                   |
| `${SETTING_URL}`                    | `https://admin-ui-integration-disco.apps.fr01.paas.tech.orange`                                         |
| `${SETTING_URL_STAGING}`            | `https://admin-ui-integration-disco.apps.fr01.paas.tech.orange`                                         |

### Execution


The CI/CD pipeline will automatically execute the tests on each commit or merge request. Ensure that your tests are correctly set up to run in the CI environment.

### Check the Results

After the pipeline runs, you can check the generated reports and artifacts in the CI/CD job logs.



## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests.



### Commit Messages Format

All commits should follow a structured format to ensure clarity and consistency. Use the following format:

```xml
<type>(<scope>): <short summary>

<body>

<footer>
```

#### Commit Header

The `header` is mandatory and must conform to:

```xml
<type>(<scope>): <short summary>
```

- **Type**: `feat`, `fix`, `test`, etc.
- **Scope**: The area of the project affected (e.g., `OM-Accessories-Buds`).

#### Samples

Here are some samples to illustrate the most relevant use cases.

| Commit Message                                   | Release Type  | Trigger Release   | CHANGELOG.md File 			|
|--------------------------------------------------|---------------|-------------------|----------------------------------------|
| feat(OM-Accessories-Buds): Add new test case     | Minor         | 😊                | Features: New test case added |
| fix(OM-Package-Basic): Fix issue in test script  | Patch         | 😊                | Bug Fixes: Fixed issue in test script 	|
| chore(OM-Package-MAX-Aquis): Update dependencies | 😡            | 😡                | 😡                  			|




### Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

In order to let the semantic release template add the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:
🔒 **GITLAB_TOKEN** (gitlab access token with api, read_repository and write repository scopes and Maintainer role).
See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

### Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

### License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
