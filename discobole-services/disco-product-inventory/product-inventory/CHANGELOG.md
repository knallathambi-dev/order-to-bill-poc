# [1.18.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.17.0...1.18.0) (2026-04-23)


### Bug Fixes

* add missing mongo variable that we use it with default value ([e5a7711](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e5a7711cb8ff17810ce2fccad815abbd699123c0))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([32d126d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/32d126dce822890477c085cb076c6f63a7942d52))


### Features

* change mongodb helm and image source to fix found vulnerability ([41c920d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/41c920d5fbc033fc5b3cae1e584cf214d7a88487))

# [1.17.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.16.0...1.17.0) (2026-04-02)


### Bug Fixes

* Export product with content type CSV produces JSON file ([fabaf0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/fabaf0c772ea241caf4ea4e3a2ec7333a52ef0f3))
* For_MGA_currency_prices_rounded ([73c197a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/73c197a944e8c19779fb889d9432f03e26f3b680))
* For_MGA_currency_prices_rounded ([3cb6657](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3cb66570436c9b0ae9f0ad8c29c082a28231b129))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([4ba5216](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4ba52160e27c65d61deecaff4b5e1ae92e2953f0))


### Features

* add csp header for each request ([fcb4c7e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/fcb4c7e6eef1fe381c4a5cebea4402af76266aea))
* PATCH Bulk Update price ([01196ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/01196ab13ab7a608b9fb45ea56e5272097801f21))

# [1.16.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.15.0...1.16.0) (2026-02-24)


### Bug Fixes

* [PATCH {id}/Bulk] Set end date time for recurring charge price when terminating product [S21] ([3fa4b52](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3fa4b526a555f7fa68094c930255fee20037dedc))
* Add charcteristic Date Time ([1de1528](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1de1528d8bbe89a1811c0ac321ee7341cf7dc41b))


### Features

* empty commit to update minor version by semantic release ([1213aea](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1213aeabcdfa915a809fa45fbd3ec7d4e0d48550))

# [1.15.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.14.0...1.15.0) (2025-11-13)


### Bug Fixes

*   Incorrect Discount Logic for Past/Future Dates ([824e4ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/824e4ee572257a92a9b9028f4111592c267f98f7))
*   Pagination and Filter Functionality Bugs ([06b9b5a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/06b9b5a9355ebde533a6f7dd1b64a07282978331))
*  Error 400 when performing transaction from PendingDelivery to Locked for Operational Status ([1b1dd7d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1b1dd7d6e87cd80d3e2658c30ccab5328e8e89de))
* [Security Audit] Elevation of Privilege via Role Manipulation [V3] ([75bd8c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/75bd8c57364226c71e742c887becefb490999368))
* [Security Audit] Elevation of Privilege via Role Manipulation [V3] ([9fdfebd](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/9fdfebd0ac5c62c34eaf2db3c0a7d425b9ad261d))
* add license name and git repo for THIRD-PARTY.txt ([4f059da](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4f059dac5d5130a8a451c5c613340301e431a976))
* No more do update root product when update relationship name ([bd1be0b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/bd1be0b9b6182f8e50e767b40254a12871a8711b))
* priceType in sub class priceAlteration not required ([7d5e223](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/7d5e2230846a7862b11fe15be090fae36d9ee202))
* remove configs for promtail and istio ([e28c273](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e28c2730b3415da733cef0ec0a89510909dcfbe5))
* remove values files as we moved them to gitlab-ci project ([cd8547e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/cd8547ebcec5166fd171ed67e26bf5a6e0cb07dd))
* security audit - CSV RCE vulnerability fix ([db6cc93](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/db6cc9349c8facd288cb392db040f094df1789b8))
* Start date updated only when product main status move from created to active ([a39a1db](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a39a1db37c1eba1dc56b65e5ef117a0fc2e6c2aa))


### Features

* Calculate and dispaly price with tax included ([4ca5828](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4ca58284416138a173efbee7508e5ba885e67808))
* Calculate and dispaly price with tax included ([414e5fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/414e5fc650779d5da4ed70aa3a7ec18f3551b3de))
* Change type of duration under productTerm ([9c37d7e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/9c37d7ea240c7b5e4228fd36eedff7e033aec460))
* New status transition ([74dca2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/74dca2d3386a1211e4c1f04b3fee44517dcc35a7))
* PATCH Bulk API fails when replace and remove operations target the same array ([1440a1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1440a1fcf93621a87498ba568e59cebdca60c006))
* PATCH Bulk API incorrectly requires value field for remove operations ([4accd10](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4accd10735204b30ec1c6d0e1496ed7004101822))
* PATCH Bulk API incorrectly requires value field for remove operations ([1c5d66f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1c5d66f3c2c83a57a077a77174e85d62378df5a2))

# [1.14.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.13.0...1.14.0) (2025-07-17)


### Features

* add new [@type](https://gitlab.tech.orange/type) "AddressCharacteristic" ([683f3c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/683f3c5d3a077f0eec581d880dc066fda75a2940))
* ran ctk conformance test ([0baccb2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/0baccb231404e0b5765543b2134a02766d977a83))
* Update Application duration type ([dcedd63](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/dcedd63b093832c500790ad63ce9c6f40332e9b6))
* Update Application duration type ([8655efe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8655efe310477c1496f71bdf4fcaf7ee8291dce2))
* Update Application duration type ([a537a37](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a537a37f048e0ac562bc7d264ba54fa02fcf1c52))

# [1.13.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.12.0...1.13.0) (2025-06-23)


### Features

* **opensource:** remove Disco in the title ([e90ff88](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e90ff88e7b619792782d8ef6cb5872c66f076d0e))
* **opensource:** use ow2 link for security doc ([2f60fa0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/2f60fa05172dc1ddb9d934bca4145ea2fc7273b4))

# [1.12.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.11.0...1.12.0) (2025-06-04)


### Bug Fixes

* move ingress license from templates to helm folder as helm was ([395ebfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/395ebfc96b3aa735b8eba26681259a5a9ce6d2e0))
* mxHeap representation ([0177492](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/01774922ae4b2c06e6aa19a3161dc5ff689a2ec8))


### Features

* change mock server name & url ([435a983](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/435a983bdbf3862d16b7bf0fb0b960132e18a234))
* Import CSV file ([4663826](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/46638263d229af69021bc5795268e25b29416365))
* **opensource:** move to Open Source Software ([065b991](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/065b991a5e7c7a5c98b1e2babd34fc2b64938f4b))
* Proper Enforcement of artifacts naming && discobole rebrand ([b501462](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b501462fc86e1dea7812ec296bdc97957b89d340))
* update om dependency. ([3cb279a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3cb279aa2000dbdca42e3409a29c39d9ed316a26))

<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# [1.11.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.10.1...1.11.0) (2025-03-26)


### Bug Fixes

* added product status change event to patch by id ([9a82abe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/9a82abef31f9e51fa2d3987ef712e8a28031138b))
* fix reporting startup logic ([06f6299](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/06f6299265901ee16e62a1af2b7ea12e26da3ae0))
* fix reporting startup logic to account for service down for a whole day ([c59a546](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/c59a5468a3ee9ec783426de6d169fa9c2def4fb8))
* partyOrPartyRole fields aren't returned ([4ca0a71](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4ca0a71a6e1501e82e9f5115d995b309fecb0d4b))
* related party mappping issue on patch ([725109e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/725109e613f7266f39623c6646c8560dcf40c1a6))


### Features

* add import job specificationn- upload imported_products_file - save uploaded file on s3 ([b0f40f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b0f40f8136887dd079d62a3908e412c022a495b3))
* add import job specificationn- upload imported_products_file - save uploaded file on s3 ([e95253c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e95253cec559d7b30852cb6ab1654bf0e60630b0))
* changed upload to use s3 presigned url. ([8db125f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8db125f2afc4edc399190e63b4eddac7c1a8341f))
* Evolve creation job specification ([58d85d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/58d85d40def3cda650475c1f6a300d96ae52dc04))
* fixing error filtre job ([e77f6c4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e77f6c49d90ca2053d4b7c32a42243d862559193))
* Support `migrateFrom` Relationship Type in Contract Creation for Migration Scenarios ([409cf30](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/409cf30f7546eee723ed00d89819e390ef60213f))
* Validate enum values in job query ([1e4fb18](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1e4fb1832a19ec42290ff85f84aa6a115378ac47))

## [1.10.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.10.0...1.10.1) (2025-03-02)


### Bug Fixes

* oro milestone2 fixes ([e0b8207](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e0b82077c23ca5bcf7c765ffeaa8628ca161df93))

# [1.10.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.9.3...1.10.0) (2025-01-26)

### Bug Fixes

*  Unsupported creationDate Filter Parameter ([53e6e1c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/53e6e1c10dc3e1facd93f59924c439bf00bffdde))
* fix missing startdate on task definition for exprtJob. ([e77d1d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e77d1d2fa09f96ddcce67181993dd3ac3e84479b))
* fix reporting startup logic ([f0eb1f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/f0eb1f883ee8318c2f7b53ad9b0be0405fcec26d))
* handle 31st of month test edge case ([33fbdbd](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/33fbdbd432543bed8f4dd110387ca2b9269722f7))
* href not returned in Get taskExecution with fields ([0ef13c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/0ef13c5f22ce97a502576503aa290dcfa42f47c6))
* **job:** fix recurring job month to always run at the day of month provided by the user ([b7bedc6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b7bedc65cbb3aae838030f5cf213df312f498b4d))
* removed rule that sets the termination date when the operationStatus is pendingTerminate but the status is active ([1e8776a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1e8776a77fbd5d91b59335d97d49bb99242f7d30))
* set descriptive error message for invalid body field ([b368e44](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b368e44726a5a57a67a9ec5183bfa9792f754cf5))
* **sonar:** fix sonar issues ([0089f5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/0089f5c2471c78fd1da25b8964ddc890d2e1feec))
* updated related Party & added migration file ([0a61adc](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/0a61adc1f92f866d401cb405167f6e9cf69e27a7))


### Features

*  Evolve rules for terminal status transition ([2b7214b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/2b7214b438992048ca9a3ea6c62b338548f2ff69))
*  refactor task ([5e259f0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/5e259f0c33c621014cdc0254a7bb8657538b98ef))
*  refactor task: added lifecycleStatus change ([c750f76](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/c750f76017a41168dc8fa620714c157c1331c4ea))
*  refactor task: review fixes ([14cf29d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/14cf29dbb37844051394d29c0866056f1e275788))
*  refactor task: termination job ([25daa8e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/25daa8e6e2f28876d58272dd52a51e0908d3df84))
* add create terminationJob specification with validator ([a0f51bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a0f51bf69afb2b1e39cd0c363e50c5b58e95a8ea))
* add price to csv export ([44b7e07](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/44b7e073c788b68ddde48cddabe2a60e32892601))
* add relationships for use case migration and update root product of the migrated product ([faa94bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/faa94bbd3bd1ad37c21217ff241b0468788c20cf))
* introducing reporting with product status reports. ([6cdcfed](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/6cdcfedd98f9e3e830334eccd890472eabf8f2e6))
* make schedule required and default value is immediate ([244cca8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/244cca85c7cce21096427a5f650ccce33bc5136d))
* refactor TerminateProductServiceImpl class ([5b2046e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/5b2046ea9b8f3bea8571ae4fcf33aea9048086a7))
* remove tag System and divide in three parts Status - Version - Configuration ([a1ad0b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a1ad0b1aabe184cde9f21db05753a4cc7bdfe37d))
* select columns to export ([dc73f9e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/dc73f9ecc1c16e38cd0305cdff29fc9d56e3d16d))
* upgrade RelatedParty to v5 ([f6543e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/f6543e3ee2c8b069479aa6c9e8240b820c2ec6c9))

## [1.9.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.9.2...1.9.3) (2024-10-21)


### Bug Fixes

* fixed om-commons-service version and added ErrorHandlingDeserializer to remove the infinite error log issue ([8f2ace9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8f2ace9fcc900c297b8c9b922612091608f225d1))

## [1.9.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.9.1...1.9.2) (2024-10-15)


### Bug Fixes

* create product relationship index ([a7fda9d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a7fda9dae56545e47a97517d1724106ff635609a))
* create product relationship index ([f0eab87](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/f0eab876775d11eb725b34ccedce18e0b6cc9323))

## [1.9.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.9.0...1.9.1) (2024-10-15)


### Bug Fixes

* create product relationship index ([818d27d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/818d27d58ff45f81a3cb8c83d3e92efc5dff789f))

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.8.1...1.9.0) (2024-10-14)


### Bug Fixes

* changed url path for task & system configs ([ae47d22](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ae47d226705e891360eae500468f4c0e692afc92))
* delete taskItems when deleting Task ([5eb413b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/5eb413b49d6f9c9bdb8021660d117181c7f8c0e4))
* fix swagger url and paths problem and fix href and location problem ([62aca55](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/62aca5578c4cb03bef0e8bfd5261231ba5890f89))


### Features

* add conformance report ([0b0253b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/0b0253be72d09548ac1016b0e48d1c8a1b3b072c))
* Add permission check for purge task ([6575c9e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/6575c9e23d6117875db1bf79c833ee57ca90cd48))
* adding ctk report ([9915411](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/9915411218a55feaf9ad97699f54e8906926533d))
* **gitleaks:** fix gitleaks ([b1a8518](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b1a85187fa738a884a6cc1c57d807ef26671885e))
* update shipment rules ([db28b78](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/db28b786d47e026fe7889cf0fd7a931f777949b8))

## [1.8.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.8.0...1.8.1) (2024-10-09)


### Bug Fixes

* add indexes for performance ([654ee30](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/654ee309cb4648452a2997bcf9f4cf00e74e5d2b))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.7.1...1.8.0) (2024-09-18)


### Bug Fixes

* fix pipeline by radding back the pipeline from main branch ([16ed870](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/16ed870efaa9dad63616c90e8675ee85a548a8fb))
* fix review remarks ([4353b25](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4353b2561f56dfb3a97914892104d0a7e7e60d3f))
* fixed task bugs : ([fdded66](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/fdded668b8c6fe8ae51974d5166e0018e2a0260f))
* fixing sonar ([4c7434d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4c7434d10d53c768ddbbe1c3a61f45d2debb981a))
* fixing tests ([8c73fff](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8c73fff20d432df3ba20afe149fc1a22e843078f))
* increase timeout for readiness command ([b675e6b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b675e6bb0f478e06f265f84d67b05b5827a07dd0))
* pagination limit ([b63fe81](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b63fe81dc15de48f3c30960c6bca2fb9c13650c3))
* pagination limit deployment ([ce1cf2c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ce1cf2cb6fd9806ee9d0af0ba23b5d1f14a455c3))
* sonar quality gate issues ([637dbf3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/637dbf358b1a266b2c6cdbf655a78600ef445aad))
* sonar quality gate issues ([16c7a36](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/16c7a36489c9074098b4c7f84778384b481bb89a))


### Features

* add rule: empty values of recurring charge period should not be accepted ([e031685](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e031685bc2971d0a7923201a1cee0eb39c852706))
* csv export change format ([1a0a096](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1a0a0968395ad157812fb41aa6293ebcdadf2d23))
* purge products and tasks ([c88344d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/c88344d15277f62a05338cb8146fa711fa547cfc))

## [1.7.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.7.0...1.7.1) (2024-09-16)


### Bug Fixes

* increase timeout for readiness command ([336ee39](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/336ee394d23d4e7933b5a2cb205137c73496aeb3))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.6.2...1.7.0) (2024-09-04)


### Bug Fixes

* add unexpected dbRef.getId() no objectId to UpdateProductRelationship migration ([524fa61](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/524fa61d846e638a6eac6228d3ed4c1b7bb21bfc))
* always adding keycloak profile as the conditional doesn't work as expected ([3c16889](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3c16889fbbb80dd865af159f3fd999afeeb8b1d1))
* disable sonar quality gate ([94dcad9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/94dcad9e351b7a35562a661053c50941c329bad4))
* fix keycloak.yml config webclient indentation ([f297c58](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/f297c58b10844aa640f3cdda85e7369073d29422))
* fix mock server service name ([aad3cee](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/aad3cee868764565c8f9171bf9c0ec2c120ca192))
* get s3 bucket name and bucket host from s3 configmap instead of ([e5d1f96](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e5d1f9685b60ca67b1c8065295cbc6815d29e756))
* increase memory limit for mongo ([cd09ebb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/cd09ebba7e4daff4f4b71d30f300e94002db364e))
* Invalid patch for billingAccount.id with empty val ([8d4aa57](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8d4aa571632f5aa3587bb553edf96595ddb1965f))
* move innovation job from staging to production stage ([99bfafb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/99bfafb5f04a7d2d74a96e481dbc06918e4f3bd6))
* readd OTEL_EXPORTER_OTLP_ENDPOINT to deployment.yaml ([ba357a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ba357a2cd37664afb33a0a666fd426e700c61dea))
* remove cert from source code and add it as variable in gitlab ci-cd ([8dc6baf](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8dc6bafe85633dacd1f7da8fb8a366bc0acb67de))
* set variable userRoleValidationEnable in production and innovation ([a69429f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a69429f9eedd45c0557403ce7397f4246d09084a))
* sync pipeline with guideline ([78ce833](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/78ce833487dc3e939d139c131595844d798442fd))
* task scheduling cronjob ([c507b3f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/c507b3f230cb7b69fa7f734b8aa763005d80b515))
* update om dependency version as there is new model changes ([77b5a0a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/77b5a0a6d7b6cb0786e2f39ee79aeb6816434d26))


### Features

* Add optional boolean filter isRoot ([9b2879c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/9b2879c35ea9b4ebdcae2c71c91573bcff500c8b))
* add test profile on security config ([c5e9290](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/c5e929012d15350ffdb950f3268d7fca75af736e))
* adding timeout to webclient and a flag to use service account ([564c05d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/564c05dff39b0a44a7c04f8225b0916f73b1c198))
* enable keycloak profile in deployment.yaml based on condition ([bf46bea](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/bf46bea9315528c5ac57ae22cf264b6c0ab239e6))
* impl enable/disable keycloak ([343199f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/343199f032381d1f1c8917e402ee5b741fd5710c))
* No optimal response in the case of Bad URL ([bb6b6c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/bb6b6c5bccb0bbcae54daec12870879e97d405c5))
* ProductOfferingRef sub-resource fields ([b9d4e84](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b9d4e8405b3d9daf5a151ccc6c5e3c37cfca3ce6))
* re-adding check on keycloak enabled in deployment.yaml ([6828440](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/68284407e47c8070543cb9a4664a0b62a68e7ec6))
* Searching incomplete character strings ([ae93064](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ae930640e24337dc8cfa97386ee4817c84c1953b))
* Searching incomplete character strings ([b353f79](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b353f79588d5c78c2bbc9870c07af48e56a842b0))
* set update lastUpdateDate for PATCH/DELETE/BATCH/EVENT ([44e14dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/44e14ddf9d81db8b39ddbe0131cf5a41790ce936))
* stop setting null array attribute to empty array in POST request ([0acf467](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/0acf4678f7a0813dab27a5d6917c6367b0a344e8))
* string value product.[@type](https://gitlab.tech.orange/type) ([b599c48](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b599c4890c213f9548859cae16b34b6a0cdb5ed3))
* task scheduling impl ([a50b29e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a50b29e8979e721caa7056a468106c0f7378f9b1))

## [1.6.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.6.1...1.6.2) (2024-08-11)


### Bug Fixes

* removing -XX:MaxMetaspaceSize from jvm argument as it was causing crash ([6ea7d73](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/6ea7d73d393910992e0913de2dbb96c8a0c9a46e))

## [1.6.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.6.0...1.6.1) (2024-08-01)


### Bug Fixes

* add missing jvm parameters for dump of gc logs ([3c3d7be](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3c3d7be006758421d3ff9ee762f4e42888f3be86))
* add missing jvm parameters for dump of gc logs ([09411ca](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/09411caf26e5dcbeb54703f853836a94d74eef59))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.9...1.6.0) (2024-07-30)


### Features

* adding extra jvm parameters ([fff882b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/fff882bacf3596c45ff090aca840b654c34f6cb2))

## [1.5.9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.8...1.5.9) (2024-07-30)


### Bug Fixes

* fix mock server service name ([deb660c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/deb660cd79a6c42c40531e9ac1070d41cb53c1eb))

## [1.5.8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.7...1.5.8) (2024-07-29)


### Bug Fixes

* set variable userRoleValidationEnable in production and innovation ([947abd7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/947abd714d4044fe6e16853ad413884ada9397ea))

## [1.5.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.6...1.5.7) (2024-07-29)


### Bug Fixes

* get s3 bucket name and bucket host from s3 configmap instead of ([03943b2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/03943b219fc3e681a83b11b4ab61edb848a968d5))

## [1.5.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.5...1.5.6) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([ec092b7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ec092b7dad957233148a6596791f2dc2c0c7a334))

## [1.5.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.4...1.5.5) (2024-07-14)


### Bug Fixes

* disable sonar quality gate ([b3eb46d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b3eb46dfd6a75ba10b52e2052f90317e952bd2c7))
* remove cert from source code and add it as variable in gitlab ci-cd ([366200c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/366200c6986265447a61950c745e5ed02c2efd3e))

## [1.5.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.3...1.5.4) (2024-06-26)


### Bug Fixes

* increase memory limit for mongo ([3a47a01](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3a47a01a1b550d8630c878d93ae32db08b953c57))

## [1.5.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.2...1.5.3) (2024-06-13)


### Bug Fixes

* sync pipeline with guideline ([96458f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/96458f928e1908478937e093e540653ba353fb5c))

## [1.5.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.1...1.5.2) (2024-06-12)


### Bug Fixes

* add missing values to production values file ([95f4227](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/95f4227bf70d645f0fc7aaee6583b0a6dadf9231))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.5.0...1.5.1) (2024-06-12)


### Bug Fixes

* fixing helm-production rules to handle tags correctly ([f1605f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/f1605f96d937c84724791af5e02782eaa17bcef2))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.4.3...1.5.0) (2024-06-04)


### Bug Fixes

*  Incorrect lifecycle for physical product status ([11889f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/11889f9562912050d6dc36ab2ca76b0a8374011e))
* application crash because of missing variable ([5e9ff55](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/5e9ff55cf00c961d93e02a5ef61e2ab0e746c946))
* fixing a bug in patch product by id where validations were being run on wrong atType ([8b22712](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8b227128a5f2c92d297a557ce0d12ba2bb161f4e))
* monitoring bugs ([ec4133c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ec4133ceba3662236ed4ef310bface08b34868aa))
* updating acceptance test url after project rename ([715af1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/715af1ffa2d7aadae478b586326a40ef14096f58))


### Features

*  Change ProductOffering Type name to BundleProductOffering ([e2bdfae](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e2bdfae8fd0fd728099065867933ca234d689bd0))
* Configurable check TMF 672 ([877c6f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/877c6f10eab85413806618378c4352b56ac0a08f))
* enable graceful shutdown in app config for deployments ([aa92fd7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/aa92fd7bf9964980643bbdce3d1353aeaa099c4a))
* Evolve add check [@type](https://gitlab.tech.orange/type) value ([caac045](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/caac045ed8096eaf62f61c1a3ddf9d0a086d2764))
* Evolve algorithm to Fetch product to terminate ([a17d6eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a17d6ebf6f4105bb99297fd5cc616b8802aa681d))
* Evolve Task Entity ([07e7484](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/07e748493af83388e526bc4e8d234cfa4e3842dd))
* export Job ([4f06040](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4f06040bfa53205d5ccfa3b8511274cd3731012c))
* Migration Postman to Bruno ([7cdf1c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/7cdf1c527adad306eeebe07bdd3933b2815945ff))
* Monitor mass update task status triggered by event ([633cd98](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/633cd980084cec3b8614ffd0ae1c464fdc3d452c))
* Refactor in terminate product scheduled service ([7604257](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/76042572237fda808a6f5662ff29f422c2410493))
* rename maven modules and folders to product-inventory ([1da6a79](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1da6a79dfcc12a5b891c98fd5b68506327270b56))
* s3 integration and uploading and downloading files support ([1eb8f0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/1eb8f0cfc406bd4c7ab78aee105fcf30714c6421))
* updating README ([121cb11](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/121cb11c22f132505614e7ee5520a5a92b3af930))

## [1.4.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.4.2...1.4.3) (2024-04-30)


### Bug Fixes

* fixing elastic search production host in values file ([8720719](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/87207195f0f21e9d7e759e3bb295f9c6b54d188e))

## [1.4.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.4.1...1.4.2) (2024-04-29)


### Bug Fixes

* add image pull secret to can pull image from openshift registry ([a44e0b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a44e0b0c7ac20798b69e2ff6517aea57b43083a6))
* fix app_name use innovation instead of innov, same value like ([bd5f64b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/bd5f64b044e36b9702b2e9aa9ef01dd3bb72e3fe))
* removed semantic image and move it to gitlab variables ([22cec09](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/22cec09549d34a26c0d11b232d9fe8269a6ef945))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.4.0...1.4.1) (2024-04-23)


### Bug Fixes

* move innovation from prod to staging ([ab6b4d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ab6b4d3ffc0acbc897f72494c254c0629ca35c4a))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.3.2...1.4.0) (2024-04-23)


### Features

* **innov:** add support for innovation environment in pipeline ([dd7d29f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/dd7d29f75df982e11472a191092a6f0d7eb7690c))

## [1.3.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.3.1...1.3.2) (2024-04-21)


### Bug Fixes

* the sed expression used to change the version and appVersion in ([a042d2e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/a042d2ea243b8875bfd6e4410867fa60529ad773))

## [1.3.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.3.0...1.3.1) (2024-04-18)


### Bug Fixes

* updating orange-mongodb helm chart version ([e8fd1a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e8fd1a65ad6a02f693c937139e254edd3b7a6cee))
* updating servers list in swagger ui using adding relative path and staging and production urls ([00ff780](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/00ff780293edb2d0d093d08d6e62960e37ea21a7))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/compare/1.2.0...1.3.0) (2024-04-04)


### Bug Fixes

* checkstyle ([b514701](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b5147015a87a422598e56c488bc0c22b20f44f17))
* **devops:** Remove `develop` branch from `.releaserc` ([76302db](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/76302db0eb021b1ab0084eb19d1745fdf37329ee))
* disable fluentd in integration environment ([9096186](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/909618621580c35affd504657d0c6e42e2d1189b))
* empty variable expansion issue ([26e8249](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/26e8249dcf1cb73ee589f9a4983eb0b9ca6246d1))
* fixing a bug where empty list was returned for all lists if fields is selected instead of null ([3328bb8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3328bb8cd8f456699200c55ca9853c723c371033))
* fixing a bug where patch by id can be used to override [@type](https://gitlab.tech.orange/type) to ProductRef ([82cde2a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/82cde2aa3666aca3e7e4eb473a946ce22a953398))
* Fixing issues after the merge related to empty aTtype. ([175d3a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/175d3a4b497c7302228c33f4abb7160439e7674b))
* fixing some regression where API allowed any case for IsUnder before ([7e6b1f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/7e6b1f493b21c71d5d0fa95aff92cf6d4906cef5))
* fixing sonar issues detected on main branch ([d916c5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/d916c5c35edb394af4ef9b8b4163bdc6c685d7a8))
* Get PhysicalProduct with worng @Type ([3cc0965](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3cc09655fa87070bc15372075db01b29c21de873))
* **PATCH Bulk Method:** required fields ([14f40cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/14f40cb1726537664254b6beb8d42c083df33310))
* Set Creation Date ([eb49d07](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/eb49d0739d05fc86b61a14caca322d441bcad215))
* wrong [@type](https://gitlab.tech.orange/type) was returned for PhysicalProduct and ShipmentProduct in list api ([c583b18](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/c583b18b28fe5198e5b5c3ce3f1685c9465da6ed))


### Features

*  add filter to get all product ([bef4ec9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/bef4ec9ddd1620f4bf5b4ab0687c20ff67aed169))
*  Evolve get All Products Sorting ([276d83b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/276d83b562eee3af42f2f63beb1f2adb956ba0ac))
*  Refactor code for pagination and filter ([ff77529](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/ff7752996bb68ec5e123ff3cffab0c65fe6a4107))
* add acceptance tests step to be run to pipeline ([3c86484](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/3c864842f8caf025d2d74e1b3ff3c3ca99123a98))
* Add Rules for IsUnder and @Contract ([d0fae22](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/d0fae2224cd0719534a021ddd86373b916043447))
* Add security with bearer scheme to open api specification ([4028e3e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4028e3ed237ba56287e078ae1c19a96400c85be5))
* adding atType migration ([044a048](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/044a04875a721a31759a70cce8714aa4c267ae1b))
* adding unit tests for migration ([55f81a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/55f81a143b3200e8595109291dbf79217f9e6348))
* Configure scheduler for the job that terminates products ([48312f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/48312f38819fe1e1842b3e9c6eafe2d5a42fe735))
* Consume TMF events in CPIB & update Order Rejected ([928ef3b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/928ef3bc2fce378d339a84883c805eb059ac26a1))
* Consume TMF events in CPIB & update Order Rejected ([375ebb8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/375ebb81bcfa40845a15bf9ac8f7f0de628152c8))
* consume user authorization api TMF-672 ([381b742](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/381b74208d6330f9a679bd50517535ebc170841a))
* create product without parent contract if physicalProduct ([b81e271](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b81e27174fc125e68becc22c479dfa563cc70072))
* enable sonar quality gate ([5c2c9eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/5c2c9ebef0b10f6f9bc805d6d59b1c55386f2802))
* Enhance postman collection to be able to execute full scenario ([5f513ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/5f513ad1c06a5cf80a6e9680ae8e93bd252575d5))
* Evolve to add ParentContract relationship ([87932f2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/87932f2a944b3fb2c280ce22b9bbd9203ae282b3))
* Evolve to instantiate Tangible product ([88f2ad9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/88f2ad95eedbe8bd2344ce4dff80ff830ff48020))
* filter by product characteristics ([91223b7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/91223b7ec47b77a8c559f7e0eddbb92c7cd5c6ed))
* increase tmf compliance to 98 percent ([8e1f5da](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/8e1f5dab324db75a509ff91305ffcb4e5a999597))
* introduce mongock migration tool and adding skeleton migration ([86a64ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/86a64ea303c3cc4fc1715be972635b5fe8ddb7d9))
* make Abstract test inherit from mongock to allow multiple migration runs ([9cabeff](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/9cabeff9a3b05a14c5f30124ed0b5803feee6cad))
* redirect requests to / to swagger-ui/index.html ([77d7edb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/77d7edb28d0f6e0d274ec9da37e9a15b80804881))
* refactor realizing resource with logical test ([661d03b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/661d03b61a6c40095e5ff624e732b3747d8c5429))
* **refactor:** refactor authorization and rename createProducts to createProduct ([46f070d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/46f070db52ef00e241afd9696e48fd4245a3b69e))
* **refactor:** refactor status change to active and terminate validation to use java streams ([b0ff7de](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/b0ff7de4e1e8e51058c36f95796de3cd76e5a9e5))
* **refactor:** use lombok required arg contstructor ([75bc748](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/75bc74866d8ce5807f5528455505b9f4a077631c))
* remove mongock inherit from Abstract test & clear migration history in mongock integration test instead ([4048e93](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4048e930df5df3489a8c1eee7241308fc8f11465))
* Set Creation Date to sysdate for new product created with status is created ([4f9ad91](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/4f9ad913a2a453922cbb8699f3f13e83a662e076))
* Terminate product ([e9d0c62](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e9d0c6221a83c670275495f21ec23a62a9d8f373))
* Update Status Active ([08341a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/08341a1601bf596f538c7bb84031e8c1183e6f10))
* using db transactions for create and patch apis ([e6e3b2c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/product-inventory/commit/e6e3b2c6a06825728aa7727bcc856de8d9be99c6))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/1.1.3...1.2.0) (2024-2-20)


### Features

* adding helm values for production and pipeline config ([df32b45](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/df32b45dfe416f49433ff6e9ba2a450b806d9ea4))

## [1.1.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/1.1.2...1.1.3) (2024-1-17)


### Bug Fixes

* adding access control expose headers for extra headers ([b4b91be](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/b4b91be1b6aaadeb78e92b3c534c77693abf24c3))

## [1.1.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/1.1.1...1.1.2) (2024-1-11)


### Bug Fixes

* added missing configuration ([0b4ce64](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/0b4ce64a6065fdd33ef17d1eff0051f31e80d347))

## [1.1.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/1.1.0...1.1.1) (2024-1-11)


### Bug Fixes

* added missing configuration ([f708bb7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/f708bb78f08328c16ce816188d19114cb1aa9876))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/1.0.0...1.1.0) (2024-1-3)


### Bug Fixes

* add constants ([98707b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/98707b5128d591d23a9f161eb29eab041f51f0df))
* add keystore and cert to dockerfile ([b76b571](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/b76b571c6520aa418d5c93cd76f1f1e56d3260b2))
* Add verification required fields ([04aab1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/04aab1f066680d2925ba136ce48dc2087d21f722))
* adding port 8080 to services in review and integration ([9b59cb5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9b59cb5d36ab9710bdde102e6bcc8259acbc37d1))
* adding webclient bean for test without oauth2 config and formatting ([97057f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/97057f475018556e62dda5bb7a4699754d6689ca))
* change the version and container image ([cf5c5b7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/cf5c5b78455320ed583bcdceb0cf712dad1ec6de))
* change type from primitive to wrapper ([9c97c5f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9c97c5f1ae108bce99b9ac197aa8768f282a04a7))
* fix build issue ([6f552d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/6f552d2d25b7c9301094eab29f3dffae42fd0694))
* fix build issues ([003f137](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/003f13766cccc61d51c3338ff23bd555596c2204))
* fix comments ([304683a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/304683ae071143a2d1920631ab6f3c30c4209046))
* fix conflicts with main branch and develop ([fe09e04](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/fe09e0472662fe4d721ec8c6ef2841f88a772974))
* fix docker-hadolint issue ([80c571c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/80c571c6c604e93878ccddec714bf372a06e85c1))
* fix dtos ([51ef49d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/51ef49d7f82e7d58bf8958f0210b738328e46091))
* fix Error dto ([003cafe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/003cafe85112f63886325df09522250d0adf9e89))
* fix exception messages ([037b568](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/037b56856a9ecb686117f5c666db989262697172))
* fix failed tests ([d9ec902](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/d9ec902fe4585f545817c01281f7229c00574f20))
* fix get all product by filter with list of values ([f94ef9f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/f94ef9f8d5d3845327fcdbebc64757ed157222f0))
* fix getProducts failed tests ([6eba79e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/6eba79e8243aa14781ab8fef73ad7f329aa1fdf7))
* fix merge issues ([6c6c17b](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/6c6c17b27d68e936cf8cb1136d1cc65426756523))
* Fix paging by Query ([1ef8440](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/1ef8440515245bc142473fd5495719c2db4b996d))
* fix sonar issues ([e67c4b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/e67c4b05a915a03288ca0ab5e4c6f218933688ac))
* fix sonarLint issues ([9843ff3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9843ff36e226a3ddd7fdb80ecb47859ac8fe976e))
* fixing load application properties dynamically in kubernetes ([f7a0326](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/f7a03265b6e63dfa33045051c681fca02029e5d1))
* Fixing spec module version in service module ([241eb38](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/241eb38eb243c147770a8b30259432224f387725))
* Get Product with invalid Id ([1019a3a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/1019a3a279881227afa201d8155382c4139828d7))
* handling filter of query params of different types and or case ([a3dba28](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a3dba28877f5c98372024d22219ebbf13e80ce06))
* improving performance of generating href for products ([aa47b92](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/aa47b927faa2726da5cd171fee7b0e40b9b526c9))
* keycloak url in integration environment ([fe8d60e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/fe8d60e1266049b354fb7b7e1b86b17e576e4bf2))
* merge prepare configuration and fix merge issues ([a9be05e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a9be05e767f7982a82b13b27da7affa53884680d))
* modify odacat url and service name ([0c575a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/0c575a60b972dee42e67c75a42d27f0e6207ad7b))
* modify patch group method to use streams and to loop once ([2b3030d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2b3030df47ad5b591270e4ccbbc62e2609095056))
* modify service name ([63ab361](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/63ab3619bb765d8b3c9d44e7178d6933c2043f62))
* modify user on dockerfile ([caa6767](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/caa6767b962dcac2b74e91b7554aea03f5a68160))
* Patch Active status with operationalStatus ([081f9e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/081f9e33d8fafa6fb91ec13890d7f0dbbaac4716))
* remove not used code ([34b9601](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/34b96017f907d5ea8517b0b13e4ee6d5e42491e4))
* rename getParameter method ([72f54a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/72f54a66f0aa34c4a8b69d1626da97a56b93e9b9))
* retrieve verification on reliesFrom product relationship when update status to Active ([248e975](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/248e975be7a9fffa3ca5da39106d7430ce6a068c))
* spec module not published to maven snapshot repository ([9a41a0f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9a41a0f2eb8b6f535063590add303908582297c7))
* update and fix code with builder ([dd12251](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/dd12251a727ef4ba62f2fa8ac74fc87a786f88db))
* update code to answer the comments ([66d7d4a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/66d7d4af125fdf87b2507a00260a14bf8928eb15))
* Update Post Creation to Set Product Relationship as Empty List ([c7efa95](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/c7efa95b7dc466577f1cf8ccaf366eca1f9cdd5a))
* Update Post Creation to Set Product Relationship as Empty List ([2e915d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2e915d86184b85285afd31a3f96243298e2b6def))
* update postman collection ([fa415ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/fa415adf9d8a75c71a6e663e151a3bfbe63ad164))


### Features

* add api-getting-started.md, api-compliance-report.pdf and update readme.md ([bcdd0b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/bcdd0b1453157c2e2d73587de3dfe973ac61c991))
* add comparing error details example in tests ([e51b191](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/e51b19137d41c69d0391ea3c6fbbfed7a65a5add))
* add feature validation ([9bf6f7e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9bf6f7eed899e7ec89177404ff165c7af62ed5c5))
* add field parameter verification ([a5eb98c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a5eb98cde634712118214720f602afee17e47bc2))
* add fields check by json ([65659a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/65659a44fd55aee807a6bb10bbc2c39df4c6e58b))
* add integration testing configuration + update code to use builder ([ff8641e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/ff8641e462e9cd1c5d9f0d512396fb7ee242d3b4))
* add json file ([198a1be](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/198a1bee8b408afdec409200c319a78b5570220d))
* add method to compare list of product entity with list of product dto ([d680e7c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/d680e7c99f57a83e24b85d66e125c9284a7c3b2a))
* add method to compare product entity with product dto ([43c66f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/43c66f982a88ad7fb344d7426146004656aaa659))
* add method to compare product entity with product dto and fix issues related to dtos and models ([33e259c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/33e259c588131a269b9bad71cfb459868dcf0dde))
* add patch bulk productRelationship ([cad4eea](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/cad4eea8d1fec31d96c4dbc525ee451d5fa65492))
* add patch bulk productRelationship ([bc133c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/bc133c7f2724f24d709cc6a2f3a1bfff1e4aa420))
* add validate and separate services ([e30871a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/e30871a18e38ae6f0faf189de02a20f118357303))
* adding authorization to apis and improving tests ([643c054](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/643c0540a89a4164bd385d2d24775428e9606672))
* adding db indexes for product ([dccb0fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/dccb0fe240e3ca4f2e5523e37ca74bc5751eeec8))
* adding extra checks for update start date and termination date ([444af2a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/444af2a410f1bac9889864c7fd9738e63d47bc0b))
* adding extra checks for update status to activated ([1f42a11](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/1f42a11a6b38c71ca756243d561cd1580325e127))
* adding extra checks for update status to terminated ([4a38a13](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/4a38a13f01941f2f3c8ab262b82aca5a87e4dfe7))
* adding secret for keycloak client key in deployment ([769ba1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/769ba1fa44c10eaaaff3fc69f160b3cc3a837ec2))
* adding value to product Characteristic ([3b1d8e2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/3b1d8e28ffaa4e8316c8879cc656caa375907b9b))
* apply json patch on Product patch API ([5825108](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/5825108dcd349640aa565b4d795fc40f07162fc9))
* apply patch on list of products using json patch ([34e8452](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/34e8452bee43e0ee28766b32378e15600e03fd6d))
* apply patch on list of products using json patch and fix sonar issues ([96cb862](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/96cb86255b2eb0d9345c9b6e705167040a03d0fa))
* authenticating to catalog using service account ([e7cd995](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/e7cd99515b103474d7c55d16ceda41603d44ce3e))
* Change Output Get By Id ([f29e6e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/f29e6e67cd15517cf1e7176405dbf056a64525d8))
* Create New Product which Is Child to an existing product ([cce0ab5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/cce0ab5c12b0e8813989716701bb7c66c1809a45))
* delete productId ([dcc84e5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/dcc84e5551daa2f0aad99ef198259ef35bfaf889))
* ignore .java-version ([d17a6b3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/d17a6b32ade4aa6d4fde9998411db317d30b1ec4))
* merge json patch with sprint 7 branch ([426eadb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/426eadb6fc3a51cf9b41e749a0f05b2eb35a5ed8))
* modify and add test cases ([2b8e155](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2b8e155b2e7983db6890c1fd3de90ac517d92217))
* modify the extracted path ([35e38fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/35e38fe4113a59dbd4690a1b0ae81b49648bd64d))
* modify the return type of groupOperationsByProductId method to be consumed in patch by id ([eb4fe85](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/eb4fe85b55b6da6cb91c8490d8cbd5649ae6668b))
* **performance:** implement default limit and max limit for get products api ([33bdbcf](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/33bdbcf817da6fcc6517e139f76186cf0876a53b))
* refactor service productEntity mapper in controller ([2175b4c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2175b4cfb0e28b44f179e6fb0902797c877c5dff))
* review and answer comments ([a6d0072](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a6d00729a02a949ea46d7ec9971be796497fcdc7))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([0f0cd2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/0f0cd2db6b64b19bd4f6e465319e68b97796f651))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([239c0ca](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/239c0ca8b81fdd18178e525ad754e3a019209698))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([98435d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/98435d293813452e4da5bf66e11029983c30db07))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([101df0e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/101df0e17bf78ce34b03b0512c4870b0e3459539))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([b002f45](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/b002f45319d940ae9de74fab4309b539e9dd399c))
* update required field from id to [@type](https://gitlab.tech.orange/type) on Characteristic ([c8649ce](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/c8649ce993b05172cc7504c0fdc07306fcb29a8a))
* use rfc6902 add to array for productRelationship ([53ee7ce](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/53ee7ce613df5d8cb758c5272c78cb9ac2ed0478))
* Using spec module api definition file in swagger ui ([a110b13](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a110b13338aeef0ae44dea562e0376a24d7477f9))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/dev-1.1.0...dev-1.2.0) (2023-12-18)


### Bug Fixes

* Add verification required fields ([04aab1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/04aab1f066680d2925ba136ce48dc2087d21f722))
* adding port 8080 to services in review and integration ([9b59cb5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9b59cb5d36ab9710bdde102e6bcc8259acbc37d1))
* update postman collection ([fa415ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/fa415adf9d8a75c71a6e663e151a3bfbe63ad164))


### Features

* Using spec module api definition file in swagger ui ([a110b13](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a110b13338aeef0ae44dea562e0376a24d7477f9))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/compare/dev-1.0.0...dev-1.1.0) (2023-12-8)


### Bug Fixes

* adding webclient bean for test without oauth2 config and formatting ([97057f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/97057f475018556e62dda5bb7a4699754d6689ca))
* fixing load application properties dynamically in kubernetes ([f7a0326](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/f7a03265b6e63dfa33045051c681fca02029e5d1))
* Fixing spec module version in service module ([241eb38](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/241eb38eb243c147770a8b30259432224f387725))
* Get Product with invalid Id ([1019a3a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/1019a3a279881227afa201d8155382c4139828d7))
* handling filter of query params of different types and or case ([a3dba28](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a3dba28877f5c98372024d22219ebbf13e80ce06))
* improving performance of generating href for products ([aa47b92](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/aa47b927faa2726da5cd171fee7b0e40b9b526c9))
* keycloak url in integration environment ([fe8d60e](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/fe8d60e1266049b354fb7b7e1b86b17e576e4bf2))
* modify patch group method to use streams and to loop once ([2b3030d](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2b3030df47ad5b591270e4ccbbc62e2609095056))
* Patch Active status with operationalStatus ([081f9e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/081f9e33d8fafa6fb91ec13890d7f0dbbaac4716))
* spec module not published to maven snapshot repository ([9a41a0f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/9a41a0f2eb8b6f535063590add303908582297c7))
* Update Post Creation to Set Product Relationship as Empty List ([c7efa95](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/c7efa95b7dc466577f1cf8ccaf366eca1f9cdd5a))
* Update Post Creation to Set Product Relationship as Empty List ([2e915d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2e915d86184b85285afd31a3f96243298e2b6def))


### Features

* add api-getting-started.md, api-compliance-report.pdf and update readme.md ([bcdd0b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/bcdd0b1453157c2e2d73587de3dfe973ac61c991))
* add patch bulk productRelationship ([cad4eea](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/cad4eea8d1fec31d96c4dbc525ee451d5fa65492))
* add patch bulk productRelationship ([bc133c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/bc133c7f2724f24d709cc6a2f3a1bfff1e4aa420))
* add validate and separate services ([e30871a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/e30871a18e38ae6f0faf189de02a20f118357303))
* adding authorization to apis and improving tests ([643c054](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/643c0540a89a4164bd385d2d24775428e9606672))
* adding db indexes for product ([dccb0fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/dccb0fe240e3ca4f2e5523e37ca74bc5751eeec8))
* adding extra checks for update start date and termination date ([444af2a](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/444af2a410f1bac9889864c7fd9738e63d47bc0b))
* adding extra checks for update status to activated ([1f42a11](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/1f42a11a6b38c71ca756243d561cd1580325e127))
* adding extra checks for update status to terminated ([4a38a13](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/4a38a13f01941f2f3c8ab262b82aca5a87e4dfe7))
* adding secret for keycloak client key in deployment ([769ba1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/769ba1fa44c10eaaaff3fc69f160b3cc3a837ec2))
* adding value to product Characteristic ([3b1d8e2](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/3b1d8e28ffaa4e8316c8879cc656caa375907b9b))
* apply json patch on Product patch API ([5825108](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/5825108dcd349640aa565b4d795fc40f07162fc9))
* apply patch on list of products using json patch ([34e8452](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/34e8452bee43e0ee28766b32378e15600e03fd6d))
* apply patch on list of products using json patch and fix sonar issues ([96cb862](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/96cb86255b2eb0d9345c9b6e705167040a03d0fa))
* authenticating to catalog using service account ([e7cd995](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/e7cd99515b103474d7c55d16ceda41603d44ce3e))
* Change Output Get By Id ([f29e6e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/f29e6e67cd15517cf1e7176405dbf056a64525d8))
* Create New Product which Is Child to an existing product ([cce0ab5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/cce0ab5c12b0e8813989716701bb7c66c1809a45))
* delete productId ([dcc84e5](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/dcc84e5551daa2f0aad99ef198259ef35bfaf889))
* ignore .java-version ([d17a6b3](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/d17a6b32ade4aa6d4fde9998411db317d30b1ec4))
* merge json patch with sprint 7 branch ([426eadb](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/426eadb6fc3a51cf9b41e749a0f05b2eb35a5ed8))
* modify the extracted path ([35e38fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/35e38fe4113a59dbd4690a1b0ae81b49648bd64d))
* modify the return type of groupOperationsByProductId method to be consumed in patch by id ([eb4fe85](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/eb4fe85b55b6da6cb91c8490d8cbd5649ae6668b))
* refactor service productEntity mapper in controller ([2175b4c](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/2175b4cfb0e28b44f179e6fb0902797c877c5dff))
* review and answer comments ([a6d0072](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/a6d00729a02a949ea46d7ec9971be796497fcdc7))
* update required field from id to [@type](https://gitlab.tech.orange/type) on Characteristic ([c8649ce](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/c8649ce993b05172cc7504c0fdc07306fcb29a8a))
* use rfc6902 add to array for productRelationship ([53ee7ce](https://gitlab.tech.orange/disco/disco-oda-components/disco-product-inventory/customer-product-installed-base/commit/53ee7ce613df5d8cb758c5272c78cb9ac2ed0478))

# 1.0.0 (2023-10-15)


### Bug Fixes

*  unit tests for ProductService ([b109eba](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b109eba23addfce22bd954b23b7f12ff5b1e2088))
* add constants ([98707b5](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/98707b5128d591d23a9f161eb29eab041f51f0df))
* add keystore and cert to dockerfile ([b76b571](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b76b571c6520aa418d5c93cd76f1f1e56d3260b2))
* change the version and container image ([cf5c5b7](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/cf5c5b78455320ed583bcdceb0cf712dad1ec6de))
* change type from primitive to wrapper ([9c97c5f](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/9c97c5f1ae108bce99b9ac197aa8768f282a04a7))
* fix build issue ([6f552d2](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/6f552d2d25b7c9301094eab29f3dffae42fd0694))
* fix build issues ([003f137](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/003f13766cccc61d51c3338ff23bd555596c2204))
* fix comments ([304683a](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/304683ae071143a2d1920631ab6f3c30c4209046))
* fix docker-hadolint issue ([80c571c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/80c571c6c604e93878ccddec714bf372a06e85c1))
* fix dtos ([51ef49d](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/51ef49d7f82e7d58bf8958f0210b738328e46091))
* fix Error dto ([003cafe](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/003cafe85112f63886325df09522250d0adf9e89))
* fix exception messages ([037b568](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/037b56856a9ecb686117f5c666db989262697172))
* fix failed tests ([d9ec902](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/d9ec902fe4585f545817c01281f7229c00574f20))
* fix get all product by filter with list of values ([f94ef9f](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/f94ef9f8d5d3845327fcdbebc64757ed157222f0))
* fix getProducts failed tests ([6eba79e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/6eba79e8243aa14781ab8fef73ad7f329aa1fdf7))
* fix merge conflicts of cpib-sprint-3 branch and refactor_get_with_criteria ([910c09d](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/910c09d26f6fd1a5d64ceaf64570e8ea5e30a98d))
* fix merge issues ([6c6c17b](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/6c6c17b27d68e936cf8cb1136d1cc65426756523))
* Fix paging by Query ([1ef8440](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/1ef8440515245bc142473fd5495719c2db4b996d))
* fix sonar issues ([e67c4b0](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/e67c4b05a915a03288ca0ab5e4c6f218933688ac))
* fix sonarLint issues ([9843ff3](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/9843ff36e226a3ddd7fdb80ecb47859ac8fe976e))
* ignore unit tests ([a670234](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/a670234b640bce4f00887df1e9564e70890dcac7))
* merge prepare configuration and fix merge issues ([a9be05e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/a9be05e767f7982a82b13b27da7affa53884680d))
* modify odacat url and service name ([0c575a6](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/0c575a60b972dee42e67c75a42d27f0e6207ad7b))
* modify service name ([63ab361](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/63ab3619bb765d8b3c9d44e7178d6933c2043f62))
* modify swagger config ([97d149f](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/97d149f6cdc21640dffdf655d99758a392de3090))
* modify swagger for get request & fix get with fields ([c4ae2e9](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/c4ae2e9cbfadecb6c7f78ce4560d011ba96c5458))
* modify user on dockerfile ([caa6767](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/caa6767b962dcac2b74e91b7554aea03f5a68160))
* patch request with relationship products ([5c9ac51](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/5c9ac51f3a5759962e47d4d07a6d24294d55d7cc))
* remove not used code ([34b9601](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/34b96017f907d5ea8517b0b13e4ee6d5e42491e4))
* rename getParameter method ([72f54a6](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/72f54a66f0aa34c4a8b69d1626da97a56b93e9b9))
* update and fix code with builder ([dd12251](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/dd12251a727ef4ba62f2fa8ac74fc87a786f88db))
* update code to answer the comments ([66d7d4a](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/66d7d4af125fdf87b2507a00260a14bf8928eb15))
* update ODACAT url ([042e646](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/042e646eccbc34217364941d2bdc9de6207d5d82))


### Features

*  handle logging for CPIB ([3ec3d8c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/3ec3d8c631c85b02be72b5550807b1fc4aad99ca))
* Add check by ProductOrderItem on Post request ([e203be0](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/e203be0436a11d65d1725e9735ddb3cc8505e3e7))
* add comparing error details example in tests ([e51b191](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/e51b19137d41c69d0391ea3c6fbbfed7a65a5add))
* add feature validation ([9bf6f7e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/9bf6f7eed899e7ec89177404ff165c7af62ed5c5))
* add field parameter verification ([a5eb98c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/a5eb98cde634712118214720f602afee17e47bc2))
* add fields check by json ([65659a4](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/65659a44fd55aee807a6bb10bbc2c39df4c6e58b))
* Add href generation and fix get request with header ([8882b68](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/8882b68ca7ad8efbb8c5658588985140d4354ccc))
* add integration testing configuration + update code to use builder ([ff8641e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/ff8641e462e9cd1c5d9f0d512396fb7ee242d3b4))
* add json file ([198a1be](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/198a1bee8b408afdec409200c319a78b5570220d))
* Add logging configuration ([062f424](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/062f4242c79e08df2bbb622c2a6a1f178b1a6add))
* add method to compare list of product entity with list of product dto ([d680e7c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/d680e7c99f57a83e24b85d66e125c9284a7c3b2a))
* add method to compare product entity with product dto ([43c66f9](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/43c66f982a88ad7fb344d7426146004656aaa659))
* add method to compare product entity with product dto and fix issues related to dtos and models ([33e259c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/33e259c588131a269b9bad71cfb459868dcf0dde))
* add Patch request with status and operational status & fix get without paging ([8bdc817](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/8bdc817c5f948b221be9e83472be323a88601469))
* add patch with status and operationalStatus check ([2236add](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/2236add5a8eacc4e933e5222842601d41c5f31b0))
* add the ErrorHandler to  webClient Configuration ([cee9eec](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/cee9eecfc42502b1a69495601f7bc939ee306f9d))
* Add the lifecycle check for check validity of state transitions ([7f9a6c5](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/7f9a6c5bfaa9d5c42d0e8d6423e086047f771269))
* Add unit tests for ProductService ([b49019b](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b49019b3f9c74625f5e428cd386a05377302fd71))
* modification restTemplate webClient implementation because blocking thread join pool ([daa5dd1](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/daa5dd1e5a7408e68ec54fae31bfb1e042e49dea))
* modify and add test cases ([2b8e155](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/2b8e155b2e7983db6890c1fd3de90ac517d92217))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([0f0cd2d](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/0f0cd2db6b64b19bd4f6e465319e68b97796f651))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([239c0ca](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/239c0ca8b81fdd18178e525ad754e3a019209698))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([98435d2](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/98435d293813452e4da5bf66e11029983c30db07))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([101df0e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/101df0e17bf78ce34b03b0512c4870b0e3459539))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([b002f45](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b002f45319d940ae9de74fab4309b539e9dd399c))

# 1.0.0 (2023-10-15)


### Bug Fixes

*  unit tests for ProductService ([b109eba](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b109eba23addfce22bd954b23b7f12ff5b1e2088))
* add constants ([98707b5](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/98707b5128d591d23a9f161eb29eab041f51f0df))
* add keystore and cert to dockerfile ([b76b571](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b76b571c6520aa418d5c93cd76f1f1e56d3260b2))
* change the version and container image ([cf5c5b7](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/cf5c5b78455320ed583bcdceb0cf712dad1ec6de))
* change type from primitive to wrapper ([9c97c5f](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/9c97c5f1ae108bce99b9ac197aa8768f282a04a7))
* fix build issue ([6f552d2](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/6f552d2d25b7c9301094eab29f3dffae42fd0694))
* fix build issues ([003f137](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/003f13766cccc61d51c3338ff23bd555596c2204))
* fix comments ([304683a](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/304683ae071143a2d1920631ab6f3c30c4209046))
* fix docker-hadolint issue ([80c571c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/80c571c6c604e93878ccddec714bf372a06e85c1))
* fix dtos ([51ef49d](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/51ef49d7f82e7d58bf8958f0210b738328e46091))
* fix Error dto ([003cafe](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/003cafe85112f63886325df09522250d0adf9e89))
* fix exception messages ([037b568](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/037b56856a9ecb686117f5c666db989262697172))
* fix failed tests ([d9ec902](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/d9ec902fe4585f545817c01281f7229c00574f20))
* fix get all product by filter with list of values ([f94ef9f](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/f94ef9f8d5d3845327fcdbebc64757ed157222f0))
* fix getProducts failed tests ([6eba79e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/6eba79e8243aa14781ab8fef73ad7f329aa1fdf7))
* fix merge conflicts of cpib-sprint-3 branch and refactor_get_with_criteria ([910c09d](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/910c09d26f6fd1a5d64ceaf64570e8ea5e30a98d))
* fix merge issues ([6c6c17b](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/6c6c17b27d68e936cf8cb1136d1cc65426756523))
* Fix paging by Query ([1ef8440](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/1ef8440515245bc142473fd5495719c2db4b996d))
* fix sonar issues ([e67c4b0](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/e67c4b05a915a03288ca0ab5e4c6f218933688ac))
* fix sonarLint issues ([9843ff3](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/9843ff36e226a3ddd7fdb80ecb47859ac8fe976e))
* ignore unit tests ([a670234](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/a670234b640bce4f00887df1e9564e70890dcac7))
* merge prepare configuration and fix merge issues ([a9be05e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/a9be05e767f7982a82b13b27da7affa53884680d))
* modify odacat url and service name ([0c575a6](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/0c575a60b972dee42e67c75a42d27f0e6207ad7b))
* modify service name ([63ab361](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/63ab3619bb765d8b3c9d44e7178d6933c2043f62))
* modify swagger config ([97d149f](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/97d149f6cdc21640dffdf655d99758a392de3090))
* modify swagger for get request & fix get with fields ([c4ae2e9](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/c4ae2e9cbfadecb6c7f78ce4560d011ba96c5458))
* modify user on dockerfile ([caa6767](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/caa6767b962dcac2b74e91b7554aea03f5a68160))
* patch request with relationship products ([5c9ac51](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/5c9ac51f3a5759962e47d4d07a6d24294d55d7cc))
* remove not used code ([34b9601](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/34b96017f907d5ea8517b0b13e4ee6d5e42491e4))
* rename getParameter method ([72f54a6](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/72f54a66f0aa34c4a8b69d1626da97a56b93e9b9))
* update and fix code with builder ([dd12251](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/dd12251a727ef4ba62f2fa8ac74fc87a786f88db))
* update code to answer the comments ([66d7d4a](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/66d7d4af125fdf87b2507a00260a14bf8928eb15))
* update ODACAT url ([042e646](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/042e646eccbc34217364941d2bdc9de6207d5d82))


### Features

*  handle logging for CPIB ([3ec3d8c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/3ec3d8c631c85b02be72b5550807b1fc4aad99ca))
* Add check by ProductOrderItem on Post request ([e203be0](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/e203be0436a11d65d1725e9735ddb3cc8505e3e7))
* add comparing error details example in tests ([e51b191](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/e51b19137d41c69d0391ea3c6fbbfed7a65a5add))
* add feature validation ([9bf6f7e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/9bf6f7eed899e7ec89177404ff165c7af62ed5c5))
* add field parameter verification ([a5eb98c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/a5eb98cde634712118214720f602afee17e47bc2))
* add fields check by json ([65659a4](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/65659a44fd55aee807a6bb10bbc2c39df4c6e58b))
* Add href generation and fix get request with header ([8882b68](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/8882b68ca7ad8efbb8c5658588985140d4354ccc))
* add integration testing configuration + update code to use builder ([ff8641e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/ff8641e462e9cd1c5d9f0d512396fb7ee242d3b4))
* add json file ([198a1be](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/198a1bee8b408afdec409200c319a78b5570220d))
* Add logging configuration ([062f424](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/062f4242c79e08df2bbb622c2a6a1f178b1a6add))
* add method to compare list of product entity with list of product dto ([d680e7c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/d680e7c99f57a83e24b85d66e125c9284a7c3b2a))
* add method to compare product entity with product dto ([43c66f9](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/43c66f982a88ad7fb344d7426146004656aaa659))
* add method to compare product entity with product dto and fix issues related to dtos and models ([33e259c](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/33e259c588131a269b9bad71cfb459868dcf0dde))
* add Patch request with status and operational status & fix get without paging ([8bdc817](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/8bdc817c5f948b221be9e83472be323a88601469))
* add patch with status and operationalStatus check ([2236add](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/2236add5a8eacc4e933e5222842601d41c5f31b0))
* add the ErrorHandler to  webClient Configuration ([cee9eec](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/cee9eecfc42502b1a69495601f7bc939ee306f9d))
* Add the lifecycle check for check validity of state transitions ([7f9a6c5](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/7f9a6c5bfaa9d5c42d0e8d6423e086047f771269))
* Add unit tests for ProductService ([b49019b](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b49019b3f9c74625f5e428cd386a05377302fd71))
* modification restTemplate webClient implementation because blocking thread join pool ([daa5dd1](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/daa5dd1e5a7408e68ec54fae31bfb1e042e49dea))
* modify and add test cases ([2b8e155](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/2b8e155b2e7983db6890c1fd3de90ac517d92217))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([0f0cd2d](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/0f0cd2db6b64b19bd4f6e465319e68b97796f651))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([239c0ca](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/239c0ca8b81fdd18178e525ad754e3a019209698))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([98435d2](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/98435d293813452e4da5bf66e11029983c30db07))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([101df0e](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/101df0e17bf78ce34b03b0512c4870b0e3459539))
* **test-lizard:** add lizard profile to use the Product Catalog API of Lizard ([b002f45](https://gitlab.tech.orange/disco/disco-product-inventory/customer-product-installed-base/commit/b002f45319d940ae9de74fab4309b539e9dd399c))
