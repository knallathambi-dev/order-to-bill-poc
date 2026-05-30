# [1.14.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.13.2...1.14.0) (2026-04-23)


### Bug Fixes

* add missing mongo variable that we use it with default value ([1e9bcc2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/1e9bcc25bc38256d23e225408db5c923afe0a5a6))
* **deps:** upgrade orchestration-delivery-asyncapi-spec to 1.5.0-SNAPSHOT ([7ed714f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/7ed714fa88789fc7e999205c6e8c6caf933464d7))
* display all OpenAPI schemas in Swagger UI ([c612f14](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/c612f1490b52037d781ef168e1aedb15c60031bb))
* enhance product order state processing for bundle items without children ([8933a16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/8933a16467b17930250c5aefc8e19e24ab8eab27))
* fix gitleaks issue as it is [secure] positive ([d4edf52](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/d4edf52114609b48b5089230a61219be57d036fe))
* fix spotbugs ([ff7af68](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/ff7af68b82b8b4ba4408a01443ec6d0a169b0d1b))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([7b862f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/7b862f44615e697a3d33531645c8c9b6204e1ef3))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([7e1ad23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/7e1ad233948deca8f67796df46feea6d8405c8a0))
* remove deprecated sonar.jacoco.reportPaths property ([f879612](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/f87961226fa7aa946c928031c8d9d6fb3bc625eb))


### Features

* add [secure]-ui & selfcare-ui env vars ([73f7ef2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/73f7ef2effbe91df6157d500f3b044f9110354c9))
* add [secure]-ui & selfcare-ui env vars ([f9b20be](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/f9b20bee82cf24a9be384cdf764150bb8f123812))
* add allowed cors origins ([6564541](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/6564541cf114d1d22d1770edf41374bcef770663))
* add allowed cors origins ([d34b308](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/d34b3089cd83bb32fb7cf8b0e220e216d2077c03))
* add csp header for each request ([c293a61](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/c293a61795ddd3a591ad3d8a21d261e9139bfb4d))
* add csp header for each request ([bba3fd7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/bba3fd734545baa4e13ac869117eb6abfe9449e3))
* add DateCharacteristicEntity with mapper support ([3f47fdf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/3f47fdfd1fe4593b9ef4982371b5188fabff8721))
* add microfront URIs ([f5605bc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/f5605bcc66b47df01cf063cf50a60d7a39c1b2f2))
* add requires value to RelationshipType enum ([05a7c56](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/05a7c569c845ca400ba1451e7af305d2dc7241db))
* enhance product offering price entity ([6a5515b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/6a5515b23294c63039c39dbc58364944baae398c))
* enhance product offering price entity ([ccd4da3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/ccd4da3c4dcc13d3363788ecdbec8aa7a7b5b687))
* enhance product order service to update the requested completion date ([6af9101](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/6af910163e97ecd5e6683ad300b69d4a74044ba5))

## [1.13.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.13.1...1.13.2) (2025-12-11)


### Bug Fixes

* add missing [@type](https://gitlab.tech.orange/type) attribute to ProductOrderAttributeValueChange event ([4cde708](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4cde70813c9814d21273d17e634a99d1b27f68c1))

## [1.13.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.13.0...1.13.1) (2025-11-14)


### Bug Fixes

* correct product order sorting ([4822463](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4822463456368e9b322c3fb383f1f58efab5a672))

# [1.13.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.12.0...1.13.0) (2025-11-13)


### Bug Fixes

* add [secure] positives to gitleaksignore ([1c197e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/1c197e740ee7962fabc0259234dc55ae26edcc43))
* apply proper naming ([609653a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/609653a2d4c7f35237731923a96209e341da2ae7))
* correct in progress state in migration use case ([e44f86c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/e44f86cf289f29915570615e9f10167f470ced37))
* correctly update order state to in-progress when item states include accepted alongside completed, failed, or partial ([c604e5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/c604e5b1e4de4b31339f9d54ad71d65fe1eb9f3d))
* enhance code to support migration use case ([09b0a1a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/09b0a1a0a4f0416cfdcf47918b1dca0a3788ca62))
* fix gitleaks issue ([7d5175c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/7d5175ce45ab917e5bdae379df43e2a523036910))
* increase metaspace memory to fix issue OutOfMemoryError Metaspace ([a3e3b45](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/a3e3b45ad8610a242cce022732975a6fb9ad276e))
* remove configs for promtail and istio ([de8307c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/de8307cfcf426658cde18c164ff867659b6cd1dc))
* remove unnecessary public modifiers from test methods ([dc427de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/dc427de3a3f0160272b8232ff49ab552aae3bb67))
* remove values file as we moved them to gitlab-ci ([efeb90c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/efeb90c5895cbf5c179c6c8e5c7210176f520ffb))
* resolve UnknownHostException for auth-userrole service ([5e02b19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/5e02b19a484a66b60d8ee7feb163338bf6589cef))
* update swagger description and version ([93c189d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/93c189d60f71447e25ef917c5dc55ec94a60d030))


### Features

* add address characteristic entity ([4de8f72](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4de8f720e9b46e79f11a7e98d828b93dc5cba211))
* add index configuration setup ([4a3e2e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4a3e2e82f572498ac75c20ae94067fc2bd6eaed1))
* add original price support to product order entity model with productOfferingPrice reference ([2158bfb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/2158bfb3a34cb5222e15df44ab4ca3e8680b687a))
* add relationshipType and validFor fields to ProductOfferingPriceRelationship model ([ee47dbd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/ee47dbd872e6482fc56d356bbe9fc02c11f606cb))
* enhance code to support migration use case ([3bdf030](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/3bdf030791be5e728d4d75f560950e812ae86daf))
* evolve product order item hierarchy state update ([3edc88a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/3edc88a10f1fbc98760bbb415e0573f17d564134))
* introduce AppointmentChangeEventCommand to handle updates for order items requiring appointment modifications ([d1b26d7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/d1b26d7682e9aca67c716447a9abea5545639b2b))
* use refactored maven spec modules ([23ea492](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/23ea492d86701007def89f9b40e11ba80293209c))
* utilize DOCKER_REGISTRY_MIRROR for container image pulls in MongoDB and Kafka test classes ([9a122a5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/9a122a58d16717c549b3de5431e824a23d18c03c))


### Performance Improvements

* speed up ProductOrder listing with caching and leaner query building ([170b3eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/170b3eba3170ac23f283eb7f4d463e3309cc4094))

# [1.12.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.11.0...1.12.0) (2025-07-16)


### Bug Fixes

* update GitLab URL to gitlab.ow2.org in helm/README.md and regenerate files ([4e0cb0a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4e0cb0a34dccf8658f84c10c106a773ec3eec697))


### Features

* add applicationDuration field under OrderPriceEntity ([f04694b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/f04694bcccde2589b39064d3afd1458aff4a47d1))

# [1.11.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.10.0...1.11.0) (2025-06-23)


### Features

* update om-commons to latest version ([332ad11](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/332ad11205c42782535fddc3968a0d7d8147549d))
* update orchestration-delivery-spec to latest version ([176e952](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/176e952f56c44805244d29e8ab382a07e1b555d4))

# [1.10.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.9.0...1.10.0) (2025-06-04)


### Features

* update documentation, regenerate CONTRIBUTING.md and adjust deployment configurations ([10ec5a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/10ec5a286c87fbc22d12979c31c2c077c628a9fc))

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.8.1...1.9.0) (2025-06-03)


### Bug Fixes

* add dummy commit ([4ea62a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4ea62a62ce0154aebd04141f827482c8ed6f6781))
* adjust code for the non installable product ([94cef7a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/94cef7af5f05c43669248060bbbc05b73fde2de7))
* **opensource:** remove annotations from helm templates to resolve deployment issues ([459a22b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/459a22b49f2edb22fca0b03864d7b44b47e20f43))


### Features

* generate event for non installable product ([cfde126](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/cfde1260030b6f7063d7e754fecba35013c243ce))
* **opensource:** add DCO.txt file ([c04393d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/c04393d7c1cb729bc3439cf3bbcce06ff00e3662))
* **opensource:** move to Open Source Sofware ([2c255b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/2c255b662d122fbed615a6b70fda7feb0e359841))
* proper enforcement for artifact name ([15225a5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/15225a5fb7c060dcc785327ccab1eb7f0bd1cc59))
* update order inventory spec with released version ([04b70e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/04b70e7b3253c1a6603c2d6e434df903a41bd30e))

<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## [1.8.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.8.0...1.8.1) (2025-03-03)


### Bug Fixes

* add dummy commit ([87a01d0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/87a01d0d725ef2358f03d8fbd234e8ccd2a0f99a))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.7.1...1.8.0) (2025-01-28)


### Bug Fixes

* disable CSRF protection and update Sonar exclusions ([72a2da4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/72a2da4631cb3af20dbda5a7da8bfe3e40d3e194))
* prevent NPE in getProductItemIdsByRelationType by handling null productOrderItem relationships ([e96e497](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/e96e497943352db44f0850bc78767d7b34830c64))
* re-enable CSRF protection in Spring Security ([e852721](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/e852721aab13a2dcf75d28d51f7602022513eadd))
* resolve Gitleaks issues by addressing detected sensitive data and exposed secrets ([bddc9ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/bddc9ad4e628e108ef5bc41511718deb1b2a8000))
* resolve placeholder issue by setting app version to 1.8.0-SNAPSHOT ([77b2d7d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/77b2d7dfdd5d92680992bfa1ba8232e863eab08f))


### Features

* add service to update product order with RequestedCompletionDate ([f13d64a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/f13d64ad101bd0905c8d91bf1e243b696fd7097c))
* add support for ValidityCharacteristic and adjust CharacteristicMapper for new DTOs ([0df92f5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/0df92f5879769807057978d6c3dce15897c657f4))
* handle "aborted" status in delivery node state change ([3053314](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/3053314a37d0553bed7e8c81da61a53c245fd49a))


## [1.7.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.7.0...1.7.1) (2024-10-27)


### Bug Fixes

* use stable repo in docker image ([080adbd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/080adbdb161eb636bc0caba004bd106d87b0b4eb))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.6.1...1.7.0) (2024-10-14)


### Bug Fixes

* correct product order item state handling for partial states ([af340b7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/af340b7e271f5790d468852af764f06dc9390793))


### Features

* add channel name as filter field ([774465a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/774465aeb93bf229ad957e8631d17ceda31baaf0))

## [1.6.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.6.0...1.6.1) (2024-10-01)


### Bug Fixes

* use common pipeline ([2e5384f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/2e5384ff62fca850772270e864345d48d563d7b2))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.5.5...1.6.0) (2024-09-04)


### Bug Fixes

* ensure events are published after updating product order state ([3cd8928](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/3cd8928ea99d671f8ef2c8e4d4e9dbdf155eb0cd))
* increase memory limit ([5515275](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/5515275f7aa7da920480003ea79ed660bdd2565b))
* increase memory limit for mongo ([66b064c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/66b064cd981a33d99ab0c21b694fe5e5fea1d8e9))
* move innovation job from staging to production stage ([5743f48](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/5743f486e703b867df2e0f91d179765982938ba6))
* publish event on ProductOrderItem state update for both bundle and atomic levels (previously only atomic level) ([690ab4e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/690ab4e01fb46c4117ea59b16b36d0038ce790e5))
* remove cert from source code and add it as variable in gitlab ci-cd ([74c39c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/74c39c5bf9af3da7396877483bb72ee8251df105))
* remove unused toEntity method for Quantity in ProductOrderMapper ([931bae8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/931bae8fe9bf98a500bc1d0d946c8adb3f6c0579))
* rename method from updateProductOrderItems to updateOrderItemsAndOrderTotalPrice and extend functionality to include order total price update ([65a065b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/65a065b72c0b698c20113b6467ca24e3fdf32df3))
* sync pipeline with guideline ([546aece](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/546aece6b927a62283a77904423b397a97c9b9a4))


### Features

* change recurringChargePeriod type from String to QuantityEntity ([4aed041](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/4aed0418eaeef004ba8b7c60f6448e81f7772331))
* remove `updateProductOrderTotalPrice` method from `ProductOrderService` class ([43193a3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/43193a3561779bb34ecdd3d9945080295fc3f45b))
* update event type and topic for RBC integration ([90e1f79](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/90e1f79bb22f49b17d221936c80cb6a6bf771976))
* update product order with billing account in product order items ([e1aa17c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/e1aa17c31d3b0a440286f05220de7b07b640ee00))
* update product order with product ref in product order items ([7bff0b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/7bff0b548882096bc8376445ab79869ef67038bb))

## [1.5.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.5.4...1.5.5) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([6b8e49c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/6b8e49c1f2823a8ad41f495ea23523af0a51ce25))

## [1.5.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.5.3...1.5.4) (2024-07-14)


### Bug Fixes

* remove cert from source code and add it as variable in gitlab ci-cd ([e9d7e62](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/e9d7e62026225a72812110a8c0a0a8580392ecba))

## [1.5.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.5.2...1.5.3) (2024-07-02)


### Bug Fixes

* increase memory limit ([ccd08f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/ccd08f4705dd9311f47bb6c466145e006aa5456e))

## [1.5.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.5.1...1.5.2) (2024-06-26)


### Bug Fixes

* increase memory limit for mongo ([c0313fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/c0313fe10faa96f3e5cb1dd2040701bf8eb23815))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.5.0...1.5.1) (2024-06-12)


### Bug Fixes

* sync pipeline with guideline ([f9a1c09](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/f9a1c092192cbcaf1ffdf8f4098c38df5cf23565))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/compare/1.4.2...1.5.0) (2024-06-07)


### Features

* update om-poi-spec version ([21f2e23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/21f2e23050055a44488f0903234ea0ef989d627e))
* update POI spec to tagged version 1.0.3 ([5ea673e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-inventory/commit/5ea673ed331514a8bb894167f799e034048a9a5d))

## [1.4.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/compare/1.4.1...1.4.2) (2024-04-29)


### Bug Fixes

* add job to deploy app on innovation environment ([00163d7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/00163d760c308d0ee2845a3c7d026090ad8c199b))
* removed semantic image and move it to gitlab variables ([620c6df](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/620c6dfe416a2dd9174761d841d461662078552b))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/compare/1.4.0...1.4.1) (2024-04-21)


### Bug Fixes

* the sed expression used to change the version and appVersion in ([764cb1d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/764cb1d827f554fc2b5857b0b7c2db73d1962cf7))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/compare/v1.3.0...1.4.0) (2024-04-07)


### Bug Fixes

* Add missing sort parameter ([3fb5bd1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/3fb5bd1bfd0040759e1e5dda41a74ab16c5e729c))
* Configure server to respect X-Forwarded headers for HATEOAS links ([33fdf43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/33fdf439a3f745eafbca47c6cd2e818c8b2a5a82))
* Correct failing unit tests in ProductOrderController ([6c16c8b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/6c16c8b73157af9b3c860432ece04f859fa7350a))
* Correct selfLink generation in ProductOrder ([4ccfb3a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/4ccfb3a0142d2293266d217524fb87e8e72537e6))
* empty variable expansion issue ([3f3cb62](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/3f3cb621578553331534ef019bdcf991fcc077b4))
* fix checkstyle issue ([362f61a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/362f61aea9ae3c64df286a6f807705d7accc073c))
* Fix offset calculation issue ([401a704](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/401a70434397648e5d9da7aa43852269bf302290))
* fix sonarqube tests ([1c97371](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/1c97371dc100d39a650455b111e7fe2660386524))
* move DTOs to commons ([936e8ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/936e8ee0b120c088569ae9bab25093c32c703dce))
* Resolve issue with duplicated sort parameter ([ae9eafc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ae9eafc1369a0aca7b5e0dcac6022332c2464be6))
* Resolve merge conflicts ([e3196ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/e3196ed73bcc876732b1c47eb13db1514b5dc797))
* Update 'DEFAULT_RELATED_PARTY_ID' value ([2c3b891](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2c3b89196b468cb9375f9618b6da142531e3d9d5))


### Features

* add authorization ([ddd5922](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ddd592249b4163bfd6ea63d16f32704f2df980a8))
* add authorization ([56161b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/56161b6ecc6765b7f961ceedf1f9f7deb122d0f4))
* enable authentication in swagger ([1e5a044](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/1e5a0449bd9ef95657d175b456efb13336fe0cdb))
* Implement order date filtering with operators and DATE format excluding minutes/seconds ([e490e81](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/e490e81b9941cf3885f9bfe1dfc856bb3681720a))
* Implement sorting feature for 'Get All Product Orders' endpoint ([ba1564f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ba1564f210f28cb16f64d27804fcc06bf04f2e6e))
* Update product order related parties ([175e98a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/175e98ab0665315fe38ef5406f5c15dbc773fca4))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/compare/v1.2.0...v1.3.0) (2024-1-17)


### Bug Fixes

* **CI:** resest semrel and make all helm deploy manual ([7bcbc51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7bcbc5130c010ba94263d111eb4abcfd9f1b6e5f))
* **devops:** Remove `develop` branch from `.releaserc` ([e919d9a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/e919d9afbcfa95c283bd617ff976f2108b918e5c))
* Set access expose headers ([7b307e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7b307e6386f78f2a164f6e1d82ea717bd9494f65))


### Features

* add kafka tracing config ([517de23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/517de23056c78896b5d35d95e631cfab4c0f07a8))

# 1.0.0 (2024-1-11)


### Bug Fixes

* add exception package to sonar coverage exclusion ([7d0ed30](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7d0ed30526cd6b3649d5faa0fb41399bf39af2f4))
* Address review comments ([f5c1e0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/f5c1e0cefb889cde20d1d3540d1695313443c459))
* **CI:** resest semrel and make all helm deploy manual ([091b07c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/091b07cb827bf29689433a8f70fcdbf6add8fba4))
* Clean code and resolve code smells issues ([236ccf2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/236ccf2183e2a01f464cb6041a799bd580194c02))
* enable debug mode ([289fa40](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/289fa40f95746c8d4ad29a5c6537661628dd9719))
* Exclude 'ProductOrderInventoryApp' from Sonar coverage ([d8cf397](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/d8cf39707368b1c735fea8444b9b098bbb32b953))
* fix integration with COOD ([4601aa6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/4601aa64bf15dbe275d39d7608f442b322a332f7))
* fix publish unchanged event ([061ff88](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/061ff88b89551a27d40b2e4577480b7bd3a5a0ae))
* fix response body for Invalid response ([92a0bee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/92a0bee5159ec7e01ab93b5b479f89ae9f1f826d))
* Fix Sonar issues ([2583cbe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2583cbe228254bcf4842bb2f0f409076ea47d462))
* fix swagger configuration ([53666c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/53666c9834e85a327523d32ae33971575d802247))
* fix swagger configuration ([86571dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/86571dde1560dae7a39cd065b560d047cc846da4))
* missing product order item ([11d183c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/11d183c8a7b557ed481abf9c50b2c4e34d615ae5))
* missing product order item ([c960bde](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/c960bdea9191ee7e8d408c6e5b08a0068b6673b9))
* Resolve issue with 'order-capture-kubernetes.yml' configuration file ([9d616f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/9d616f163603fd2973fded79333e60f01cf50636))
* Resolve issues with 'get products' when applying filters and specifying fields ([97cf412](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/97cf4129b2f416e6be320e0f4f0c31e31259290b))
* Resolve SonarQube issues ([66a4acd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/66a4acde95257790fe76e61e18f30f706b7393d8))
* Update CommandType value ([2daf12a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2daf12a0a6c2bceb4a7e5c0db1891e3438ede872))
* Update commons version ([887d299](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/887d299fe0720f49a5926b7be506f60f016ab176))
* Update Node and commons dependencies to the latest versions ([7d12961](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7d1296199acae6ae9991a1463fa41e92385254c6))


### Features

* Add 'immediatePayment' to the 'ProductOfferingPriceRefEntity' domain ([5528068](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/55280685051f2bff7c3f8e046f8a7fb7fe7d4c58))
* add async api ([2349bba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2349bba029e099eac6e8e9fe11e4f793d6a79576))
* add command handling for product order ([e5c1531](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/e5c1531e8b115416542da795be2ed05f39e551f9))
* add enum converter for mongo DB ([4099112](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/409911264c324325bb01ce91640f35c81111b15a))
* add log config ([3501f80](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/3501f80abe2b7c2acb0f7718ad4cf56896d5e433))
* add log config ([f37a45f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/f37a45f793895c8123adad5eb634878afdd60ad6))
* add log config ([91cd0a0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/91cd0a00b17b67fb757380022544b3688ea2464f))
* add log config ([ba5d3e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ba5d3e87c55024317c8674c5f0d4d43696338a5b))
* Contract first development implementation ([55fc2af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/55fc2afc1c6d3bd94082424d561d04e296e7ecf4))
* enable actuator ([f29a79c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/f29a79c5b58769a89d2db7dd5c7faafea6fed749))
* expose get product order by id ([2ee13ff](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2ee13ffce61901c63c99ac5474e81fa41c763119))
* expose get product order by id ([2e06c3f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2e06c3fdee8d52ad908b9e0a03499a65b9aab84a))
* expose get product order by id ([99293f7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/99293f757f833b6558abfd0bb8dcc1bd57c0dead))
* expose get product order by id ([acdce00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/acdce00799e55fc087ad5aca8350fe0f471a2f58))
* expose get product order by id ([a6f8abb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/a6f8abb7a4bafe8cb1c93f4b19d32540658f3bfa))
* expose get product order by id ([f36a327](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/f36a327da2493a1b01bba17a379423901a64e22c))
* expose get product order by id ([fb6a019](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/fb6a0190ddc54bd428fd5edc1c018085248844ad))
* first commit ([c1eff19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/c1eff1927ed83a6cb4e042c01a374bf36367771c))
* fix cert ([ad8b38b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ad8b38bc9689027df4ab8c5193d38e380ed34015))
* fix config ([6d1bc71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/6d1bc71d52aeb6693d26cabb8eaddcdac51e3ffb))
* Fix CORS Origin issue ([90ca75d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/90ca75d4daa480a936d85aa2d59a7004b193fb78))
* fix keycloak config ([8f5325c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/8f5325c1532fb2c2ec28329ec65065ab94244701))
* fix sonar issues ([0a61b8c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/0a61b8c249c0fc348559f0556b38fbdb3896be2a))
* fix sonar issues ([e75afc3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/e75afc3074835d22e3b3942debf58bf5b738ed72))
* Implement 'Get Product Order(s)' with filter and fields compliance to tmf622 ([6e90079](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/6e900795ed9425ef14548103e3d7cf7aeb3c8fb6))
* Implement logic to update product order hierarchy ([b13abb9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/b13abb9a686ab100e241ee1afaa757a333dfab40))
* Modify dates type to  Instant instead of LocalDateTime ([7c9e6fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7c9e6fc5bccd5e41a302c74dcaf82179f1026cad))
* Replace removed and deprecated functionality for securing requests ([065ff07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/065ff078bec27897d11b560457f2df070f4ffa5d))
* Update 'ProductOrderEntity' domain by adding 'ProductRelationshipEntity' ([69eb250](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/69eb25082a1f35ab07d7d73c4752f181b4dce501))
* update deployment configuration ([c23d6cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/c23d6cdc095b8c55291f267945b380ad79ac1367))
* update keycloak config ([671babd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/671babd862155e505dfc28dc46031c6f4d1d4422))
* Update order orchestration event type ([7f7d073](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7f7d073e7730722c9609e48aaf315c21e2e3e0d8))


### Reverts

* Revert "Add docker-compose tag to mvn-build job" ([cb9dffb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/cb9dffb02ac9f6fa987db0e5132be74b646b38dc))
* Revert "1-add-build-stages add postfix to executable jar" ([69ecd43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/69ecd432265e32850a2f67872f78b9d8dffd4f8d))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/compare/1.1.0...1.2.0) (2024-1-3)


### Bug Fixes

* add exception package to sonar coverage exclusion ([7d0ed30](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7d0ed30526cd6b3649d5faa0fb41399bf39af2f4))
* Address review comments ([f5c1e0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/f5c1e0cefb889cde20d1d3540d1695313443c459))
* Clean code and resolve code smells issues ([236ccf2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/236ccf2183e2a01f464cb6041a799bd580194c02))
* fix response body for Invalid response ([92a0bee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/92a0bee5159ec7e01ab93b5b479f89ae9f1f826d))
* fix swagger configuration ([86571dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/86571dde1560dae7a39cd065b560d047cc846da4))
* missing product order item ([c960bde](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/c960bdea9191ee7e8d408c6e5b08a0068b6673b9))
* Resolve issue with 'order-capture-kubernetes.yml' configuration file ([9d616f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/9d616f163603fd2973fded79333e60f01cf50636))
* Resolve issues with 'get products' when applying filters and specifying fields ([97cf412](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/97cf4129b2f416e6be320e0f4f0c31e31259290b))
* Resolve SonarQube issues ([66a4acd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/66a4acde95257790fe76e61e18f30f706b7393d8))
* Update CommandType value ([2daf12a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2daf12a0a6c2bceb4a7e5c0db1891e3438ede872))
* Update commons version ([887d299](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/887d299fe0720f49a5926b7be506f60f016ab176))
* Update Node and commons dependencies to the latest versions ([7d12961](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7d1296199acae6ae9991a1463fa41e92385254c6))


### Features

* Add 'immediatePayment' to the 'ProductOfferingPriceRefEntity' domain ([5528068](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/55280685051f2bff7c3f8e046f8a7fb7fe7d4c58))
* add async api ([2349bba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/2349bba029e099eac6e8e9fe11e4f793d6a79576))
* add log config ([91cd0a0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/91cd0a00b17b67fb757380022544b3688ea2464f))
* add log config ([ba5d3e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ba5d3e87c55024317c8674c5f0d4d43696338a5b))
* Contract first development implementation ([55fc2af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/55fc2afc1c6d3bd94082424d561d04e296e7ecf4))
* expose get product order by id ([acdce00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/acdce00799e55fc087ad5aca8350fe0f471a2f58))
* expose get product order by id ([a6f8abb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/a6f8abb7a4bafe8cb1c93f4b19d32540658f3bfa))
* expose get product order by id ([f36a327](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/f36a327da2493a1b01bba17a379423901a64e22c))
* expose get product order by id ([fb6a019](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/fb6a0190ddc54bd428fd5edc1c018085248844ad))
* fix cert ([ad8b38b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/ad8b38bc9689027df4ab8c5193d38e380ed34015))
* fix config ([6d1bc71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/6d1bc71d52aeb6693d26cabb8eaddcdac51e3ffb))
* Fix CORS Origin issue ([90ca75d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/90ca75d4daa480a936d85aa2d59a7004b193fb78))
* fix keycloak config ([8f5325c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/8f5325c1532fb2c2ec28329ec65065ab94244701))
* fix sonar issues ([e75afc3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/e75afc3074835d22e3b3942debf58bf5b738ed72))
* Implement 'Get Product Order(s)' with filter and fields compliance to tmf622 ([6e90079](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/6e900795ed9425ef14548103e3d7cf7aeb3c8fb6))
* Modify dates type to  Instant instead of LocalDateTime ([7c9e6fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7c9e6fc5bccd5e41a302c74dcaf82179f1026cad))
* Replace removed and deprecated functionality for securing requests ([065ff07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/065ff078bec27897d11b560457f2df070f4ffa5d))
* Update 'ProductOrderEntity' domain by adding 'ProductRelationshipEntity' ([69eb250](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/69eb25082a1f35ab07d7d73c4752f181b4dce501))
* update keycloak config ([671babd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/671babd862155e505dfc28dc46031c6f4d1d4422))
* Update order orchestration event type ([7f7d073](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/product-order-inventory/commit/7f7d073e7730722c9609e48aaf315c21e2e3e0d8))

# 1.0.0 (2023-10-29)


### Bug Fixes

* add exception package to sonar coverage exclusion ([7d0ed30](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/7d0ed30526cd6b3649d5faa0fb41399bf39af2f4))
* enable debug mode ([289fa40](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/289fa40f95746c8d4ad29a5c6537661628dd9719))
* Exclude 'ProductOrderInventoryApp' from Sonar coverage ([d8cf397](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/d8cf39707368b1c735fea8444b9b098bbb32b953))
* fix integration with COOD ([4601aa6](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/4601aa64bf15dbe275d39d7608f442b322a332f7))
* fix publish unchanged event ([061ff88](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/061ff88b89551a27d40b2e4577480b7bd3a5a0ae))
* fix response body for Invalid response ([92a0bee](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/92a0bee5159ec7e01ab93b5b479f89ae9f1f826d))
* Fix Sonar issues ([2583cbe](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/2583cbe228254bcf4842bb2f0f409076ea47d462))
* fix swagger configuration ([53666c9](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/53666c9834e85a327523d32ae33971575d802247))
* missing product order item ([11d183c](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/11d183c8a7b557ed481abf9c50b2c4e34d615ae5))
* Update Node and commons dependencies to the latest versions ([7d12961](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/7d1296199acae6ae9991a1463fa41e92385254c6))


### Features

* add command handling for product order ([e5c1531](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/e5c1531e8b115416542da795be2ed05f39e551f9))
* add enum converter for mongo DB ([4099112](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/409911264c324325bb01ce91640f35c81111b15a))
* add log config ([3501f80](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/3501f80abe2b7c2acb0f7718ad4cf56896d5e433))
* add log config ([f37a45f](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/f37a45f793895c8123adad5eb634878afdd60ad6))
* enable actuator ([f29a79c](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/f29a79c5b58769a89d2db7dd5c7faafea6fed749))
* expose get product order by id ([2ee13ff](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/2ee13ffce61901c63c99ac5474e81fa41c763119))
* expose get product order by id ([2e06c3f](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/2e06c3fdee8d52ad908b9e0a03499a65b9aab84a))
* expose get product order by id ([99293f7](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/99293f757f833b6558abfd0bb8dcc1bd57c0dead))
* first commit ([c1eff19](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/c1eff1927ed83a6cb4e042c01a374bf36367771c))
* fix sonar issues ([0a61b8c](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/0a61b8c249c0fc348559f0556b38fbdb3896be2a))
* Implement logic to update product order hierarchy ([b13abb9](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/b13abb9a686ab100e241ee1afaa757a333dfab40))
* Update 'ProductOrderEntity' domain by adding 'ProductRelationshipEntity' ([69eb250](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/69eb25082a1f35ab07d7d73c4752f181b4dce501))
* update deployment configuration ([c23d6cd](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/c23d6cdc095b8c55291f267945b380ad79ac1367))


### Reverts

* Revert "Add docker-compose tag to mvn-build job" ([cb9dffb](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/cb9dffb02ac9f6fa987db0e5132be74b646b38dc))
* Revert "1-add-build-stages add postfix to executable jar" ([69ecd43](https://gitlab.tech.orange/disco/disco-order-management/product-order-inventory/commit/69ecd432265e32850a2f67872f78b9d8dffd4f8d))
