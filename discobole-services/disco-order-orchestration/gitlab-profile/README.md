<!--
Software Name: disco-order-orchestrationlab-profile
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Order Orchestration and Delivery (COOD) Component

Responsible for orchestrating of the delivery of product orders.

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting started

### Prerequisites

- git
- Java 17
- Maven 3.8
- IntelliJ IDEA
- MongoDB Compass
- Docker Desktop
- macOS/Linux or Git Bash on Windows

### Installing

To install locally recursively the projects, please clone the [know-how project](https://gitlab.ow2.org/discobole/disco-project/know-how).

```bash
git clone git@gitlab.ow2.org:discobole/disco-project/know-how.git
cd know-how
```

By default, the projects are cloned into `$HOME/workspaces`. To change the workspaces folder and other options, please run `git-all --help` script.

Then clone all repositories of `disco-order-orchestration`, as follows:

```bash
git-all clone discobole/disco-oda-components/disco-order-orchestration
```

### First Step

Update the `settings.xml` file located in the `.m2` directory with your credentials:

```xml
<server>
  <id>gitlab-maven</id>
  <configuration>
    <httpHeaders>
      <property>
        <name>Private-Token</name>
        <value>${GITLAB_TOKEN}</value>
     </property>
    </httpHeaders>
  </configuration>
</server> 
```

To build and deploy the application, the following environment variables need to be defined and exported in your terminal. 

| Name                 | Description                                                  | Value                                                        |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| :lock: `GITLAB_TOKEN` | The GitLab private access token defined with the `api` scope. | See your [preferences](https://gitlab.ow2.org/-/user_settings/personal_access_tokens?page=1&state=active&sort=expires_asc) (*required to deploy the project artifacts*). |
| `CI_API_V4_URL`      | The GitLab API v4 root URL.                                  | `https://gitlab.ow2.org/api/v4` (*required to pull & deploy the project artifacts*). |
| `CI_PROJECT_ID`      | The ID of the GitLab project. This ID is part of the GitLab project package registry URL. | See the project general settings (*required to deploy the maven artifacts*). |
| `GROUP_ID`           | The ID of the GitLab group visible in the [project general settings](https://gitlab.ow2.org/groups/discobole/-/edit). This ID is part of the GitLab group package registry URL. | `5972` (*required only if the project depends on `DISCOBOLE` maven artifac*t). |

:information_source: If you don't have access to the project general settings, the projects ID may be retrieved as follows: 

```bash
# Get project ID of the orchestration-delivery project
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery | jq .id
# Get project ID of the orchestration-delivery-fallout project
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery-fallout | jq .id
# Get project ID of the orchestration-delivery-management project
curl -s https://gitlab.ow2.org/api/v4/projects/discobole%2Fdisco-oda-components%2Fdisco-order-orchestration%2Forchestration-delivery-management | jq .id
```

## Keycloak + Kafka + MongoDB Setup

1- Add the following line to your `/etc/hosts` file (required for Docker networking):

```bash 
127.0.0.1 host.docker.internal
```

2- Download [`keycloak-kafka-mongo-setup`](./keycloak-kafka-mongo-setup) directory as zip file. 

This setup uses Docker Compose to run the following services:

- **Zookeeper**
- **Kafka**
- **Kafka UI**
- **Kafka Connect**
- **MongoDB (with replica set)**
- **Debezium MongoDB connectors**
- **Keycloak (with realm import and user initialization)**

### Start the Docker Services

Run this command from the root directory`keycloak-kafka-mongo-setup`:
```bash
docker compose up -d
```

### Initialize Keycloak

After containers are running, run the initialization script:

```bash
bash keycloak-init.sh
```

This will:

- Wait for Keycloak to be ready
- Regenerate the client secret
- Create user `admin-ui@orange.com` (if it doesn't exist)
- Assign roles to user
- Generate `NEW_CLIENT_SECRET`, you can see it printed in the terminal or follow these steps to get it manually:
  - Navigate to http://localhost:8080
  - Select **SpringBootKeyCloak** realm
  - Navigate to **Clients**
  - Navigate to **cood** client
  - Select the **Credentials** tab
  - Copy the client secret as your `KEYCLOAK_CLIENT_SECRET`

Add the following environment variables in all applications:

```bash
KEYCLOAK_CLIENT_ID=cood
KEYCLOAK_CLIENT_SECRET=`NEW_CLIENT_SECRET`
KEYCLOAK_URI=http://localhost:8080
```
### Using Remote Keycloak (Non-Local Setup)

If you are **not using a local Keycloak instance**, follow these steps to configure each microservice:

#### 1. Add Environment Variables

Add the following environment variables to the run configuration of **each microservice**:

```properties
APP_SECURITY_USERROLERETRIEVALURL=[USER_ROLE_API_URL]

KEYCLOAK_CLIENT_ID=[YOUR_CLIENT_ID]

KEYCLOAK_CLIENT_SECRET=[YOUR_CLIENT_SECRET]

KEYCLOAK_URI=[KEYCLOAK_SERVER_URL]

### Retrieve Keycloak Configuration Data

The Keycloak configuration data (`KEYCLOAK_CLIENT_ID`, `KEYCLOAK_CLIENT_SECRET`, and `KEYCLOAK_URI`) can be retrieved by following these steps:

1. **Open Keycloak Admin Console**
2. **Choose the corresponding Realm**
3. **Select your client from the Clients tab**
4. **Open the Credentials tab** 

#### 2. Fix SSL Certificate

Import the SSL certificate to your JVM's trusted certificate store:

```bash
keytool -import -alias myserver -keystore [JVM_PATH]/Contents/Home/lib/security/cacerts -file [CERTIFICATE_FILE]

### Obtain the Certificate

The certificate file can be retrieved by following these steps:

1. Open your Keycloak URL in a web browser
2. Click on the **site information** icon (usually a padlock icon in the address bar)
3. Select **Show connection details** or **Certificate**
4. Click **Show certificate** or **View certificate**
5. Navigate to the **Details** tab
6. Click the **Export** button
7. Save the certificate file to a location on your computer


## MongoDB Compass Setup

1. Open MongoDB Compass and connect to localhost

2. Create the COOD Database
    - inside it create orchestrationPlan collection

3. Create the falloutManagement Database
    - inside it create falloutIncident collection

4. Open Mongo Shell from MongoDB Compass
   - Run the following commands to create a user for the falloutManagement database:
   
    ```bash
     use falloutManagement
     db.createUser({
       user: "adminUser",
       pwd: "adminUser",
       roles: [
         { role: "readWrite", db: "falloutManagement" }
       ]
     });
    ```

### Running tests

Add the following line to your `/etc/hosts` file (used by tests):
```bash 
127.0.0.1       docker1
```

- Run tests using maven command, this will run unit tests, integration tests, BDD tests.

```bash 
  mvn clean test
```

### Running Feature Files

**Edit Run Configurations of feature files in orchestration delivery service**:

- In the **Glue** input field, enter:
    - If the feature file exists in the `repo` folder
  
         ```java
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.mocked.repo
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.common
         ```

    - If the feature file exists in the `eventPublisher` folder

         ```java
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.mocked.eventPublisher
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.common
         ```

    - If the feature file exists in the `deprecated` folder
    
         ```java
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.deprecated
        com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.common
         ```

- If your operating system is **Windows** or **Linux** in the **VM Options** field enter:

     ```java
     -Dcontainer.vm.fake.dns=true
     ```

Please, have a look on the `README.md` of each Git repository.

## Built With

Add tools used to build your application.

- [Git](https://git-scm.com/) - Open source distributed version control system
- [Java 17](https://jdk.java.net/)
- [Maven](https://maven.apache.org/) - Dependency Management

## Documentation

- [Overall DISCOBOLE documentation](https://discobole.ow2.io/doc)
- [Component documentation](https://discobole.ow2.io/disco-oda-components/disco-order-orchestration/doc)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
