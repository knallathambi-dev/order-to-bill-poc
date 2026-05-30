<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# security-authorization-tests

This is the security-authorization-tests APIs testing requests material to be used with [Bruno](https://docs.usebruno.com/introduction/what-is-bruno).

## Getting started

### Prerequisites

To run those tests **locally**, you should follow those **main steps**:

1. [Download Bruno](https://www.usebruno.com/downloads) depending on your OS.

2. Install Bruno (example for Linux):

    ```bash
    sudo apt update
    sudo apt install bruno
    ```

    Nota: the version of Bruno should be equal or greater than `v1.28.0`.

3. Install `Nodejs` and `npm` (example for Linux):

    ```bash
    sudo apt install -y nodejs
    ```

    ```bash
    sudo apt install -y npm
    ```

    Nota: also `nvm`, the Node Version Manager, could be installed if you managed many `npm` versions: see [here](https://www.linode.com/docs/guides/how-to-install-use-node-version-manager-nvm/#use-nvm-to-install-node).

4. Install globally [Bruno CLI](https://docs.usebruno.com/bru-cli/overview) with `npm`:

    ```bash
    npm install -g @usebruno/cli
    ```

### Installing

To install **locally** this project, please follow those steps:

1. `git clone` this project under `<your_custom_directory>` from our remote Git repository:

    ```bash
    cd <your_custom_directory>
    git clone git@gitlab.tech.orange:disco/disco-oda-components/disco-security/security-authorization-tests.git
    ```

2. Change variables values inside the **Bruno Collection** you want to run:  
 
   - all the **endpoint URIs** used to call the DISCO suite components API, depending on your own deployment.  
   
   Environement Details:

   Variable list to be changes as per environment
   1)base_uri
   2)Secret
   3)client_id
   4)client_secret
   5)keycloak_url
   6)username
   7)password

## Overview

This collection is used to create User Roles in the system.

```json
    Sample Request Body :
     {
    "involvementRole": "ProductCatalogAdmin",
    "@type": "UserRole",
    "entitlement": [
        {
            "id": "x20",
            "action": "read",
            "function": "CFS Spec",
            "@type": "Entitlement"
        }
                   ]
     }
```

**Details on attributes:**

- `involvementRole`: name  of the Role created
- `entitlement`: These are the entitlements/Rights which will be assigned to the role.
  - `id`: These are predetermined ids.Only change the id if requires to assign different function
  - `function`: These are predetermined labels.Only change the if requires to assign different function

## Running the tests

### Locally

1. First, go to the directory where you've installed this project. 

2. Open the collection in bruno you want to run , select the environement or if u need to run on any different environment then update the Environement variables (mentioned above) accoridingly.

3. run the command  bru run {{$folder}} --env Integration
    (for report generation add --reporter-html results.html)
    
4. At the end of the run, a `report.html` with the results of those tests should be generated on the **same folder**, and you can open it with your favorite browser (firefox for instance):

    ```bash
    firefox report.html
    ```

Just browse the report clicking the requests and folders you want, to expand them to see more details.

you can explore more cli command option from here : https://docs.usebruno.com/bru-cli/commandOptions

## Roadmap

On each PI, those  tests are enhanced to add new tests about the new features and to update the ones that have changed.

## Contributing

Any contributions you may make are **greatly appreciated**.

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## License

Please read [LICENSE.md](./LICENSE.md) for more information.
