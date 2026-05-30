# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.8.0...1.9.0) (2026-04-28)


### Bug Fixes

* add auth to test endpoints ([31ad831](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31ad831d68d8cf3dffda7e68aaad005098316cde))
* add check if time range is null ([68c89cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68c89cf8977c2f1bf27a763c8ce7129859ecdc16))
* add global exception handler ([9d115c1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9d115c13d88ee47472ea3c7f05ae14ff4af5c03f))
* correct database IP address in application.yml ([ba2dfcd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ba2dfcd696a56f9f76b084b9fce0417a5a9c0a7f))
* delivery order fallout executer ([f1d69cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f1d69cd6b6523d2841657fd3f5d4b1788aba3324))
* dummy commit to increase semantic release version ([5fa04bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5fa04bf687e708cfa13c5f850976cba573100350))
* dummy commit to increase semantic release version ([25394ae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/25394ae73cb397f4f63ea5c7fe233dcf5666d65f))
* fix conflicts ([5447aa7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5447aa7d4f5637670a7794764a9d12fa41e3a66f))
* fix gitleaks issue as it is [secure] positive ([b94997a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b94997ae22ea7edc48faee00ef36d94bd0211719))
* fix gitleaks issue as it is [secure] positive ([7a9ebba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7a9ebbae4342c7c3707f0521e88010cbb2066217))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([d345aeb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d345aeb80c86d25a9c0e3c8b76d50b136e16c4b9))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([a9840b3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a9840b39dc19add112e1eb7eddab3ce7534b2295))
* if plan already held don't fallout ([ba469fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ba469fd21686a5711bb0d6cd225dbf5181d07e76))
* **IPCEISCOOD-1056:** [integration] [modification] Plan Held When Modifying... ([4cc0527](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4cc052708ea768d629f59a29afdb1a4f6acb830a))
* **IPCEISCOOD-1056:** add null check ([9c73717](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9c73717afa92b2b4b77f36a2d5d31dfa25ef3a99))
* **IPCEISCOOD-1056:** add null check to prevent runtime error ([0023875](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0023875703799b048b72ba4fae84f0c68178ad05))
* remove manual retries add mongo retries on kafka ([b0c96ae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b0c96ae312f8ce707a06e5c7bb1c380406e966bb))
* remove null check on productOrderItemRelationship ([d372ae3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d372ae3210c4974c56af3ba478feaad1d89d5a86))
* resolve BDD test failures by adjusting TestContainers version ([17797df](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/17797dfcbef082f2d3ff22fcf8b14424b5c4dad8))
* typo in ProductOrderStateChangeEvent deadletter ([0d34c5f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0d34c5f2db7e0af0ee6fdf51cdc2f3489eb09451))
* use configurable properties and add backoff ([d89f5f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d89f5f94a6b75460f1e870a8c8b499af1e2a51e2))


### Features

* **IPCEISCOOD-1062:** enhance shipping deliverwith relationship ([8a94bee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8a94bee5ebfa3696ce9adeef4bcb138725d7d460))
* **IPCEISCOOD-1063:** Enhance Shipping - Evolve Deliver Selected Node ([6d30a98](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6d30a987b8f424e4ba78fb8ab9776146044deb60))
* **IPCEISCOOD-1072:** sort plans by start date ([3624aca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3624aca99147c0ca426ec41aee25773ee45c1f80))
* **IPCEISCOOD-1104:** evolve cfs delivery management ([638253e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/638253e1dde635baed2040fd450accdb7e10442b))
* **IPCEISCOOD-1105:** evolve tangible delivery management ([8062751](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/80627510149cafe9a2122c2ff99e12489776da9e))
* **IPCEISCOOD-930:** Check start time for Root nodes before changing status to InProgress ([f022f7b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f022f7b70b64a2b45788affb69ac5f4d56160906))

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.8.0...1.9.0) (2026-04-23)


### Bug Fixes

* add auth to test endpoints ([31ad831](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31ad831d68d8cf3dffda7e68aaad005098316cde))
* add check if time range is null ([68c89cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68c89cf8977c2f1bf27a763c8ce7129859ecdc16))
* add global exception handler ([9d115c1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9d115c13d88ee47472ea3c7f05ae14ff4af5c03f))
* correct database IP address in application.yml ([ba2dfcd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ba2dfcd696a56f9f76b084b9fce0417a5a9c0a7f))
* delivery order fallout executer ([f1d69cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f1d69cd6b6523d2841657fd3f5d4b1788aba3324))
* dummy commit to increase semantic release version ([5fa04bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5fa04bf687e708cfa13c5f850976cba573100350))
* fix conflicts ([5447aa7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5447aa7d4f5637670a7794764a9d12fa41e3a66f))
* fix gitleaks issue as it is [secure] positive ([b94997a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b94997ae22ea7edc48faee00ef36d94bd0211719))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([d345aeb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d345aeb80c86d25a9c0e3c8b76d50b136e16c4b9))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([a9840b3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a9840b39dc19add112e1eb7eddab3ce7534b2295))
* if plan already held don't fallout ([ba469fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ba469fd21686a5711bb0d6cd225dbf5181d07e76))
* **IPCEISCOOD-1056:** add null check ([9c73717](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9c73717afa92b2b4b77f36a2d5d31dfa25ef3a99))
* **IPCEISCOOD-1056:** add null check to prevent runtime error ([0023875](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0023875703799b048b72ba4fae84f0c68178ad05))
* remove manual retries add mongo retries on kafka ([b0c96ae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b0c96ae312f8ce707a06e5c7bb1c380406e966bb))
* remove null check on productOrderItemRelationship ([d372ae3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d372ae3210c4974c56af3ba478feaad1d89d5a86))
* resolve BDD test failures by adjusting TestContainers version ([17797df](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/17797dfcbef082f2d3ff22fcf8b14424b5c4dad8))
* typo in ProductOrderStateChangeEvent deadletter ([0d34c5f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0d34c5f2db7e0af0ee6fdf51cdc2f3489eb09451))
* use configurable properties and add backoff ([d89f5f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d89f5f94a6b75460f1e870a8c8b499af1e2a51e2))


### Features

* **IPCEISCOOD-1062:** enhance shipping deliverwith relationship ([8a94bee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8a94bee5ebfa3696ce9adeef4bcb138725d7d460))
* **IPCEISCOOD-1063:** Enhance Shipping - Evolve Deliver Selected Node ([6d30a98](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6d30a987b8f424e4ba78fb8ab9776146044deb60))
* **IPCEISCOOD-1072:** sort plans by start date ([3624aca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3624aca99147c0ca426ec41aee25773ee45c1f80))
* **IPCEISCOOD-1104:** evolve cfs delivery management ([638253e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/638253e1dde635baed2040fd450accdb7e10442b))
* **IPCEISCOOD-1105:** evolve tangible delivery management ([8062751](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/80627510149cafe9a2122c2ff99e12489776da9e))
* **IPCEISCOOD-930:** Check start time for Root nodes before changing status to InProgress ([f022f7b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f022f7b70b64a2b45788affb69ac5f4d56160906))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.7.0...1.8.0) (2026-02-10)


### Bug Fixes

* add auth to test endpoints ([fd66c17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fd66c1719a47eb36dc1046d1aa148ee2ada7929b))
* fetch planned plans query ([16d272e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/16d272eaa846ac8936785a7808d535fc2d60de23))
* if plan already held don't fallout ([a9540ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a9540ea246195670829e36f3f9aadbdc0cb96db4))
* **IPCEISCOOD-1056:** add null check ([692f2c6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/692f2c6b01594658750b7f726680472888e355dc))
* **IPCEISCOOD-1056:** add null check to prevent runtime error ([e02923f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e02923f88589a0e22f81d4ca9ac0956290ba15a4))
* remove manual retries add mongo retries on kafka ([3d43772](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3d437725295e684cf1f09f74117d274bb780f927))
* sonar issue ([2872f6c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2872f6c31d788e0e0db8d45ee2551874a337c2d1))
* use configurable properties and add backoff ([ca675d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ca675d45216b69fb6c966d6665a352a516b703c6))
* userrole role error updating commons ([f7fb922](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f7fb922f851a558ef3ba6261c52630f602578ef6))


### Features

* **1050:** aggregate product leadtime ([7e2e804](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7e2e80419376e07bd897b3e6608fd3cf943784e1))
* **933:** set start time for planned orders ([f933b44](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f933b44fe955d4b460a7fad457376700f19e1039))
* **IPCEIS-COOD-1034:** set-estimate-lead-time-for-held-node ([5f74b65](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5f74b65e413869f4518ba6a2eedd3cb51c8d3376))
* **IPCEISCOOD-1012:** Gradual Plan State Transition ([5410076](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/54100762845cbf13da9a089e2b99ff17e005b27d))
* **IPCEISCOOD-1029:** Error messages enhancement - implementation ([9ccaaee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9ccaaeed91b0c6619e4e37add07104e7d57d0916))
* **IPCEISCOOD-1031:** Evolve Create Orchestration plan: Set the initial... ([f9030a9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f9030a9cdbfbcd332ca107bf8aa8707a32620208))
* **IPCEISCOOD-1036:** manage planned plans ([9f08fb7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9f08fb7cdd5d695278492a38773df053afc7fee1))
* **IPCEISCOOD-1038:** add status endpoint ([e371622](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e371622ffaadb55eae86f4c49d463d9343fc69a6))
* **IPCEISCOOD-1062:** enhance shipping deliverwith relationship ([1aaf24c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1aaf24c8170ef49675560cc8ab9d50759864f360))
* **IPCEISCOOD-1063:** Enhance Shipping - Evolve Deliver Selected Node ([891e5aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/891e5aaff5b2979ca7cb9d09a17a30a5fba4a729))
* **IPCEISCOOD-930:** Check start time for Root nodes before changing status to InProgress ([ac0eef4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ac0eef4a8193521434fb16b2cbd3567f254c6dab))
* **IPCEISCOOD-940:** support date characteristic type ([68cc9da](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68cc9daeacc148c8a4a797ae06fa8c8a78577230))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.6.0...1.7.0) (2025-11-13)


### Bug Fixes

* **988:** user roles permissions ([b1adc30](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1adc30636c89060fb04a4fa684b465230d55714))
* add prometheus annotation for mongodb ([302c77a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/302c77ab615be734158c9ad6092c343d656ad9c6))
* **IPCEISCOOD-1004:** tmf compliance ([24a0735](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/24a0735d1c30acdfdf09c1beb177a9e568b434bc))
* **IPCEISCOOD-1013:** Tangible product remains in “In Progress” instead of... ([cf3c0c8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cf3c0c8e4b049b931db41b4b58aacb2755980341))
* **IPCEISCOOD-938:** skip publishing events if new state equals previous state ([6e9b655](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e9b655b1145d65c9f63ef0c27265722b191f437))
* **IPCEISCOOD-962:** [Integration] When a user migrates from an old contract to a new contract, the old contract name is still displayed in Plan Details. ([2ae645d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ae645d8ab08d4ace78772b05e99ce334c221ab3))
* **IPCEISCOOD-965:** migartion plan is held with incorrect error message ([6737905](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6737905f0b4b7414a23bc25ddb66b455ff7296d5))
* **IPCEISCOOD-967:** exclude related products with MIGRATETO relationship type ([d24d333](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d24d3331de4b4581a1a247f2c2cbecf358283851))
* **IPCEISCOOD-989:** fix plan completion date passing last node completion ([ca741d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ca741d4ff0516a9a9dc285cbca51e9d1608c5e42))
* remove values files as we moved them to gitlab-ci project ([8b14b08](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8b14b080367ad7093c79d0d4ea80b22ae215eb60))
* sonar qube issues ([1d0d4b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1d0d4b5977d855f83b97b4a704b8fbf90b65e9b9))


### Features

* **IPCEISCOOD-809:** Update CPIB state before Node state (In Delivery) ([2216f00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2216f00352401846f9f53262f92c3584d6e18856))
* **IPCEISCOOD-871:** Consume new DTO for Recurring discount valid for a specified period ([68d9df1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68d9df19d89e75507dee4003c75b76a2f3b8e26a))
* **IPCEISCOOD-986:** Create index for relatedProductOrder.id ([44566e5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/44566e5d97951b495a103e40dbc7cfd32e529424))
* **upgrade-orchestration-spec-module:** upgrade orchestration spec module to 1.0.1 ([c6ba91f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c6ba91f2f0526eaa1673d00d5f7e6c76d5f5408e))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.6.0...1.7.0) (2025-11-13)


### Bug Fixes

* **988:** user roles permissions ([b1adc30](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1adc30636c89060fb04a4fa684b465230d55714))
* add prometheus annotation for mongodb ([302c77a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/302c77ab615be734158c9ad6092c343d656ad9c6))
* **IPCEISCOOD-1004:** tmf compliance ([24a0735](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/24a0735d1c30acdfdf09c1beb177a9e568b434bc))
* **IPCEISCOOD-1013:** Tangible product remains in “In Progress” instead of... ([cf3c0c8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cf3c0c8e4b049b931db41b4b58aacb2755980341))
* **IPCEISCOOD-938:** skip publishing events if new state equals previous state ([6e9b655](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e9b655b1145d65c9f63ef0c27265722b191f437))
* **IPCEISCOOD-962:** [Integration] When a user migrates from an old contract to a new contract, the old contract name is still displayed in Plan Details. ([2ae645d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ae645d8ab08d4ace78772b05e99ce334c221ab3))
* **IPCEISCOOD-965:** migartion plan is held with incorrect error message ([6737905](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6737905f0b4b7414a23bc25ddb66b455ff7296d5))
* **IPCEISCOOD-967:** exclude related products with MIGRATETO relationship type ([d24d333](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d24d3331de4b4581a1a247f2c2cbecf358283851))
* **IPCEISCOOD-989:** fix plan completion date passing last node completion ([ca741d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ca741d4ff0516a9a9dc285cbca51e9d1608c5e42))
* remove values files as we moved them to gitlab-ci project ([8b14b08](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8b14b080367ad7093c79d0d4ea80b22ae215eb60))
* sonar qube issues ([1d0d4b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1d0d4b5977d855f83b97b4a704b8fbf90b65e9b9))


### Features

* **IPCEISCOOD-809:** Update CPIB state before Node state (In Delivery) ([2216f00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2216f00352401846f9f53262f92c3584d6e18856))
* **IPCEISCOOD-871:** Consume new DTO for Recurring discount valid for a specified period ([68d9df1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68d9df19d89e75507dee4003c75b76a2f3b8e26a))
* **IPCEISCOOD-986:** Create index for relatedProductOrder.id ([44566e5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/44566e5d97951b495a103e40dbc7cfd32e529424))
* **upgrade-orchestration-spec-module:** upgrade orchestration spec module to 1.0.1 ([c6ba91f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c6ba91f2f0526eaa1673d00d5f7e6c76d5f5408e))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.5.5...1.6.0) (2025-07-16)


### Bug Fixes

* add new user role urls ([22f4753](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/22f47531db82b6e8a7e7da578e4ca5efc13e480e))
* add summary to readme ([9f86674](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9f86674d68156ecba0ca27623f99dd837ce2a1a3))
* apply pascal format to md file ([f80cd5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f80cd5deddec5c23b201f083c15f45cca86d8134))
* change add max-poll-records & increase kafka log level to debug ([dff09b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/dff09b95e86e5e887d0934bc89cff38d67c5fda0))
* change max-poll-record to 100 & enable auto commit ([5e5031b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5e5031b317c54d96be1ec8ac0b946666b9445b38))
* configure Plan purge duration on all enviornment ([f397277](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f397277950096dba5122bdeb6020a58612ad5eda))
* fix add default coodError for CoodTechnicalException ([d2e936a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d2e936a27f1ce23e7c714fa251fa180bcfd92cc2))
* fix add default coodError for CoodTechnicalException ([3d50310](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3d503106fa61e977d3b079009da94bffa5dea2fa))
* fix add default coodError for CoodTechnicalException ([5b3bf24](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5b3bf24f10efc868fe90d876b53733de60026cd1))
* fix add default coodError for CoodTechnicalException ([d6ac136](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d6ac1369f3bd82fcb7cc1d27ac5e40b46f6286c4))
* fix rebase sprint1-pi7 on develop ([5b6fabe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5b6fabeff730c98a63f42c9d6a2a73197db04b88))
* fix recuring object inside order price ([730dd43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/730dd43cdab1be2092d8e3724e0346330611b980))
* fix urls for others service for innovation environment ([df52ba6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/df52ba6ad27735054061f8372272a684dc0607b0))
* Get Orchestration plans with query param fields return bad request ([af7c1cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/af7c1cd262964200d03fa1d2233a7228dfa2b759))
* increase test MISSEDCOUNT ([17d225e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/17d225ea80359079c88d21a65a5747a26354da21))
* **IPCEISCOOD-354:** Wrong response code for Partial resource returned in... ([132cd31](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/132cd31eaf9e0d01df84a4dc8110e72f2fe03df6))
* **IPCEISCOOD-361:** Wrong responses in case of listing Orchestration Plans... ([e2cbc91](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e2cbc9108760ce0ea6f0418436411fb2e0fde0bd))
* **IPCEISCOOD-440:** Get Orchestration plans with query param fields return bad request ([2cc7144](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2cc714456ed0a7d3f9ac4ccde41a6bbe2c94f39b))
* **IPCEISCOOD-499:** Issue happened and unexpected behaviour in delivery and node state change ([4730d3c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4730d3c59f12bcf898c53f0ec61adca74a4992d2))
* **IPCEISCOOD-527:** separate flow for node state change from ack to inDelivery ([795d310](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/795d310cb6af625bafbab0c8ef9bac3451aee115))
* **IPCEISCOOD-544:** SIM Card Status didn't updated as Sold and ML and Connectivity nodes got held ([ec1cc13](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ec1cc130f19a58193182c4b52bcab150626edad6))
* **IPCEISCOOD-601:** SIM delivery is stuck on integration environment ([0dd7572](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0dd7572aba0101887b3d73899d66ba329046cb5f))
* **IPCEISCOOD-623:** Skip update cpib product on orchestration plan node held... ([4b3420a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4b3420adc9271ca0244bbf2a651d94196c1961cc))
* **IPCEISCOOD-674:** Enhance service order & shipping order scheduler jobs ([80639e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/80639e41e80acd5bda63809079fd3da504301163))
* **IPCEISCOOD-684:** Product characteristics are duplicated for all products,... ([b91df3a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b91df3a7870b8b2ca593a29df73f458fd5e2597f))
* **IPCEISCOOD-690:** integration - offer name info is missed in the plane details ([f991d0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f991d0ce7e9fee8c16b53cb5fb476eb9733f5c76))
* **IPCEISCOOD-702:** Orchestration plan node pervious state parameters is update incorrectly ([88348b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/88348b6e9c3b7313a3929cf182fe5f22afab0b9a))
* **IPCEISCOOD-743:** [Integration] Occasionally, orchestration plan nodes are... ([cb962dc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cb962dcb36d81180411221df8777a296af303f1f))
* Listing Orchestration Plans with EMPTY attribute Name responds with 500 ([af96ad7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/af96ad7349700cbabff70244ef6ab76e92ee565f))
* log error, skip mandatory field in order ([bc9cb7b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bc9cb7b5c581ef4ed3494facaf764e315f7231e7))
* Move node to DLT and fire Create fallout in all DLT consumer- fallout instance was not created ([1f3d7fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1f3d7fd873f5ea0852e08b4c4bbc01ca5636f1f0)), closes [#IPCEISCOOD-459](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-459)
* Related product order item quantity issue ([37e034a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/37e034a1fb3fd182f0ee75b06cfaecb1adbeb462))
* release api 0.10.0 ([9685446](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/968544641c8fb37ba55f94a67aa5d67f39576a96))
* release api 1.0.1 ([e0ce63d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e0ce63da6e6964e3a20ce0dbd3f6025edf3a9ba4))
* release pi7 sp2 apis ([8e33336](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8e33336b404fd53baa59595ecca49b5b49e1f42c))
* release spec module version 0.9.0_release ([dce6077](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/dce6077dac1bae7b3452b0cefde71e52db56773e))
* remove extra files ([a8567dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a8567ddc7d8d0bb710810aa5ff09592d737b7fbb))
* remove extra files, adjust getting stated md file ([cfe7e92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cfe7e925b88084ac7aae89d35c8be625e311949a))
* rename userrole url ([4379e9b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4379e9bae70d3153721f76e12985745adbd19d19))
* replace deletePlanThresholdDuration with variable to can change it ([2728cbc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2728cbc28fb2af5155f79332c0ea8588b70871e9))
* revert back the removal of dependencies ([ccf1462](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ccf1462024efe0d2a28673dce8776e2b333b364a))
* revert fallout dependency in pom.xml ([a483765](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a483765e5b48741c94dabbb95faa7093a097d795))
* revert pom.xml jacoco folder ([4adb394](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4adb3946f83b341e7c8f0238f7c78591bddfe30e))
* Some times plan stucked in Ack state ([ac843e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ac843e6be10593e7fba6dfb2390ea7713599189b))
* Tag swagger for sprint1 PI7 and remove uneeded dependencies ([12d7f96](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/12d7f967b24137260008c4eda85e63032254b4fc))
* temp fix see keycloak of integration ([818c775](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/818c775ad809dbfa634e26a5a482992f000b963d))
* update kafka consumer config ([5c1b411](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5c1b4116a8b3c36c601cf1372b493c342bc348c0))
* update kafka consumer config ([cd7cf2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cd7cf2d9ccfa7567829ce179f3cb2f914c633670))
* update kafka consumer config ([23877a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/23877a25395c139fb6341839323d47c4e8746f97))
* upgrade fallout version ([cf0dd38](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cf0dd38800c6d171be677790bed8abb77e5fe630))
* use fallout module release version ([8869e52](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8869e52895f65918c6bb0604a325a9f5bc41a317))
* Wrong responses in case of listing Orchestration Plans with invalid value... ([a11503f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a11503fff7d0bf37e2b64eaad2eaaf549b268d1e))


### Features

* add errorMessage dto to orchesterationPlan dto ([09d8409](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/09d8409ba67219a705cd8b6c641c27a5460ee595))
* Display Contract Name ([3d30bb5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3d30bb57abb6337fbc00e9b22f8dcb3489f31bab))
* Ensure Atomic Boundary for Status Change in Database & Initialisation of the Event ([214c758](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/214c75881b782ca3289d2916d44e9ff28c272ac7))
* Implement automatic retry in all event consumers for transient errors ([614149b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/614149b3c9cd5ea33d68077f5b0b92b4bdb4811d))
* **IPCEISCOOD-417:** Add Kafka session to presist all entities at the end of consumers and update order items map key in startDeliverSelectedNodes ([e2b5bc9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e2b5bc9879cef1231e857dd41ed1d65123b3a49a))
* **IPCEISCOOD-474:** Adapt with Order DTO new changes after change recuring object ([624c3ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/624c3abef28ee9029f218cf63b0b5cc6d79031d1))
* **IPCEISCOOD-491:** Update cpib spec version ([0fd7ab4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0fd7ab412c31a2e452e2a9f18d2fe8d7e008c6d9))
* **IPCEISCOOD-514:** Create a deep cloning endpoint to update orchestration... ([bd9fa22](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bd9fa220e20b2c7d8ca7036a20910ddd469e5db2))
* **IPCEISCOOD-543:** Change log level for testing env of develop ([6eb5bfe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6eb5bfe7af0ab3b69d88fab0be990fb1cba2ded2))
* **IPCEISCOOD-593:** Include the Debezium configuration details in the README... ([1975771](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/19757718699a60698ca2c33797d1f7582f691708))
* **IPCEISCOOD-638:** Remove poi yml files ([b381d09](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b381d09e80d9480e4fd097adb39ab5504e00e870))
* **IPCEISCOOD-677:** Enhance test cases execution time ([c3c2e5e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c3c2e5e11f1ec12c78112a00862600a6f2eb85b3))
* **IPCEISCOOD-744:** Add logs for OrchestrationPlanModificationServiceImpl ([dcefd25](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/dcefd25ede1e40188a524933d1a33baaf533374e))
* **IPCEISCOOD-766:** Create a migration script to add MongoDB indexes for... ([5ffd8fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5ffd8fa3e8243c9618134a72416c2de90e90c87a))
* **IPCEISCOOD-889:** Migrate to G1GC and Update JVM Parameters ([467c4bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/467c4bf31141c894e5a7164b1052cb42fd56e54e))
* Modify "Create Orchestration plan" to be idempotent ([829e68f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/829e68fa89c6ee8f2941d3b7155b3b1994ccf94d))
* Update Fallout incident resource ([25c2ae8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/25c2ae8b6ad8fb22f7a844b6d7bac61ced59a710))


### Reverts

* Revert "chore: export build log to file as gitlab does not display whole logs" ([eb43279](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/eb43279ec29b04eaf5f2acea40fc2d7f4b40507f))
* Revert "fix: replace keycloak url with service name" ([9c73a42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9c73a420ebf09e6bc54838bc3f31caedfc284c5d))

<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.5.0...1.5.1) (2024-10-27)


### Bug Fixes

* fake commit, update authors ([27ea05e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/27ea05e5f1fccd195818dbb85d18cb24acea81df))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.10...1.5.0) (2024-09-04)


### Bug Fixes

* change docker image config ([998e699](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/998e69970f6e13094036ab402962502c4e4e797f))
* fix add default coodError for CoodTechnicalException ([5b0ce23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5b0ce236c5966d56aaf06e8f1bc3e5f755db3c54))
* fix add default coodError for CoodTechnicalException ([ed984bc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ed984bc789801607d8ee44bbeb2fb09bd4b43e66))
* fix add default coodError for CoodTechnicalException ([28d2134](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/28d21344de431d4577f3c9373cb2809a8de5cf52))
* fix add default coodError for CoodTechnicalException ([3df6f86](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3df6f867339508bbbe359d2a3383b8fe77c3942f))
* fix add default coodError for CoodTechnicalException ([6c6edd0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6c6edd03c8b754cdcad15e82cb3949429825e5c1))
* fix add default coodError for CoodTechnicalException ([8052e37](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8052e3755bf33f971b02a87ddf1c0b064c634364))
* fix add default coodError for CoodTechnicalException ([aa9438b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aa9438b7bc13bf78a7977a55080ee6604045f669))
* fix add default coodError for CoodTechnicalException ([b5d92d0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b5d92d0a7e88f7cbc563c955938a485d2e9b143d))
* fix rebase sprint1-pi7 on develop ([00b3e73](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/00b3e73e2efaa2a7a6784b6890ca4780d5ac832a))
* fix rebase sprint1-pi7 on develop ([8b44a80](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8b44a808dd2a78f624b8c30bfd73eee6d14146f0))
* fix recuring object inside order price ([6e6c85d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e6c85dd96c502d2ee7903a24f74dad53d6ea090))
* fix recuring object inside order price ([f693206](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f6932062cb7e8d61edb60f044fb811483d89dec4))
* fix urls for others service for innovation environment ([30e6bfb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/30e6bfba0d5ae24575c280c462f0a870176fd26c))
* Get Orchestration plans with query param fields return bad request ([31ea82e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31ea82e7c3e89e230827e6c8dfe37ed8a06717fa))
* Get Orchestration plans with query param fields return bad request ([8c69aef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c69aef51dd48e077799435704efc5125966658a))
* increase memory limit for mongo ([cd63644](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cd63644ac4d72cee3eaa2b0e545ed16123e671e9))
* **IPCEISCOOD-354:** Wrong response code for Partial resource returned in... ([ff63302](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ff63302d035af92e2f3c6635855a7673b9e982ba))
* **IPCEISCOOD-354:** Wrong response code for Partial resource returned in... ([0e5c702](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0e5c7023813973c8a74b0cf203b01d4e16220317))
* **IPCEISCOOD-361:** Wrong responses in case of listing Orchestration Plans... ([5c21d00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5c21d007ab53bfb2b5bfa62c686abb9251289bb4))
* **IPCEISCOOD-361:** Wrong responses in case of listing Orchestration Plans... ([31b0418](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31b0418ff2b12b81af20f9d8553cc228282b37c1))
* **IPCEISCOOD-440:** Get Orchestration plans with query param fields return bad request ([8c342fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c342fa37a497228cb38bab046e267dcbc13d82f))
* **IPCEISCOOD-440:** Get Orchestration plans with query param fields return bad request ([f31fb19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f31fb1929790ee8c07c98f6c150bcf7b0e459e62))
* Listing Orchestration Plans with EMPTY attribute Name responds with 500 ([e35b158](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e35b158ea4c33acb418f0b6255c626a232762ecb))
* Listing Orchestration Plans with EMPTY attribute Name responds with 500 ([161b38d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/161b38df7939b3ea7d277a1fa2dd0abbc6c7584a))
* log error, skip mandatory field in order ([976ebee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/976ebeef3819c0ec8be71efe3a8665daea18566b))
* log error, skip mandatory field in order ([11a7c0f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/11a7c0f90c027aa5faee03bd9123a0b13e44d618))
* Merge branch 'release-api-pi7-sp2' into 'develop' ([beea88e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/beea88ec82cadee2c942414ab8e12580f76b1c02))
* merge Tag swagger for sprint1 PI7 and remove uneeded dependencies to develop ([14bd3d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/14bd3d4f8be901b804d5745f094d82387af62d1a))
* move innovation job from staging to production stage ([7baacd3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7baacd3905b3c8c64ceb1897246dcb3d364ab60f))
* Move node to DLT and fire Create fallout in all DLT consumer- fallout instance was not created ([1501045](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1501045028d742f7bae89b239d0974e05f06f784)), closes [#IPCEISCOOD-459](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-459)
* Move node to DLT and fire Create fallout in all DLT consumer- fallout instance was not created ([4f07b16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4f07b16ae9a4e6fa3b1e09fd6a924eb8a21110d7)), closes [#IPCEISCOOD-459](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-459)
* Related product order item quantity issue ([83bd524](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/83bd524c25755818b9901d85aebe2c03ba687ed7))
* Related product order item quantity issue ([b1d4695](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1d46955134952cc3d44c1da2b51af64a3266f00))
* release pi7 sp2 apis ([8bd2d8b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8bd2d8be57149b67b5366d5b3be9c69d43843741))
* release pi7 sp2 apis ([903546e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/903546e1fdd3dbb6fcda6d7ad06f0ab0abca262a))
* release spec module version 0.9.0_release ([49eb9cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/49eb9ccf1588c8ec83e1b83e9391e44e4e50e944))
* release spec module version 0.9.0_release ([d3f1e93](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d3f1e93158662acddbfc7ebd88356ee70f9d5117))
* remove cert from source code and add it as variable in gitlab ci-cd ([4736999](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/473699950b33aaba0e5d43ab1b0034532b78b079))
* remove extra dash ([651dc76](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/651dc76238d425f8008a8f9a47e89300fd3be664))
* remove issue of uploading jar to artificatory ([4fbfa10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4fbfa100905b5f1a956723dc78c30625bf7dcb61))
* revert back the removal of dependencies ([e3180f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e3180f40939a5ff90497bf329afddc7612a0a952))
* revert back the removal of dependencies ([145ecc5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/145ecc5c517430215f0892634c365d1e37c935d9))
* revert fallout dependency in pom.xml ([8e4555f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8e4555f784be8d4568de1b3db8c03eb4a3758b6d))
* revert fallout dependency in pom.xml ([4811a0d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4811a0d90e4ebc53dd54f1eeea8ffd28210b5a5a))
* revert pom.xml jacoco folder ([0efe318](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0efe31897acf531f9c64a4ee7fd428affbaaf7ac))
* Some times plan stucked in Ack state ([eba5764](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/eba57643eae7212c5f3ce4df144bf84f6fc4ba2c))
* Some times plan stucked in Ack state ([8b769ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8b769ee73a447ae5976ccd484be7abf0fa7d3669))
* Tag swagger for sprint1 PI7 and remove uneeded dependencies ([9a69ff2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9a69ff249f92e7af5c8a1c68391acdda3c162a49))
* Tag swagger for sprint1 PI7 and remove uneeded dependencies ([741cbb6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/741cbb608cbefd444c91fedf97f981a85f78a316))
* temp fix see keycloak of integration ([790688f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/790688fa2067eb0e29fe7996fb49efbdb6639d28))
* temp fix see keycloak of integration ([a1cde01](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a1cde01eb9e9297a23467a9c9f3383781e85b2ba))
* use fallout module release version ([2d67f2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2d67f2d13645c5c124e552765321779547088d78))
* use fallout module release version ([391bf40](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/391bf40e03d8133296ad522d75e48fcd478327a6))
* Wrong responses in case of listing Orchestration Plans with invalid value... ([1cddcf1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cddcf167562a569792e1c853c56075acdf9441e))
* Wrong responses in case of listing Orchestration Plans with invalid value... ([1a8683f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1a8683f6a6df60689f9b52a997b66fd0ba293311))


### Features

* add errorMessage dto to orchesterationPlan dto ([1dd7369](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1dd73698e99d183e883292269b5df35871a3d0ef))
* add errorMessage dto to orchesterationPlan dto ([2dedeae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2dedeae2a2e4a27e8ee3d405f14901209fbedbe6))
* Display Contract Name ([d6447d6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d6447d6053dc7d1807d4035a3f3d3a302067aec2))
* Display Contract Name ([d1ae5ca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d1ae5ca89b9eb7e24698282d487553ecda740220))
* Ensure Atomic Boundary for Status Change in Database & Initialisation of the Event ([e1f29ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e1f29ed2a96b4814169dc3fdba280552dc17d025))
* Ensure Atomic Boundary for Status Change in Database & Initialisation of the Event ([c1ac90d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c1ac90d72edd765afc35a0814a5c117ee371f211))
* Implement automatic retry in all event consumers for transient errors ([f0885c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f0885c7c987953d3b8ac24bf84827efe7b93b6de))
* Implement automatic retry in all event consumers for transient errors ([05cce67](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/05cce677e8eba6f6bc1f727271c0197b9dbf6b46))
* **IPCEISCOOD-417:** Add Kafka session to presist all entities at the end of consumers and update order items map key in startDeliverSelectedNodes ([9af91c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9af91c9e07734cdbefc842400eef8265ea152cd9))
* **IPCEISCOOD-417:** Add Kafka session to presist all entities at the end of consumers and update order items map key in startDeliverSelectedNodes ([bf07330](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bf073301085f51994c0e35e6341f97143f48c5bf))
* **IPCEISCOOD-474:** Adapt with Order DTO new changes after change recuring object ([58a0f51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/58a0f511a8e36b75777101cee08bc9b50d74fe61))
* **IPCEISCOOD-474:** Adapt with Order DTO new changes after change recuring object ([efbf963](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/efbf963141e8d34d435c278fb8afb78aad7166fa))
* **IPCEISCOOD-491:** Update cpib spec version ([5f14323](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5f14323656416d3ffed57c4f93064d682b4231cc))
* **IPCEISCOOD-491:** Update cpib spec version ([9f6d608](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9f6d6080d5c1dd1390ad6ee316a63182816d167c))
* Modify "Create Orchestration plan" to be idempotent ([fe26960](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fe26960b5124ef3d3032bb01a1050206243acc8a))
* Modify "Create Orchestration plan" to be idempotent ([73d18b8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/73d18b8691c948cc9ca818490d76cb05c88ed174))
* Update Fallout incident resource ([bc3aeab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bc3aeab53c80a14390a2a9627cfd2fc728922be0))
* Update Fallout incident resource ([970605d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/970605d107945b4e5c5742236081ace9e51c1cf4))


### Reverts

* Revert "chore: export build log to file as gitlab does not display whole logs" ([2da1039](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2da1039365c3a168f35cc09b7fcac8ba013a8c67))

## [1.4.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.1...1.4.2) (2024-06-12)


### Bug Fixes

* change docker image config ([aefbc98](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aefbc984b40e096954643914fdc9b1dd273c4aa0))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.0...1.4.1) (2024-06-07)


### Bug Fixes

* remove duplocate test to fix flaky ([c5dae25](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c5dae251bdef7963d35f817a795791f288a94499))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.3.3...1.4.0) (2024-06-07)


### Bug Fixes

* add deadletter strategy, disbale retries in cood ([89f3512](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/89f3512435295ce6195cb0f248d78812e6680c8d))
* add deadletter strategy, disbale retries in cood ([d1a5ac5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d1a5ac5c6f2d5093f5f9cab8dcf8e016c6e59554))
* add deadletter strategy, disbale retries in cood ([912121e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/912121e6b20f8a6fc6a86dd3389f97c0f3cbb669))
* add deadletter strategy, disbale retries in cood ([1b6d829](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b6d8290a4460578a1df22c9cacaab388a6588b1))
* Corrupted PO event make COOD service down ([6c84f81](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6c84f81591c5faa9791851e4e2bf2cf2f8eb4f9c))
* Corrupted PO event make COOD service down ([c5d02e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c5d02e6f26d19bc9e7738b89195ac255ba4969e2))
* enable retry block (1) and remove try/catch in cpib ([60fbe04](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/60fbe0430a8778bd4862f9d185651784ee6b72f9))
* First request to mock server gives timeout ([b6d0095](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b6d0095930a972bf38966858d53c63d137f2f0c8))
* fix failed test ([783fa39](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/783fa3987283b50bcac6c14bf3e53132a11d2097))
* fix format of getting started md file ([b02c67d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b02c67dceefac3d50faddc3271c4fa5cf6409b46))
* fix processFlowSpecification, add update error with held state ([5472016](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/54720166221ec4511209906403993fb57f8c023c))
* fix sonar issue ([684a636](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/684a6368933edd2721f618b3c01eaf7e0a8f3110))
* fix WebClientRequestException in cpib calls ([67b317f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/67b317fc760d9b736c236931b819b2d071446689))
* formate parameters table in /doc/api-getting-started.md ([4ef3355](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4ef3355e9f354c834f942ce2aabd0d37c4b1ea0a))
* increase delay time in scenarios ([9823d51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9823d51c81fc836ab3275dcfda70885937e4d130))
* **IPCEISCOOD-312, IPCEISCOOD-313:** fix bugs ([36c9f8d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/36c9f8d27171a53ab2c845a544e6bf62d75d35b5))
* **IPCEISCOOD-312, IPCEISCOOD-313:** fix bugs ([8f7262e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8f7262ebe23668b2755fd20bf8d686a0b0003888))
* **IPCEISCOOD-364:** presist message in case of held, fix exception while create fallout ([0309f3f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0309f3fbf7c9917ee6062261e0cef38d31267127))
* **IPCEISCOOD-378:** REST apis, Offset is not as expected, it send wrong value ([d464b1d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d464b1d5f8aff03240ccf04d82f243e99e40810d))
* Purge mechanism - Plans are not getting deleted ([57793c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/57793c35c439e3d31225c30a466d30f5c610bdaf))
* Purge mechanism - Plans are not getting deleted ([e599984](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e59998480ffb462553aa6d0392fc1497966f85fd))
* remove confluence links in api getting started md file ([c43de16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c43de16fee132adf016d4ad50d69a84533409f6a))
* remove notnull on immidiate payment in PO event ([5532a92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5532a92cba4eb8895a543130daf9f077927b676a))
* undo develop port to env port ([c26690c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c26690cd70e52df9a3b74ac5eeb7733998c4f5e9))
* update error message in delivery status consumer if state is held ([4fa1b96](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4fa1b96b41e47fdccd88c4b7f0688c6717d2065b))


### Features

* Change the integration between CPIB & COOD ([bdf8f20](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bdf8f20f0b50cd624a63fe1ce9b67be1327c29d8))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([fc691f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fc691f34d6137adf5309b5434371ca96b7caaa96))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([7ba7592](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7ba7592889508d10e148a88cc1017360bba15fc9))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([10039c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/10039c7833c8cf4ffc938b0a54f7c8a8c907eb96))
* **IPCEISCOOD-38:** add execution plans bdd implementation as integration test - feature ([68b40bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68b40bdf5c3b0a238d9625fa35d9d6dd08a22fc5))
* **IPCEISCOOD-38:** add execution plans bdd implementation as integration test - merge ([fc27b3b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fc27b3b220cfe9c4e6dcc62dff36b10997ffdb6a))
* **IPCEISCOOD-71:** Enhance and add more logs for COOD ([b149991](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1499917c12451649fa31bf923e684a056c53e1e))
* **IPCEISCOOD:** Enable DB transaction ([0116fa6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0116fa6ca76d63f3a879c91cbdff8a60ffdd3d07))
* **IPCEISCOOD:** Enable DB transaction ([4b0a074](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4b0a0744359e70c02840a416f340e51e8cf58415))
* **IPCEISCOOD:** Enable DB transaction ([48df3af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/48df3afa0661cffb6789b5f9799440ab6e18e8a5))
* **IPCEISCOOD:** Enable DB transaction ([71d5b13](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/71d5b13000979e9ee400f3bc861b7d23f57a6490))
* **IPCESCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([57ae59e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/57ae59e70dbada42d2d65d6061cbe4d2ac10eca5))
* **IPCESCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([05802cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/05802ccf9c21e111afec2a28a1b0c37c07ce7227))
* Persist Fallout Entity - To Track Fallout Status ([aea43cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aea43ccaae7d3cbc585d841234df2cb1e7982110))
* Purge Mechanism ([50fa279](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/50fa279d9991ec28e00992204ea2ef47b695e3ca)), closes [#IPCEISCOOD-296](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-296)

## [1.4.10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.9...1.4.10) (2024-08-27)


### Bug Fixes

* fix urls for others service for innovation environment ([ff43627](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ff43627d4b74397f1c8737d3d4bef7701186e5c4))

## [1.4.9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.8...1.4.9) (2024-07-17)


### Reverts

* Revert "fix: replace keycloak url with service name" ([a21f651](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a21f651d43659a9025b8a2b62e3839ab03a94aa6))

## [1.4.8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.7...1.4.8) (2024-07-17)


### Bug Fixes

* replace keycloak url with service name ([2ce2826](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ce282623100f025e15f75262c98aea12d769158))

## [1.4.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.6...1.4.7) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([ac24127](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ac241275721023f0d006b305f2b2b245c8da3d50))

## [1.4.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.5...1.4.6) (2024-07-14)


### Bug Fixes

* remove cert from source code and add it as variable in gitlab ci-cd ([0ecaf38](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0ecaf3871461f8ee3fbc8990905879949a5ea121))
* remove extra dash ([e4dec4c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e4dec4c4fceb112a5c749786f28981be6159f98a))

## [1.4.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.4...1.4.5) (2024-07-14)


### Bug Fixes

* remove issue of uploading jar to artificatory ([ba2271b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ba2271be0fcde9dca09b608da9710ce6bd4786fe))

## [1.4.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.3...1.4.4) (2024-06-26)


### Bug Fixes

* increase memory limit for mongo ([38931e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/38931e4edf65734e9abc813ca54209f59b4d989d))

## [1.4.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.2...1.4.3) (2024-06-12)


### Bug Fixes

* Some times plan stucked in Ack state ([fd9a281](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fd9a281469a343bbb8103bf583188651cd3e3b18))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.10...1.5.0) (2024-09-04)


### Bug Fixes

* change docker image config ([998e699](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/998e69970f6e13094036ab402962502c4e4e797f))
* fix add default coodError for CoodTechnicalException ([5b0ce23](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5b0ce236c5966d56aaf06e8f1bc3e5f755db3c54))
* fix add default coodError for CoodTechnicalException ([ed984bc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ed984bc789801607d8ee44bbeb2fb09bd4b43e66))
* fix add default coodError for CoodTechnicalException ([28d2134](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/28d21344de431d4577f3c9373cb2809a8de5cf52))
* fix add default coodError for CoodTechnicalException ([3df6f86](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3df6f867339508bbbe359d2a3383b8fe77c3942f))
* fix add default coodError for CoodTechnicalException ([6c6edd0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6c6edd03c8b754cdcad15e82cb3949429825e5c1))
* fix add default coodError for CoodTechnicalException ([8052e37](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8052e3755bf33f971b02a87ddf1c0b064c634364))
* fix add default coodError for CoodTechnicalException ([aa9438b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aa9438b7bc13bf78a7977a55080ee6604045f669))
* fix add default coodError for CoodTechnicalException ([b5d92d0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b5d92d0a7e88f7cbc563c955938a485d2e9b143d))
* fix rebase sprint1-pi7 on develop ([00b3e73](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/00b3e73e2efaa2a7a6784b6890ca4780d5ac832a))
* fix rebase sprint1-pi7 on develop ([8b44a80](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8b44a808dd2a78f624b8c30bfd73eee6d14146f0))
* fix recuring object inside order price ([6e6c85d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e6c85dd96c502d2ee7903a24f74dad53d6ea090))
* fix recuring object inside order price ([f693206](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f6932062cb7e8d61edb60f044fb811483d89dec4))
* fix urls for others service for innovation environment ([30e6bfb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/30e6bfba0d5ae24575c280c462f0a870176fd26c))
* Get Orchestration plans with query param fields return bad request ([31ea82e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31ea82e7c3e89e230827e6c8dfe37ed8a06717fa))
* Get Orchestration plans with query param fields return bad request ([8c69aef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c69aef51dd48e077799435704efc5125966658a))
* increase memory limit for mongo ([cd63644](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cd63644ac4d72cee3eaa2b0e545ed16123e671e9))
* **IPCEISCOOD-354:** Wrong response code for Partial resource returned in... ([ff63302](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ff63302d035af92e2f3c6635855a7673b9e982ba))
* **IPCEISCOOD-354:** Wrong response code for Partial resource returned in... ([0e5c702](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0e5c7023813973c8a74b0cf203b01d4e16220317))
* **IPCEISCOOD-361:** Wrong responses in case of listing Orchestration Plans... ([5c21d00](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5c21d007ab53bfb2b5bfa62c686abb9251289bb4))
* **IPCEISCOOD-361:** Wrong responses in case of listing Orchestration Plans... ([31b0418](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31b0418ff2b12b81af20f9d8553cc228282b37c1))
* **IPCEISCOOD-440:** Get Orchestration plans with query param fields return bad request ([8c342fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c342fa37a497228cb38bab046e267dcbc13d82f))
* **IPCEISCOOD-440:** Get Orchestration plans with query param fields return bad request ([f31fb19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f31fb1929790ee8c07c98f6c150bcf7b0e459e62))
* Listing Orchestration Plans with EMPTY attribute Name responds with 500 ([e35b158](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e35b158ea4c33acb418f0b6255c626a232762ecb))
* Listing Orchestration Plans with EMPTY attribute Name responds with 500 ([161b38d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/161b38df7939b3ea7d277a1fa2dd0abbc6c7584a))
* log error, skip mandatory field in order ([976ebee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/976ebeef3819c0ec8be71efe3a8665daea18566b))
* log error, skip mandatory field in order ([11a7c0f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/11a7c0f90c027aa5faee03bd9123a0b13e44d618))
* Merge branch 'release-api-pi7-sp2' into 'develop' ([beea88e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/beea88ec82cadee2c942414ab8e12580f76b1c02))
* merge Tag swagger for sprint1 PI7 and remove uneeded dependencies to develop ([14bd3d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/14bd3d4f8be901b804d5745f094d82387af62d1a))
* move innovation job from staging to production stage ([7baacd3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7baacd3905b3c8c64ceb1897246dcb3d364ab60f))
* Move node to DLT and fire Create fallout in all DLT consumer- fallout instance was not created ([1501045](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1501045028d742f7bae89b239d0974e05f06f784)), closes [#IPCEISCOOD-459](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-459)
* Move node to DLT and fire Create fallout in all DLT consumer- fallout instance was not created ([4f07b16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4f07b16ae9a4e6fa3b1e09fd6a924eb8a21110d7)), closes [#IPCEISCOOD-459](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-459)
* Related product order item quantity issue ([83bd524](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/83bd524c25755818b9901d85aebe2c03ba687ed7))
* Related product order item quantity issue ([b1d4695](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1d46955134952cc3d44c1da2b51af64a3266f00))
* release pi7 sp2 apis ([8bd2d8b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8bd2d8be57149b67b5366d5b3be9c69d43843741))
* release pi7 sp2 apis ([903546e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/903546e1fdd3dbb6fcda6d7ad06f0ab0abca262a))
* release spec module version 0.9.0_release ([49eb9cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/49eb9ccf1588c8ec83e1b83e9391e44e4e50e944))
* release spec module version 0.9.0_release ([d3f1e93](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d3f1e93158662acddbfc7ebd88356ee70f9d5117))
* remove cert from source code and add it as variable in gitlab ci-cd ([4736999](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/473699950b33aaba0e5d43ab1b0034532b78b079))
* remove extra dash ([651dc76](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/651dc76238d425f8008a8f9a47e89300fd3be664))
* remove issue of uploading jar to artificatory ([4fbfa10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4fbfa100905b5f1a956723dc78c30625bf7dcb61))
* revert back the removal of dependencies ([e3180f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e3180f40939a5ff90497bf329afddc7612a0a952))
* revert back the removal of dependencies ([145ecc5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/145ecc5c517430215f0892634c365d1e37c935d9))
* revert fallout dependency in pom.xml ([8e4555f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8e4555f784be8d4568de1b3db8c03eb4a3758b6d))
* revert fallout dependency in pom.xml ([4811a0d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4811a0d90e4ebc53dd54f1eeea8ffd28210b5a5a))
* revert pom.xml jacoco folder ([0efe318](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0efe31897acf531f9c64a4ee7fd428affbaaf7ac))
* Some times plan stucked in Ack state ([eba5764](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/eba57643eae7212c5f3ce4df144bf84f6fc4ba2c))
* Some times plan stucked in Ack state ([8b769ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8b769ee73a447ae5976ccd484be7abf0fa7d3669))
* Tag swagger for sprint1 PI7 and remove uneeded dependencies ([9a69ff2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9a69ff249f92e7af5c8a1c68391acdda3c162a49))
* Tag swagger for sprint1 PI7 and remove uneeded dependencies ([741cbb6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/741cbb608cbefd444c91fedf97f981a85f78a316))
* temp fix see keycloak of integration ([790688f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/790688fa2067eb0e29fe7996fb49efbdb6639d28))
* temp fix see keycloak of integration ([a1cde01](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a1cde01eb9e9297a23467a9c9f3383781e85b2ba))
* use fallout module release version ([2d67f2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2d67f2d13645c5c124e552765321779547088d78))
* use fallout module release version ([391bf40](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/391bf40e03d8133296ad522d75e48fcd478327a6))
* Wrong responses in case of listing Orchestration Plans with invalid value... ([1cddcf1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cddcf167562a569792e1c853c56075acdf9441e))
* Wrong responses in case of listing Orchestration Plans with invalid value... ([1a8683f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1a8683f6a6df60689f9b52a997b66fd0ba293311))


### Features

* add errorMessage dto to orchesterationPlan dto ([1dd7369](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1dd73698e99d183e883292269b5df35871a3d0ef))
* add errorMessage dto to orchesterationPlan dto ([2dedeae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2dedeae2a2e4a27e8ee3d405f14901209fbedbe6))
* Display Contract Name ([d6447d6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d6447d6053dc7d1807d4035a3f3d3a302067aec2))
* Display Contract Name ([d1ae5ca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d1ae5ca89b9eb7e24698282d487553ecda740220))
* Ensure Atomic Boundary for Status Change in Database & Initialisation of the Event ([e1f29ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e1f29ed2a96b4814169dc3fdba280552dc17d025))
* Ensure Atomic Boundary for Status Change in Database & Initialisation of the Event ([c1ac90d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c1ac90d72edd765afc35a0814a5c117ee371f211))
* Implement automatic retry in all event consumers for transient errors ([f0885c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f0885c7c987953d3b8ac24bf84827efe7b93b6de))
* Implement automatic retry in all event consumers for transient errors ([05cce67](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/05cce677e8eba6f6bc1f727271c0197b9dbf6b46))
* **IPCEISCOOD-417:** Add Kafka session to presist all entities at the end of consumers and update order items map key in startDeliverSelectedNodes ([9af91c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9af91c9e07734cdbefc842400eef8265ea152cd9))
* **IPCEISCOOD-417:** Add Kafka session to presist all entities at the end of consumers and update order items map key in startDeliverSelectedNodes ([bf07330](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bf073301085f51994c0e35e6341f97143f48c5bf))
* **IPCEISCOOD-474:** Adapt with Order DTO new changes after change recuring object ([58a0f51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/58a0f511a8e36b75777101cee08bc9b50d74fe61))
* **IPCEISCOOD-474:** Adapt with Order DTO new changes after change recuring object ([efbf963](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/efbf963141e8d34d435c278fb8afb78aad7166fa))
* **IPCEISCOOD-491:** Update cpib spec version ([5f14323](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5f14323656416d3ffed57c4f93064d682b4231cc))
* **IPCEISCOOD-491:** Update cpib spec version ([9f6d608](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9f6d6080d5c1dd1390ad6ee316a63182816d167c))
* Modify "Create Orchestration plan" to be idempotent ([fe26960](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fe26960b5124ef3d3032bb01a1050206243acc8a))
* Modify "Create Orchestration plan" to be idempotent ([73d18b8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/73d18b8691c948cc9ca818490d76cb05c88ed174))
* Update Fallout incident resource ([bc3aeab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bc3aeab53c80a14390a2a9627cfd2fc728922be0))
* Update Fallout incident resource ([970605d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/970605d107945b4e5c5742236081ace9e51c1cf4))


### Reverts

* Revert "chore: export build log to file as gitlab does not display whole logs" ([2da1039](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2da1039365c3a168f35cc09b7fcac8ba013a8c67))

## [1.4.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.1...1.4.2) (2024-06-12)


### Bug Fixes

* change docker image config ([aefbc98](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aefbc984b40e096954643914fdc9b1dd273c4aa0))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.0...1.4.1) (2024-06-07)


### Bug Fixes

* remove duplocate test to fix flaky ([c5dae25](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c5dae251bdef7963d35f817a795791f288a94499))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.3.3...1.4.0) (2024-06-07)


### Bug Fixes

* add deadletter strategy, disbale retries in cood ([89f3512](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/89f3512435295ce6195cb0f248d78812e6680c8d))
* add deadletter strategy, disbale retries in cood ([d1a5ac5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d1a5ac5c6f2d5093f5f9cab8dcf8e016c6e59554))
* add deadletter strategy, disbale retries in cood ([912121e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/912121e6b20f8a6fc6a86dd3389f97c0f3cbb669))
* add deadletter strategy, disbale retries in cood ([1b6d829](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b6d8290a4460578a1df22c9cacaab388a6588b1))
* Corrupted PO event make COOD service down ([6c84f81](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6c84f81591c5faa9791851e4e2bf2cf2f8eb4f9c))
* Corrupted PO event make COOD service down ([c5d02e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c5d02e6f26d19bc9e7738b89195ac255ba4969e2))
* enable retry block (1) and remove try/catch in cpib ([60fbe04](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/60fbe0430a8778bd4862f9d185651784ee6b72f9))
* First request to mock server gives timeout ([b6d0095](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b6d0095930a972bf38966858d53c63d137f2f0c8))
* fix failed test ([783fa39](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/783fa3987283b50bcac6c14bf3e53132a11d2097))
* fix format of getting started md file ([b02c67d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b02c67dceefac3d50faddc3271c4fa5cf6409b46))
* fix processFlowSpecification, add update error with held state ([5472016](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/54720166221ec4511209906403993fb57f8c023c))
* fix sonar issue ([684a636](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/684a6368933edd2721f618b3c01eaf7e0a8f3110))
* fix WebClientRequestException in cpib calls ([67b317f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/67b317fc760d9b736c236931b819b2d071446689))
* formate parameters table in /doc/api-getting-started.md ([4ef3355](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4ef3355e9f354c834f942ce2aabd0d37c4b1ea0a))
* increase delay time in scenarios ([9823d51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9823d51c81fc836ab3275dcfda70885937e4d130))
* **IPCEISCOOD-312, IPCEISCOOD-313:** fix bugs ([36c9f8d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/36c9f8d27171a53ab2c845a544e6bf62d75d35b5))
* **IPCEISCOOD-312, IPCEISCOOD-313:** fix bugs ([8f7262e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8f7262ebe23668b2755fd20bf8d686a0b0003888))
* **IPCEISCOOD-364:** presist message in case of held, fix exception while create fallout ([0309f3f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0309f3fbf7c9917ee6062261e0cef38d31267127))
* **IPCEISCOOD-378:** REST apis, Offset is not as expected, it send wrong value ([d464b1d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d464b1d5f8aff03240ccf04d82f243e99e40810d))
* Purge mechanism - Plans are not getting deleted ([57793c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/57793c35c439e3d31225c30a466d30f5c610bdaf))
* Purge mechanism - Plans are not getting deleted ([e599984](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e59998480ffb462553aa6d0392fc1497966f85fd))
* remove confluence links in api getting started md file ([c43de16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c43de16fee132adf016d4ad50d69a84533409f6a))
* remove notnull on immidiate payment in PO event ([5532a92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5532a92cba4eb8895a543130daf9f077927b676a))
* undo develop port to env port ([c26690c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c26690cd70e52df9a3b74ac5eeb7733998c4f5e9))
* update error message in delivery status consumer if state is held ([4fa1b96](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4fa1b96b41e47fdccd88c4b7f0688c6717d2065b))


### Features

* Change the integration between CPIB & COOD ([bdf8f20](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bdf8f20f0b50cd624a63fe1ce9b67be1327c29d8))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([fc691f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fc691f34d6137adf5309b5434371ca96b7caaa96))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([7ba7592](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7ba7592889508d10e148a88cc1017360bba15fc9))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([10039c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/10039c7833c8cf4ffc938b0a54f7c8a8c907eb96))
* **IPCEISCOOD-38:** add execution plans bdd implementation as integration test - feature ([68b40bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68b40bdf5c3b0a238d9625fa35d9d6dd08a22fc5))
* **IPCEISCOOD-38:** add execution plans bdd implementation as integration test - merge ([fc27b3b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fc27b3b220cfe9c4e6dcc62dff36b10997ffdb6a))
* **IPCEISCOOD-71:** Enhance and add more logs for COOD ([b149991](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1499917c12451649fa31bf923e684a056c53e1e))
* **IPCEISCOOD:** Enable DB transaction ([0116fa6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0116fa6ca76d63f3a879c91cbdff8a60ffdd3d07))
* **IPCEISCOOD:** Enable DB transaction ([4b0a074](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4b0a0744359e70c02840a416f340e51e8cf58415))
* **IPCEISCOOD:** Enable DB transaction ([48df3af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/48df3afa0661cffb6789b5f9799440ab6e18e8a5))
* **IPCEISCOOD:** Enable DB transaction ([71d5b13](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/71d5b13000979e9ee400f3bc861b7d23f57a6490))
* **IPCESCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([57ae59e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/57ae59e70dbada42d2d65d6061cbe4d2ac10eca5))
* **IPCESCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([05802cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/05802ccf9c21e111afec2a28a1b0c37c07ce7227))
* Persist Fallout Entity - To Track Fallout Status ([aea43cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aea43ccaae7d3cbc585d841234df2cb1e7982110))
* Purge Mechanism ([50fa279](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/50fa279d9991ec28e00992204ea2ef47b695e3ca)), closes [#IPCEISCOOD-296](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-296)

## [1.4.10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.9...1.4.10) (2024-08-27)


### Bug Fixes

* fix urls for others service for innovation environment ([ff43627](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ff43627d4b74397f1c8737d3d4bef7701186e5c4))

## [1.4.9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.8...1.4.9) (2024-07-17)


### Reverts

* Revert "fix: replace keycloak url with service name" ([a21f651](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a21f651d43659a9025b8a2b62e3839ab03a94aa6))

## [1.4.8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.7...1.4.8) (2024-07-17)


### Bug Fixes

* replace keycloak url with service name ([2ce2826](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ce282623100f025e15f75262c98aea12d769158))

## [1.4.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.6...1.4.7) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([ac24127](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ac241275721023f0d006b305f2b2b245c8da3d50))

## [1.4.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.5...1.4.6) (2024-07-14)


### Bug Fixes

* remove cert from source code and add it as variable in gitlab ci-cd ([0ecaf38](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0ecaf3871461f8ee3fbc8990905879949a5ea121))
* remove extra dash ([e4dec4c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e4dec4c4fceb112a5c749786f28981be6159f98a))

## [1.4.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.4...1.4.5) (2024-07-14)


### Bug Fixes

* remove issue of uploading jar to artificatory ([ba2271b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ba2271be0fcde9dca09b608da9710ce6bd4786fe))

## [1.4.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.3...1.4.4) (2024-06-26)


### Bug Fixes

* increase memory limit for mongo ([38931e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/38931e4edf65734e9abc813ca54209f59b4d989d))

## [1.4.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.2...1.4.3) (2024-06-12)


### Bug Fixes

* Some times plan stucked in Ack state ([fd9a281](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fd9a281469a343bbb8103bf583188651cd3e3b18))

## [1.4.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.1...1.4.2) (2024-06-12)


### Bug Fixes

* change docker image config ([aefbc98](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aefbc984b40e096954643914fdc9b1dd273c4aa0))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.4.0...1.4.1) (2024-06-07)


### Bug Fixes

* remove duplocate test to fix flaky ([c5dae25](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c5dae251bdef7963d35f817a795791f288a94499))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.3.3...1.4.0) (2024-06-07)


### Bug Fixes

* add deadletter strategy, disbale retries in cood ([89f3512](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/89f3512435295ce6195cb0f248d78812e6680c8d))
* add deadletter strategy, disbale retries in cood ([d1a5ac5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d1a5ac5c6f2d5093f5f9cab8dcf8e016c6e59554))
* add deadletter strategy, disbale retries in cood ([912121e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/912121e6b20f8a6fc6a86dd3389f97c0f3cbb669))
* add deadletter strategy, disbale retries in cood ([1b6d829](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b6d8290a4460578a1df22c9cacaab388a6588b1))
* Corrupted PO event make COOD service down ([6c84f81](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6c84f81591c5faa9791851e4e2bf2cf2f8eb4f9c))
* Corrupted PO event make COOD service down ([c5d02e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c5d02e6f26d19bc9e7738b89195ac255ba4969e2))
* enable retry block (1) and remove try/catch in cpib ([60fbe04](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/60fbe0430a8778bd4862f9d185651784ee6b72f9))
* First request to mock server gives timeout ([b6d0095](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b6d0095930a972bf38966858d53c63d137f2f0c8))
* fix failed test ([783fa39](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/783fa3987283b50bcac6c14bf3e53132a11d2097))
* fix format of getting started md file ([b02c67d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b02c67dceefac3d50faddc3271c4fa5cf6409b46))
* fix processFlowSpecification, add update error with held state ([5472016](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/54720166221ec4511209906403993fb57f8c023c))
* fix sonar issue ([684a636](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/684a6368933edd2721f618b3c01eaf7e0a8f3110))
* fix WebClientRequestException in cpib calls ([67b317f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/67b317fc760d9b736c236931b819b2d071446689))
* formate parameters table in /doc/api-getting-started.md ([4ef3355](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4ef3355e9f354c834f942ce2aabd0d37c4b1ea0a))
* increase delay time in scenarios ([9823d51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9823d51c81fc836ab3275dcfda70885937e4d130))
* **IPCEISCOOD-312, IPCEISCOOD-313:** fix bugs ([36c9f8d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/36c9f8d27171a53ab2c845a544e6bf62d75d35b5))
* **IPCEISCOOD-312, IPCEISCOOD-313:** fix bugs ([8f7262e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8f7262ebe23668b2755fd20bf8d686a0b0003888))
* **IPCEISCOOD-364:** presist message in case of held, fix exception while create fallout ([0309f3f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0309f3fbf7c9917ee6062261e0cef38d31267127))
* **IPCEISCOOD-378:** REST apis, Offset is not as expected, it send wrong value ([d464b1d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d464b1d5f8aff03240ccf04d82f243e99e40810d))
* Purge mechanism - Plans are not getting deleted ([57793c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/57793c35c439e3d31225c30a466d30f5c610bdaf))
* Purge mechanism - Plans are not getting deleted ([e599984](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e59998480ffb462553aa6d0392fc1497966f85fd))
* remove confluence links in api getting started md file ([c43de16](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c43de16fee132adf016d4ad50d69a84533409f6a))
* remove notnull on immidiate payment in PO event ([5532a92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5532a92cba4eb8895a543130daf9f077927b676a))
* undo develop port to env port ([c26690c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c26690cd70e52df9a3b74ac5eeb7733998c4f5e9))
* update error message in delivery status consumer if state is held ([4fa1b96](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4fa1b96b41e47fdccd88c4b7f0688c6717d2065b))


### Features

* Change the integration between CPIB & COOD ([bdf8f20](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bdf8f20f0b50cd624a63fe1ce9b67be1327c29d8))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([fc691f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fc691f34d6137adf5309b5434371ca96b7caaa96))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([7ba7592](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7ba7592889508d10e148a88cc1017360bba15fc9))
* **IPCEISCOOD-297:** Synchronize Model with DTO for GET plan API ([10039c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/10039c7833c8cf4ffc938b0a54f7c8a8c907eb96))
* **IPCEISCOOD-38:** add execution plans bdd implementation as integration test - feature ([68b40bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/68b40bdf5c3b0a238d9625fa35d9d6dd08a22fc5))
* **IPCEISCOOD-38:** add execution plans bdd implementation as integration test - merge ([fc27b3b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/fc27b3b220cfe9c4e6dcc62dff36b10997ffdb6a))
* **IPCEISCOOD-71:** Enhance and add more logs for COOD ([b149991](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b1499917c12451649fa31bf923e684a056c53e1e))
* **IPCEISCOOD:** Enable DB transaction ([0116fa6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0116fa6ca76d63f3a879c91cbdff8a60ffdd3d07))
* **IPCEISCOOD:** Enable DB transaction ([4b0a074](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4b0a0744359e70c02840a416f340e51e8cf58415))
* **IPCEISCOOD:** Enable DB transaction ([48df3af](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/48df3afa0661cffb6789b5f9799440ab6e18e8a5))
* **IPCEISCOOD:** Enable DB transaction ([71d5b13](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/71d5b13000979e9ee400f3bc861b7d23f57a6490))
* **IPCESCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([57ae59e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/57ae59e70dbada42d2d65d6061cbe4d2ac10eca5))
* **IPCESCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([05802cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/05802ccf9c21e111afec2a28a1b0c37c07ce7227))
* Persist Fallout Entity - To Track Fallout Status ([aea43cc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aea43ccaae7d3cbc585d841234df2cb1e7982110))
* Purge Mechanism ([50fa279](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/50fa279d9991ec28e00992204ea2ef47b695e3ca)), closes [#IPCEISCOOD-296](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/issues/IPCEISCOOD-296)

## [1.3.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.3.2...1.3.3) (2024-04-29)


### Bug Fixes

* add pull secret to can pull image from openshift repo ([aec89db](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/aec89db60cd76e1f6972c3e7d075e2b1d3b1c711))
* fix app_name use innovation instead of innov, same value like ([c53192d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c53192d0f8503db46cc60ff010d853895e6e8c1d))
* removed semantic image and move it to gitlab variables ([823a917](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/823a917de27b5de11c5630b26c3fd57322790724))

## [1.3.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.3.1...1.3.2) (2024-04-18)


### Bug Fixes

* the sed expression used to change the version and appVersion in ([6885145](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6885145fe6d962ff002100c79233cc0acccf0b0d))

## [1.3.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.3.0...1.3.1) (2024-04-18)


### Bug Fixes

* blocking exception in consumer ([58af853](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/58af8539151e141aa19aa8e42acf3fcadf11a61e))
* blocking exception in consumer (Cont.) ([c68a0e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c68a0e0a4bc6443f02f805096e4433e0f04cb10c))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.2.1...1.3.0) (2024-04-07)


### Bug Fixes

* add docs ([914c89d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/914c89df1e1b5aa6f3e69cf2f7b060ef9ebca617))
* add docs ([ef5876c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ef5876c5f5abf4dceb7c35eb4cc775fc782b8832))
* add SNAPSHOT suffix for spec ([16c1b5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/16c1b5ca28e3d68b9d4a1e0123255de8a6dea78f))
* add SNAPSHOT suffix for spec ([5cdb1e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5cdb1e35dd2a11f1f0bc94b758d373859277653d))
* add typer for kafka consumer, skip header check ([6b68e64](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6b68e64393d801244dd86c48b13547e03b56585c))
* add typer for kafka consumer, skip header check ([f5acde0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f5acde086f5b5cb237b701270f6bd323325c5793))
* change som event name ([1f8d853](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1f8d853b245f6836159d8db39671f08ad7d00fa7))
* change som event name ([ad8b29a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ad8b29ac4fa0abb4d9a70a15d05db80e35428e3c))
* chnage type of timestamp in error message to be Instant ([18f4556](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/18f45568b76e127a5dce31d16183e4b88b3c0e5a))
* **devops:** Remove `develop` branch from `.releaserc` ([570b551](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/570b551e4d7d75cff77f4e5fa13b9ffb4ef23825))
* empty variable expansion issue ([45dd2ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/45dd2ab52180f5fd32c146c890cf3501b687c4bb))
* enable fluentd and otel on prod ([5dec8f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5dec8f37e3e87bb178cbbeef63c979cacc7dccaf))
* fix serialize and deserialize issue for any dto use write as timestamp=false ([bb8ff42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb8ff4240ac93d0a180faadd8799a34bde092d26))
* fix serialize and deserialize issue for any dto use write as timestamp=false ([a9dff89](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a9dff891a500598b4f33e1437c50c7b85804b9e3))
* fix spotbugs issue ([4d1b6ef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4d1b6efb61b874a2b74211d2609129219732efbf))
* fix spotbugs issue ([8f32423](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8f3242318c29b5a62139f4ba21d8d719ee6e6471))
* fix staging catalog service port ([bb8a4ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb8a4ad60fa1fcf9e1eb493360b8ad220d4e1c65))
* fix tests 227 ([3120501](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/31205016abe257866753abe65836e591e001e423))
* fix tests 227 ([e6372b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e6372b5f4545489938fbbb155f61eeed0fcf1337))
* fix tests 227 ([c0f20a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c0f20a2739ec86d2f3fb8351ed4351b380895090))
* fix tests sprint branch ([91e05b4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/91e05b45ca42ba756d7563c14f9220f32897442a))
* fix typo ([c0b896f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c0b896fb13ba2c97a34bdcdea5de3ee11e702621))
* fix typo ([5d0e8e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5d0e8e07099d8893200d543528ace9c22ad82e19))
* merge master to develop, and enable fluentd ([9d75da3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9d75da3de5a406718c4556662ef9fd9a3a022cb0))
* nullPointerException in method getItemIdWithActionOrchestrationPlanNodeMap ([573e132](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/573e132693519a9a35c1637bd066d47b3acd4cba))
* remove deployAtEnd as a workaround for deploy silent failure ([c07632a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c07632a7a2721c9e3842492a9b4f8166934bcccc))
* remove deployAtEnd as a workaround for deploy silent failure ([a167e01](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a167e018e25eca666c8900b610e4d3316f965e71))
* revert configs in gitlab ci ([ca13511](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ca13511e8a84a754e178288c81069cb1c969bf5f))
* revert configs in gitlab ci related to sonar ([2e46cfb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2e46cfbd09caa2b04d281150f952456bc6861ec5))
* send tangible nodes to delivery management service instead of deliver in orchesteration service ([0197600](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/01976003897da244573318d85b4019f16bb7da1d))


### Features

* **IPCEISCOOD-199:** Move Delivery business to DM service ([224b4ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/224b4ad9acc04f6be1d78874480a727e4130cf92))
* **IPCEISCOOD-199:** Move Delivery business to DM service ([e3a3b56](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e3a3b5653b996279065a5d131529f627e674f98d))
* **IPCEISCOOD-223/224:** fallout in execution/delivery module ([d088a2c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d088a2ceb896de45f70c8d3f17b9dcf9eaf58c34))
* **IPCEISCOOD-223/224:** fallout in execution/delivery module ([ad7faf8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/ad7faf8463039fabeec572aabf6be1494c02563d))
* **IPCEISCOOD-223/224:** fallout in execution/delivery module ([27005ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/27005ece27e732d3ee65b1b871910d2385d6959e))
* **IPCEISCOOD-223/224:** fallout in execution/delivery module ([f284c60](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f284c60e9b3b71fa585d4065ef22003df00a0f3f))
* **IPCEISCOOD-223/224:** fallout in execution/delivery module ([959a655](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/959a655f4b55267bb32e388f69dd875e20ee5685))
* **IPCEISCOOD-241:** Add exceptions with handler and sample use for each one in the code ([20858ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/20858ee976f9f88713199e074fee30ea258c34ea))
* **IPCEISCOOD-241:** Add exceptions with handler and sample use for each one in the code ([2f7ffb7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2f7ffb7968cdc6d21a63fb12971636abe1776dca))
* **IPCEISCOOD-241:** Add exceptions with handler and sample use for each one in the code ([2ac8949](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ac89499a9343386c99a830281e7e7af6344352b))
* **IPCEISCOOD-241:** Add exceptions with handler and sample use for each one in the code ([64dd4c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/64dd4c90e15c5d635639ed942ec4917c32a072bc))
* **IPCEISCOOD-241:** Add exceptions with handler and sample use for each one in the code ([e249821](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e24982157a7e70ced2090624b6306052fdd680cf))
* **IPCEISCOOD-252:** Verify Product State in CPIB Depending on isInstallable Flag ([e02b69c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e02b69c1454ecb86181ae6c3a2b0ea59a81e9caf))
* updating CPIB spec dependency version ([f4cca0a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f4cca0a92ee115a6e2bc266d5ffe46eec33d043a))
* updating CPIB spec dependency version ([7319765](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/731976550e41f0f6d3fd25530b6859652bec43c9))

## [1.2.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.2.0...1.2.1) (2024-2-21)


### Bug Fixes

* fix keycloak uri value in prod env ([95a16b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/95a16b61677726580248d93c11b0eb4b63ef8aa4))
* fix keycloak uri value in prod env ([bd41779](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bd4177924cac9bdc66b92c569d5b572416523c36))
* fix keycloak uri value in prod env ([f3c4d1c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f3c4d1ce24ff81847cb443399aecb05569573c08))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.1.2...1.2.0) (2024-2-20)


### Features

* add production env config ([27fc1bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/27fc1bd6879542e4b475cb72161e398f17e85fd8))

# 1.0.0 (2024-1-23)


### Bug Fixes

* add ring chararatistics ([972df70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/972df7040775ea052fb8d26540b6be44f5515a67))
* adjust review props in helm and app.yaml ([018d2cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/018d2cdea67700398fb1cc2bd7d57fbf0a613f38))
* change in som mock job, update postman ([0d8e77d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0d8e77d83abd7637a4d95af2389d0a267ea3be03))
* change mock server review to mock server integration in review profile ([3db8396](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3db8396ad3ad7cb8fc4b728b14acba2b4c21596c))
* change product catalog url ([1ecc3f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1ecc3f1c6da91ad0bca8f7709a3748f241507298))
* **COOD-sprint7:** updated the servers url, and allow for null realizing service comming from product DTO ([06cc35f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/06cc35fc9e4e6b2207a18d2c0075208f0adfe376))
* fix kafka tracing config ([121787a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/121787ae1f343d4a76b451effbe922d91a20b7aa))
* fix PI4 integration issues & add integration tests ([75aecf0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/75aecf07d37c17c5180b24bed5016f75231411e0))
* fix restructure packages with merge with sprint8 ([b484502](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b484502117ea976a86ecfa1bb372c1459580f6bf))
* fix sonar major issues ([1cd18b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cd18b9880c07cde99b2c2bb7a5d58b02f8b0335))
* fix sonar major issues ([d2f0f19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d2f0f190fb023ada7782dd1657fb9de162693359))
* fix sonar major issues ([d9ba4aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d9ba4aadba3abcc6d2d149bc58128ee7c8ac9c48))
* fix sonar major issues ([44fbc22](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/44fbc223771a31338daa209ecb831510be21c6a2))
* fix sonar major issues ([241f378](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/241f3787214a7fa84e2aecb8f994d39ded030916))
* fix update product status ([11df9ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/11df9ec1591e947682c8c3a37fc552d9a5ae979b))
* fix update product status ([2288939](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2288939a48e830e2772a893fbbdca76c859b34d9))
* **IPCEISCOOD-123:** fix failing tests ([76e7f2e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/76e7f2e16580c4b9fa307410923f2a61a98e3bb9))
* **IPCEISCOOD-123:** fix filtering with received date ([7876fee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7876fee4ce33344f5c77d6b60fa04b6998d5c632))
* **IPCEISCOOD-137:** Technical task | implement Plan API controller ([7a30344](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7a30344923e8c36f2171ee0d8211e3cc1f05dbdd))
* **IPCEISCOOD-170:** adding missing async-api ([1f682b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1f682b9efd522fdc2301c8d7577d8b818274338a))
* **IPCEISCOOD-170:** handle when productorder.product exists in product order. ([302d23f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/302d23f0ad3708e2192cac124ebd4fa8b29ff898))
* **IPCEISCOOD-194:** Merge branch 'IPCEISCOOD-194' into 'COOD_Sprint_12' See merge request disco/disco-oda-components/disco-order-orchestration/orchestration-delivery!153 ([58c1384](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/58c138425241c157f8a5033c116e6da05c96eaae))
* **IPCEISCOOD-36:** applying review comments ([e9df5db](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e9df5db9195b661ed3edbc9409fc2365c2c0d507))
* **IPCEISCOOD-36:** applying review comments ([6007a42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6007a4268de8b139767934f2545437ead168b3b6))
* **IPCEISCOOD-36:** fix keycloak-client-secret.yaml file ([2915d77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2915d778fb6537b3d368e64f05900fe4f14b94ce))
* **IPCEISCOOD-36:** fix public endpoints matchers ([f875cb7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f875cb76e1a4f50be25fa929ffec6addf005649a))
* **IPCEISCOOD-36:** fix webclient resolver issue ([0a9db89](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0a9db89224c11950eac245c557b18a917d84958b))
* **IPCEISCOOD-36:** modify user on dockerfile ([4a43900](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4a43900249759156ec0d497bb4a49a29a915872e))
* **IPCEISCOOD-36:** moving certificates file to the root directory ([d3d01fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d3d01fa5155898798486daf165ddfe86cde83b8a))
* **IPCEISCOOD-36:** removing useless config ([06e639b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/06e639b9cc2ed0fd08cf5e233282de6f758414c2))
* **IPCEISCOOD-50:** fix constructor injection issue ([d26dce1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d26dce19414eb680050bea29a6c9a4cc4abc3a2d))
* **IPCEISCOOD-50:** fix verifyDependentForDeleteAction - skip verifying  if action not equal delete ([7bfc5d6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7bfc5d647618f837f787e319c3a7dd4c5656b64b))
* **IPCEISCOOD-50:** fixing AddOrchestrationPlanTests after merge - update kafka test container version to 7.5.1 ([c238181](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c238181dc080d0d5bc5d9ed83d3275eef88fdcf0))
* **IPCEISCOOD-54:** Pagination header returned is incorrect ([4736326](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/473632608be1e95977e87c170102fedc31aa457c))
* **IPCEISCOOD-68:** fix docker-pull rate limit using the dockerproxy orange repo and refactor code to use @Value for the topic names and added wiremock reset ([8c9ed10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c9ed108d142b4522eb6fcb7e08b565f7b0292fb))
* **IPCEISCOOD-68:** fix docker-pull rate limit using the dockerproxy orange repo and refactor code to use @Value for the topic names and added wiremock reset ([1b0635c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b0635c4cf02e1664de902a7c78f60d42b6b2586))
* listing issues ([5fff6ba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5fff6baf859da54b009227df80a83e4810d46b1c))
* Merge branch 'PCEISCOOD-147' into 'COOD_Sprint_12' See merge request disco/disco-oda-components/disco-order-orchestration/orchestration-delivery!148 ([1b115a3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b115a310b44278ffae081b2b32bbdecd9c941e7))
* merge master ([cc25ebe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cc25ebe91343f71aa492da2f8594f98a236704ed))
* merge orch yaml file domain names ([b0012bc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b0012bc1b4d758139ba7fd470463ba5e852c8085))
* merge PI4 integration issues to develop ([4344b53](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4344b53f0f2f525ace1aa80041c9e6d09d05d92e))
* merge review config ([d4cf9c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d4cf9c333486c26ed0c8f81813d75306eaea2161))
* **PI4 integration fixes:** add workaround to allow localdatetime in product order ([853ef17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/853ef177701622e9083932f9fd50b3f0b162bffa))
* **PI4 integration fixes:** add workaround to allow localdatetime in product order merge to develop ([1245713](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1245713ba155bc3de232121ca4c3ff2f33ca6f05))
* publish event after patch request ([bf3580a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bf3580ad45b089adc68a4637766f8a6030455a00))
* pushing to trigger pipeline ([2ebafea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ebafead6275ca2fce0d246053ea02c0f67de689))
* remove reformat commit and fix sonar ([142a663](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/142a663ee140f3a7fbdb82b62e601e5a6f5f22d1))
* rename handler name, add todos and update the deliver method to remove unneeded published event ([d23a292](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d23a292d6bd3fa69cfc8489a2113c671ab221028))
* rename SOM serviceOrderStateChange event ([9887db1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9887db1faa1cbef42d5a6d7f09dce64e3ebe5f95))
* restructure packages ([a112499](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a112499c5706ee2572ab42621b12d214f17ba246))
* restructure packages ([cfd9145](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cfd91457fbce498c84ea3c223c9b611ee4da5fd5))
* rollback review changes ([8c2683c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c2683cf068c524a10c67537687888ae5ac1542c))
* skip from coverage event and mocks packages ([8ab8acb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8ab8acba458445dd64f9f94931bcea00dce8cc99))
* update collection ([bb1c5d1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb1c5d175d6d2565670acc555a498e8b6427917c))
* use latest swagger yaml in api designer, fix spring doc to get the doc from the yaml ([f12bf43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f12bf4369d6a9180c866dd0707ef0a7773dbd122))
* use latest swagger yaml in api designer, fix spring doc to get the doc from the yaml ([a4a20f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a4a20f89e47d52831e6b74e6db0595c9a20e8096))
* use mock-server ([8288595](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/828859556f5d01f401e86bc2c276370a7af70dd3))
* use mock-server in integration ([687c4d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/687c4d4e17f0c12ee82266ab6c878b81a8f9325b))
* use mock-server-integration ([73d3476](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/73d34767f7454c7f10fab401f37771255e2c3b92))
* utilize deliver method to accept list of nodes ([9becb5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9becb5b9554db437bb1cbb9d8756791a0a8aae37))


### Features

* add initial compliance report ([a5714c0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a5714c0ce0e0faa2ad90f4da33fdd2b095d5cc40))
* add review comments with story 60 investigation ([e815b77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e815b77d58fd48d7770feb6269c3451e6718fa5a))
* add spec empty package ([1ec943a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1ec943aba643e380345ccfec9d115ee0cf2397a6))
* add todo ([2033f5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2033f5b0b8e7df385693921183ad1824fe876c9d))
* add todo in delivery after dms ([9151da0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9151da0710120c5eb332a906f5d89d6f0181aa5a))
* add todos ([be579f6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/be579f63e5b649d98657bef36cc820c8ec64a2a1))
* fix checkstyle ([a6b21e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a6b21e3c38ab66e369ca1e00723fd6dee1f2e344))
* fix pmd and spotbugs ([718205d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/718205de4d7ff7decbbad21127e5dd53189cd368))
* fix sonar issues ([6e7d66d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e7d66d03cbb6ff00171c42e25ec373e25857cbc))
* **IPCEISCOOD-111:** add productCharacteristic in patch request body ([c76f0de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c76f0de6c38032ec67f2a90956172f099b6beca0))
* **IPCEISCOOD-111:** added test models helper builders ([a763af0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a763af0ebae99909fcda54eb55d00fab7619980f))
* **IPCEISCOOD-111:** apply review comment ([48cede6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/48cede69f9f335403d9e22b718758c4836c4103c))
* **IPCEISCOOD-111:** apply review comment ([b6a162b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b6a162b8abcae206bbfe74574cca21b94d606c1a))
* **IPCEISCOOD-111:** fix test after applying review comment ([5a61fd8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5a61fd85b4a1db464a0c07ea966474f4dd7c5f79))
* **IPCEISCOOD-111:** undo last review comment ([960c761](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/960c761b122a0e58a78057425cd44a820f73d39b))
* **IPCEISCOOD-111:** use map instead of forEach ([1cf86de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cf86dec45bd72b818be64cb950ff96561ae6ebc))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([2c9a07d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2c9a07d576fd53c853195266f1e116c085f62670))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([6929b71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6929b714ac58e4cc56b5c43b485ca50e13d3d147))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([c45928f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c45928f1c05e7cbd73f91646cf59793e25a7d509))
* **IPCEISCOOD-132:** make bulk call to get prerequisite and dependent status - fix tests ([f4097cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f4097cdf9bb248e4287425ce133d67e870c47711))
* **IPCEISCOOD-132:** Merge origin/COOD-Sprint8 to IPCEISCOOD-132 ([6b89a06](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6b89a06c0a3ffa708d621209fb02ccb3b2e79e0b))
* **IPCEISCOOD-136:** adding jsonschema2pojo plugin, lombok-custom-annotator... ([0ed85ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0ed85ea7141ebd527d434ae8c545d72a8f8c50e2))
* **IPCEISCOOD-184:** change product order test api URI ([64a4de9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/64a4de91ef804ee530f3eefd018a823dd43b281b))
* **IPCEISCOOD-184:** rename test controller uri ([5034bfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5034bfc6fd1da9d9eb793abca48ed3003d3653b3))
* **IPCEISCOOD-184:** rename uri in collection ([861a41c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/861a41c88b1cc9fbd528045df40e48d7c49d8d48))
* **IPCEISCOOD-184:** update productSpec to be productSpecification ([bb488ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb488eeaf993429c1f7f9eb993efebbf2668454b))
* **IPCEISCOOD-184:** upgrade major version cood api ([96ab9a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/96ab9a204e7ae77b1d75375cd63f33c37b0478d8))
* **IPCEISCOOD-194:** Change in Product State Update After Delivery to include correct states ([f0759de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f0759de32ee3120bd3fdf9b87163b45accaaff85))
* **IPCEISCOOD-36:** add Webclient host resolver ([4a1e7e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4a1e7e3d3b0d9aec453ba8709bb5a82c3b9b04b9))
* **IPCEISCOOD-36:** adding keycloak configs and fix sonarIssues ([3f4a1e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3f4a1e031066b0a22d84070ee6e6551554ae076b))
* **IPCEISCOOD-36:** rebase from  origin/COOD-Sprint9 ([a2db729](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a2db729e4a8898e2689246febce4d414ac1b6bab))
* **IPCEISCOOD-36:** updating external services ports config ([f606080](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f60608077da9d3886e890ec11c6e7183743b112f))
* **IPCEISCOOD-68:** Add integration tests preparation (mongodb, kafka, wiremock) ([27ea8aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/27ea8aa21b835f2b8068ba654a69da66e3be9d15))
* **IPCEISCOOD-70:** documenting events using async api ([2cbbc12](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2cbbc12a07f793df144a0792662c0cdf46a16df5))
* **IPCEISCOOD-70:** review comments ([f40c387](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f40c387ea3105709107e3fc7393304df2059a089))
* **IPCEISCOOD-70:** updated README.md ([6bb21cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6bb21cbe89bcee83fc8411a8f6c2318ddb5ee003))
* **PCEISCOOD-147:** implement BDD of creation phase ([a8e26cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a8e26cb72b133d7a1e9b20d5bcdf0ffc5f400e6c))
* prepare project base ([4894563](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/489456312a95067e9e8b8e7290e252246740f42c))
* supporting contract first approach ([09e63f5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/09e63f5514c4d727e3c8e936789d3d702c417845))
* update compliance report ([c7f7aba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c7f7aba737422b34af67ed4607fb9e07efe39d9e))
* update compliance report ([d479db6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d479db680c056d3ad04870b6616a148dc1b16cfb))
* Update Kafka topic names to follow naming standard and apply OM topic name change ([c2e3d54](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c2e3d5495600750a24bf897f41c6df04d301406d))
* upgrade pom version ([d5ab359](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d5ab359b52bd7ed1f908319c26b976872abcacba))

## [1.1.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.1.1...1.1.2) (2024-1-10)


### Bug Fixes

* pushing to trigger pipeline ([2ebafea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2ebafead6275ca2fce0d246053ea02c0f67de689))

## [1.1.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/1.1.0...1.1.1) (2024-1-10)


### Bug Fixes

* use latest swagger yaml in api designer, fix spring doc to get the doc from the yaml ([f12bf43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f12bf4369d6a9180c866dd0707ef0a7773dbd122))
* use latest swagger yaml in api designer, fix spring doc to get the doc from the yaml ([a4a20f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a4a20f89e47d52831e6b74e6db0595c9a20e8096))

## [1.3.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/dev-1.3.0...dev-1.3.1) (2024-1-9)


### Bug Fixes

* merge master ([cc25ebe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cc25ebe91343f71aa492da2f8594f98a236704ed))
* use latest swagger yaml in api designer, fix spring doc to get the doc from the yaml ([f12bf43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f12bf4369d6a9180c866dd0707ef0a7773dbd122))
* use latest swagger yaml in api designer, fix spring doc to get the doc from the yaml ([a4a20f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a4a20f89e47d52831e6b74e6db0595c9a20e8096))
* add ring chararatistics ([972df70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/972df7040775ea052fb8d26540b6be44f5515a67))
* adjust review props in helm and app.yaml ([018d2cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/018d2cdea67700398fb1cc2bd7d57fbf0a613f38))
* change in som mock job, update postman ([0d8e77d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0d8e77d83abd7637a4d95af2389d0a267ea3be03))
* change mock server review to mock server integration in review profile ([3db8396](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3db8396ad3ad7cb8fc4b728b14acba2b4c21596c))
* change product catalog url ([1ecc3f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1ecc3f1c6da91ad0bca8f7709a3748f241507298))
* **COOD-sprint7:** updated the servers url, and allow for null realizing service comming from product DTO ([06cc35f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/06cc35fc9e4e6b2207a18d2c0075208f0adfe376))
* fix kafka tracing config ([121787a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/121787ae1f343d4a76b451effbe922d91a20b7aa))
* fix PI4 integration issues & add integration tests ([75aecf0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/75aecf07d37c17c5180b24bed5016f75231411e0))
* fix restructure packages with merge with sprint8 ([b484502](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b484502117ea976a86ecfa1bb372c1459580f6bf))
* fix sonar major issues ([1cd18b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cd18b9880c07cde99b2c2bb7a5d58b02f8b0335))
* fix sonar major issues ([d2f0f19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d2f0f190fb023ada7782dd1657fb9de162693359))
* fix sonar major issues ([d9ba4aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d9ba4aadba3abcc6d2d149bc58128ee7c8ac9c48))
* fix sonar major issues ([44fbc22](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/44fbc223771a31338daa209ecb831510be21c6a2))
* fix sonar major issues ([241f378](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/241f3787214a7fa84e2aecb8f994d39ded030916))
* fix update product status ([11df9ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/11df9ec1591e947682c8c3a37fc552d9a5ae979b))
* **IPCEISCOOD-123:** fix failing tests ([76e7f2e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/76e7f2e16580c4b9fa307410923f2a61a98e3bb9))
* **IPCEISCOOD-123:** fix filtering with received date ([7876fee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7876fee4ce33344f5c77d6b60fa04b6998d5c632))
* **IPCEISCOOD-137:** Technical task | implement Plan API controller ([7a30344](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7a30344923e8c36f2171ee0d8211e3cc1f05dbdd))
* **IPCEISCOOD-170:** adding missing async-api ([1f682b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1f682b9efd522fdc2301c8d7577d8b818274338a))
* **IPCEISCOOD-170:** handle when productorder.product exists in product order. ([302d23f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/302d23f0ad3708e2192cac124ebd4fa8b29ff898))
* **IPCEISCOOD-194:** Merge branch 'IPCEISCOOD-194' into 'COOD_Sprint_12' See merge request disco/disco-oda-components/disco-order-orchestration/orchestration-delivery!153 ([58c1384](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/58c138425241c157f8a5033c116e6da05c96eaae))
* **IPCEISCOOD-36:** applying review comments ([e9df5db](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e9df5db9195b661ed3edbc9409fc2365c2c0d507))
* **IPCEISCOOD-36:** applying review comments ([6007a42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6007a4268de8b139767934f2545437ead168b3b6))
* **IPCEISCOOD-36:** fix keycloak-client-secret.yaml file ([2915d77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2915d778fb6537b3d368e64f05900fe4f14b94ce))
* **IPCEISCOOD-36:** fix public endpoints matchers ([f875cb7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f875cb76e1a4f50be25fa929ffec6addf005649a))
* **IPCEISCOOD-36:** fix webclient resolver issue ([0a9db89](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0a9db89224c11950eac245c557b18a917d84958b))
* **IPCEISCOOD-36:** modify user on dockerfile ([4a43900](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4a43900249759156ec0d497bb4a49a29a915872e))
* **IPCEISCOOD-36:** moving certificates file to the root directory ([d3d01fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d3d01fa5155898798486daf165ddfe86cde83b8a))
* **IPCEISCOOD-36:** removing useless config ([06e639b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/06e639b9cc2ed0fd08cf5e233282de6f758414c2))
* **IPCEISCOOD-50:** fix constructor injection issue ([d26dce1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d26dce19414eb680050bea29a6c9a4cc4abc3a2d))
* **IPCEISCOOD-50:** fix verifyDependentForDeleteAction - skip verifying  if action not equal delete ([7bfc5d6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7bfc5d647618f837f787e319c3a7dd4c5656b64b))
* **IPCEISCOOD-50:** fixing AddOrchestrationPlanTests after merge - update kafka test container version to 7.5.1 ([c238181](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c238181dc080d0d5bc5d9ed83d3275eef88fdcf0))
* **IPCEISCOOD-54:** Pagination header returned is incorrect ([4736326](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/473632608be1e95977e87c170102fedc31aa457c))
* **IPCEISCOOD-68:** fix docker-pull rate limit using the dockerproxy orange repo and refactor code to use @Value for the topic names and added wiremock reset ([8c9ed10](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c9ed108d142b4522eb6fcb7e08b565f7b0292fb))
* **IPCEISCOOD-68:** fix docker-pull rate limit using the dockerproxy orange repo and refactor code to use @Value for the topic names and added wiremock reset ([1b0635c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b0635c4cf02e1664de902a7c78f60d42b6b2586))
* listing issues ([5fff6ba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5fff6baf859da54b009227df80a83e4810d46b1c))
* Merge branch 'PCEISCOOD-147' into 'COOD_Sprint_12' See merge request disco/disco-oda-components/disco-order-orchestration/orchestration-delivery!148 ([1b115a3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b115a310b44278ffae081b2b32bbdecd9c941e7))
* merge master ([cc25ebe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cc25ebe91343f71aa492da2f8594f98a236704ed))
* merge orch yaml file domain names ([b0012bc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b0012bc1b4d758139ba7fd470463ba5e852c8085))
* merge PI4 integration issues to develop ([4344b53](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4344b53f0f2f525ace1aa80041c9e6d09d05d92e))
* merge review config ([d4cf9c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d4cf9c333486c26ed0c8f81813d75306eaea2161))
* **PI4 integration fixes:** add workaround to allow localdatetime in product order ([853ef17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/853ef177701622e9083932f9fd50b3f0b162bffa))
* **PI4 integration fixes:** add workaround to allow localdatetime in product order merge to develop ([1245713](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1245713ba155bc3de232121ca4c3ff2f33ca6f05))
* publish event after patch request ([bf3580a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bf3580ad45b089adc68a4637766f8a6030455a00))
* remove reformat commit and fix sonar ([142a663](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/142a663ee140f3a7fbdb82b62e601e5a6f5f22d1))
* rename handler name, add todos and update the deliver method to remove unneeded published event ([d23a292](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d23a292d6bd3fa69cfc8489a2113c671ab221028))
* rename SOM serviceOrderStateChange event ([9887db1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9887db1faa1cbef42d5a6d7f09dce64e3ebe5f95))
* restructure packages ([a112499](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a112499c5706ee2572ab42621b12d214f17ba246))
* restructure packages ([cfd9145](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cfd91457fbce498c84ea3c223c9b611ee4da5fd5))
* rollback review changes ([8c2683c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c2683cf068c524a10c67537687888ae5ac1542c))
* skip from coverage event and mocks packages ([8ab8acb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8ab8acba458445dd64f9f94931bcea00dce8cc99))
* update collection ([bb1c5d1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb1c5d175d6d2565670acc555a498e8b6427917c))
* use mock-server ([8288595](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/828859556f5d01f401e86bc2c276370a7af70dd3))
* use mock-server in integration ([687c4d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/687c4d4e17f0c12ee82266ab6c878b81a8f9325b))
* use mock-server-integration ([73d3476](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/73d34767f7454c7f10fab401f37771255e2c3b92))
* utilize deliver method to accept list of nodes ([9becb5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9becb5b9554db437bb1cbb9d8756791a0a8aae37))


### Features

* add initial compliance report ([a5714c0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a5714c0ce0e0faa2ad90f4da33fdd2b095d5cc40))
* add review comments with story 60 investigation ([e815b77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e815b77d58fd48d7770feb6269c3451e6718fa5a))
* add spec empty package ([1ec943a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1ec943aba643e380345ccfec9d115ee0cf2397a6))
* add todo ([2033f5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2033f5b0b8e7df385693921183ad1824fe876c9d))
* add todo in delivery after dms ([9151da0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9151da0710120c5eb332a906f5d89d6f0181aa5a))
* add todos ([be579f6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/be579f63e5b649d98657bef36cc820c8ec64a2a1))
* fix checkstyle ([a6b21e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a6b21e3c38ab66e369ca1e00723fd6dee1f2e344))
* fix pmd and spotbugs ([718205d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/718205de4d7ff7decbbad21127e5dd53189cd368))
* fix sonar issues ([6e7d66d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e7d66d03cbb6ff00171c42e25ec373e25857cbc))
* **IPCEISCOOD-111:** add productCharacteristic in patch request body ([c76f0de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c76f0de6c38032ec67f2a90956172f099b6beca0))
* **IPCEISCOOD-111:** added test models helper builders ([a763af0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a763af0ebae99909fcda54eb55d00fab7619980f))
* **IPCEISCOOD-111:** apply review comment ([48cede6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/48cede69f9f335403d9e22b718758c4836c4103c))
* **IPCEISCOOD-111:** apply review comment ([b6a162b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b6a162b8abcae206bbfe74574cca21b94d606c1a))
* **IPCEISCOOD-111:** fix test after applying review comment ([5a61fd8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5a61fd85b4a1db464a0c07ea966474f4dd7c5f79))
* **IPCEISCOOD-111:** undo last review comment ([960c761](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/960c761b122a0e58a78057425cd44a820f73d39b))
* **IPCEISCOOD-111:** use map instead of forEach ([1cf86de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cf86dec45bd72b818be64cb950ff96561ae6ebc))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([2c9a07d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2c9a07d576fd53c853195266f1e116c085f62670))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([6929b71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6929b714ac58e4cc56b5c43b485ca50e13d3d147))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([c45928f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c45928f1c05e7cbd73f91646cf59793e25a7d509))
* **IPCEISCOOD-132:** make bulk call to get prerequisite and dependent status - fix tests ([f4097cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f4097cdf9bb248e4287425ce133d67e870c47711))
* **IPCEISCOOD-132:** Merge origin/COOD-Sprint8 to IPCEISCOOD-132 ([6b89a06](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6b89a06c0a3ffa708d621209fb02ccb3b2e79e0b))
* **IPCEISCOOD-136:** adding jsonschema2pojo plugin, lombok-custom-annotator... ([0ed85ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0ed85ea7141ebd527d434ae8c545d72a8f8c50e2))
* **IPCEISCOOD-184:** change product order test api URI ([64a4de9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/64a4de91ef804ee530f3eefd018a823dd43b281b))
* **IPCEISCOOD-184:** rename test controller uri ([5034bfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5034bfc6fd1da9d9eb793abca48ed3003d3653b3))
* **IPCEISCOOD-184:** rename uri in collection ([861a41c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/861a41c88b1cc9fbd528045df40e48d7c49d8d48))
* **IPCEISCOOD-184:** update productSpec to be productSpecification ([bb488ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb488eeaf993429c1f7f9eb993efebbf2668454b))
* **IPCEISCOOD-184:** upgrade major version cood api ([96ab9a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/96ab9a204e7ae77b1d75375cd63f33c37b0478d8))
* **IPCEISCOOD-194:** Change in Product State Update After Delivery to include correct states ([f0759de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f0759de32ee3120bd3fdf9b87163b45accaaff85))
* **IPCEISCOOD-36:** add Webclient host resolver ([4a1e7e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4a1e7e3d3b0d9aec453ba8709bb5a82c3b9b04b9))
* **IPCEISCOOD-36:** adding keycloak configs and fix sonarIssues ([3f4a1e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3f4a1e031066b0a22d84070ee6e6551554ae076b))
* **IPCEISCOOD-36:** rebase from  origin/COOD-Sprint9 ([a2db729](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a2db729e4a8898e2689246febce4d414ac1b6bab))
* **IPCEISCOOD-36:** updating external services ports config ([f606080](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f60608077da9d3886e890ec11c6e7183743b112f))
* **IPCEISCOOD-68:** Add integration tests preparation (mongodb, kafka, wiremock) ([27ea8aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/27ea8aa21b835f2b8068ba654a69da66e3be9d15))
* **IPCEISCOOD-70:** documenting events using async api ([2cbbc12](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2cbbc12a07f793df144a0792662c0cdf46a16df5))
* **IPCEISCOOD-70:** review comments ([f40c387](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f40c387ea3105709107e3fc7393304df2059a089))
* **IPCEISCOOD-70:** updated README.md ([6bb21cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6bb21cbe89bcee83fc8411a8f6c2318ddb5ee003))
* **PCEISCOOD-147:** implement BDD of creation phase ([a8e26cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a8e26cb72b133d7a1e9b20d5bcdf0ffc5f400e6c))
* supporting contract first approach ([09e63f5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/09e63f5514c4d727e3c8e936789d3d702c417845))
* update compliance report ([c7f7aba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c7f7aba737422b34af67ed4607fb9e07efe39d9e))
* update compliance report ([d479db6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d479db680c056d3ad04870b6616a148dc1b16cfb))
* Update Kafka topic names to follow naming standard and apply OM topic name change ([c2e3d54](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c2e3d5495600750a24bf897f41c6df04d301406d))
* upgrade pom version ([d5ab359](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d5ab359b52bd7ed1f908319c26b976872abcacba))
>>>>>>> CHANGELOG.md

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/dev-1.2.0...dev-1.3.0) (2024-1-4)


### Bug Fixes

* **IPCEISCOOD-194:** Merge branch 'IPCEISCOOD-194' into 'COOD_Sprint_12' See merge request disco/disco-oda-components/disco-order-orchestration/orchestration-delivery!153 ([58c1384](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/58c138425241c157f8a5033c116e6da05c96eaae))
* Merge branch 'PCEISCOOD-147' into 'COOD_Sprint_12' See merge request disco/disco-oda-components/disco-order-orchestration/orchestration-delivery!148 ([1b115a3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1b115a310b44278ffae081b2b32bbdecd9c941e7))


### Features

* **IPCEISCOOD-194:** Change in Product State Update After Delivery to include correct states ([f0759de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f0759de32ee3120bd3fdf9b87163b45accaaff85))
* **PCEISCOOD-147:** implement BDD of creation phase ([a8e26cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a8e26cb72b133d7a1e9b20d5bcdf0ffc5f400e6c))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/dev-1.1.0...dev-1.2.0) (2023-12-21)


### Bug Fixes

* add ring chararatistics ([972df70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/972df7040775ea052fb8d26540b6be44f5515a67))
* change in som mock job, update postman ([0d8e77d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0d8e77d83abd7637a4d95af2389d0a267ea3be03))
* fix kafka tracing config ([121787a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/121787ae1f343d4a76b451effbe922d91a20b7aa))
* fix PI4 integration issues & add integration tests ([75aecf0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/75aecf07d37c17c5180b24bed5016f75231411e0))
* **IPCEISCOOD-137:** Technical task | implement Plan API controller ([7a30344](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/7a30344923e8c36f2171ee0d8211e3cc1f05dbdd))
* **IPCEISCOOD-170:** adding missing async-api ([1f682b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1f682b9efd522fdc2301c8d7577d8b818274338a))
* **IPCEISCOOD-170:** handle when productorder.product exists in product order. ([302d23f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/302d23f0ad3708e2192cac124ebd4fa8b29ff898))
* **IPCEISCOOD-54:** Pagination header returned is incorrect ([4736326](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/473632608be1e95977e87c170102fedc31aa457c))
* merge PI4 integration issues to develop ([4344b53](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4344b53f0f2f525ace1aa80041c9e6d09d05d92e))
* **PI4 integration fixes:** add workaround to allow localdatetime in product order ([853ef17](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/853ef177701622e9083932f9fd50b3f0b162bffa))
* **PI4 integration fixes:** add workaround to allow localdatetime in product order merge to develop ([1245713](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1245713ba155bc3de232121ca4c3ff2f33ca6f05))
* publish event after patch request ([bf3580a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bf3580ad45b089adc68a4637766f8a6030455a00))


### Features

* **IPCEISCOOD-136:** adding jsonschema2pojo plugin, lombok-custom-annotator... ([0ed85ea](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0ed85ea7141ebd527d434ae8c545d72a8f8c50e2))
* **IPCEISCOOD-184:** change product order test api URI ([64a4de9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/64a4de91ef804ee530f3eefd018a823dd43b281b))
* **IPCEISCOOD-184:** rename test controller uri ([5034bfc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5034bfc6fd1da9d9eb793abca48ed3003d3653b3))
* **IPCEISCOOD-184:** rename uri in collection ([861a41c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/861a41c88b1cc9fbd528045df40e48d7c49d8d48))
* **IPCEISCOOD-184:** update productSpec to be productSpecification ([bb488ee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb488eeaf993429c1f7f9eb993efebbf2668454b))
* **IPCEISCOOD-184:** upgrade major version cood api ([96ab9a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/96ab9a204e7ae77b1d75375cd63f33c37b0478d8))
* update compliance report ([c7f7aba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c7f7aba737422b34af67ed4607fb9e07efe39d9e))
* update compliance report ([d479db6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d479db680c056d3ad04870b6616a148dc1b16cfb))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/compare/dev-1.0.0...dev-1.1.0) (2023-11-19)


### Bug Fixes

* adjust review props in helm and app.yaml ([018d2cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/018d2cdea67700398fb1cc2bd7d57fbf0a613f38))
* change mock server review to mock server integration in review profile ([3db8396](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3db8396ad3ad7cb8fc4b728b14acba2b4c21596c))
* fix restructure packages with merge with sprint8 ([b484502](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b484502117ea976a86ecfa1bb372c1459580f6bf))
* fix sonar major issues ([1cd18b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cd18b9880c07cde99b2c2bb7a5d58b02f8b0335))
* fix sonar major issues ([d2f0f19](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d2f0f190fb023ada7782dd1657fb9de162693359))
* fix sonar major issues ([d9ba4aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d9ba4aadba3abcc6d2d149bc58128ee7c8ac9c48))
* fix sonar major issues ([44fbc22](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/44fbc223771a31338daa209ecb831510be21c6a2))
* fix sonar major issues ([241f378](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/241f3787214a7fa84e2aecb8f994d39ded030916))
* **IPCEISCOOD-36:** applying review comments ([e9df5db](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e9df5db9195b661ed3edbc9409fc2365c2c0d507))
* **IPCEISCOOD-36:** applying review comments ([6007a42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6007a4268de8b139767934f2545437ead168b3b6))
* **IPCEISCOOD-36:** fix keycloak-client-secret.yaml file ([2915d77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2915d778fb6537b3d368e64f05900fe4f14b94ce))
* **IPCEISCOOD-36:** fix public endpoints matchers ([f875cb7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f875cb76e1a4f50be25fa929ffec6addf005649a))
* **IPCEISCOOD-36:** fix webclient resolver issue ([0a9db89](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/0a9db89224c11950eac245c557b18a917d84958b))
* **IPCEISCOOD-36:** modify user on dockerfile ([4a43900](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4a43900249759156ec0d497bb4a49a29a915872e))
* **IPCEISCOOD-36:** moving certificates file to the root directory ([d3d01fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d3d01fa5155898798486daf165ddfe86cde83b8a))
* **IPCEISCOOD-36:** removing useless config ([06e639b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/06e639b9cc2ed0fd08cf5e233282de6f758414c2))
* merge orch yaml file domain names ([b0012bc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b0012bc1b4d758139ba7fd470463ba5e852c8085))
* merge review config ([d4cf9c3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d4cf9c333486c26ed0c8f81813d75306eaea2161))
* remove reformat commit and fix sonar ([142a663](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/142a663ee140f3a7fbdb82b62e601e5a6f5f22d1))
* rename handler name, add todos and update the deliver method to remove unneeded published event ([d23a292](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d23a292d6bd3fa69cfc8489a2113c671ab221028))
* restructure packages ([a112499](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a112499c5706ee2572ab42621b12d214f17ba246))
* restructure packages ([cfd9145](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/cfd91457fbce498c84ea3c223c9b611ee4da5fd5))
* rollback review changes ([8c2683c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8c2683cf068c524a10c67537687888ae5ac1542c))
* skip from coverage event and mocks packages ([8ab8acb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/8ab8acba458445dd64f9f94931bcea00dce8cc99))
* update collection ([bb1c5d1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/bb1c5d175d6d2565670acc555a498e8b6427917c))
* use mock-server ([8288595](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/828859556f5d01f401e86bc2c276370a7af70dd3))
* use mock-server in integration ([687c4d4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/687c4d4e17f0c12ee82266ab6c878b81a8f9325b))
* use mock-server-integration ([73d3476](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/73d34767f7454c7f10fab401f37771255e2c3b92))
* utilize deliver method to accept list of nodes ([9becb5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9becb5b9554db437bb1cbb9d8756791a0a8aae37))


### Features

* add review comments with story 60 investigation ([e815b77](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/e815b77d58fd48d7770feb6269c3451e6718fa5a))
* add spec empty package ([1ec943a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1ec943aba643e380345ccfec9d115ee0cf2397a6))
* add todo ([2033f5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2033f5b0b8e7df385693921183ad1824fe876c9d))
* add todo in delivery after dms ([9151da0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/9151da0710120c5eb332a906f5d89d6f0181aa5a))
* add todos ([be579f6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/be579f63e5b649d98657bef36cc820c8ec64a2a1))
* fix checkstyle ([a6b21e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a6b21e3c38ab66e369ca1e00723fd6dee1f2e344))
* fix pmd and spotbugs ([718205d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/718205de4d7ff7decbbad21127e5dd53189cd368))
* fix sonar issues ([6e7d66d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6e7d66d03cbb6ff00171c42e25ec373e25857cbc))
* **IPCEISCOOD-111:** add productCharacteristic in patch request body ([c76f0de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c76f0de6c38032ec67f2a90956172f099b6beca0))
* **IPCEISCOOD-111:** added test models helper builders ([a763af0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a763af0ebae99909fcda54eb55d00fab7619980f))
* **IPCEISCOOD-111:** apply review comment ([48cede6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/48cede69f9f335403d9e22b718758c4836c4103c))
* **IPCEISCOOD-111:** apply review comment ([b6a162b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/b6a162b8abcae206bbfe74574cca21b94d606c1a))
* **IPCEISCOOD-111:** fix test after applying review comment ([5a61fd8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/5a61fd85b4a1db464a0c07ea966474f4dd7c5f79))
* **IPCEISCOOD-111:** undo last review comment ([960c761](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/960c761b122a0e58a78057425cd44a820f73d39b))
* **IPCEISCOOD-111:** use map instead of forEach ([1cf86de](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/1cf86dec45bd72b818be64cb950ff96561ae6ebc))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([2c9a07d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/2c9a07d576fd53c853195266f1e116c085f62670))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([6929b71](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6929b714ac58e4cc56b5c43b485ca50e13d3d147))
* **IPCEISCOOD-127:** Retrieve Multiple Products Specifications Details From Product Catalog ([c45928f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/c45928f1c05e7cbd73f91646cf59793e25a7d509))
* **IPCEISCOOD-132:** make bulk call to get prerequisite and dependent status - fix tests ([f4097cd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f4097cdf9bb248e4287425ce133d67e870c47711))
* **IPCEISCOOD-132:** Merge origin/COOD-Sprint8 to IPCEISCOOD-132 ([6b89a06](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/6b89a06c0a3ffa708d621209fb02ccb3b2e79e0b))
* **IPCEISCOOD-36:** add Webclient host resolver ([4a1e7e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/4a1e7e3d3b0d9aec453ba8709bb5a82c3b9b04b9))
* **IPCEISCOOD-36:** adding keycloak configs and fix sonarIssues ([3f4a1e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/3f4a1e031066b0a22d84070ee6e6551554ae076b))
* **IPCEISCOOD-36:** rebase from  origin/COOD-Sprint9 ([a2db729](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/a2db729e4a8898e2689246febce4d414ac1b6bab))
* **IPCEISCOOD-36:** updating external services ports config ([f606080](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/f60608077da9d3886e890ec11c6e7183743b112f))
* upgrade pom version ([d5ab359](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery/commit/d5ab359b52bd7ed1f908319c26b976872abcacba))

# 1.0.0 (2023-10-29)


### Bug Fixes

* change product catalog url ([1ecc3f1](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/1ecc3f1c6da91ad0bca8f7709a3748f241507298))
* **COOD-sprint7:** updated the servers url, and allow for null realizing service comming from product DTO ([06cc35f](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/06cc35fc9e4e6b2207a18d2c0075208f0adfe376))
* fix update product status ([11df9ec](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/11df9ec1591e947682c8c3a37fc552d9a5ae979b))
* fix update product status ([2288939](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/2288939a48e830e2772a893fbbdca76c859b34d9))
* **IPCEISCOOD-123:** fix failing tests ([76e7f2e](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/76e7f2e16580c4b9fa307410923f2a61a98e3bb9))
* **IPCEISCOOD-123:** fix filtering with received date ([7876fee](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/7876fee4ce33344f5c77d6b60fa04b6998d5c632))
* **IPCEISCOOD-50:** fix constructor injection issue ([d26dce1](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/d26dce19414eb680050bea29a6c9a4cc4abc3a2d))
* **IPCEISCOOD-50:** fix verifyDependentForDeleteAction - skip verifying  if action not equal delete ([7bfc5d6](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/7bfc5d647618f837f787e319c3a7dd4c5656b64b))
* **IPCEISCOOD-50:** fixing AddOrchestrationPlanTests after merge - update kafka test container version to 7.5.1 ([c238181](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/c238181dc080d0d5bc5d9ed83d3275eef88fdcf0))
* **IPCEISCOOD-68:** fix docker-pull rate limit using the dockerproxy orange repo and refactor code to use @Value for the topic names and added wiremock reset ([8c9ed10](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/8c9ed108d142b4522eb6fcb7e08b565f7b0292fb))
* **IPCEISCOOD-68:** fix docker-pull rate limit using the dockerproxy orange repo and refactor code to use @Value for the topic names and added wiremock reset ([1b0635c](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/1b0635c4cf02e1664de902a7c78f60d42b6b2586))
* listing issues ([5fff6ba](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/5fff6baf859da54b009227df80a83e4810d46b1c))
* rename SOM serviceOrderStateChange event ([9887db1](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/9887db1faa1cbef42d5a6d7f09dce64e3ebe5f95))


### Features

* add initial compliance report ([a5714c0](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/a5714c0ce0e0faa2ad90f4da33fdd2b095d5cc40))
* **IPCEISCOOD-68:** Add integration tests preparation (mongodb, kafka, wiremock) ([27ea8aa](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/27ea8aa21b835f2b8068ba654a69da66e3be9d15))
* **IPCEISCOOD-70:** documenting events using async api ([2cbbc12](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/2cbbc12a07f793df144a0792662c0cdf46a16df5))
* **IPCEISCOOD-70:** review comments ([f40c387](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/f40c387ea3105709107e3fc7393304df2059a089))
* **IPCEISCOOD-70:** updated README.md ([6bb21cb](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/6bb21cbe89bcee83fc8411a8f6c2318ddb5ee003))
* prepare project base ([4894563](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/489456312a95067e9e8b8e7290e252246740f42c))
* supporting contract first approach ([09e63f5](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/09e63f5514c4d727e3c8e936789d3d702c417845))
* Update Kafka topic names to follow naming standard and apply OM topic name change ([c2e3d54](https://gitlab.tech.orange/disco/disco-order-orchestration/orchestration-delivery/commit/c2e3d5495600750a24bf897f41c6df04d301406d))
