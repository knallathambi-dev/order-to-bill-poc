# [1.16.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.15.0...1.16.0) (2026-04-23)


### Bug Fixes

* add missing mongo variable that we use it with default value ([2af8db2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2af8db211ae97b3e41b9e92fbcc4307de4a0596b))
* clean up OrderUpdateActionTest by removing unused imports ([c7ad161](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c7ad161a7af1d45b381572f33e024a9d29a1728d))
* correct tests by removing unused code ([02e54ef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/02e54efa1b36e1c0b51f3df0393d73ce5bbbbd15))
* fix gitleaks issue as it is [secure] positive ([1df6df8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1df6df8052946fb11f013e6e8ff38d36a84cdf5c))
* fix patch bulk for modification use case ([fb82cd6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fb82cd6b3b839ac2c9335cd5ed6a3023320e2841))
* fix spotbugs ([305f530](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/305f5303634f7a373f5d7d8a8b9319ae16f5aff4))
* handle DateCharacteristic in ResourcesReservationAction extractCharacteristicValue ([1e84f79](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1e84f79fe27ccb9077560682b28691f0a642218c))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([f707a0e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f707a0ea9378408af61ef1765511aaa0b640ead6))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([f41239b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f41239bce9eca2e3efa42c895a280988083ebda6))
* Incorrect currency formatting ([10b8931](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/10b8931c8ef4dc655beb48ae38506d1e9824bffa))
* map the price alteration for the installment price charge ([5a6cd0f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5a6cd0fa26545ddd1ed02774a1e056511c788716))
* map the productOfferingPrice into the installment items ([9c41f26](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9c41f2605dfd03c8bc6100bf0a501c7aa4576ca6))
* refactor ProductOrderMapper to improve item processing and relationship handling ([d80d53d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d80d53dd083d4afd562586d9f440a2bb7be00f0b))
* remove deprecated sonar.jacoco.reportPaths property ([3e58f19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3e58f19ee4efd0413b1b59df319a61cccfb0bbd9))
* reserve resources per order item to avoid duplicates ([b52c2b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b52c2b089e3a0ade7d62d1c0dc2623bfa9c4f92f))
* temporarily map DateCharacteristic as StringCharacteristic ([40e8ab6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/40e8ab6820095dc6f87403801f23b784058004ee))
* update API specifications and improve code readability ([b54688f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b54688fd23141513cc4201b420aae3c139b37dea))


### Features

* add [secure]-ui & selfcare-ui env vars ([e0de472](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e0de472aa7edb504c38b56d560248637fedab5c9))
* add [secure]-ui & selfcare-ui env vars ([82dd213](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/82dd2136bd99c1084d365f5d2a18a696c54bbdb6))
* add allowed cors origins ([b9db5df](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b9db5df87ce4de2f9cc0d2ea2ab7e9ecc3717296))
* add allowed cors origins ([91be844](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/91be844a06e2e2350311196047d1f66ba5b9c11e))
* add csp header for each request ([fe491ca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fe491ca24ea311f6effb24f6cf5f6fb5e37e9529))
* add micro FE URI ([9d270c6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9d270c628e8ea7510838c6f531560f8f618fd146))
* add requires value to RelationshipType enum ([67cccee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/67cccee2b26224e1c4d4be6083b7d2ff5c1cfe67))
* currency-specific rounding rules ([96185f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/96185f1575775b3aeed4f81e35a930a6f86871a0))
* enhance order capture to manage rollback ([de17cc9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/de17cc9e5a679098affd68e8ec8a1a5a71bddc95))
* enhance the end of bill cycle update when order state is changed ([8b3cb4f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8b3cb4f66db2afe49de21f5354f19fc55df74766))
* enhance the mapper to support multi type of product offering price ([6d8877c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6d8877c6eaae6d5e560ed09c4452a25869f181c5))
* evolve order capture to support installment feature ([a167f88](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a167f88de809b61be05da170aa1d0b1cb935e561))
* exclude swagger-ui from csp restriction ([df95ef5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/df95ef552c37f6285af551db599f268af7fde691))
* exclude swagger-ui from csp restriction ([5873c00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5873c002bd491e0c233c27bbbc9f917fdedc911e))
* extend characteristic mapping logic with date support ([61a8f5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/61a8f5c558be879e5f263fb77ab068df7e5f5d54))
* extend migration bill-cycle deferral to cover non-immediate payments ([3c79836](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3c798362dd701188855807c4779a0e2c8f2c7dc7))
* fix dateCharacteristic mapping implementation ([1b77af4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1b77af454a30d1f5fb13ef413850f83120d9d256))
* Incorrect currency formatting MGA without decimal ([3e4d01e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3e4d01e8415883741b4a3d121edac12f9f75f547))
* upgrade product inventory REST API spec to 1.3.0-SNAPSHOT with DateCharacteristics support ([33ecc62](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/33ecc62f23a7c595da1508527d959c481d793598))


### Performance Improvements

* replace multiple API calls with batch operations ([c679069](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c679069eec4ee3a46a78945d403a013382465aef))


### Reverts

* Rename relationship requires for shipments ([c62aea3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c62aea3dc92357b3ac6243f1e9d206b5523d6234))

# [1.15.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.14.0...1.15.0) (2025-12-02)


### Bug Fixes

* map term for migrate item ([7ec6421](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7ec64219b56a2bdf096a0141061f2c995a40aca2))
* use dynamic partyReferredType from context to resolve RelatedParty identification ([99cb4e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/99cb4e7e363b61d55dd7d1f6215f5d0cba267032))


### Features

* add reliesOn relationships for terminate items ([e26e18b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e26e18be601493fcf5e386e82309fdd9b9dfc259))
* enhance mapper to support configuration price atType ([4e5525c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4e5525ca254a858d2951a4b4d3ab894e41d41076))

# [1.14.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.13.0...1.14.0) (2025-11-13)


### Bug Fixes

* add mapping for TimePeriod to convert OffsetDateTime to Instant and resolve date mapping issue ([9913ec5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9913ec58fd04f455e1b1d695c3d4f451784da205))
* add multi-bundle modification support ([3d5f31d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3d5f31dd3d86fca9c68d518d2366615314c47553))
* add product identifier for the migrated product ([90eb3a5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/90eb3a5f4c7de57692a08e8501233e3ca5849d9c))
* add reliesOn relationship for migrate Item ([9d51a08](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9d51a08145a4d3c7ba44979344a8f3819cf98ac0))
* add reliesOn relationship for modify item in case of migration ([c6e75e9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c6e75e9b656a6e6dcf383e0a69d9dc5c529b2f55))
* change storage class to block-default-storage-class ([d7ccd9c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d7ccd9cd725a8805aa821c40339e8940a975022f))
* correct bundles migrate relationship in migration ([608bb8e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/608bb8e43f7d9070a09a36d157303435ba759dd2))
* correct product price update in terminate action ([bc92143](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bc921431116bd4a9901013a9cb47a632be7d291c))
* correct relies on relationship in migration ([4f2c828](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4f2c828540959402520c83fb3e7a3f0514d3a356))
* correct relieson relationship in modification use case ([1126df9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1126df9f7439511060c19db9e1ebd6dd90fafbe5))
* correct the product contract verification ([49c689b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/49c689b08365e38687549c6bd6d742471466cc84))
* correct the swagger ui ([b55b766](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b55b766ee19dd49e779db41ecba4f4fbb5b7de81))
* correct total discount calculation to use [@type](https://gitlab.tech.orange/type) instead of priceType during order instantiation action ([32c5b52](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/32c5b52ace416f9db4e4dcb7d67e92deea199a0e))
* disable arbiter ([ab7d89c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ab7d89c86b95f80c3760878e094b507d7638e718))
* enable mongo metrics ([b8f9c7c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b8f9c7c23f8fd94f4ee9347aecd48ec4913bde6a))
* enhance configuration relationships mapping ([58e4b79](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/58e4b79328d193d75bf09417b0842bda8c57a877))
* enhance reliesOnMigrate relationships for migration use case ([83afef8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/83afef8af81d93928b930d5571d45dd3fd8a0934))
* enhance the persisting of characteristics for migration use case ([c71965b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c71965b1a33c7da6e1638ebdb3d9b3d77b6b1539))
* fix gitleaks issue ([c193d42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c193d42c9678799e41e5258c55bc4f33b6698b13))
* fix gitleaks issue in helm and gitlab pipeline files ([d3acc56](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d3acc56f78b67c3683986e338ef5f2a9ac7ef388))
* fix Indentation ([7591b22](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7591b227b7be13f4d94dd9564544723422d94cf6))
* handle null configurationCharacteristicValues in technical eligibility check ([76653b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/76653b0eb2c1062742ff15deb805cbeacd02ed66))
* handle related party customer when check is disabled ([e9d4acf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e9d4acfbfa61110022deb923b5e42918e39005bf))
* map missing product characteristics ([13d3806](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/13d3806728903b44dfa747e540186a51e246dd81))
* missing price details when upgrading from Basic to Standard with Netflix option ([7c07dd7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7c07dd72e375923153b36d29c1f482ce2032fb34))
* modify SettingsServiceImplTest setup method to differ from setUp() implementation ([d5b62ac](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d5b62ac7fdd277e3ea4aaa3f5ca1a348e827f8c3))
* prevent termination from being held for non-acquisition orders ([efae496](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/efae4968342712bf23114c90e8f4922154b5fe52))
* remove config for promtail and istio, also add common labels for mongo ([727aab7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/727aab75cd25093e2ffad68f214a191e13839e3e))
* remove values files as we moved them to project gitlab-ci ([36f4825](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/36f482525fe31ed99acdc8fb3f069401882d81bb))
* skip appointment item processing when appointment not required ([c3713b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c3713b1a784b4f074ba56aba879ca0c4a964624c))
* **tests:** resolve unit test failures with missing role config ([712240f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/712240fd3b703a1b0ad0e95a22c0bd65c472cb68))
* use replicaset in mongo ([556dfd2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/556dfd23c65590970cbc305de2c910ce0650d69a))


### Features

* add product characteristic and resources for noChange and migrate Item ([ef95e4c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ef95e4cb4e128c136a63716c203aecd638f55f3e))
* add the product relationship migrateFrom ([b6974fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b6974fea5e09802d27f4422bbc430ba0f8d29330))
* configure webclient timeouts ([1ca0f3c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1ca0f3cc1464ad60b2c28056584b4d15368cd1ac))
* enhance code to support multiple migrations ([f506034](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f506034d23c0e7fca2a500c4a87abd55af71cc5b))
* evolve mapper to map configuration term field ([aeaaf9d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/aeaaf9d05a166a36b9f127506acb894203a784ff))
* evolve process to support the technical eligibility checks ([d055f24](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d055f24da8543d83e9b619838a3aac661560112c))
* implement null-safe [@type](https://gitlab.tech.orange/type) mapping for product order ([226e511](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/226e51139729f08b12fcddb34f989081d7aaebe8))
* implement updates for product order items with appointment references ([8780ec6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8780ec66f5ef9cd2d04ecb199d62da6a7591e2aa))
* improve automated processes within the fiber workflow ([15b8232](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/15b823246d4aca69813dc6832871bf7690bce6d5))
* update product instantiation to include original price in dutyFreeAmount and remove taxIncludedAmount ([4cd019b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4cd019b83993e359e0186b80289578254ca83a32))
* update ProductOrderMapper to map productOfferingPrice fields correctly ([640122f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/640122f86aee75a4ad905029312e5e5942bd66c0))
* update service name and URL for order inventory ([dab62c8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/dab62c80e4637f90ba9a51f4f614d30fb2010bd5))
* use refactored maven spec modules ([ae29660](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ae29660182b5ee8831e608d8323e122935d18515))
* utilize DOCKER_REGISTRY_MIRROR for container image pulls in MongoDB and Kafka test classes ([4a45f47](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4a45f475a697472ce49144f9ea853902dd6d603e))

# [1.13.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.12.0...1.13.0) (2025-07-16)


### Bug Fixes

* update GitLab URL to gitlab.ow2.org in helm/README.md and regenerate files ([e66474e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e66474e8c7956b31c39f438152546b3c275663c0))


### Features

* support new application duration through acquisition process ([1f4118f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1f4118f74fa83468129c236f836eeb2a84cd813a))

# [1.12.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.11.0...1.12.0) (2025-06-23)


### Features

* support new value for Tax Price Alteration ([e78b576](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e78b576f26ef5a19e5b0a724eb0142411ef3ca3a))
* update om-commons to latest version ([4c48c07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4c48c07675c2a6931b6a9fc63ad7203f52364502))
* update product-inventory to latest version ([cab2daf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/cab2daf03cdeb6003c65e2753fd910b7bb23e103))

# [1.11.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.10.0...1.11.0) (2025-06-04)


### Features

* update documentation, regenerate CONTRIBUTING.md and adjust deployment configurations ([df18c07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/df18c0777ffc63472b3d7cf44ec66afa7b914a58))

# [1.10.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.9.2...1.10.0) (2025-06-03)


### Bug Fixes

* add default case to switch statement in updateContractOperationalStatus method in ProductOrderServiceImpl ([fdb7a28](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fdb7a287ad7f3793323799c8aa6cbc806bb7eefe))
* add dummy commit ([231174d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/231174d4a24c8a22700b42f15c1e198fcae9e4e7))
* adjust the reservation for the VOIP resource ([dae46ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/dae46eccb1bf6d6d3d417afaf07d8f788b2a3639))
* assign billingAccount object to products with BundleProductOffering or Contract product offering types ([a0872a8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a0872a8d1f8d7e3bf6f40c229763dc93ef6a5c16))
* correct completion task logic: all prices require payment, any price triggers billing ([a1f3bd4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a1f3bd4be49dfceceabc047c69ce273a5cd8cf0d))
* fix set reliesOn relationship ([a2fdb80](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a2fdb80c30f54db9b63d612314b4b4e0a525e305))
* fix set reliesOn relationship ([635cbf4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/635cbf4ecbde2d0834660d96087e45c40e5ebf7f))
* **opensource:** remove annotations from helm templates to resolve deployment issues ([af634fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/af634fde52d25462ebb9fa70a6476e62f2fa19b2))
* remove certificate from docker file ([5d91542](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5d9154280385b7b71a4ee2373d1c5a79d76ef934))


### Features

* add checkPartyManagementEnabled Setting ([dca1aff](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/dca1aff50e319067cdd6cee367920e46199dd2c9))
* add lifecycleStatus filter to include 'active' for offers/devices ([30f5e31](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/30f5e31daedcc0faeb474de3f46e1371c391ec6c))
* include process-flow-spec Swagger YAML file ([1fb5e53](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1fb5e534268412d061faef18c642ca8c03389b45))
* map isVisible field from configurator to isCustomerVisible in product inventory ([09ed6b2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/09ed6b22afc63a4e289efcc0c321f0ca86f35ec8))
* **opensource:** add DCO.txt file ([c02e61f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c02e61fa877b0511ccdbd244fa907100d44e811d))
* **opensource:** move to Open Source Sofware ([562de78](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/562de7888cd745fb3aaef0255d09bea1f5682190))
* persist characteristic values into product characteristic ([5fc4238](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5fc4238d480485ef3af8d300c34a58aac203607f))
* proper enforcement for artifact name ([ab7de07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ab7de07c990b2c837aa70048ee6e72cf56c0682d))
* update Swagger YAML from process-flow-spec to order-capture-spec ([b8db7d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b8db7d28758a374a99a5f05ad51e3649736aaded))

<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## [1.9.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.9.1...1.9.2) (2025-03-05)


### Bug Fixes

* add dummy commit ([cc42118](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/cc42118f51e81f3ee7402b352738536caab12ff1))
* Ensure bundle product status is set to 'terminate' for tangible products ([3ee4296](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3ee429631c97b5952f769d072c535d6f8fd61824))
* resolve future date scheduling for terminating item modifications when CheckandSetBillCycle is enabled ([7166fd5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7166fd5348e3eb2e31c31339ec1f3880b22fdcac))

## [1.9.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.9.0...1.9.1) (2025-03-03)


### Bug Fixes

* add dummy commit ([c753b41](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c753b41cb800f8b147bd5781310e26cf31a32849))

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.8.0...1.9.0) (2025-02-10)


### Bug Fixes

* prevent null values in characteristic mapping in ProductOrderMapper ([13227b4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/13227b47814d284b03deeeee6fa5176dbc75e904))
* update omMockServer value in values-cicd-ocp-integration ([b15c822](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b15c822bfe25cb72bca9ccf888ac548a95f89277))


### Features

* update products to add new relationship ([ceffc87](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ceffc87afd173d26d1c6cfc0a10077552bef8d3b))


### Reverts

* rollback omMockServer value change in values-cicd-ocp-integration ([540fb75](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/540fb75d188abe2d9855b17628210cee7d3fd23d))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.7.1...1.8.0) (2025-01-28)


### Bug Fixes

* correct environment variable injection in MongoDbTestContainer ([43927a5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/43927a5c2cea55e2bc86eb1ba96b3d96cb2deca6))
* correct mapping in ProductOrderMapper for ValidityCharacteristic ([71461fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/71461fdfc275494d8bb67a20d5dd79f76e9240f6))
* correct ProductRef creation in addRelationshipHasParent to ensure accurate mapping of relationship product details ([89e2d81](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/89e2d8104fd4f01bd3db4af323641c9dc6dfc6b6))
* enhance null safety and refine filtering in `hasNonImmediatePaymentModification` ([ac3df16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ac3df16f8dcf06236551f546d93b10db2a26d13c))
* ensure correct parent relationship for product order items attached to bundles or directly to contracts, improving order hierarchy and processing accuracy ([bc93c2e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bc93c2e3226ea4af280c44ad6970050367d8dc85))
* fix gitleaks issues ([3437f41](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3437f41072212616e93cee7d0ec75fc9f272d48e))
* prevent BigDecimal divide by zero in calculateAmount by adding null/zero check ([8a7845d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8a7845d4aad1adb0b98273303be1fd377ea26818))
* refactor code to support nw changes from configurator ([54f7190](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/54f719084fd7844cd7f72ba1229f2a7fb6982206))
* remove duplicate method invalidContractProductOfferingProvider to resolve checkstyle violation ([9fc027e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9fc027e6935e0f0ffcaa4598ac6955f3e7703b7e))
* resolve GitLeaks issues by removing duplicate entries and redundant fingerprints ([2f62964](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f62964fec5125056fd44e8c447311aa74e02556))
* resolve Sonar issues ([f191017](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f191017e6503cf23099d4ada3769df401a332df6))
* update isValidForMapping to allow products without specifications ([b9d33ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b9d33eecc893115326a669dacfe44209c14616a9))
* update mapper after configurator's changes ([c2ed721](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c2ed721ae4cce338512bf659a9c1abcd504f52a9))


### Features

* exclude zero-priced items from payment during complete order setup in order capture process ([52df649](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/52df649b01e5541362482641ed598682beb1e1c5))
* handle modification and termination orders based on billing cycles for accurate delivery timing ([96353df](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/96353df1b8dc20be3924bbee1660e60911976439))
* handle PendingModification operational status for contract product ([3cb8a65](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3cb8a65f967fe8ad0efa7225a0debb51908f8d78))
* handle validity characteristics for postpaid/hybrid offers ([12a1aef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/12a1aef0eceacce2326fdc9e6264d6e52a2df074))
* instantiate products for migration use case ([e1964de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e1964de6d842fa9a55732c5a5d517dffa01b089a))
* integrate oc process with configurator api for modification use case ([05ee11e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/05ee11e4433f1a7c87f1d0b9fef5b6ae97cfe5bf))
* integrate oc process with configurator api for modification use case ([b2f1b92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b2f1b92a95a02243f28c75ef19f53b597ef3d57c))
* map all characteristics from Configurator api ([21ed6e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/21ed6e095d182012d355f200a72778a04fb368d0))
* update mapper to support migration use case ([f168c27](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f168c27b3a67eaa13b4fbcfd8d44374458a45bb3))

## [1.7.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.7.0...1.7.1) (2024-10-27)


### Bug Fixes

* use stable repo in docker image ([93e6912](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/93e6912cb42df5b7b4cde4a54f7b346d4ee9251c))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.6.1...1.7.0) (2024-10-14)


### Features

* add price alteration for product ([cf95b97](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/cf95b976c912b94b7812874cd20cfd021b1851df))
* calculate and apply price alterations to update the total order price ([7663dac](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7663dac84985cdb2c7016675e87b68dbe04beb18))
* integrate TMF679 & TMF760 changes ([5aa98a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5aa98a28276ec0311e7c67b0e990b51379296ff6))
* persist the channel in the order ([ce14d5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ce14d5d43591b20957e40d21dd4fb214e1f204b7))
* update mapper and order instantiation to support termination use case ([b9e82c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b9e82c9c604dfc8eca09dde1c32d00232806fc7e))
* update the offering validation to skip the eligibility for accessory ([868a577](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/868a5771d80fc5ce547ab8107c759f5ebb46c68d))

## [1.6.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.6.0...1.6.1) (2024-10-01)


### Bug Fixes

* dummy commit to run semantic release ([8f41460](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8f41460e5edf83b9fde6c6658c21e9138bdece73))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.8...1.6.0) (2024-09-04)


### Bug Fixes

* change pipeline configuration ([67b8fa7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/67b8fa774836e023548dec7562264c1b15b3f2eb))
* ensure order total price is updated when configuration is modified ([6ec39aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6ec39aae1bf613ba95523456abc26f6c4eafeb7a))
* fix fullname of chart ([0001f05](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0001f0570bb5cd976d7e7c97bf9966a4de6c005f))
* fix helm production ([521f5c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/521f5c7f07de77c41f2e54a3294655ab4347d024))
* fix production keycloak url ([37fa804](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/37fa804a6064d066e194adb77abb36dc0299db55))
* fix the shipment product creation ([e543a7a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e543a7ab568fca5074635fd76badfe84c1478598))
* include both non-recurring and recurring items in order total price calculation ([ddbabdc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ddbabdc958cb69d33e650d1353d19280114674e8))
* increase memory limit for mongo ([0ec92ae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0ec92aee2c3170c30d4109c5f3110ebd82c0e5ad))
* **mapper:** correct mapping for isBundle property in ProductOrderItem to ProductDTO ([2b458dc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2b458dc593ecc73e9a4913873f6731b5796cd3eb))
* move innovation job from staging to production stage ([a8994ac](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a8994acf7dc02ef1f6726122527d6faab38320fa))
* **OrderInstantiationActionTest:** resolve failing unit test ([3c31ea4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3c31ea4fbdbb0d08e1b3281e9242085ec72a80a3))
* remove Basic FO Offer.7z and correct configId with extra memory suffix for FO offer in Bruno collections ([196d588](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/196d588822e5a068df992dd951d25ae6125eddb7))
* remove cert from source code and add it as variable in gitlab ci-cd ([d4fa11f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d4fa11f81a02c5a9ec100e53639abeedc1e38962))
* remove duplicated variables ([8135cd6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8135cd6a8fe44d22f1418ec081f6dcb305012900))
* reorder imports in OrderInstantiationActionTest.java for lexicographical order ([2dc9565](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2dc956571a012ebd93031e738885f8baf50d4808))
* reorder imports in ValidationProductIdentifierGuard.java for proper lexicographical order ([78deffc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/78deffcbcc3d72ffda71a9abfd92379e307189f7))
* replace envtype with environmentType ([f73cb4a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f73cb4aba08cab3341e21bd25027c61b7c7b9a81))
* resolve mapProductSpecificationsToCharacteristics issues during resource reservation ([e1fcb31](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e1fcb31494ddbef031a3c1c94f17ee1e26e40e9c))
* resolve merge conflicts between develop and oc-pi7-sprint2 ([e4e2902](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e4e2902113be15a4a44e4bc818f864ed4e4eddc4))
* restore memory resource limits for mongo in values-cicd-ocp-common.yaml ([9c68b4a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9c68b4ac6925395452c0a2eeb929a04128eb0a92))
* update `REQUESTED_DELIVERY_DATE` value to "Requested delivery date" ([d16c1e5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d16c1e567555d27bd0ae47dccef6130e56a8f5c1))
* update mapConfigPriceToOrderPrice with new @Mapping for recurringChargePeriod fields ([d6c0b32](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d6c0b32fed2628bfda5baef6517be1b5ca12ece6))
* update product configurator URLs ([7a8c4a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7a8c4a13d35ef2a45e97b9c022882403edf7fcf5))


### Features

* adapt code to support stock products ([683acfd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/683acfd6fe633767b4f8cfed3079ff6dceb501c7))
* adapt code to support the configurator ([4024d11](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4024d11320807128d3230633cc98ee0939a05f56))
* adapt OC process for combined products in CPIB ([fbb3bab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fbb3bab24ac68c13928d2989a7072e52d3f03ffa))
* add a check before the product cancellation ([5403d1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5403d1f80542ee6b534f0b246323bb130f36f4c0))
* add business rule check for identifying order items requiring BA ref ([f16ea45](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f16ea45e79248a6ad970b69969d19bee2ffa8c46))
* add setting for reserve physical resources ([ddeede8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ddeede873404187b5904b128d1951c025e0a6c31))
* add unit test ([1eb5ed3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1eb5ed32059eb07779c52ba39a40b288de6d093e))
* adjust for recurringChargePeriod type change to Quantity ([d1bfa17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d1bfa17987bb4cb8de4f2fd5eb4ff043e8a3d018))
* calculate and include order total price in order creation request ([1e80816](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1e8081657283dbf4e8f6ad125faafa0e3bdeb7f8))
* evolve OC process flow user task to request billing account reference ([6f17cbf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6f17cbfde5f03024864d45719b305843e9f2eaf3))
* include product details in productOrderItem for 'add' action ([1304008](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/13040084b08ef6a1c11d600c9c69ff77e7bb72ab))
* refactor scripts and add new collection for FO offer in Bruno Collections ([1d85ad9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1d85ad97a0993ab2db897b6b8b42cd0df0abd7cc))
* refactor to combine order items related to atomic product offering and product specification ([9476844](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/947684409919443d5947e8f790fa53dd700f07ee))
* update mapper to support version attribute ([1c0306f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1c0306f8d1d2adf45b3e033c81fc20964d1add4a))
* update product inventory specification version ([55fe4d5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/55fe4d50decb97978eb8113ec1bdccbb63a31a71))


### Reverts

* undo changes to mapConfigPriceToOrderPrice for recurringChargePeriod fields ([73e4b45](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/73e4b458cfaf5927be067bd00ea74e6c9894940d))

## [1.5.8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.7...1.5.8) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([03f8e77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/03f8e77bf75d228b462dccfd11b7b84232677af5))

## [1.5.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.6...1.5.7) (2024-07-14)


### Bug Fixes

* remove cert from source code and add it as variable in gitlab ci-cd ([3970f46](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3970f462c9b66d0966c43b229bcf12cfe51291b6))

## [1.5.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.5...1.5.6) (2024-06-26)


### Bug Fixes

* increase memory limit for mongo ([12fe2f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/12fe2f9274b75796aa0ed6bcd3b7514d7b339b02))

## [1.5.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.4...1.5.5) (2024-06-13)


### Bug Fixes

* fix fullname of chart ([70adacd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/70adacd17f131490d7f21e20ba749a1e6df1b87a))

## [1.5.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.3...1.5.4) (2024-06-13)


### Bug Fixes

* remove duplicated variables ([a978531](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a97853115ccea7a9fa0f490b1183f33cb657bed6))

## [1.5.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.2...1.5.3) (2024-06-12)


### Bug Fixes

* replace envtype with environmentType ([7a522e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7a522e6d0b7f24ac2d6e5c95cbb19c5fedf6155f))

## [1.5.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.1...1.5.2) (2024-06-12)


### Bug Fixes

* fix helm production ([d45aefd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d45aefd7fee118097c1ad059d66fbdf68e4a7762))
* fix production keycloak url ([b18749f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b18749fbc117658245b0a05048154401d555f31a))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.5.0...1.5.1) (2024-06-12)


### Bug Fixes

* change pipeline configuration ([665b94f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/665b94f6569d7a7f54a30f7f9c38c59e0e8d602a))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.4.2...1.5.0) (2024-06-07)


### Bug Fixes

*  disable configuration check for physical product reservation ([e5c3cbf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e5c3cbf0e98a2b3cd20ec37e87ea9b81fbc85147))
* add base type to product specification in mapper ([f9a8124](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f9a8124cd41deaa78c665fe79dd9de939e1ebe69))
* exclude mapper from Sonar coverage calculations ([5f3d2fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5f3d2fe2560bd45c85fb85c123f83f5cf5686bfc))
* fix issue with retrieving unpaid order item IDs in termination use case ([b3d4a5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b3d4a5b12527dd007bc28fc0d670a7b860de325c))
* fix missing reliesOn relationship in PS level ([183386a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/183386a6fc1b153cce5536b193cb2c20ca5e68ae))
* fix regressions in order capture process flow ([9ec13eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9ec13eb74225af97ad947bdf6de09b75cd9a8c5b))
* fix the delivery date for resource reservation ([f52d7f2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f52d7f27b177164a8097095fc4c79daa001b4db6))
* fix unit test ([5dc0a48](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5dc0a48daeced95862627b9b98e5ee9422ca0502))
* fix unit tests ([266f212](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/266f212c534a14580553b939b04ec69b62da2d8f))
* implement error handling in apply function of CompletionTasksAction ([91c99c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/91c99c55bc4051f292816568632cee1d239f3561))
* refactor code and update unit tests ([3733092](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/37330926039059bf9ab663e499f9be1c66411772))
* resolve CheckStyle violation ([9161448](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9161448f2155f1a279efce2ed8250395b117a95b))
* resolve CheckStyle violation ([f73be71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f73be71a1868967576c74ab8edaf327d9ad03886))
* resolve regression in retrieving stock item ID for product items ([2b4b9c0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2b4b9c00f2532c7744745a3a32f61a919dbf3b9e))
* revert server port from 9005 to 9000 ([ab3fd34](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ab3fd3476295c6c8ef39117e8af76368029274d3))
* update unit tests for ReserveResourcesAction class ([eafe61c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/eafe61c003c5b0056948ff889c4e385b4c1d24e4))


### Features

* adapt mapper to support TMF configuration mode ([bd960d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bd960d84eb28871e5b6a88cbbadde2711daf8a63))
* add changes after review ([1b96f17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1b96f173c2236506d61618bfdbf9542dae87b2f0))
* add check on the payment ref flag ([a817d13](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a817d131c5593b4f53b852d3a413cd3e3b8b7689))
* add checks for technical errors in cancel order step ([afb3c1f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/afb3c1fc6af04eb8e47b75eeefee8a56e63e05fb))
* add checks for technical errors in cancel order step ([4beec47](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4beec47644e5e463f04a32101bb0896b97920580))
* add conditional check to exclude null descriptions from valuesPrice map ([e8de967](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e8de967d1fd94d27eff9116a8356110d6a9ede3d))
* after confirm conf re-check eligibility ([58a1c93](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/58a1c93f5877dcc435e8063843de2fd78b92614e))
* check immediate payment flag in order items ([b6ae93d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b6ae93dd336277d66d2cf6f622fd7dc967357440))
* enhance order and product creation  to support accessory product. ([ebb289f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ebb289f250addc148b43415e4ef9a8d0bbf9510f))
* enhance process checks to be editable ([925e50a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/925e50ae05a49ab788acaaf4179739e85dafdda6))
* implement options to use mock file patterns or real configuration API ([f568b4b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f568b4b540deced236986c8f813a1522dba35017))
* retrieve PS level relationship reliesOn from commercial catalog to add POI relationship ([f8a1897](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f8a1897d302675658a4cc8e02eb152e912937cba))
* retrieve stock item id from catalog ([0c20649](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0c20649118308e060fa236388ed57e1d15f5ee3c))
* set reliesOn relationship for shipment product order item ([4fba0e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4fba0e444386bba1e39eedd7fc2594b8f1481dd3))
* set reliesOn relationship for shipment product order item ([b56f807](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b56f807cbae2fb38c261d920e59c582ef67a91de))
* update Bruno collections ([349ebb8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/349ebb8722b4ff94afd68669b2e540c377b3428f))
* update Bruno Collections and add missing Collections for Basic, Max and Max Plus use cases ([a2f639f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a2f639fe0f6b4e7ef03c1bc76c9d94a5b8658099))
* update the term 'Shipment' to 'Shipping' ([6013a40](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6013a40899db642e23397e1d92e44ca677c96aad))
* upgrade process flow library and specs in order-capture ([e304d4c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e304d4c53446e4290250c2b6466abba346f23a7e))

## [1.4.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.4.1...1.4.2) (2024-04-29)


### Bug Fixes

* removed semantic image and move it to gitlab variables ([a3ba932](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a3ba932ccb9c7afe1895a68082e2f1f1bff2161e))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.4.0...1.4.1) (2024-04-29)


### Bug Fixes

* add job to deploy mock-server on innovation environment ([efe5fc7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/efe5fc73bfad98e58560eb211780accf654f77c5))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/v1.3.0...1.4.0) (2024-04-07)


### Bug Fixes

* Address CheckStyle violations ([613f43e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/613f43e3961c5480345df11b91f28fc13c1f1d64))
* address review comments ([a5a34f5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a5a34f52b0f86fc3451acdfeccef3bff1f2e8331))
* Address review comments ([ea22938](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ea229387dd70c49304eaa27934aaf302f074c091))
* Address review comments for the state machine YAML update ([622ea85](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/622ea8587a4c0375170d5e7f9bd640d592490559))
* Configure server to respect X-Forwarded headers for HATEOAS links ([36ec417](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/36ec41707b58a39de7a332eb664423e58187c953))
* delete unnecessary log file from repository ([ed3fefa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ed3fefad63669aa1251b03cd9d88fe686b440d92))
* empty variable expansion issue ([eece05a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/eece05a0af86fbb7915efd92b4bcfa36f5b2accb))
* Enhance code coverage for unit tests ([870b723](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/870b7230c11f2a26db7d835fcd4e3ab939e30c6f))
* fix checkstyle issues ([766bdfd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/766bdfd26b59c48504bbe8aa36dd417a09342337))
* fix configuration ([3f5b0ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3f5b0abe78464412fac11c225a3cc4d2b81ddd33))
* Fix handling of empty related party entries ([bfe6394](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bfe6394da441c5623b1abf896caf58b69778acf0))
* fix the termination use case ([32f5158](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/32f5158a1ce148ab7a4fd366710b5dd6159021cb))
* fix unit test ([7a9e683](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7a9e683fc8eb85c450f43fac837fe3a65f360875))
* Organize imports to adhere to CheckStyle conventions ([62f7fdf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/62f7fdf8803e53c5406081e29d022961541f08a5))
* Prevent setting empty or null party name ([01b077c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/01b077cbee70948b7723784a88866893ab98630b))
* Resolve checkstyle violation ([c498d94](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c498d94c5eb76ecdc69429cfec0fd0b1727fdafc))
* Resolve checkstyle violations ([87c63e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/87c63e4f48d7ef23a6871991a6b97f15687e2f5d))
* Resolve null party role in selectOfferOrContract user task ([a1e1dd0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a1e1dd033b643413e2aab913f12133489f92d76b))
* Resolve related party update bug ([fcd84c0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fcd84c0e791b3d92e55edcc23d16262accfd0bcd))


### Features

* Add additional query to get party for checking party roles ([77c7d24](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/77c7d247c2e9bb3167c2fd6b681b46c85022902c))
* add authorization ([abf62b8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/abf62b8fd6777ea0da60fac31ac8c2b86a2605f4))
* add checks for technical errors in confirm configuration step ([43296f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/43296f35e07eb4b166ef3f29a6fb301912e8dbb2))
* add technical errors to identify party ([8c9c2be](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8c9c2beafba6391772701930c25c2428abe73029))
* Introduce party identification & implement identify customer step ([581947e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/581947e9664798df4d0bd7cfc90a3287f0aab29b))
* IPCEISOM-532 - technical error management for pay order ([669fd0d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/669fd0d50d4455410fae2642faefbc50a4b5dd10))
* Manage order creation upon party identification and party role change ([661c33d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/661c33d11c359d4440ab70a7b49fac2539544b9c))
* Remove automated task create configuration session ([b16a1a9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b16a1a9566aa43054087d0d21a098439b16434fd))
* reserve tangible product ([9cfab02](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9cfab029d5b8c34de5a47a23e1bf62e1579d0b31))
* Update resource reservation task post order validation ([9b5d3b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9b5d3b1eebbdae69b661c30f436fa4f778729695))


### Performance Improvements

* update deployment resource limits ([ca3a0ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ca3a0ede5a7aa36a201bbe8bae94575147dc3fbd))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/v1.2.0...v1.3.0) (2024-1-15)


### Bug Fixes

* **CI:** resest semrel and make all helm deploy manual ([6563b38](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6563b383a004780180b047aeb8e351115168d705))
* Remove corsConfigurer bean ([ffc9b17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ffc9b173a4ab461318d3d3ed191b87203ffd4718))


### Features

* add kafka tracing config ([24ad650](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/24ad650353cce4b02b2d3f47b819378c14b75270))
* Update processflow version ([db4c0ef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/db4c0ef1f7be9390770c87ca820b9a4b9ffc0e67))
* Update services names in values-cicd-ocp-staging.yml ([5c229a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5c229a2f0ac89ce42fa51056fbce7891802c6ec4))

# 1.0.0 (2024-1-11)


### Bug Fixes

* Add unit tests and fix sonar issues ([866b618](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/866b618a2eddd3df6a245f4a27f473334069d43d))
* Adding port 8080 to services in integration ([bfb39cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bfb39cbaf61f14084f55d1daff4343aee937dd36))
* Address review comments ([7d447fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7d447fa554f3d534c54d07b215fc9b58ea395fd0))
* after code review ([92e0ef1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/92e0ef1d5172cab5a2fb7bbffd340d8a45a34e64))
* after code review ([1923041](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1923041d8e738f4dea75b3033938a83d009f54a2))
* after merge request ([806441e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/806441eea86dfdacf80a8e109b7b7f41a0f1b2f0))
* **CI:** resest semrel and make all helm deploy manual ([8679234](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/86792340a616f83705f7224c35668b829f34a82a))
* clear cancel task on the end of the process ([a317e91](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a317e913e4eada05ce13e26f30a418629babdd33))
* Exclude 'OrderItemToBePaidServiceImpl' from Sonar coverage ([32f6c55](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/32f6c55876062914cdfe300a7ed6cca102b47958))
* Exclude 'PayOrderUserAction' from Sonar coverage ([5f13293](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5f13293c18f038ad1bf141a4439eb9af35a8f613))
* fix conflict ([d80681a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d80681a8c5656df166b98df3db691dcca829bcc2))
* fix conflicts ([2e28927](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2e289275a27bdb964cb0426c191076d9480ef040))
* fix conflicts ([d70d4f7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d70d4f7df315cade577577c54af763bdec83b9bc))
* Fix RestTemplate config ([3d6adfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3d6adfc25779cf9da721b5a07de84dee184ebb36))
* fix RestTemplate PATCH request ([5ec4f5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5ec4f5bf3364bf5508c48e42d888361d2381a4e9))
* fix the imports issue ([bd6b781](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bd6b781e217cadc9139aa02c17bf551ae7e95ab2))
* fix unit test ([c31e513](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c31e5132dee2adb58e761d79e854f44006b57671))
* fix unit test ([d953da0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d953da0015c668034ac9d72061f6cdee6d49c8ee))
* fix unit tests ([6285821](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6285821b198f02b5495b0a23f42f9f7d314f6690))
* refactor code ([d9a42a0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d9a42a0031e3770d38a00fbcb295f6edeb98b676))
* Remove unnecessary PATCH products for modify/delete use cases ([8265b78](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8265b78bf3e2f40fdce4739c9f6e531da1db352a))
* Resolve issue for Keycloak config in yml file ([65b728a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/65b728aa6aa07cb00f32d6b92772a909da3b95f1))
* Resolve issue with 'order-capture-kubernetes.yml' configuration file ([6fdc89b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6fdc89b88a54067073b821abc5562bed9f2a0f29))
* Resolve related entity set in context ([a009ddb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a009ddb23082f3e1b46a71ade56c790070e76eb5))
* Resolve SonarQube issues ([9bb723a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9bb723a2a75bd66e497a439cc0084b7fc7169d9e))
* Resolve unit tests ([a867e70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a867e708d72628654b2855ee33be76ead2ccfef3))
* Update 'gitlab-ci.yml' to use the latest version of Node ([14d71fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/14d71fe4fbb96fb196b9cdb18db6525799a71e66))
* Update 'MAVEN_IMAGE' in gitlab-ci.yml ([c3e1d71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c3e1d71e9b7e92b1fe43c9b1e864f523cb6fa200))
* Update 'Product Offering Qualification' URL ([84c7357](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/84c7357ead2bf56a6ffd50e79dae350cae968d55))
* Update commons version ([a658be1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a658be17c397a5e4ad7f838cd13bfff7c886606a))
* Update default URLs for website and shop in 'order-capture-kubernetes.yml' ([4d2e350](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4d2e35099d5effb8af03df8ac8e843218b78a542))
* Update Inventory Management URL ([f46ddd1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f46ddd1d88f161e4d506838ee4e7feecea9527c1))
* update package name ([f116693](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f1166938e8b425bd10d6da801a3e5eb27b0f44da))
* Update unit tests ([7b7b3cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7b7b3cbc0131e96bb3edfac793b5ec8eea7aafbe))


### Features

* -add product price ([2f1a6a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f1a6a118d24dfa1a250a519ba73b09c339e371b))
* -add product price ([90e45ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/90e45ea8941939455d9fb44e3249ba5967a270ca))
* -add ReliesFrom RelationShip ([21f5f6c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/21f5f6c860f3f0a7972a358e6aa47b420a4ee3e2))
* -add ReliesFrom RelationShip ([d464173](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d464173783357679ee27f169598f6832053b878b))
* add async ([2984a74](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2984a7468f6709a8b00fa904eac9db0f1bf7b376))
* add deployment config ([c52e277](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c52e277f4de8464471790f70bc24af64383acff4))
* add guard to check the product order instantiation ([9179113](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/917911326565ba28caf96299d04c38321a28118b))
* add id for product order item ([ddc1db5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ddc1db5126e9dbef809ef5dbc00bc4761e57324f))
* add instantiate order action ([3d4cdf0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3d4cdf0981161bfa97a49a0ec8ccc8b2d4836fdc))
* add Inventory Resource DTO ([0fe2437](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0fe243702c8c56031122b2ce87f9ba4eee9f40a4))
* add logging configuration ([8afd817](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8afd8172c18dcbe37716bb58ef988f8f36b61bc0))
* add logging configuration ([3680608](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/368060826719fb3f614476f9566bbdc70ca258ea))
* add mapper from ProductOrder to ProductOrderDTO ([98637b1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/98637b1bf07350af6b891675c6aaf03795fd4b7a))
* Add new process flow evolutions ([f1db752](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f1db752334a252fe198a94bfe9b6806f8e81b4d8))
* add postman collection ([bb20d04](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bb20d04e8285cc3487c7bef173a88f41d65b664f))
* Add Postman Collection for Modification Use Case in Addition to Acquisition ([051b0e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/051b0e7a07893cd737acff4aa1620393afd6046e))
* Add product Order Event ([329e606](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/329e6060ae265795117dcf6bf513210ae113f7a8))
* add related party validation ([6e1c790](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6e1c790a627c6a0e2ad343537294fc8864671e47))
* add reserved resource ref to POI ([55df1fb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/55df1fbfb25580e7bc10fb3264699dac6df197ea))
* add unit test ([2306fcf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2306fcf17f8b549c67652c209e737d77119afc2c))
* add unit test ([d1b2281](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d1b2281b42d46126963f04596178db6daf407431))
* Add unit tests for the create product order and for the mapper from configItem to prodOrderItem ([e6d7dcf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e6d7dcf6c168d4c6829e36f0e0bb7a27dfe2a6b1))
* Add unit tests for the create product order and for the mapper from configItem to prodOrderItem ([b6fa08e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b6fa08e484b72033c0b332d9751ab145eefb421b))
* add webclient and add keycloak config ([2f5c2b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f5c2b6d0cf42aec591786df5d54f14bfafcbe88))
* add webclient and add keycloak config ([6a35611](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6a35611bef9da1ad22a102739a1aff80708facf0))
* cancelOrder ([71d5220](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/71d5220a23ee3b0bfc2e52d9a2b3388b0c0fa4e7))
* cancelOrder add check productOrder is not null ([541b5cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/541b5cd43a9fa8967eb84f4b3c26e0c453e0af5f))
* cancelOrder update cancel process ([0d59a56](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0d59a56813a0290414e41df01f2f24d1f040dfe6))
* configure test container ([7f3abb1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7f3abb178969edbd079b23814271355cae1a3330))
* configure test container ([fe481f7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fe481f7c626bef33eab7a1a490a571427654eda7))
* create action for product order instantiation in DB ([214751f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/214751ffe79fcba0bf1207847325e02954a0db0e))
* create dynamic configuration ([81332c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/81332c58a6cf88528b37dbe8ccfa05f1cbd0ec11))
* create installed base ([e8810d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e8810d23de08f6153946bb1b1cc80245b76e99a4))
* Create mapper to convert configurationItem(s) to productOrderItem(s) ([b4e0082](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b4e00824ade3edae80c17f74a8febf581f2690b8))
* Create mapper to convert configurationItem(s) to productOrderItem(s) ([2345e0a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2345e0a2c3d8fcb0445f53709eb073919d30edbf))
* create update order action ([33f44de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/33f44dea5b28bded997c72e0f694c3d0bf8394fb))
* create update product order service ([fe28e7f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fe28e7fe297b808f624a24ba82c216c6551193cd))
* Display specific message in case of check eligibility failure ([73b3418](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/73b3418c4e0879ee9a6476933107e5776ce80e3d))
* Display specific message in case of validate offer failure ([19c542e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/19c542e9e24ec5fa4d056f3f4ccce7cc0a85dd22))
* Employ reserved resource IDs instead of utilizing available ones ([68315c8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/68315c8a71976d28077ba1cc260cd9b851029bb7))
* Enhance 'create configuration session' automated task ([03b457b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/03b457b8f1ee8463f090ff962967597f685dae91))
* Enhance PATCH method for updating products in CPIB ([1c82dca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1c82dca06bb326cb57e438d47a9f8304e29b2315))
* Enhance payOrder user action ([d46ecc4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d46ecc4de733faa30e0e2592cf3632a04dd35c16))
* exclude config class for sonar checking ([c69d6c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c69d6c352a5f519cac223d3aa0dc471809141b62))
* exclude config class for sonar checking ([629ba62](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/629ba62a714e22eff588d5a3dfd9af53d4acae93))
* Finalize the integration with CPIB ([2ae66cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2ae66cf8b275240c7c15b14afd556f0d4a112844))
* fix after conflicts. ([d797acc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d797acc3cc5ec91241610a3018f173abdae7f249))
* fix after merge conflicts. ([9d9b2ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9d9b2ec7417e56989f3a4f3d917b9c7e52d40c3c))
* fix after review. ([a2fb5e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a2fb5e4cf385b71f8f640c6675e81539a8f37bfd))
* fix after review. ([61d4348](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/61d4348d0fdcc4f37e74baaca93f0504dc72755e))
* fix code coverage for config class ([afceb44](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/afceb44c88353da198b6ee8ed06e0432412a1db6))
* fix config ([660f4b2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/660f4b25f0278571ad9a7511f14fd395bac86f9f))
* fix config ([a28c49f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a28c49f1109ba49ef9b651c529c89bd23d680086))
* fix conflict ([82ed5af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/82ed5afea744eb703d1b1f941649373893ad08f4))
* Fix CORS Origin issue ([516551b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/516551b2bc19629160c0a8a59bf7ac069934989e))
* fix mvn build ([69ef3af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/69ef3af9d86f2fee6e76a2202970520a96a34394))
* fix project name for logback appender ([4d5fd39](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4d5fd39ca0b4616378d6c63403e2aba4d03d7e6b))
* fix sonar config ([f8b0b96](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f8b0b967f53d4f123b3a06bf8d19464b73dfe497))
* fix sonar config ([0cb4f6d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0cb4f6d61c16bfef52c789241ebb85d943e0ea3a))
* fix sonar issue ([3786225](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3786225676de8cd219e3ebb20ebb6a1c73e90371))
* fix ssl configuration ([eaa7c82](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/eaa7c8276ff0464fd1a2136b0065f943b18714ce))
* fix tests ([18c6bf2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/18c6bf29943398d86ff0e844b14815efff75e462))
* fix unit test ([f770d8a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f770d8ae915ee80eb08c1c09cc0f2bc46a70cd34))
* fix unit test ([ba2996a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ba2996aa9497453ae01a3f9236fa683f7e88a1a4))
* fix unit test ([7a7159f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7a7159f6086359b9dd3a181e6d945211c0d1f626))
* fix unit test ([a28ee33](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a28ee33a1a3e4bc421135ab5aee88ab26a22e803))
* fix unit test ([48e3e6e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/48e3e6e994c29eb4eb3414a363da1af105ec1cec))
* fix unit test ([cee416f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/cee416fb3ef1df5c3fcc56f0f5dabb2fb210038c))
* fix unit test. ([83981ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/83981ed75b949ace3eabd71aebe7b194701b84bb))
* fix unit test. ([bf93b07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bf93b0748b3db9294a60d3370a93fce663d97135))
* Implement functionality to modify Contract Product ([deca716](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/deca716c751808b769d9a0c890bdc2ea6105ff40))
* implement get related resources' id(s) for each product item ([6e1456b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6e1456b53c19e7c6d96041db8e9540e706a0ff79))
* implement get resources id(s) for productItem(s) ([ba02db7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ba02db7f4bd1047ae861237df642928bfd775189))
* implement get resources service and code refactoring ([daf8aa8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/daf8aa8bf49e44879ce79cc88c93fedd090193ea))
* implement modify confirm configuration ([758cab4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/758cab4a5d9ea05e8a3009650bc571e03cdbd086))
* implement transition action to set catalog driven tasks ([5ad2901](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5ad2901243d5a624917f6e7bf86107d69a5eac7e))
* Increase timeout ([829ebe2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/829ebe2362ee95c1736ae2d33e05653100d4df41))
* Integrate Product Offering Qualification API ([d281504](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d2815046de0a05b181444e4d0bff70001720c688))
* integrate the contract modification use case ([aa30e35](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/aa30e358c6bf66eb52fc53043201676ae74572b3))
* Integration with CPIB ([3f1403c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3f1403c82d1f5c4b0192bda61bd6df1f97f5512c))
* integration with product order inventory ([d2ae4c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d2ae4c969e05491cd4c1579d597352280486bc9e))
* refactor 'setCompletionTasks' and remove workaround after processflow enhancements ([94fa597](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/94fa5970638303bdb336199d1852f9c7224c293e))
* Update Catalog and Qualification with Mocked URLs ([c692fcb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c692fcb1860eebb03b626d34aca9f595bcfadca6))
* Update code and imports with commons new version ([2f97967](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f97967fdde7af4a43c4f0be0329ec36a303f998))
* update config ([210895b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/210895b927e736b0258f37069ce5f339b4ed36a5))
* update config ([5f9f34a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/5f9f34a453c88b1af781a29310b4b31f1dc1a006))
* update config files ([f858896](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f858896c3d3d82202e15ba5ebb1bf9aa61dcef93))
* update config files ([808b652](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/808b6529a7c9f486f84cf53db6a7e865c5ad6c39))
* update confirm configuration service ([9f20086](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9f200865dc2c842ea1bbd7cf7d4190bec83ceab6))
* update entities to comply with the product order data model ([670bbf1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/670bbf156b768eb5293a431ba265d653d55abf2e))
* Update failure message for eligibility check ([71b08f6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/71b08f6f6a3951e1f90136f6da093c667cb0d6ae))
* update kafka config ([df261cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/df261cf1e4b2da298c200b316196dc74b02fa535))
* Update Postman collections ([b3f9ab6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b3f9ab659f59c28a74eb6479092f88b71f5b2879))
* Update Postman Collections ([efd159f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/efd159f65fac580212a355d753fb4d95bcdd0a77))
* Update Postman collections for acquisition and modification use cases ([4e8bdba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4e8bdbabdc8aa13b9a8815b82bd6cb4166882ec1))
* update process for modify contract ([8a397e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8a397e443ad565a181edf1d259c2e1843eccd317))
* update product offering endpoint version ([75bb5de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/75bb5de526d04049a315fcebd4fb9f82eeb0cdbe))
* Update Product Offering Qualification service name in integration ([d35540a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d35540a4acc9b65b722a8f9198c96e720131c722))
* update product order state to accepted ([d22924c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d22924ceb8528aa881155cac815d043e75f686db))
* update ssl configuration ([fb67931](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fb67931f9ed3114c63a4420d1aba1eaed9a95328))
* Update State Machine process ([7d2783c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7d2783c7c62824b9d1f4b49f712288e33f5e8042))
* update the deployment config ([60b0422](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/60b042244be42dffd5cf3b1d292af657614e8e4c))
* update the deployment config ([4965106](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/496510676043ce1854f593d44ddacfcf62975ce3))
* update the integration config with mock server ([1b5f1a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1b5f1a4fe0138930e525b98a4549da4cad2daaff))
* update the integration config with mock server ([2839efd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2839efd3cde2a25f8f41d3c8dd172bdb630351e1))
* update the integration config with mock server ([e7c2c5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e7c2c5d48591ba29ae85e5bbaf3f91c21231d55b))
* Upgrade order-capture to SpringBoot 3.1 and Java 17 ([4539ad8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4539ad8ea31c7e257c0bcafc481ef5403bf6dd4b))
* validate offer for contract modification ([80a355c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/80a355cc164bd99e7cc5582705a3db61817d9fce))


### Reverts

* Revert "remove health from otel" ([0c219bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0c219bda590da701c3c5bc8b7880fe1549e0fe68))
* Revert "remove health from otel" ([44170d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/44170d33426591331a44c3cac64b9d1042b43936))
* Revert "remove health from otel using span" ([2f0e989](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f0e989220956c5915a9a9e827842b05825d31b6))
* Revert "Update target file name" ([1b4ce3d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1b4ce3d271e84758997f883f9358874ad33770d4))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/compare/1.1.0...1.2.0) (2024-1-3)


### Bug Fixes

* Adding port 8080 to services in integration ([bfb39cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bfb39cbaf61f14084f55d1daff4343aee937dd36))
* Address review comments ([7d447fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7d447fa554f3d534c54d07b215fc9b58ea395fd0))
* Fix RestTemplate config ([3d6adfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3d6adfc25779cf9da721b5a07de84dee184ebb36))
* fix unit test ([c31e513](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c31e5132dee2adb58e761d79e854f44006b57671))
* refactor code ([d9a42a0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d9a42a0031e3770d38a00fbcb295f6edeb98b676))
* Remove unnecessary PATCH products for modify/delete use cases ([8265b78](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8265b78bf3e2f40fdce4739c9f6e531da1db352a))
* Resolve issue for Keycloak config in yml file ([65b728a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/65b728aa6aa07cb00f32d6b92772a909da3b95f1))
* Resolve issue with 'order-capture-kubernetes.yml' configuration file ([6fdc89b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6fdc89b88a54067073b821abc5562bed9f2a0f29))
* Resolve SonarQube issues ([9bb723a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9bb723a2a75bd66e497a439cc0084b7fc7169d9e))
* Resolve unit tests ([a867e70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a867e708d72628654b2855ee33be76ead2ccfef3))
* Update 'gitlab-ci.yml' to use the latest version of Node ([14d71fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/14d71fe4fbb96fb196b9cdb18db6525799a71e66))
* Update 'MAVEN_IMAGE' in gitlab-ci.yml ([c3e1d71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c3e1d71e9b7e92b1fe43c9b1e864f523cb6fa200))
* Update 'Product Offering Qualification' URL ([84c7357](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/84c7357ead2bf56a6ffd50e79dae350cae968d55))
* Update commons version ([a658be1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a658be17c397a5e4ad7f838cd13bfff7c886606a))
* Update default URLs for website and shop in 'order-capture-kubernetes.yml' ([4d2e350](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4d2e35099d5effb8af03df8ac8e843218b78a542))
* Update Inventory Management URL ([f46ddd1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f46ddd1d88f161e4d506838ee4e7feecea9527c1))
* Update unit tests ([7b7b3cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7b7b3cbc0131e96bb3edfac793b5ec8eea7aafbe))


### Features

* -add product price ([2f1a6a1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f1a6a118d24dfa1a250a519ba73b09c339e371b))
* -add product price ([90e45ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/90e45ea8941939455d9fb44e3249ba5967a270ca))
* -add ReliesFrom RelationShip ([21f5f6c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/21f5f6c860f3f0a7972a358e6aa47b420a4ee3e2))
* -add ReliesFrom RelationShip ([d464173](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d464173783357679ee27f169598f6832053b878b))
* add async ([2984a74](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2984a7468f6709a8b00fa904eac9db0f1bf7b376))
* Add Postman Collection for Modification Use Case in Addition to Acquisition ([051b0e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/051b0e7a07893cd737acff4aa1620393afd6046e))
* add webclient and add keycloak config ([2f5c2b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f5c2b6d0cf42aec591786df5d54f14bfafcbe88))
* add webclient and add keycloak config ([6a35611](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/6a35611bef9da1ad22a102739a1aff80708facf0))
* configure test container ([7f3abb1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7f3abb178969edbd079b23814271355cae1a3330))
* configure test container ([fe481f7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/fe481f7c626bef33eab7a1a490a571427654eda7))
* create dynamic configuration ([81332c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/81332c58a6cf88528b37dbe8ccfa05f1cbd0ec11))
* Enhance 'create configuration session' automated task ([03b457b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/03b457b8f1ee8463f090ff962967597f685dae91))
* Enhance PATCH method for updating products in CPIB ([1c82dca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1c82dca06bb326cb57e438d47a9f8304e29b2315))
* Enhance payOrder user action ([d46ecc4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d46ecc4de733faa30e0e2592cf3632a04dd35c16))
* Finalize the integration with CPIB ([2ae66cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2ae66cf8b275240c7c15b14afd556f0d4a112844))
* fix after conflicts. ([d797acc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d797acc3cc5ec91241610a3018f173abdae7f249))
* fix after merge conflicts. ([9d9b2ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/9d9b2ec7417e56989f3a4f3d917b9c7e52d40c3c))
* fix after review. ([a2fb5e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a2fb5e4cf385b71f8f640c6675e81539a8f37bfd))
* fix after review. ([61d4348](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/61d4348d0fdcc4f37e74baaca93f0504dc72755e))
* fix config ([660f4b2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/660f4b25f0278571ad9a7511f14fd395bac86f9f))
* fix config ([a28c49f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/a28c49f1109ba49ef9b651c529c89bd23d680086))
* fix conflict ([82ed5af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/82ed5afea744eb703d1b1f941649373893ad08f4))
* Fix CORS Origin issue ([516551b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/516551b2bc19629160c0a8a59bf7ac069934989e))
* fix mvn build ([69ef3af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/69ef3af9d86f2fee6e76a2202970520a96a34394))
* fix sonar config ([f8b0b96](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f8b0b967f53d4f123b3a06bf8d19464b73dfe497))
* fix sonar config ([0cb4f6d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0cb4f6d61c16bfef52c789241ebb85d943e0ea3a))
* fix sonar issue ([3786225](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/3786225676de8cd219e3ebb20ebb6a1c73e90371))
* fix tests ([18c6bf2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/18c6bf29943398d86ff0e844b14815efff75e462))
* fix unit test ([f770d8a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f770d8ae915ee80eb08c1c09cc0f2bc46a70cd34))
* fix unit test ([ba2996a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/ba2996aa9497453ae01a3f9236fa683f7e88a1a4))
* fix unit test. ([83981ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/83981ed75b949ace3eabd71aebe7b194701b84bb))
* fix unit test. ([bf93b07](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/bf93b0748b3db9294a60d3370a93fce663d97135))
* Implement functionality to modify Contract Product ([deca716](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/deca716c751808b769d9a0c890bdc2ea6105ff40))
* Increase timeout ([829ebe2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/829ebe2362ee95c1736ae2d33e05653100d4df41))
* Integrate Product Offering Qualification API ([d281504](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d2815046de0a05b181444e4d0bff70001720c688))
* integrate the contract modification use case ([aa30e35](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/aa30e358c6bf66eb52fc53043201676ae74572b3))
* refactor 'setCompletionTasks' and remove workaround after processflow enhancements ([94fa597](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/94fa5970638303bdb336199d1852f9c7224c293e))
* Update Catalog and Qualification with Mocked URLs ([c692fcb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/c692fcb1860eebb03b626d34aca9f595bcfadca6))
* Update code and imports with commons new version ([2f97967](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f97967fdde7af4a43c4f0be0329ec36a303f998))
* update config ([210895b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/210895b927e736b0258f37069ce5f339b4ed36a5))
* update config files ([f858896](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/f858896c3d3d82202e15ba5ebb1bf9aa61dcef93))
* update config files ([808b652](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/808b6529a7c9f486f84cf53db6a7e865c5ad6c39))
* Update Postman collections ([b3f9ab6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/b3f9ab659f59c28a74eb6479092f88b71f5b2879))
* Update Postman Collections ([efd159f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/efd159f65fac580212a355d753fb4d95bcdd0a77))
* Update Postman collections for acquisition and modification use cases ([4e8bdba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4e8bdbabdc8aa13b9a8815b82bd6cb4166882ec1))
* update process for modify contract ([8a397e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/8a397e443ad565a181edf1d259c2e1843eccd317))
* Update Product Offering Qualification service name in integration ([d35540a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/d35540a4acc9b65b722a8f9198c96e720131c722))
* Update State Machine process ([7d2783c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/7d2783c7c62824b9d1f4b49f712288e33f5e8042))
* update the integration config with mock server ([1b5f1a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/1b5f1a4fe0138930e525b98a4549da4cad2daaff))
* update the integration config with mock server ([2839efd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2839efd3cde2a25f8f41d3c8dd172bdb630351e1))
* update the integration config with mock server ([e7c2c5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/e7c2c5d48591ba29ae85e5bbaf3f91c21231d55b))
* Upgrade order-capture to SpringBoot 3.1 and Java 17 ([4539ad8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/4539ad8ea31c7e257c0bcafc481ef5403bf6dd4b))
* validate offer for contract modification ([80a355c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/80a355cc164bd99e7cc5582705a3db61817d9fce))


### Reverts

* Revert "remove health from otel" ([0c219bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/0c219bda590da701c3c5bc8b7880fe1549e0fe68))
* Revert "remove health from otel" ([44170d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/44170d33426591331a44c3cac64b9d1042b43936))
* Revert "remove health from otel using span" ([2f0e989](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-capture/commit/2f0e989220956c5915a9a9e827842b05825d31b6))

# 1.0.0 (2023-10-29)


### Bug Fixes

* Add unit tests and fix sonar issues ([866b618](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/866b618a2eddd3df6a245f4a27f473334069d43d))
* after code review ([92e0ef1](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/92e0ef1d5172cab5a2fb7bbffd340d8a45a34e64))
* after code review ([1923041](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/1923041d8e738f4dea75b3033938a83d009f54a2))
* after merge request ([806441e](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/806441eea86dfdacf80a8e109b7b7f41a0f1b2f0))
* clear cancel task on the end of the process ([a317e91](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/a317e913e4eada05ce13e26f30a418629babdd33))
* Exclude 'OrderItemToBePaidServiceImpl' from Sonar coverage ([32f6c55](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/32f6c55876062914cdfe300a7ed6cca102b47958))
* Exclude 'PayOrderUserAction' from Sonar coverage ([5f13293](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/5f13293c18f038ad1bf141a4439eb9af35a8f613))
* fix conflict ([d80681a](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/d80681a8c5656df166b98df3db691dcca829bcc2))
* fix conflicts ([2e28927](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/2e289275a27bdb964cb0426c191076d9480ef040))
* fix conflicts ([d70d4f7](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/d70d4f7df315cade577577c54af763bdec83b9bc))
* fix RestTemplate PATCH request ([5ec4f5b](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/5ec4f5bf3364bf5508c48e42d888361d2381a4e9))
* fix the imports issue ([bd6b781](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/bd6b781e217cadc9139aa02c17bf551ae7e95ab2))
* fix unit test ([d953da0](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/d953da0015c668034ac9d72061f6cdee6d49c8ee))
* fix unit tests ([6285821](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/6285821b198f02b5495b0a23f42f9f7d314f6690))
* Resolve related entity set in context ([a009ddb](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/a009ddb23082f3e1b46a71ade56c790070e76eb5))
* Update 'gitlab-ci.yml' to use the latest version of Node ([14d71fe](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/14d71fe4fbb96fb196b9cdb18db6525799a71e66))
* update package name ([f116693](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/f1166938e8b425bd10d6da801a3e5eb27b0f44da))


### Features

* add deployment config ([c52e277](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/c52e277f4de8464471790f70bc24af64383acff4))
* add guard to check the product order instantiation ([9179113](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/917911326565ba28caf96299d04c38321a28118b))
* add id for product order item ([ddc1db5](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/ddc1db5126e9dbef809ef5dbc00bc4761e57324f))
* add instantiate order action ([3d4cdf0](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/3d4cdf0981161bfa97a49a0ec8ccc8b2d4836fdc))
* add Inventory Resource DTO ([0fe2437](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/0fe243702c8c56031122b2ce87f9ba4eee9f40a4))
* add logging configuration ([8afd817](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/8afd8172c18dcbe37716bb58ef988f8f36b61bc0))
* add logging configuration ([3680608](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/368060826719fb3f614476f9566bbdc70ca258ea))
* add mapper from ProductOrder to ProductOrderDTO ([98637b1](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/98637b1bf07350af6b891675c6aaf03795fd4b7a))
* Add new process flow evolutions ([f1db752](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/f1db752334a252fe198a94bfe9b6806f8e81b4d8))
* add postman collection ([bb20d04](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/bb20d04e8285cc3487c7bef173a88f41d65b664f))
* Add Postman Collection for Modification Use Case in Addition to Acquisition ([051b0e7](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/051b0e7a07893cd737acff4aa1620393afd6046e))
* Add product Order Event ([329e606](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/329e6060ae265795117dcf6bf513210ae113f7a8))
* add related party validation ([6e1c790](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/6e1c790a627c6a0e2ad343537294fc8864671e47))
* add reserved resource ref to POI ([55df1fb](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/55df1fbfb25580e7bc10fb3264699dac6df197ea))
* add unit test ([2306fcf](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/2306fcf17f8b549c67652c209e737d77119afc2c))
* add unit test ([d1b2281](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/d1b2281b42d46126963f04596178db6daf407431))
* Add unit tests for the create product order and for the mapper from configItem to prodOrderItem ([e6d7dcf](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/e6d7dcf6c168d4c6829e36f0e0bb7a27dfe2a6b1))
* Add unit tests for the create product order and for the mapper from configItem to prodOrderItem ([b6fa08e](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/b6fa08e484b72033c0b332d9751ab145eefb421b))
* cancelOrder ([71d5220](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/71d5220a23ee3b0bfc2e52d9a2b3388b0c0fa4e7))
* cancelOrder add check productOrder is not null ([541b5cd](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/541b5cd43a9fa8967eb84f4b3c26e0c453e0af5f))
* cancelOrder update cancel process ([0d59a56](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/0d59a56813a0290414e41df01f2f24d1f040dfe6))
* configure test container ([7f3abb1](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/7f3abb178969edbd079b23814271355cae1a3330))
* configure test container ([fe481f7](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/fe481f7c626bef33eab7a1a490a571427654eda7))
* create action for product order instantiation in DB ([214751f](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/214751ffe79fcba0bf1207847325e02954a0db0e))
* create installed base ([e8810d2](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/e8810d23de08f6153946bb1b1cc80245b76e99a4))
* Create mapper to convert configurationItem(s) to productOrderItem(s) ([b4e0082](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/b4e00824ade3edae80c17f74a8febf581f2690b8))
* Create mapper to convert configurationItem(s) to productOrderItem(s) ([2345e0a](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/2345e0a2c3d8fcb0445f53709eb073919d30edbf))
* create update order action ([33f44de](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/33f44dea5b28bded997c72e0f694c3d0bf8394fb))
* create update product order service ([fe28e7f](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/fe28e7fe297b808f624a24ba82c216c6551193cd))
* Display specific message in case of check eligibility failure ([73b3418](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/73b3418c4e0879ee9a6476933107e5776ce80e3d))
* Display specific message in case of validate offer failure ([19c542e](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/19c542e9e24ec5fa4d056f3f4ccce7cc0a85dd22))
* Employ reserved resource IDs instead of utilizing available ones ([68315c8](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/68315c8a71976d28077ba1cc260cd9b851029bb7))
* Enhance 'create configuration session' automated task ([03b457b](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/03b457b8f1ee8463f090ff962967597f685dae91))
* Enhance PATCH method for updating products in CPIB ([1c82dca](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/1c82dca06bb326cb57e438d47a9f8304e29b2315))
* exclude config class for sonar checking ([c69d6c3](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/c69d6c352a5f519cac223d3aa0dc471809141b62))
* exclude config class for sonar checking ([629ba62](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/629ba62a714e22eff588d5a3dfd9af53d4acae93))
* fix code coverage for config class ([afceb44](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/afceb44c88353da198b6ee8ed06e0432412a1db6))
* fix mvn build ([69ef3af](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/69ef3af9d86f2fee6e76a2202970520a96a34394))
* fix project name for logback appender ([4d5fd39](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/4d5fd39ca0b4616378d6c63403e2aba4d03d7e6b))
* fix sonar issue ([3786225](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/3786225676de8cd219e3ebb20ebb6a1c73e90371))
* fix ssl configuration ([eaa7c82](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/eaa7c8276ff0464fd1a2136b0065f943b18714ce))
* fix tests ([18c6bf2](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/18c6bf29943398d86ff0e844b14815efff75e462))
* fix unit test ([7a7159f](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/7a7159f6086359b9dd3a181e6d945211c0d1f626))
* fix unit test ([a28ee33](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/a28ee33a1a3e4bc421135ab5aee88ab26a22e803))
* fix unit test ([48e3e6e](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/48e3e6e994c29eb4eb3414a363da1af105ec1cec))
* fix unit test ([cee416f](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/cee416fb3ef1df5c3fcc56f0f5dabb2fb210038c))
* Implement functionality to modify Contract Product ([deca716](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/deca716c751808b769d9a0c890bdc2ea6105ff40))
* implement get related resources' id(s) for each product item ([6e1456b](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/6e1456b53c19e7c6d96041db8e9540e706a0ff79))
* implement get resources id(s) for productItem(s) ([ba02db7](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/ba02db7f4bd1047ae861237df642928bfd775189))
* implement get resources service and code refactoring ([daf8aa8](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/daf8aa8bf49e44879ce79cc88c93fedd090193ea))
* implement modify confirm configuration ([758cab4](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/758cab4a5d9ea05e8a3009650bc571e03cdbd086))
* implement transition action to set catalog driven tasks ([5ad2901](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/5ad2901243d5a624917f6e7bf86107d69a5eac7e))
* Increase timeout ([829ebe2](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/829ebe2362ee95c1736ae2d33e05653100d4df41))
* integrate the contract modification use case ([aa30e35](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/aa30e358c6bf66eb52fc53043201676ae74572b3))
* Integration with CPIB ([3f1403c](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/3f1403c82d1f5c4b0192bda61bd6df1f97f5512c))
* integration with product order inventory ([d2ae4c9](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/d2ae4c969e05491cd4c1579d597352280486bc9e))
* Update Catalog and Qualification with Mocked URLs ([c692fcb](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/c692fcb1860eebb03b626d34aca9f595bcfadca6))
* update config ([210895b](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/210895b927e736b0258f37069ce5f339b4ed36a5))
* update config ([5f9f34a](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/5f9f34a453c88b1af781a29310b4b31f1dc1a006))
* update confirm configuration service ([9f20086](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/9f200865dc2c842ea1bbd7cf7d4190bec83ceab6))
* update entities to comply with the product order data model ([670bbf1](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/670bbf156b768eb5293a431ba265d653d55abf2e))
* Update failure message for eligibility check ([71b08f6](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/71b08f6f6a3951e1f90136f6da093c667cb0d6ae))
* update kafka config ([df261cf](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/df261cf1e4b2da298c200b316196dc74b02fa535))
* update process for modify contract ([8a397e4](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/8a397e443ad565a181edf1d259c2e1843eccd317))
* update product offering endpoint version ([75bb5de](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/75bb5de526d04049a315fcebd4fb9f82eeb0cdbe))
* update product order state to accepted ([d22924c](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/d22924ceb8528aa881155cac815d043e75f686db))
* update ssl configuration ([fb67931](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/fb67931f9ed3114c63a4420d1aba1eaed9a95328))
* update the deployment config ([60b0422](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/60b042244be42dffd5cf3b1d292af657614e8e4c))
* update the deployment config ([4965106](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/496510676043ce1854f593d44ddacfcf62975ce3))
* validate offer for contract modification ([80a355c](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/80a355cc164bd99e7cc5582705a3db61817d9fce))


### Reverts

* Revert "Update target file name" ([1b4ce3d](https://gitlab.tech.orange/disco/disco-order-management/order-capture/commit/1b4ce3d271e84758997f883f9358874ad33770d4))
