<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# disco-catalog

This is the disco-catalog APIs testing requests material to be used with [Bruno](https://docs.usebruno.com/introduction/what-is-bruno).

## Community

You can chat with the core team on [![Discord logo](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting started

### Prerequisites

To run those tests **locally**, you should follow those **main steps**:

1. [Download Bruno](https://www.usebruno.com/downloads) depending on your OS.
1. Install Bruno (example for Linux):

    ```bash
    sudo apt update
    sudo apt install bruno
    ```

    Nota: the version of Bruno should be equal or greater than `v1.28.0`.
1. Install `Nodejs` and `npm` (example for Linux):

    ```bash
    sudo apt install -y nodejs
    ```

    ```bash
    sudo apt install -y npm
    ```

    Nota: also `nvm`, the Node Version Manager, could be installed if you managed many `npm` versions: see [here](https://www.linode.com/docs/guides/how-to-install-use-node-version-manager-nvm/#use-nvm-to-install-node).
1. Install globally [Bruno CLI](https://docs.usebruno.com/bru-cli/overview) with `npm`:

    ```bash
    npm install -g @usebruno/cli
    ```

### Installing

To install **locally** this project, please follow those steps:

1. `git clone` this project under `<your_custom_directory>` from our remote Git repository:

    ```bash
    cd <your_custom_directory>
    git clone git@gitlab.ow2.org:discobole/disco-oda-components/disco-catalog/catalog-automation-collections/bruno.git
    ```

1. Change variables values inside the **Bruno Collection** you want to run:  

   All the **endpoint URIs** used to call the DISCOBOLE product-catalog (ODACAT) components API, depending on your own deployment.  
   For instance into `Catalog/environments/Integration.bru` Bruno Collection, here are the variables to be changed as per environment:
   1. Auth
   1. Secret
   1. Event_service
   1. host
   1. User_role
   1. username
   1. Pswd
   1. query
   1. Service_href
   1. Resource_href
   1. ServiceFO_href
   1. ResourceFO_href

## Overview

1. This collection is used to create CFS, Productspecification, ProductOffering (Atomic, Bundle, Contract), ProductOfferingPrice, PolicyRule, Category and to validate the corresponding tests.
1. Do not change the folder structure and order of the requests in the collection as it is designed to follow a specific process order.
1. Runtime variables have been used inside the scripts in order to run the tests without any mannual interventention (apart from the variable mentioned above in 'Installing' section)
1. DO NOT Change the request body of any Request as it can lead to changes in further requests as it follows the chain request pattern.
1. Note that every entity will be unique in the created Contract (offer).

## Running the tests

### Locally

1. First, go to the directory where you've installed this project.
1. Open the collection in bruno you want to run, select the environement or if you  need to run on any different environment then update the Environement variables (mentioned above) accoridingly.
1. `bruno run <path-to-collection> --env <environment-name> --format html --output report.html`
   (the `--format html --output report.html` options are there to get the results formatted as an html report)
1. At the end of the run, a `report.html` with the results of those tests should be generated on the **same folder**, and you can open it with your favorite browser (firefox for instance):

    ```bash
    firefox report.html
    ```

    Just browse the report clicking the requests and folders you want, to expand them to see more details.

    You can explore more cli command option from here : <https://docs.usebruno.com/bru-cli/commandOptions>

## Support

Anyone from the **"Product Catalog Management team"** (ODACAT) of DISCOBOLE could support you.

## Roadmap

On each PI, those tests are enhanced to add new tests about the new features and to update the ones that have changed.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
