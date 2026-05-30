## [1.8.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.8.1...1.8.2) (2026-03-02)


### Bug Fixes

* correct typo in instantInsyncQualification field ([3f5c3be](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/3f5c3be43757baf9edba62be13d93639cee5de9f))

## [1.8.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.8.0...1.8.1) (2026-02-26)


### Bug Fixes

* add type field to StockItemCharacteristic ([a5244da](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/a5244da7f36d093d9bbe105fa9690226773bdd1d))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.7.0...1.8.0) (2026-02-26)


### Features

* add new dtos for the party management api ([353687b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/353687bcc1fc6293d1d402ed67ab9e25e1c1c5cf))
* add the class TimePeriod for party management ([dcfb6d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/dcfb6d872ed91f5613259ad6831c7043b2efe888))
* add the missing field to support the feature installment ([d72ebc1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/d72ebc1b90bc976febeb0e0942343f248f449e54))
* eliminate duplicate classes ([8e29272](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/8e292720c9d2f749ec1e661cf4ffeb1875a42560))
* rename package name ([062035c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/062035c363bda2e66653cd772fb02b47fe0a408f))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.6.0...1.7.0) (2025-11-11)


### Bug Fixes

* ignore null properties in UserRole and Entitlement ([3dbf860](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/3dbf8603106f0da3b535f0496691d0f04a4c3499))


### Features

* **gitleaks:** use latest gitleaks image and template in .gitlab-ci.yml ([fa40a8f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/fa40a8f8a0cfd8d235d03378efe34141c1a74ae6))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.5.0...1.6.0) (2025-10-13)


### Bug Fixes

* change relatedParty from object to list ([1067b7b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/1067b7bd6e3b8b51594371e87e2a131863542f9e))


### Features

* add new fields to ProductOfferingPriceRef in product configuration ([607d18c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/607d18c777fd8386da3b58f62334e003f6ffcfe6))
* add product characteristic dtos for  product configuration ([cf36437](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/cf36437f5af79f01c0c06ffc035a03b044a8881a))
* add relationshipType and validFor fields to ProductOfferingPriceRelationship model ([789f62f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/789f62f5183ed0256bd07a2c0807228534c46501))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.4.0...1.5.0) (2025-09-17)


### Bug Fixes

* add exceptions in.gitleaksignore when OW2 gitleaks settings are used ([837dc68](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/837dc68522dad7e2583d4262b530f7c15d102534))


### Features

* update appointment dto ([8397f1b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/8397f1b7e1815b9435c612f2a65bb84f5e8aa57d))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.4.0...1.5.0) (2025-07-24)


### Features

* update appointment dto ([8397f1b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/8397f1b7e1815b9435c612f2a65bb84f5e8aa57d))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.3.0...1.4.0) (2025-07-04)


### Bug Fixes

* update GitLab URL to gitlab.ow2.org in helm/README.md and regenerate files ([fdd035d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/fdd035d014d0fa6458551208f717c8a3f0f50d55))


### Features

* **opensource:** move to open soure (apply requirements and changes for ow2) ([b4ed0f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/b4ed0f4cd7ade98eef4609a55ebdf8485179d114))

<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.2.0...1.3.0) (2025-06-03)


### Bug Fixes

* adjust Gitleaks configuration to reduce [secure] positives ([60b7d6f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/60b7d6f9ee6851f1118ceb2d65acac325aa89b4d))
* **opensource:** remove annotations from helm templates to resolve deployment issues ([6b41237](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/6b412376fc88b7bedaa11453323cb0b552b642fc))
* prevent deserialization errors in StockItemType ([3b307b2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/3b307b2fcdac2ec9cc433a5f5cf0f61e82902a9d))


### Features

* add value field for Resource class ([c53f9d5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/c53f9d56bb29709d238e630327e2bd7692b67f39))
* **opensource:** add DCO.txt file ([7a0bcd9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/7a0bcd9bc21b9a5fd03f2e971807a6377b6184d9))
* **opensource:** move to Open Source Sofware ([02bf2c6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/02bf2c63e9d4c10529e49519c5830ba895dadc08))
* update applicationDuration in PriceAlteration class from Integer to Quantity for data consistency ([cde2efe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/cde2efec3304b8f42d93307229f67d0605c0d78d))
* update artifact name ([2e8018f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/2e8018f0ce3df89c58123e6fa3b3854b49b0204f))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.1.0...1.2.0) (2025-01-28)


### Bug Fixes

* handle "aborted" delivery status same as "failed" ([c409279](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/c409279b829be122588725ff58ac0fb90a30e1b8))
* make value and name optional in characteristic object in order inventory spec YAML ([1e52bd9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/1e52bd92fc7c6fa1d26c405bf154a2a64a74fd05))
* resolve JSON deserialization issue in QueryProductConfiguration DTOs ([39844e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/39844e64ba743de9cd362906ec411918fe846285))


### Features

* add ValidityCharacteristic DTO to order inventory spec ([d73338a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/d73338a4af66adaf4c58ffd38132e39276d4f745))
* update BillingAccount with BillStructure and modify command.yml with RequestedCompletionDateValueChangeCommand ([c717c67](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/c717c67c782a12b5e7f7aa383b016dc5441e07ad))
* update poi spec to support migration use case ([6c756ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/6c756ab1183faf7a86d889dbaae08d2b3679043a))
* upgrade TMF622 Product Ordering Management to v5 ([f30f57b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/f30f57b778194ac2fa3c472bea65ca6ec6bfc756))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/compare/1.0.0...1.1.0) (2024-10-14)


### Features

* add channel as filter field ([d2f3a09](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/d2f3a090ecf14fc06a042a60aadf371892a56894))

# 1.0.0 (2024-09-04)


### Bug Fixes

* Resolve conflict in ProductOperationalStatusTypeEnum enum ([28da16b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/28da16b974ad4b1e9b34df3e8cc67e2cf87c1fb6))
* Resolve naming issue ([dbe0107](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/dbe0107fc8583f9ad5022d0df15d00984c72880e))
* Update 'gitlab-ci.yml' to use the latest version of Node ([7e517b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/7e517b662d014451710745f44c421ad46208436f))


### Features

* add 'configurable' field to ConfigurationCharacteristic DTO ([6d82268](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/6d822689fe8982ed58b0530b4ad47f0ca397e596))
* Add 'ProductOfferingPrice' dto for Product Configuration and 'immediatePayment' for 'ProductOfferingPriceRef' dto ([ed72e0d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/ed72e0df05336a2b92ee2cf798a49bc3d65cae2e))
* Add 'ProductPatchRequestDTO' DTO for CPIB PATCH Request ([38b7a2c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/38b7a2cccc14fa1e4f74c243283c4c383e5235f8))
* Add 'sort' parameter to POI spec ([eeca671](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/eeca6719fc101e3a250a274afcd7751710f0141c))
* add @Getter and @Setter annotations to ConfigurationCharacteristic ([acefe19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/acefe19ff635c035c9760d1cedbc39b67d255bdc))
* add async api ([4fec293](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/4fec2933ece06fa8b1255a62ead3a7b1aa4070ff))
* add Authorization DTO ([dcd1dcb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/dcd1dcb1cad0137c1a8365f61d6780043f922185))
* add billing account DTO for account management API ([79e4ce3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/79e4ce39dae7c179e6f8486f265513edf49a536e))
* add dto ([e8acc1a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/e8acc1ad110942a81cf96212f2e78d256072021d))
* add dto ([c76a820](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/c76a82044f9fd9401c5e0ed5f3fa48c27d9f7b20))
* add dto ([5be8d07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/5be8d0778dfb989de564417832ec374d15e204f8))
* add dto ([7b4d1c1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/7b4d1c1655339c6353f5a63559b32571b33851e6))
* add dto ([c3d9d0a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/c3d9d0a6de557a62e3678d34a0a5649e8483225d))
* add dto ([d09bbb5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/d09bbb511a0bd75609b844893860f3fd30e9331d))
* add dto for cood ([9070f50](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/9070f50c25775a76190ebfe20fb033b60636d158))
* add dtos ([02e7062](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/02e70620b2ab9cf3049a8bc3f1f1828fee618e2d))
* Add DTOs for consuming COOD event and publishing order item state change event ([b01e7a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/b01e7a189fd514e58625c40428392de030224f5f))
* add enum converter ([298cad4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/298cad453cac61e3398b428d81ebbf6ba7bc9c09))
* add id filter to get all product orders ([36e8778](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/36e87780d11bfc4400ed3fd0453298d61cb3d01d))
* add new TMF Product Configurator DTOs and replace old ones ([a849525](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/a849525041d98eeca68ce4680fb20b8d96430467))
* Add om-product-order-inventory-spec module ([6d1cb01](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/6d1cb01a96f23b1122744a391a2f0b4d0e24b9f9))
* Add order date filter parameters to POI spec ([751df9e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/751df9ea79b0f7c8ce9d69c30ade094e6f1b1d28))
* Add PartyRole dto ([d753e5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/d753e5ce261de1652368c910685ac6dc1ab839c9))
* Add Patch dto ([acbfaa8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/acbfaa86ea3e88e1db92a0dedde03f480cf4ec2d))
* add payment pojo to support get payment by id ([0d9af50](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/0d9af500ece466d12c0cca06e31549be0d8510ca))
* add payment pojo to support get payment by id ([644fcc7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/644fcc7cf644d909d518b042a1fac154fbdb9fae))
* add ProductOfferingPrice DTO along with associated sub-DTOs ([62a1e8e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/62a1e8ee733949e06c188869a83e5276b402fa2b))
* add ProductRefValueChangeCommand enum to Command EventType ([010584f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/010584f92127602492052c133177c0dfedd158b6))
* add setting DTO ([2bcb23e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/2bcb23e18f5b0ad0734ea28696453622cd3ab84a))
* Evolve ProductOrder data model by adding 'ProductRelationship' ([b14b593](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/b14b59307c247f863a80399df9c39b0cfb825d63))
* first commit ([c29174e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/c29174e0e211db95996c3ffb3c3b73387ab1b801))
* first commit ([2d9e691](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/2d9e691441483e37f159a0e1956966fc0e0a8bfe))
* first commit ([88306a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/88306a4d4dce94e0e9a881e13040dab0bbbcfa64))
* Modify Swagger to generate dates with Instant instead of LocalDateTime ([6988285](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/6988285b347729ddf7582116444c17d8e8c017b5))
* remove 'role' field from ProductConfigurationItemRelationship DTO ([aed528a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/aed528a2b282d35c2c2c362b455f24625c6cc909))
* remove ProductOrderTotalPriceChangeCommand from EventType enum ([a0719cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/a0719cb105175eeaa64590164004385fd7aed71b))
* Update 'OrchestrationPlanNode' dto ([fab4517](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/fab45170cc928c1f0a38adaf62125ee7540dc543))
* Update CommandType ([f670f32](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/f670f327f2fa02cd66a0318ed12aea36b7dceb88))
* Update commons service version ([b2f534b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/b2f534b300fcdce250600ecdfc730c2b6294e1a3))
* update config dto ([95197a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/95197a26325c212c09abc0ff055162b14b4717ab))
* update dtos ([12e8354](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/12e8354e45710aaf8c2724444ec33233ecc25ae4))
* update dtos ([9e5b254](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/9e5b254c7ce07b83afc79b28a3611d4e48315764))
* update DTOs ([4691c3e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/4691c3eec92cffc47eb224d624d88fb4a4198611))
* update isSellable field in ProductConfigurationSpec DTO ([f926d06](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/f926d06433f7d401aebd28d2b9bc980de436ad6c))
* update om-poi-spec and ProductSpecification DTO for stock item retrieval ([d8ea1bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/d8ea1bb9a66d16ea398f46d422f043303b8c8219))
* Update order orchestration event type ([703bada](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/703badab2b3b12b2bd387c3ca6d389a232799150))
* update order-inventory spec to 1.0.4-SNAPSHOT, change recurringChargePeriod type to Quantity ([b1f7931](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/b1f793143cc88f92bb4d073522cfe0fa375bffb7))
* update POI spec to tagged version 1.0.3 ([7632347](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/76323478c91710cea7be301aaf9d97d71391d10c))
* Update POI spec version ([b7a4107](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/b7a41074d2468f68e523479e02b6bd7cdfd5780d))
* update recurringChargePeriod in productOrder.yml to use $ref for Quantity ([f60a642](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/f60a64230a64f44f7f9dd57d4fc4cf5f889c0cd8))
* Upgrade commons to SpringBoot 3.1 and Java 17 ([bcbea5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/bcbea5d57e15de813360cf6657b9bd49c0834d77))
* Upgrade commons to SpringBoot 3.1 and Java 17 ([90e9aa4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/90e9aa42dfa6fea98f6c76472504ca3e688a8f6c))


### Reverts

* undo recurringChargePeriod refactor in ConfigurationPrice DTO ([2d22404](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-commons/commit/2d2240486331daa0a45eb2cfc32f45e0c75ea114))
