# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.6.0...1.7.0) (2026-04-24)


### Bug Fixes

* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([e372306](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/e372306655f9081449cdce2c28eb1f1ceea14f78))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([f6dd0c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/f6dd0c991ffef532d1b5629c78d512d049cf9904))
* **helm:** upgrade version only for the chart's version [ci skip] ([7b8de30](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/7b8de300841f36f0c79ef860e3fddae77bcdaff8))
* **IPCEISCOOD-1136:** Tangible nodes in same batch stuck in InDelivery due to... ([94e7afa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/94e7afa5258103f14b04deb29a7b7270e69a0684))
* **IPCEISCOOD-1138:** Delivery events consumed but shipping/service order not triggered ([eeb87c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/eeb87c5d30e8eb73518a3c85e1f2336c01fb688f))
* **IPCEISCOOD-1139:** fix sonar qube issues ([beb2715](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/beb2715960bb3c73f17f7537fae1d26c8fedafa7))
* **IPCEISCOOD-1141:** [Technical Debt] Add missing feature file coverage for the latest delviery order changes ([766f601](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/766f6014a832027d5632b07fd06e0894213757c4))
* **IPCEISCOOD-1161:** Plans stuck in progress under high load due to reactive... ([d2a25c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/d2a25c50b7f82e7dc949a571c0f31c41bedaee96))
* **IPCEISCOOD-1197:** review the fallout process after the delivery management evolution ([377feb9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/377feb95031cb0ef3b21bbf269d5bf83cf96ed38))
* persist delivery order status by mutating refs from the same object instance passed to save ([99f1beb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/99f1bebe1e7d335d2d2fa55f6ef5606ed4054900))
* Update the “retries exhausted” error message to be more clear and meaningful. ([72c81fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/72c81fd1b5284ed796f58860d807f16c5a1ccaf2))


### Features

* implement som batching for mobile request ([dcb8df6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/dcb8df67f669d5680bb0d59a8516d5772f62bfba))
* **IPCEISCOOD-1104:** evolve cfs delivery management ([35a0f54](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/35a0f5415a9894ee0853c5cadd2fda471dff07d5))
* **IPCEISCOOD-1152:** Make service order batching configurable via application properties ([249d619](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/249d619f1abd68d483d32c38f14408e8361200af))
* **IPCEISCOOD-1158:** Refactor delivery status and error handling ([540b880](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/540b8803160dbb02ab583a77a6f1b3d9162ded1d))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.5.1...1.6.0) (2026-02-19)


### Bug Fixes

* add global exception handler ([8624200](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/86242001a632665418f5d7c51e72cb859a5e1d62))


### Features

* add flag to skip service catalog validation ([4654218](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/465421873f765767981160e77a4d4c85267a04b8))
* **IPCEISCOOD-1029:** Error messages enhancement - implementation ([000cc55](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/000cc55a64fc972cd1f76b24089978853d53e1dd))
* move delay mechanism to scheduler ([ffe2598](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/ffe259880aeef60c6ca55bc3d5e2d6d06e69c431))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.5.0...1.5.1) (2026-02-10)


### Bug Fixes

* authorization forbiden response on roles with spaces ([713f072](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/713f07240ac0686488b9ef5ce268a2611e38c48a))
* **IPCEISCOOD-1116:** Orchestration delivery node still stuck in IN_DELIVERY... ([91097da](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/91097da433092399fbccc6fe9ca3b687691cf6e4))
* **IPCEISCOOD-1117:** Resume Pending Deliveries Fails for Legacy Plans with... ([1436e83](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/1436e834365a7f830b6cad821950a910c05b7822))
* **IPCEISCOOD-1118:** Refactor and fix issues on resume delivery for nodes ([284a482](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/284a4826d6b9f988e58211c857f1ccb5e6bdc114))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.4.0...1.5.0) (2025-11-13)


### Bug Fixes

* **988:** user roles permissions ([ec7ea58](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/ec7ea58d11ad142c192484d7b36c81cff0918670))
* **IPCEISCOOD-919:** add auth to test endpoints ([1acf0b2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/1acf0b27c337465ecfd4428e13308c2591e0b620))
* make mock delivery deliver any node ([69d9cde](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/69d9cdeb7c2a4a0503ba26ec4708b2df936b7b60))
* mock delivery factory response delays ([d708b8c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/d708b8cdba8f3d599c667dbf979219af03079df9))
* only delivery one node per invocation ([3623dbd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/3623dbd37c25e6f5ab847aa8b545db7b1ce33c59))
* remove un-used config for promtail and istio ([f13aeba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/f13aeba1d944dcd35ed2c8ed42cf7f6417ed5c74))


### Features

* add property to choose between delivering all nodes or a single one ([ae44657](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/ae446574f68a9d9c6ef67bfb5cd4f5b627651c53))
* **IPCEISCOOD-952:** COOD performance remove thread sleep ([aa28c47](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/aa28c47a69bd595e7278ede7fd24388bd57e013d))
* **upgrade-orchestration-spec-module:** upgrade orchestration spec module to 1.0.1 ([de65f36](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/de65f3658d041fc37917c9f396b0ee254570d2a2))
* **upgrade-order-inventory-spec-module:** upgrade order inventory spec module to 1.1.0 ([0134215](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/01342156fcbc5d33a15ef2f70c57cfb5fb845d48))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.3.3...1.4.0) (2025-06-06)


### Bug Fixes

* apply pascal format to md file ([82b14aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/82b14aafaf5e7d9de4f289ae3a2972571bf0be33))
* apply pascal format to md file ([035f075](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/035f07504b95980278e0098f139cee8996a9b5f0))
* **IPCEISCOOD-667:** InDelivery nodes published more than once on service... ([710aa75](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/710aa758e23ca8e94b9a622ceefee8e2f1d7e38f))
* **IPCEISCOOD-667:** InDelivery nodes published more than once on service... ([992a503](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/992a503317638f5a879fad7e8248e5d67729e338))
* **IPCEISCOOD-667:** InDelivery nodes published more than once on service... ([7a1ed52](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/7a1ed52ee44dda0306bf37594ab0c2ce6c42c299))
* **IPCEISCOOD-674:** Enhance service order & shipping order scheduler jobs ([4161216](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/416121625f528c96577abb26b8b5e56c67ffca14))
* **IPCEISCOOD-685:** Integration _the SIM card is being held. The... ([5ca045a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/5ca045a98f73bad87aab0171495d12eccb6fe6e7))
* **IPCEISCOOD-743:** [Integration] Occasionally, orchestration plan nodes are... ([6afba37](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/6afba37813223dc1fea77c0974aea47966ab0f3b))


### Features

* **IPCEISCOOD-602:** [External Resolution] error messages are not clear ([cf8c093](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/cf8c09390ac16e32d8aa18ea12e43a4787f308a2))
* refactor to enum map to comply with sonar standards ([264ef5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/264ef5c25967899f60c060a8769611abf41e93cd))

<!--
Software Name: orchestration-delivery-management
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## [1.3.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.3.2...1.3.3) (2025-03-02)


### Bug Fixes

* trigger release ([ef6bc9d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/ef6bc9d38c2671a46cd52314c9be8bacbe5db4d2))

## [1.3.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.3.1...1.3.2) (2024-10-27)


### Bug Fixes

* use stable repo instead of unstable repo in production ([ac630c0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/ac630c0b5005827c19d59c95b65a3b906dfddf07))

## [1.3.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.3.0...1.3.1) (2024-10-13)


### Bug Fixes

* add snapshot to 1.3.0-SNAPSHOT ([203ae9e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/203ae9e72def6a18ff636e2e6389ddf6285db3ab))
* change userrole urls ([0105c47](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/0105c47f0932a9e334dbda4762d798186bd88a2b))
* **IPCEISCOOD-499:** Issue happened and unexpected behaviour in delivery and node state change ([acb4f84](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/acb4f8474a213ad6c1aeda5a3b650f9aa164d571))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.2.8...1.3.0) (2024-09-04)


### Bug Fixes

* change docker image config ([c121e2a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/c121e2ad3a776535aa47168d8d5c2cc01e5bf682))
* fix failed test ([35991f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/35991f3c59a9e76007e9f807fa3937a2a5d78987))
* fix sonar issues ([7e5f3b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/7e5f3b1bcfea01fa60b8d8f7e673cc7f0b7fe4cc))
* **IPCEISCOOD-447:** fix sonar issues, increase coverage ([681ff19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/681ff1956e3582baee5b3eb68ca35f78797eb33f))
* move innovation job from staging to production stage ([e2a71a0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/e2a71a022ce62783bc0d3762d5ec47a381588e50))
* remove cert from source code and add it as variable in gitlab ci-cd ([859a3c4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/859a3c42d77e57c86761c5f54f360d1e1dcc0eee))
* remove issue of uploading jar to artificatory ([4523a75](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/4523a75211c76e037cbac0ee52e7d83621b92195))
* return HELM_BASE_APP_NAME variable ([f07eec9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/f07eec9475315fecdb27e60d46f7ccd073cc198f))
* revert version ([6bcd5a3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/6bcd5a38f20509d78015e047108f22e066b5881e))
* use git project name as app_name ([2230224](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/22302249567c85753390e786f829a10ccc8fddb3))
* use pi6 cood module release version ([81d0c5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/81d0c5d04a8f4a0edff4e7222d40113e9d7c8a52))


### Features

* **IPCEISCOOD-447:** enable sonar quality gate ([cfd6bf4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/cfd6bf449ca6ea89cd536a03f638a68e60cf6cab))
* **IPCEISCOOD-447:** enable sonar quality gate ([fb1d56e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/fb1d56e913ad8727a5d6b6c0f78d18bd21753458))

## [1.2.8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.2.7...1.2.8) (2024-07-16)


### Bug Fixes

* return HELM_BASE_APP_NAME variable ([6d2f9d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/6d2f9d3b4178cb250fb7f1da9f4b7a39ab90f567))

## [1.2.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.2.6...1.2.7) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([157bd31](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/157bd310d241556adcc2e353233ac8748f09beb0))

## [1.2.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.2.5...1.2.6) (2024-07-14)


### Bug Fixes

* remove cert from source code and add it as variable in gitlab ci-cd ([1df4e7b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/1df4e7b18aae3c3e171f83611ca4edf83631e721))

## [1.2.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.2.4...1.2.5) (2024-07-14)


### Bug Fixes

* remove issue of uploading jar to artificatory ([c90887e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/c90887ef965f3ae12d912c0d99756f1e2d174cbf))

## [1.2.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/compare/1.2.3...1.2.4) (2024-07-03)


### Bug Fixes

* use git project name as app_name ([aaa408e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-management/commit/aaa408e5678eb35a800e880327fa5a3be1ccabcb))

## [1.2.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.2.2...1.2.3) (2024-06-12)


### Bug Fixes

* change docker image config ([ccbffed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/ccbffed72329365aa3e55edf75a28002ec6af077))

## [1.2.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.2.1...1.2.2) (2024-06-11)


### Bug Fixes

* **HotFix:** fix error message returned from som, to be saved ([f3fee1a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/f3fee1a8fbac69d84f5dcfc29fee95e0343653c9))

## [1.2.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.2.0...1.2.1) (2024-06-10)


### Bug Fixes

* Staging - all the nodes have error messages section even if there is no error ([ecd6ee9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/ecd6ee9f5677c8fced75c16f391e95821b4a5279))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.1.0...1.2.0) (2024-06-07)


### Bug Fixes

* add new elasticsearch service name in fluent config ([93c9be2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/93c9be284eafe3c220b4662a1371805a621ff688))
* Can not make manual delivery ([154521e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/154521e382f3bbd49d18163fe01b6f49b16b68fb))
* Corrupted PO event make COOD service down ([9e42d74](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/9e42d74c9611fc512ae6d3cab6536fb5c72715f8))
* Corrupted PO event make COOD service down ([a1bef44](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/a1bef44911f14d65a5f3ece46d48bada009908a3))
* First request to mock server gives timeout ([012b638](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/012b63804213bf231f34de6acd0dfa77abcbc372))
* **IPCEISCOOD-381:** add service order handling when held state and error message ([b06bc3d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/b06bc3dc6a420d89af4e5e26a5f5c685ec16801b))
* **IPCEISCOOD-381:** add service order handling when held state and error message ([df6de2c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/df6de2c6e7b19de5bce9c427874eb976ecbe5ac9))
* **IPCEISCOOD:** Invalid JSON property in logback files ([75af46b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/75af46b8dae5d731c0955c4adb78696f207df667))
* **IPCEISCOOD:** prevent retry logic and set error message ([e9b9687](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/e9b9687dcae452cd010d4dea5287162dbc6fe7de))


### Features

* enhance and add more logs for COOD ([a5feb6f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/a5feb6f5cae7b663f6202ee8f07f109eebdddf7a)), closes [#IPCEISCOOD-72](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/issues/IPCEISCOOD-72)
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([9b08bdf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/9b08bdfa1d8c22b3044eaced335664a1db45c704))
* **IPCEISCOOD:** enable db Transaction ([4ddb6a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/4ddb6a6f2b88fabfeefc89d8f6293e0b21c3b884))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.0.2...1.1.0) (2024-04-30)


### Bug Fixes

* enable production job and add image pull secret for openshift images ([c5012c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/c5012c948ed5be9a63cedfa28d0a5c52621feb0f))
* fix app_name use innovation instead of innov, same value like ([a22afee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/a22afee6152e05cf7117ad7a51495f51142e0150))


### Features

* add configuration for innovation environment ([6ff255e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/6ff255e3740e28271fc77d2717f46ae06547eaec))

## [1.0.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.0.1...1.0.2) (2024-04-18)


### Bug Fixes

* **IPCEISCOOD-294:** Support Multiple Delivery Factories (Catalogs & SOMs) - SOMRef value turns to be null in case of nodes with state completed ([26b7ecd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/26b7ecd830d9f845504bdf63a5ccd5e556856211))
* keycloak uri in staging helm values ([eb17cf7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/eb17cf7efecd38b29ac2bab1b6a6ea6707255c30))

## [1.0.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/compare/1.0.0...1.0.1) (2024-04-07)


### Bug Fixes

* keycloak uri in staging helm values ([bce43fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/bce43fc52bebf990c3527d5063f16b6886f89cd6))

# 1.0.0 (2024-04-07)


### Bug Fixes

* add cloud and fabric dependencies ([69335d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/69335d220b28912a933e2d42da03872085afff47))
* add diff blue tests ([1de2633](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/1de263316c7705a43088311ce066a9773aa93203))
* add kafka typer to parse the event without header ([f103bc2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/f103bc228fb3c697e59f219d126a125c142c9d90))
* add kafka typer to parse the event without header ([8acb836](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/8acb836c57a08e78a01bc0be97dc113b85cac707))
* change keycloak client id ([3108bc9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/3108bc925ac94f6116f26de2c0a19d9f50c9771e))
* empty variable expansion issue ([7dd42e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/7dd42e87f2c2443c353db844e0ff5ac73dcffd87))
* fix kafka config in app yamk ([6c590b7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/6c590b7d1fe3856160f8232b8c027c493abafc72))
* fix kafka config in app yamk ([0edc4d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/0edc4d357bc786c873a2b2831370d1d22e305484))
* fix serialize-deserialize objects in kafka for time to remove timestamps ([77f119c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/77f119ce0f194779a3f334c88b8f541ec56e31ca))
* fix spotbug issue ([4b85a94](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/4b85a9426bba47febdb666aded350df4ac57dec2))
* fix tests ([ca8e8ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/ca8e8edb0ed92c19159811195355ad091e4bcd10))
* fix tests ([536c3ef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/536c3effde57c8c32dbe1ba83761f5a8ef189726))
* fix tests ([be72d11](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/be72d11f0a1f6082ca071d1bea6d6cf8ed965db4))
* fix tests ([1e75913](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/1e75913baeef02d4bfc25c7617f11caa1233edc4))
* fix tests ([4a5b8a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/4a5b8a46dced2697b089e9ad6d77d89387dd97a9))
* fix violation ([152ece7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/152ece726077e754611ad3986d89d4a8cf1bebe5))
* fix violations ([255d87b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/255d87bc85be7dc78c4102f6816cdb29a4f566dd))
* fix violations ([481d508](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/481d5087490a386c6e43d0a4b82fb82f9594929a))
* handle tangible nodes delivery ([ef6b0b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/ef6b0b94505ab48c18a4c56c279f3b2df28fba8c))
* handle tangible nodes delivery ([d0f097d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/d0f097d8f7ba9ef1a22f437155d4389952ed65af))
* match spec name with ignore case ([1122e9c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/1122e9c2a067dedfea7e567ea4ffbace090a2bc4))
* match spec name with ignore case ([7a6641d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/7a6641d01ca36148c6d5ef32503dd3e1e7e73150))
* merge changes of use same db as cood ([5838673](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/5838673b46d500c0cdd7c36494ec2df52d4d3800))
* merge changes of use same db as cood and change keycloak client id ([7b0d425](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/7b0d425a02dc137b05fd8751cdce01c9c119fa89))
* revert actuator port ([b7db85c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/b7db85cb9105c1da4e8717b2dcdafd6013bb3be3))


### Features

* **IPCEISCOOD-199:** Move Delivery business to DM service ([cd166cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/cd166cc17017d548d36802c6f59b4f4f75ebbbac))
* **IPCEISCOOD-199:** Move Delivery business to DM service ([120165b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/120165be4e34df15e7069dceedfa0ddce62241c9))
* **IPCEISCOOD-199:** Move Delivery business to DM service ([0dbc324](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/delivery-management/commit/0dbc3245d460d32c5d27e38e9fd790dc94fbff39))
