# [1.14.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.13.0...1.14.0) (2026-02-11)


### Bug Fixes

* add required atType field ([36791a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/36791a43fc77fef91579a6886b152aaac2440fae))
* adjust dateShift range in billingAccountByRelatedPartyId.json ([7be41f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7be41f12197b509a82ff9f63249cdaa0356b618d))
* **IPCEISCOOD-1010:**  SMS Option got held in modification offer ([3a6a866](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3a6a8661469053a56ae360c0766be444abb01b5b))
* update dateShift range in billingAccountByRelatedPartyId.json ([4dcb40d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4dcb40d18a9ffd21aac4cf0c8529fe93f5cb4578))


### Features

* **940:** support date characteristic ([dfdee53](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/dfdee53bbd87dce1d59a98048c34ee476cd799c2))
* **IPCEISCOOD-1065:** enhance shipping delivery status update ([b5af668](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b5af6686efb33bac5648fc298e80c48f57c6632d))
* **IPCEISCOOD-1065:** enhance shipping delivery status update ([47deab9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/47deab9c416225fa1cc3ad8f14d60c782e15e255))


### Performance Improvements

* replace multiple API calls with batch operations ([720c552](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/720c552d87bd0c4c9916d01c118393235cf7a3e3))

# [1.13.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.12.0...1.13.0) (2025-12-02)


### Bug Fixes

* **cood-1049:** comma separation Fibber Access ([420218a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/420218a128c23f75792376271d0871f0fb2a52fe))
* re-add BOOT-INF/classes prefix in wiremock mapping path ([4cccfd7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4cccfd7f55bfea90f3e9fd6ba99f17135555f913))


### Features

* **cood-1049:** add omg characterstics ([decdb94](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/decdb943c8435e154773c623abb7e50a1b85d17a))

# [1.12.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.11.0...1.12.0) (2025-11-13)


### Bug Fixes

* add stubs for service qualification management ([38e99d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/38e99d2717c2bfd60fc267d97a20f38e623f80f3))
* fix gitleaks issue ([f4e0c5a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f4e0c5a0002aa2c1ab0b916a65bf48e67bbe296f))
* **IPCEISCOOD-862:** update serviceCharacteristic to use full object ([e132d68](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/e132d68d1aa47ee9d5d87f24c4b3c9aa16fabbb1))
* **IPCEISCOOD-862:** use shared file for service order response ([6fed642](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6fed6421eda21ed9a4a3e25edceaac81dbfdf55d))
* **IPCEISCOOD-887:** add validity charachteristic to sms option ([d716a0f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/d716a0f6162ded2c7dda335195402062ba19b4e3))
* **IPCEISCOOD-945:** Incorrect error message when the SIM card specification is missing ([4877038](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4877038a5c736323932ccf2ce4a3ef8e615b7c30))
* **IPCEISCOOD-969:** Update Product Specification Mocks to Generate relatedResource.href Dynamically from Request URL ([3077b29](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3077b2991ff143c943d60ef569103f82480ecc18))
* remove values files as we moved them to gitlab-ci project ([7eee90b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7eee90b4318d47a394d65c6af1be3c9579675d63))
* restore BOOT-INF/classes prefix in wiremock mapping path ([b0812d5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b0812d5f24e8cb24b265493f984f39c5261c13f9))
* update relatedParty from object to array ([564bc8f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/564bc8fff118d5594ad39f818ada4801fab887ee))


### Features

* add appointment stubs ([910c4eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/910c4eb476c5db5d9eef019bfc94364f1620ed8c))
* add mock files for service qualification api ([fab1ccd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/fab1ccd8a9584bf699f0e79ba3905b46ce633de2))
* add Party Management (TMF632) Individual stub used in RBC project ([808940f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/808940fe951827feb5be1e928c99c702b48f494f))
* **IPCEISCOOD-952:** COOD performance limit request journal entreis ([ccc29cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ccc29cc631af92b395bece69724811e64a1dee95))
* **IPCEISCOOD-971:** support different delivery factories ([4041c4a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4041c4aa9b4b38be70b7f3e9a23931e400ef57e7))
* limit max request journal entries ([f327b07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f327b073f173c0c5c51be939684e087c2172728f))
* modify billing account response for RBC project with enhanced data ([2018b87](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/2018b87a303b4cbd697c92db8bd7ba0e06f3fadc))
* support address characteristic ([15276a9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/15276a9b2841bdb891dae884004508543efa0d24))

# [1.11.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.10.0...1.11.0) (2025-07-16)


### Bug Fixes

* update GitLab URL to gitlab.ow2.org in helm/README.md and regenerate files ([60eff31](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/60eff316f71441ba1025bda079f42c0b42cb5c93))


### Features

* upgrade stub priority ([e3df40d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/e3df40d76a286f775ac5e798c31ad2dab7f3966c))

# [1.10.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.9.0...1.10.0) (2025-06-04)


### Bug Fixes

* **IPCEISCOOD-859:** [Review] node  got held in cases Termination and modification ([9755d1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/9755d1f4e6a1d71ce84d81a4c96f2817d74d8b04))


### Features

* update documentation, regenerate CONTRIBUTING.md and adjust deployment configurations ([78cd0ff](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/78cd0ffb6e1fd5bdf7b5c31a4374d92e16a914e6))

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.8.0...1.9.0) (2025-06-03)


### Bug Fixes

* add dummy commit ([228aa90](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/228aa90ef4ba64c57bfc9fd30f3276e400e1c5fb))
* fix issue function mobile not defined ([389fbe7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/389fbe7f67f61f764a980688c508b5d6386d9a68))
* **opensource:** remove annotations from helm templates to resolve deployment issues ([b71267a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b71267ac80c6cc2fc9a239ef55f913cc0a9def7e))
* update the VOIP resource name ([625ccfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/625ccfcdb94e4ade08ad6d48ab2e73c1fe681176))


### Features

* **IPCEISCONF-291:** Add Connectivity FO in mock ([1f7c169](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/1f7c169e15537f7817ea4dc6389f7346bacbcbd5))
* **opensource:** add DCO.txt file ([6bcf6eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6bcf6eb7128b1ccbe2cc48960a0dd7c75d8def3d))
* **opensource:** move to Open Source Sofware ([0b75eb4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/0b75eb470ecd99865833dbbcf084c8ff7e8fe874))
* update mock files for reservation after review ([7d5f038](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7d5f0383e7cc1788b5094acdd36c35a70fc6433b))
* update mock files for reservation after review ([44d02d5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/44d02d5b73e36359f08cc734d8ff10d4a9d96834))
* update mock files for reservation after review ([b3bf684](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b3bf6840b148e4da0ed5f8e2e2c53011748ca5e7))
* update mock files to add characteristic value into resources ([ba3ab46](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ba3ab46ee3d5129246bcc3d126e9f2bf1b907e7b))
* update mock files to add characteristic value into resources ([deace5a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/deace5a25e69b69b07c6ff70ad0cb67cdd1da2f0))
* update resource characteristic values ([1f2b8e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/1f2b8e39ebcfa8152bbb8ca55b9d5720d528fbc9))
* update the characteristic value of resources ([fa0bf02](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/fa0bf02b4e5cb6671945702d1d7d3145591b0cad))

<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.7.0...1.8.0) (2025-03-27)


### Features

* **IPCEISCOOD-749:** [Review Env] Add smart contract use case on mock server ([3a9ecde](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3a9ecde792a554c59fcb28906524eb10d59e650e))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.6.2...1.7.0) (2025-03-05)


### Bug Fixes

* add dummy commit ([4b70b8b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4b70b8b4453b7ffd06590cd5dae051eab89f255b))


### Features

* adapt stock item reservation to support any new stock item ([3397ea0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3397ea0abeab4fc260d26fa6f1da2c71462cf842))

## [1.6.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.6.1...1.6.2) (2025-03-03)


### Bug Fixes

* add dummy commit ([c0a3a49](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c0a3a49d6621d14adea2a5590940c5c065e6d2a9))

## [1.6.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.6.0...1.6.1) (2025-02-10)


### Bug Fixes

* update mock file for migration use case ([e6f1f89](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/e6f1f893dd9836943d0c9fb37bea51a5fdb469ca))
* update mock file for migration use case ([3214c1b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3214c1bd465dc10928925abb52389f2b0da56b36))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.5.2...1.6.0) (2025-01-27)


### Bug Fixes

* add gitleaks fix files ([bbe4d7e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/bbe4d7e681089f1665e0193efede7b3e7e6e25f7))
* correct resource path from 71x to 80x in invalid resource status tangible product spec ([ee3da2b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ee3da2b6581c464e5bcdb7af5f9e8e37f7276936))
* **IPCEISCOOD-500:** Deserialization error is not handled in http calls ([a2e2b00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/a2e2b003d68ff6e1dc1cf6cde7f780eab92fb602))
* **IPCEISCOOD-686:** Integration Env Mobile Line node goes to Held state because of the service catalog has change the serviceSpecification id ([57705c2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/57705c25f5717d1f66af109084238486304590b8))
* **IPCEISCOOD-689:** Integration Relx offer nodes goes to held state because of missing service catalog specs ([36b10ce](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/36b10cecb18263473a1adf9920f24985b8c24dcc))
* **IPCEISCOOD-699:** Update Ring back tone product specification relationship for migrate action ([1f9127a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/1f9127a14f5048715190b20a64df37126264dd8f))
* **IPCEISCOOD-704:** Update Bruno collection for all core scenarios ([42b1ecf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/42b1ecfbbbaa44e0ed8426e24ee5f7475a617736))
* resolve GitLeaks issues by removing duplicate entries and redundant fingerprints ([1f80062](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/1f800629daae08272cf95c5d2df3f703ff916486))
* update mock file for migration use case ([e545f10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/e545f1026c1054fb2f091a1ea9320a089cd4ee29))
* update relatedParty.partyOrPartyRole type to 'PartyRef' in billingAccountByRelatedPartyId response ([80d2db2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/80d2db2ae562ee9c9fa658e243ea9267cf3bd957))


### Features

* add mock file for migration use case ([562f0fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/562f0fc93e71ee24a6117d9ac8dda5f898129ba6))
* add stubs for mocking Get billing account by related party id ([b1111a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b1111a153bf73eb5cd0050fe41a8be3bff576af5))
* add stubs for Pixel 8 stock inventory and resource inventory tangible products ([0427c6f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/0427c6fdd6fd9bb3d7ff4552505ce6814309a05f))
* **IPCEISCOOD-625:** Add new service specification characteristics ([2ce259d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/2ce259df761b46cd368f2badc64e6dd83879012f))
* **IPCEISCOOD-697:** status of node that has migrate action is held and it should be completed ([3b823bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3b823bbbe1b50b26d0987c5731a4a868a3ab77eb))

## [1.5.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.5.1...1.5.2) (2024-10-27)


### Bug Fixes

* use stable repo in docker image ([0d6b45c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/0d6b45c79809bec3fbddeeff86ef20b8d263baf9))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.5.0...1.5.1) (2024-10-14)


### Bug Fixes

* sync master and develop branch ([a0dd4d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/a0dd4d2a9f33e376a301a5c2a03762b794d8acb4))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.4.1...1.5.0) (2024-10-14)


### Bug Fixes

* add new data bundle service spec ([5bb4ec6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/5bb4ec624b9883ea46f4c1dd229022cb603b37a6))
* change HELM_PUBLISH_ON to prod ([df790b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/df790b96a7c296032f692f43e91d2814bf62fecd))
* **IPCEISCOOD-499:** Issue happened and unexpected behaviour in delivery and node state change ([06f0c1d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/06f0c1d3ccdd641055be4c643c7c49b1c5488d8c))
* move configurationPrice under productConfiguration in mock files for Product Configurator stubs (modification use case with add SMS) ([bebb493](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/bebb493d1832bf8a41befcd40d87b434ec7201c7))
* move innovation job from staging to production stage ([f5e5b8d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f5e5b8d357a74f91b63d2f5719b2a5ec1d46e157))
* update SMS price to 3 euros for modification use case ([6c6c393](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6c6c393d8ebd5d3e136bcad3581eeeffdd456f31))


### Features

* add necessary files to support accessories stock reservation ([6972f02](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6972f02e0d50674ea92ef3357a0cda75bacf1a67))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.4.0...1.4.1) (2024-10-01)


### Bug Fixes

* use common pipeline ([16ddfcc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/16ddfcc972719a8db5972c0acfdb598a3773e1fe))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.3.0...1.4.0) (2024-09-30)


### Bug Fixes

* adapt product offering object for modification use case files ([6e75820](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6e75820758f6d4d073e261090ec47c3d924c616f))
* adapt product offering object for modification use case files ([70b2309](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/70b23099e183b6ad1c29667b547b1669e46b3d9f))
* adapt product offering object for modification use case files ([08d0009](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/08d0009974b089b314c51638defac569eb329a23))
* adapt product offering object for modification use case files ([351f155](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/351f1558fcf23841eb02472843bc387fbb35c2cc))
* add missing stub for fetching valid accessory products in resource inventory ([1e5feb1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/1e5feb1b4e07c0d81b94d26723841ff76306e255))
* add missing stub for retrieving valid tangible product for Samsung S24 in resource-inventory-tangible-products.stubs ([fe06ef5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/fe06ef554b1ead30d1b0f6d31b1952db822d91bb))
* add new data bundle service spec ([7db05cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7db05cceb0802b7168545f9a3cc7eed4c650f8ff))
* change docker image config ([32d6c4a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/32d6c4a15593cfebe75a61d21e08ac9c26094379))
* change HELM_PUBLISH_ON to prod ([6f9f4bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6f9f4bb3455b3e0808d0d0f3a5f76739704afa1f))
* change route urls ([f4d1138](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f4d11380bf7d18e12a369af3e47dd7626f77d2cd))
* correct JSON response for unqualified offer in Product Configurator ([b79a59f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b79a59f0a3a882c968757254615fe5f80fcff1e5))
* correct URL pattern for unqualified offer acquisition ([3bfbf05](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3bfbf05ec2db154a3bc7616a66e13e6fdf9f9a80))
* **IPCEISCOOD-463:** Modify Exceptions in Create Orchestration Plan - message& reason& code are not defined ([9ae0c1a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/9ae0c1adcbe7bdb3be41baa150e5a3ee23594ea3)), closes [#IPCEISCOOD-463](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/issues/IPCEISCOOD-463)
* move innovation job from staging to production stage ([d984bb6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/d984bb6d7d9ece0b6bb33a578e1a5f423ee1b801))
* replace `role` with `relationshipType` for product configuration item relationships in Product Configurator responses ([043b9fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/043b9fcb7dfa46019fdc672fb65919eb87d473f7))
* resolve issues in mock file responses for modification use cases in Product Configurator ([343361b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/343361b6b4cea70c2f9023042f8a3a3da0064a90))
* update ID query parameter in ConfigurationAcquisitionMaxPlusHandset.json for Product Configurator ([f495be7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f495be71200a2392a0beafc48418355f494f380f))
* update ID query parameter in ConfModificationAddHandset_AddSMS_AddRing.json ([177acd3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/177acd371dc8a7a34f368f8d2f849bec495e702f))


### Features

* adapt mock file responses for modification and termination use cases in Product Configurator ([23338e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/23338e3d0929fecfe1282e39ccbd49ba79e8c400))
* adapt modification json files to Configurator api ([46c7287](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/46c72875d719ed94b9abd53cb353df739807066a))
* adapt modification json files to Configurator api ([262723a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/262723aea0201e40eff62d91b8619ef884c71c9d))
* adapt modification json files to Configurator api ([6697159](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6697159afaf5d4194e9f82119461955d853bce33))
* adapt modification json files to Configurator api ([efa2386](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/efa2386721bae736f2d9acd4c11a891158ffe7f7))
* add databundle charactaristics ([001e273](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/001e27333bf557b678d456cb4ead73c666a5f897))
* add files to support modification use case for comfort offer ([74e5dd9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/74e5dd9ef412dd78e8a88af2a0f0da2bbb5f036c))
* add files to support modification use case for comfort offer ([8258153](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/82581535571a83d2e43306721c92bd3e594ea25d))
* add files to support modification use case for comfort offer ([b0bae11](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b0bae1165813a876b638ca1c037cf5bb5c44672e))
* add files to support modification use case for comfort offer ([c399d11](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c399d11c1a41f82e81c3f0bbcf33597d041c51d6))
* add mock configuration for unqualified offer in Product Configurator ([3b449db](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3b449dbc0e68bf8211a35835ce8f3127ba6f0f66))
* add mock configuration for unqualified offer in Product Configurator ([ff63d89](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ff63d894463f09da7702657d49f08d0970d221ce))
* add necessary files to support accessories stock reservation ([4925471](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4925471bed9127c882c77b055ca2ef02ed29156c))
* add stub for billing account retrieval in account management API ([b41f07c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b41f07cb0579d1cdaa020cf47c5ae626c74d716d))
* add stubs for Samsung S24 product stock management ([c4ee47d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c4ee47daf3b1a04355574c00dbe23c0f901addda))
* Modify "Create Orchestration plan" to be idempotent ([7b09e59](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7b09e5919d926cd27efd07490816032e0a211d79)), closes [#IPCEISCOOD-406](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/issues/IPCEISCOOD-406)
* Modify HTTP Exception details for transient exceptions ([c7c7499](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c7c7499e50c3d4b648743ee15057a2522da0c251)), closes [#IPCEISCOOD-405](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/issues/IPCEISCOOD-405)
* update mobile package config for acquisition use case with new recurring price type and charge period ([02cf886](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/02cf886184209ed799267f597fc77138f10a6329))

## [1.3.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.3.3...1.3.4) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([02d7abe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/02d7abe5c7408e0e70125464b117285ec7000be1))

## [1.3.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.3.2...1.3.3) (2024-06-12)


### Bug Fixes

* change docker image config ([0619f56](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/0619f562fca5aa4798c85894f3c67c549a54bd4b))
* change HELM_PUBLISH_ON to prod ([31de57d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/31de57d4b57df97ced5fc0c864e4a86b5cdd77b8))

## [1.3.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.3.1...1.3.2) (2024-06-10)


### Bug Fixes

* change route urls ([f4d1138](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f4d11380bf7d18e12a369af3e47dd7626f77d2cd))

## [1.3.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.3.0...1.3.1) (2024-06-10)


### Bug Fixes

* update ID query parameter in ConfigurationAcquisitionMaxPlusHandset.json for Product Configurator ([f495be7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f495be71200a2392a0beafc48418355f494f380f))
* update ID query parameter in ConfModificationAddHandset_AddSMS_AddRing.json ([177acd3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/177acd371dc8a7a34f368f8d2f849bec495e702f))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.2.1...1.3.0) (2024-06-07)


### Bug Fixes

* add review remarks ([20aa034](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/20aa0346c6ddead968cb455e0b948c2cd5f01fce))
* adjust Post reserve Handset Stock stub to use regex matching at start of stock item IDs ([dbd84c6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/dbd84c6257b840e303472399daefddef6b6196fb))
* correct [@type](https://gitlab.tech.orange/type) and [@referred](https://gitlab.tech.orange/referred)Type in Product Configurator stub for Max and Max Plus offers ([085542f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/085542fc99c2c9de7ba0de7637d37daae0363fbb))
* correct missing bundle relationships and update handset device data in mock files for Product Configurator ([f46110c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f46110c09cac063903fedf7411c44d4b8d380658))
* correct placement of configurationPrice in ConfigurationAcquisitionMaxPlusHandset JSON response ([c76d620](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c76d620e56bcdf99ae0a168a97e40c0dc03c4890))
* correct the [@type](https://gitlab.tech.orange/type) of all productOffering for the acquisition use cases ([228d2ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/228d2ee89f194a6cf082ad83ca1cb4df34ed9042))
* correct the [@type](https://gitlab.tech.orange/type) of all productOffering for the modification and termination use cases ([3c95ac5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3c95ac5683f226360c591faf2191b8444e22b043))
* fix missing reliesOn relationship in PS level by updating mock files in Product Configurator ([0d06a23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/0d06a235cc338719ba945e2d91fd3a4b3fa1f37a))
* fix mock file responses for Product Configurator ([a7dcb76](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/a7dcb7696c6f6026468390b90927d6511ab400af))
* remove extra comma in JSON response for Product Configurator mock file for 'Acquisition Max Plus with Ring and Handset' ([68b99b7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/68b99b75863224e91f1bf5011016e65ff127853b))
* resolve issue in JSON responses for product configurator mock files ([6e1054c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6e1054cc2d70a32e3584e3f4618c78ad6819c2d3))
* update JSON responses for Product Configurator modification and termination use cases ([18b42d5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/18b42d514285a27fca2a02855d124634cbec0cba))
* update memory value format in Product Configurator stub responses for Handset ([08e4794](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/08e47949486971b6fcc9dbe9c31aade76c32b12a))
* update mock file responses for Product Configurator in acquisition use case ([25fcd3f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/25fcd3f62cd8a2a4fc8a7e324c6f6677810507a2))
* update productOffering name for handset device casing in product configurator mock files ([e1ecd92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/e1ecd922376c2ae26aba7b2d8c812cc7711687b4))
* update shipping characteristic name to "Requested delivery date" in mock responses for product configurator ([cd98228](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/cd98228ad17d07ee861ddd14cef86dcd12a95783))


### Features

* add get payment by id stubs and responses ([46c335f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/46c335f691cc43042d9450470d93078b1d753c98))
* add mock file for Basic FO Mobile Package in Get Product Configurator stubs for acquisition use case ([077c42b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/077c42bf1a79fdc87d56cdbf0b53e892b4721866))
* add mock files for termination use case in mobile max and basic ([ace41dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ace41dde32ec56e392c697cfd185e3f9ffec80b3))
* add necessary files to support acquisition's case for accessory ([7be211e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7be211e993a288dcf5bed2cabd295cc736352e76))
* refactor configuration files after review ([62a6f8b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/62a6f8bcd35e583fcf6cb016c1ba4aef28388164))
* rename "Shipment" to "Shipping" in mock responses for Product Configurator ([ebd6b76](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ebd6b766038995197b04219547eda05cc8ef6d0f))
* update PO and PS IDs for Samsung galaxy S10 reconditioned offer ([7f1bd71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/7f1bd714ad3d023988b60296a09f20e0200064e4))

## [1.2.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.2.0...1.2.1) (2024-04-29)


### Bug Fixes

* add job to deploy mock-server on innovation environment ([c5a7d0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c5a7d0c7217fd6decda3388c774495644c06056a))
* removed semantic image and move it to gitlab variables ([0d4efb4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/0d4efb46cd9ad236729a988f98afbdf6cb8596b5))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.1.3...1.2.0) (2024-04-07)


### Bug Fixes

* add changed service catalog names ([5579d33](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/5579d333c33fd184b6538cddb425d19324a4221d))
* **devops:** Remove `develop` branch from `.releaserc` ([6a0e899](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6a0e89941be6f269dd1684e043049aa4158595e5))
* empty variable expansion issue ([78bfa97](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/78bfa97d4241a9e753e6da800897e5fb6f4efe5d))
* fix mocks of multiple som ([812a4c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/812a4c7e2c0a2b5edfb53486ed50646db2b22f4b))
* fix mocks of multiple som ([5c4533b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/5c4533b9553aa3b05fda65e5a7ee06b2642af4fc))
* rename charactaristic name of sms option ([4426957](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/4426957037495a1e972a7e2f363c784b43be1700))
* Restore product configuration files ([c451a23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c451a2370619ecb7feb5c3be58daa3852f0780f6))


### Features

* add  termination configuration ([ad89fbd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/ad89fbd5e3b1fb08f2780cec019e93744da98701))
* add configuration for Pro max package ([2fd775a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/2fd775ade34b0d9368ffae49d22fd527a0e5ac38))
* add new mock files for Max Plus offer scenarios ([b38ac4d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b38ac4d848684f4b48f240d504f44adce8890a97))
* add new modify file with different charactaristics value ([2c7f2e1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/2c7f2e1bb9096c8502159157c57a411d383e6ec7))
* Add party role management stubs and update "isInstallable" flag ([01617d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/01617d8c38365bd5d45cecce2574839005ffdec0))
* add user role get api ([f4c710c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f4c710cc0fa210df87f680fd8b128acdd6bba007))
* **IPCEISCOOD-265:** Handle Termination Use Case on Integration Env ([970c18c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/970c18c646ebcc721900776aea1575cc5d423265))
* **IPCEISCOOD-265:** Handle Termination Use Case on Integration Env ([38cc29d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/38cc29df024694520664e158c807781096c5fde9))
* Update acquisition configurations for Max Plus offer ([f5f38b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f5f38b0101be691c5be7aaf7ed1b6f17e2c22ce0))
* Update acquisition configurations for Max Plus offer to support item prices ([352c9b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/352c9b90a7261b89c14c93558e55b4bf75cb3972))
* Update engagedParty ID pattern for new user ([394c392](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/394c3929208b3314e3d038a6ecc4bbe3a61c747f))
* Update getPartyRoles response for non-customer and non-prospect roles ([edaa6ae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/edaa6ae165c042a4f6ab381a2a17291a46336a6c))
* update Max Plus offer mocks for termination use case ([38fd59c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/38fd59c037dbcc44f7fc7b41cb91253d89c18214))
* update reservation configuration ([3b64e70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3b64e70e76861812f7f1cfd09a71c4af4d0fabb2))
* Update termination configurations for Max Plus offer ([2c93d52](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/2c93d52ac57a21c59c2e9ac1f4448dbb879743b3))
* Update termination configurations for Max Plus offer ([fe68c4f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/fe68c4f8538fec4808ea5423d0dce75b14ee5573))

## [1.1.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.1.2...1.1.3) (2024-1-16)


### Bug Fixes

* Update configuration response for acquisition ([68b448d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/68b448d4fcb41acaabd7a709395ebe080e60da92))

## [1.1.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.1.1...1.1.2) (2024-1-10)


### Bug Fixes

* Merge (remove poi prefix, rename tone charactaristic id in catalog) into 'develop' ([a57c0cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/a57c0cc6957c9878aeed5abb2878f5a2abb64747))

## [1.1.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.1.0...1.1.1) (2024-1-4)


### Bug Fixes

* change charactaristics type to be string ([2ee2550](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/2ee25502709305680e6a99b98265dd4f58af5b19))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/compare/1.0.1...1.1.0) (2024-1-3)


### Bug Fixes

* change [@type](https://gitlab.tech.orange/type) to be ProductRef in cpib ([9ab59d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/9ab59d8d876ea0bd273d603f6d7aaa0fdac5cb14))
* change om url to match original service ([1405fa7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/1405fa7652b00a6edacaf045228f8cd482c4fbe4))
* fix conflict in mapping multi ids and single id ([9440dca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/9440dca96273f7b0dd53b8fe639158a95410b576))
* modify held scenario product ids ([f77fea2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f77fea28acf0e62a223f4dc9cca38668bf2d932f))
* Update 'gitlab-ci.yml' to use the latest version of Node ([b6f967d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/b6f967d169768b976f60eddfb0031b227d12c5e8))
* update mapping of cpib for cood scenarios to retrieve related nodes level1 (mobile line and connectivity) ([f64fe05](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/f64fe056378c3d151d7aff8a8e5654799ccf2651))


### Features

* Add horizontal reliesOn relationship for acquisition use case ([e7cc2e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/e7cc2e447e58461fb97c7af295f40dc5d2fb7bc3))
* add product configuration ([6553cdc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/6553cdc6ba3920da6657a03405555c04fc397eb7))
* add ringbacktone and sms option scenarios mocks ([3a8f5de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3a8f5dea284419a7d87875e648ac5ba2a4e2c9eb))
* Add SMS Option stub to get product spec ([3fa1e10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/3fa1e1096410314e00b295c7436db046e65b1080))
* Include 'productOfferingPrice' in ConfigurationItemAcquisition response ([31ef575](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/31ef5758b93d48ee953d3f184b7934d54f630e24))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([8d31f93](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/8d31f933f4027929b84f627b79d17fecc743d4d3))
* **IPCEISCOOD-132:** adding and update mocks related to activeDelete,activeModify,activeHeld flows ([898d535](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/898d5359c5b2b7cb84f6015f132b6c2fc3342e12))
* **IPCEISCOOD-132:** update .gitlab-ci.yml ([c523289](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/c523289583b424fcdf4f9956d2c12a02f59f9c0f))
* update create product response ([8ec6a01](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/8ec6a01bfa1898af94e2ab14dcd4dc25df64dd65))
* Update Product Configuration JSON Responses in GET Requests ([cea86fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/cea86faacb5092b1f8ebbd1b4002b88d05d8045b))
* Update Product Configuration stubs ([213075f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/213075f3f75193161e39db5a4962dec893e38cd4))
* Update Product Management stubs ([afbaf2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/mock-server/commit/afbaf2dcaf7cb036e2e271e187e95788880f58fc))

# 1.0.0 (2023-10-29)


### Bug Fixes

* change om url to match original service ([1405fa7](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/1405fa7652b00a6edacaf045228f8cd482c4fbe4))
* fix inventory resources stubs ([ee25c49](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/ee25c49fcf1745312c5c3dafc560772d532b38b4))
* modify held scenario product ids ([f77fea2](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/f77fea28acf0e62a223f4dc9cca38668bf2d932f))
* Update 'gitlab-ci.yml' to use the latest version of Node ([b6f967d](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/b6f967d169768b976f60eddfb0031b227d12c5e8))


### Features

* add check product availability APIs ([c9d760c](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/c9d760cbe43eba461399e8f3022b03785e292a7c))
* add GET and PATCH for CBIP API ([09c67a4](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/09c67a4762475a630c8329a89286be1f8d602b50))
* add GET and PATCH for CBIP API ([d8ad535](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/d8ad53540b0dbdde14efafc1f836fdaff68b8290))
* Add GET products and PATCH for CPIB API ([8869225](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/88692250e4b0dbf52def23d8a0d280540c1f7021))
* add Product cancellation postman ressource ([558e6c7](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/558e6c72993b716921e99d081ca73ed76512feff))
* add Product cancellation postman ressource ([028813c](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/028813c385c4d21ff71cccb2758784331ced5d6d))
* add Resource Inventory Stubs ([a44841f](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/a44841ff965669238f4371b8fc18bf10f55b16ec))
* Mock Get configuration items API ([6779e31](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/6779e31ef38335f44fa5ad5852b1533f5ea9ac1d))
* Mock Get configuration items API ([d39efed](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/d39efedca7d2244d263eea02df7153d5c97d2d38))
* Mock Get configuration items API ([0b86a30](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/0b86a3007a7010683b0fae5a64da238faf21120f))
* mock product specification API and refactor code ([a17a656](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/a17a656ca4073019e1f6d10853c0476e76aabf9e))
* remove unnecessary files ([651a6ac](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/651a6ac70bb2e26332d0158fa35f70844117134f))
* Update Product Management stubs ([afbaf2d](https://gitlab.tech.orange/disco/disco-order-management/mock-server/commit/afbaf2dcaf7cb036e2e271e187e95788880f58fc))
