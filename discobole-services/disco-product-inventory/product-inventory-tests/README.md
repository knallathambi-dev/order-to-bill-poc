<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# 📦 Product Inventory - API Test Suite

Automated API testing for the **Product Inventory (PI)** component of the **DISCOBOLE Suite**, built using [Robot Framework](https://robotframework.org/).

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

Welcome! This document will guide you through setting up and running the tests for the **Product Inventory** component. Whether you're setting this up for the first time or contributing to the project, you'll find all the necessary steps below.

### Prerequisites

Before running any tests, ensure your environment is properly set up. This involves installing **Python**, **Robot Framework**, and cloning the project repository.

#### 1. Install Python 3.9.1

Ensure Python 3.9.1 or higher is installed on your system:

```bash
sudo apt update
sudo apt install -y python3.9 python3.9-venv
python3.9 --version
```

#### 2. Install Robot Framework 6.1.1

Next, install **Robot Framework**, which is the foundation for the automated tests:

```bash
pip install robotframework==6.1.1
robot --version
```

#### 3. Install SeleniumLibrary and Others

To make Robot Framework fully operational, install the following additional libraries:

```bash
pip install robotframework-seleniumlibrary
pip install robotframework-sshlibrary
pip install robotframework-evaluate
```

- SeleniumLibrary: Enables browser automation
- SSHLibrary: Enables SSH-based test automation
- Evaluate: Adds enhanced expression evaluation capabilities

#### 4. Clone the Repository

Now that the prerequisites are installed, clone the project repository to your local machine:

```bash
git clone https://gitlab.ow2.org:discobole/disco-oda-components/disco-product-inventory/product-inventory-test.git
cd product-inventory-test
```

## Project Structure

The test project is carefully structured to provide **clarity, scalability, and maintainability**. It is organized by business domain (e.g., Product, Job) and then subdivided by **HTTP method** or **BDD scenario style**, making it easy to locate, understand, and execute test cases.

Each directory represents a specific **API domain** defined in the Swagger/OpenAPI specification and includes tests that verify the behavior of corresponding endpoints.

### Structure Overview

| Folder                          | Purpose                                                                                   | What It Contains                                                                                                                                                                                 |
|--------------------------------|-------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Test_Cases/Job/`              | Tests for **background job management** (e.g., import/export processes, async operations). | - `BDD_Scenarios/`: High-level business flows (Gherkin syntax)<br>- `GET_Method/`: Retrieve job data                                                                                                |
| `Test_Cases/JobSpecification/`| Covers **job specification objects**, used to define how jobs are configured.              | - `GET/POST/DELETE_Method/`: Endpoint-specific tests<br>- `BDD_Scenarios/`: End-to-end validations<br>- `Data/`: Input data files for requests                                                    |
| `Test_Cases/Product/`          | Handles all tests for the **Product Inventory API** (core business entity).                | - `GET/POST/PATCH/DELETE_Method/`: Standard REST actions<br>- `Export/`: Export product data tests<br>- `UpgradeRelatedPartyToV5/`: Upgrade/migration logic<br>- `Data/`: Reusable test datasets    |
| `Test_Cases/Reporting/`        | Validates **report generation and access APIs**.                                          | - List and fetch different report types, including product offering reports                                                                                                                       |
| `Test_Cases/System/`           | System-level tests: **status, version, and configuration** endpoints.                      | - System version verification, and configuration inspection                                                                                                                                       |
| `Global_Configuration/`        | Holds environment-specific variable files and configuration to enable easy switching between Review, Staging, Integration environments. | - `Review_Variables.robot`, `Staging_Variables.robot`, `Integration_Variables.robot`, and other global config files                                                                                 |

### How the Structure Helps

- **Domain-Centric Organization**  
  Each business entity (Product, Job, etc.) has its own directory, making it easy to understand which tests apply to which part of the API.

- **Method-Based Grouping**  
  Within each domain, test cases are grouped by HTTP method (`GET_Method`, `POST_Method`, etc.). This provides a logical and modular layout that simplifies maintenance and debugging.

- **Behavior-Driven Tests (BDD)**  
  High-level user scenarios are written in natural language (Gherkin syntax) under `BDD_Scenarios/`, enabling collaboration between QA, devs, and business analysts.

- **Separation of Test Data**  
  The `Data/` folders store reusable JSON, CSV, or variable files to keep the test logic clean and centralized.

- **Keyword Reusability**  
  Custom keywords are stored in dedicated `Keywords/` folders, and better maintainability.

## Execution Modes

You can execute the tests either locally on your machine or remotely via GitLab CI/CD. Below are the detailed steps and options available for both.

### Local Execution

#### Step 1: Clone the Repository

```bash
git clone https://gitlab.ow2.org:discobole/disco-oda-components/disco-product-inventory/product-inventory-test.git
cd product-inventory-test
```

#### Step 2: Configure Environment Variables Locally

```bash
export KC_URL_Value="https://keycloak-integration-disco...tech.orange/realms/SpringBootKeycloak"
export KC_Username_Value="cpib-user@test.com"
export KC_Password_Value="your_password_here"
```

#### Step 3: Switching Between Environments Locally

To switch between environments like `Review`, `Staging`, or `Integration`, set the `${Environnement_used}` variable. This variable dynamically loads the correct configuration file from `Global_Configuration/`:

```bash
export Environnement_used=Review
```

Or directly in the Robot test file:

```robot
${Environnement_used}    %{ENV=Review}
```

This approach allows seamless switching just by changing a single variable.

#### Step 4: Get Access Token

The test suite uses a dedicated keyword to retrieve an OAuth2 access token from Keycloak.

In `Manage_Token.robot`, the keyword `Get_token` does the following:

```robot
Get_token
    log         ${USERNAME}
    Create Session    keycloak    ${KEYCLOAK_URL}
    ${headers}=    Create Dictionary    Content-Type=application/x-www-form-urlencoded
    ${data}=       Create Dictionary
    ...     grant_type=password
    ...     username=${USERNAME}
    ...     password=${PASSWORD}
    ...     scope=openid profile email
    ...     client_id=${CLIENT_ID}
    ${response}=    Post Request    keycloak    /protocol/openid-connect/token
    ...     headers=${headers}    data=${data}
    Should Be Equal As Strings    ${response.status_code}    200
    ${CPIB_TOKEN}=    Set Variable     ${response.json()["access_token"]}
    Set Global Variable           ${CPIB_TOKEN}
```

The token is then used in all authenticated requests, ensuring secure access to the API.

#### Step 5: Run Tests Locally

Execute test suites using the `robot` command:

```bash
robot Test_Cases/Product/GET_Method/Get_Product_By_Id.robot
robot Test_Cases/Job
```
#### Step 6: View Results

After execution, the generated files (log.html, report.html, output.xml) are located in the local log/ folder.

### Remote Execution – CI/CD Pipeline (GitLab)

Our CI/CD pipeline is designed for automatic execution upon code changes or merge requests.

#### Step 1: Configure GitLab CI/CD Variables

Add the following variables in your GitLab project’s settings:

| Variable            | Value                                                                                                  |
|---------------------|--------------------------------------------------------------------------------------------------------|
| `KC_URL_Value`      | `https://keycloak-integration-disco...tech.orange`                                        |
| `KC_Username_Value` | `cpib-user@test.com`                                                                                   |
| `KC_Password_Value` | `your_password_here`                                                                                   |
| `AUTH_CLIENT_ID`    | `admin-ui`                                                                                             |
| `PI`                | `https://product-inventory-integration-disco...tech.orange`                               |
| `ENV`               | `Review` / `Staging` / `Integration` (used to select proper environment from `Global_Configuration/`)  |

#### Step 2: Define CI/CD Job for Acceptance Tests

In `.gitlab-ci.yml`, you may define a job like the following to trigger the acceptance test pipeline:

```yaml
acceptance-tests:
  stage: acceptance
  variables:
    ENV: Review
  trigger:
    project: disco/disco-oda-components/disco-product-inventory/product-inventory-test
    branch: sp3-pipline
    strategy: depend
  allow_failure: true
  rules:
    - if: $CI_COMMIT_TAG
      when: never
    - if: '$ACCEPTANCE_TEST_ENABLED != "true"'
      when: never
    - if: '$HELM_REVIEW_ENABLED != "true"'
      when: never
    - if: '$CI_COMMIT_REF_NAME =~ $PROD_REF || $CI_COMMIT_REF_NAME =~ $INTEG_REF || $CI_MERGE_REQUEST_TARGET_BRANCH_NAME !~ $INTEG_REF'
      when: never
    - !reference [.test-policy, rules]
```

This configuration ensures the pipeline only runs under specified conditions and loads the appropriate environment variables.

#### Step 3: Trigger and View Pipeline Results

- Pipelines are triggered via commit/push or manually.
- Navigate to the job page to view artifacts: `log.html`, `report.html`, `output.xml`.

#### Step 4: Downloading Artifacts After Pipeline Completion

  Once the pipeline has finished:

- Navigate to CI/CD > Pipelines > Jobs in the GitLab interface.

- Click on the completed job (e.g., POST-Product, GET-Product, Job, etc.).

- Go to the Artifacts tab and download the generated reports and logs, including: report.html, log.html and XML result files (Output_*.xml)

⚠️ Important: Artifacts are stored for a limited time, as defined by the expire_in directive in the .gitlab-ci.yml file:

```yaml
artifacts:
expire_in: 72h
  ```
Make sure to download or archive your artifacts before they expire, as they will be permanently deleted after the retention period.

## Commit Message Format

Please ensure your commit messages follow the standard format:

```text
<type>(<scope>): <short summary>

<body>

<footer>
```

Here are some examples:

- `feat(Products): Add new test case for Product API`
- `fix(JobSpecification): Correct token handling`
- `chore(TMFC): Update naming conventions`

## Roadmap

The test suite will continue to evolve with new features and improvements. Key goals include:

- Expanding test coverage with new test cases
- Maintaining compliance with TMF standards
- Improving test automation for faster execution

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-product-inventory/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

In order to let the semantic release template add the necessary changes on your Git repository, it is required to define credentials in this CI/CD variable:
🔒 **GITLAB_TOKEN** (gitlab access token with api, read_repository and write repository scopes and Maintainer role).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
