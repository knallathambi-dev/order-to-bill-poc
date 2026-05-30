# [1.12.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.11.1...1.12.0) (2026-04-23)


### Bug Fixes

* add missing mongo variable that we use it with default value ([9e14d51](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9e14d51a1df9f3491cd569cd554b2affe3303364))
* enhance ProductDateHelper to support plural time units in date calculations ([dfc2aa7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/dfc2aa782481609ce74350ee9c3af99e31e39885))
* fix gitleaks issue as it is [secure] positive ([aafa8c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/aafa8c79010760fbe15b18a5be0a626ea903ed3b))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([c56af31](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/c56af3143c16dda8f29bade4229dffb14c9af7b3))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([cd6fe70](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/cd6fe70d03f877b6d50f6c841ca76b24e40ed56f))
* prevent duplicate state change events ([e65b5cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/e65b5cff1d6bb935d5c486a7e4294bf191fa4508))
* remove deprecated sonar.jacoco.reportPaths property ([fbc23e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/fbc23e7b010ef00823f4f9938aa1461822554402))


### Features

* add csp header for each request ([f2743f0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f2743f0704706cdc2a40bc0312519e0eba41ae24))
* add requires value to RelationshipType enum ([eb7a443](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/eb7a443329733cb3985ba0c3680b724f42fa6b5e))
* enhance logging ([5975ec9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5975ec9d3a4e38b767fe78beb8f0281102d4a549))
* enhance price alteration update to support application offset ([5575cf9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5575cf9913a278597e3d656b9f84ca1f68d83d98))
* evolve date helper to support installment ([4616a6e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4616a6e123190e8fb2a7fc24671037f3ff87d4ab))
* exclude swagger-ui from csp restriction ([dff1cd5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/dff1cd507e8153c1f72cf68a4c99444a4a03f96c))
* improve CSP policy handling and update dependency versions ([abde195](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/abde195745db63dbe529c426b474febe309eec9c))
* switch to custom MongoDB chart ([b131cfe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b131cfe527a0ee348474bb3d8933331e9449a38d))
* upgrade product inventory REST API spec to 1.3.0-SNAPSHOT with DateCharacteristics support ([66e8d27](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/66e8d27d16401e7285c21e8e7426471b419ceafc))

## [1.11.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.11.0...1.11.1) (2025-12-02)


### Bug Fixes

* correct product price assignment ([ab81a9a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ab81a9a0c743bf86f5bc97e321e871425205d2cb))
* correct product term assignment during migration ([8aad4ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/8aad4ed12030640b6f11b108f55a0636bea2804c))
* correct status handling for bundles with no change action during migration ([4601676](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/460167629670030a545a22573323beba1446b56f))

# [1.11.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.10.0...1.11.0) (2025-11-13)


### Bug Fixes

* add common label to allow prometheus monitoring ([47ced6f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/47ced6ffd4719b730d8c372a7c8988126734553e))
* enforce proper naming ([3966ada](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/3966adab7884a00f4eecff9f46d0f8e8003ab62d))
* fix gitleaks issue ([b1532e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b1532e7dbdbf7f17e361363e47a6092bd7b7149a))
* increase memory request and limits as service restart many times due to OOM ([f4a2c56](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f4a2c56b6b6fc94bdd2339abef432a7a89c3a7c5))
* remove config for promtail and istio ([e76b8e2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/e76b8e2c3af3762d1c2d536f3b02ba452246a787))
* remove istio config ([7bc9f57](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7bc9f57144a6fda62725f0412dcf0a800803ce01))
* remove promtail config ([2c65a04](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/2c65a0425c4c44f802d6309d413cb7205c2becbc))
* remove the add of reliesOn relationship for migrate product ([e3fc3f0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/e3fc3f026cd7d0a486169dfd863dd176c6cd80af))
* remove values file as we moved them to gitlab-ci ([3655609](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/36556094401811a4a69646737fd43701fe5cfbc0))
* rename productOrderInternalEvent to productOrderAttributeValueChangeEvent ([b2f3430](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b2f3430f8c158d2c5f233f7edf2c23ba8b4eebc5))
* **tests:** resolve unit test failures with missing role config ([b120372](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b120372a86f2369be8b5f83b1154496452b7eabc))


### Features

* configure webclient timeouts ([4eef0d9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4eef0d93e4965f5b6a801267f4d08289b87d710d))
* enhance ofup to support migration use case ([63321cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/63321cbfc5cde69845aa4fecdf6bda4c40bbc0c2))
* enhance ofup to support multiple migrations ([e6d3b7f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/e6d3b7f33491b0e681d1a35fdf00eeb47d2d7fad))
* evolve product state update for use case migration ([d8e6c72](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d8e6c723390cce7ec5b637c1494a20facb2d3b40))
* update root product id for noChange/modify product ([4efe6ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4efe6ad2a2f63ec63eb60aca35ff1a8214cccd4f))
* update the validFor for the product term ([f3fa55d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f3fa55d519aa394cbac454e006f7808c1e5fd448))
* use refactored maven spec modules ([7be1b8c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7be1b8c2af44d9c30c0fb3292b5f0108c3c4d114))
* utilize DOCKER_REGISTRY_MIRROR for container image pulls in MongoDB and Kafka test classes ([ffee22e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ffee22ef31161a659a0de91ab181d00c9869bca1))

# [1.10.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.9.0...1.10.0) (2025-07-16)


### Bug Fixes

* update GitLab URL to gitlab.ow2.org in helm/README.md and regenerate files ([956f992](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/956f992a81a4eeb876f27d921496758cecc21953))
* update order spec version ([3679a61](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/3679a61312a7d5f9167cda4dec8ae8edebf797ab))


### Features

* manage product Price date ([3c1a71a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/3c1a71adb464f292cbd00b8ae2a425019c59de4e))

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.8.0...1.9.0) (2025-06-23)


### Features

* upgrade om-commons and product-inventory to latest versions ([b725bbd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b725bbddb6f21e285dae122fe763c4763f9a6783))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.7.0...1.8.0) (2025-06-04)


### Features

* update documentation, regenerate CONTRIBUTING.md and adjust deployment configurations ([1199357](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/11993570dfb5c06de8d3a9b1cf507aaa637103fc))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.6.2...1.7.0) (2025-06-03)


### Bug Fixes

* **opensource:** remove annotations from helm templates to resolve deployment issues ([ddf9bbf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ddf9bbff01a6f24c71fd9ae50a367c512da66940))


### Features

* generate event for non installable product ([5b49fa9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5b49fa9c20cacfb604a14ffa99db54de5608d6b3))
* include process-flow-spec Swagger YAML file ([4f129c6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4f129c613a91d207778a0871b7a1c19a6cc1ab99))
* **opensource:** add DCO.txt file ([70494e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/70494e8917121bb9165e740a38012deb68b1bf70))
* **opensource:** move to Open Source Sofware ([1fb1d24](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/1fb1d24b7c1c840423ad83d9aa4d8b2652ca0895))
* proper enforcement for artifact name ([7cb822f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7cb822f1193ea9bf9e30f19810bdea638b8d9a7a))
* update Swagger YAML from process-flow-spec to order-followup-spec ([6592008](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/65920084e43c617ab98bdff30b2874f10db7395d))

<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## [1.6.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.6.1...1.6.2) (2025-03-05)


### Bug Fixes

* add dummy commit ([e28b9fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/e28b9faa012f60149ad07398e34b4677fc84f16b))
* add dummy commit ([308302e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/308302eece7a8e6a416f4786f977363f42b95bba))
* resolve termination handling issue for multi-bundle scenarios ([9f32b86](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9f32b8639e2dae6b2568012f9ebb6faccd194106))


## [1.6.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.6.0...1.6.1) (2025-03-03)


### Bug Fixes

* add dummy commit ([80c18f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/80c18f85082fff3b32c0f75795139aa1355bbb29))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.5.1...1.6.0) (2025-01-28)


### Bug Fixes

* resolve Gitleaks issues by addressing detected sensitive data and exposed secrets ([ba0c8df](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ba0c8df26318181124ab4120cbba3cdad5699c5d))


### Features

* modify contract operational status to "active" ([0b7793d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/0b7793deb77cfccdd36e90478651f8cfdce28389))
* modify contract operational status to "active" ([94f44eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/94f44ebea9613b558db81e5f7f7d1918953c6eec))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.5.0...1.5.1) (2024-10-27)


### Bug Fixes

* use stable repo in docker image ([1daca80](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/1daca8063fc8c188c17164f10317e5f80c7a654d))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.4.1...1.5.0) (2024-10-14)


### Features

* update the spec version for the poi ([fe54463](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/fe5446349d65515cefdd3f1b70c382ef95432bad))

## [1.4.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.4.0...1.4.1) (2024-10-01)


### Bug Fixes

* dummy commit to run semantic release ([531dd3a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/531dd3a9c6c535923d4f00ae28e37547c8d0a6ff))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.7...1.4.0) (2024-09-04)


### Bug Fixes

* accept only ProductOrderItem with a ProductSpecification in ProductOrderAttributeValueChangeEvent ([2cb6432](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/2cb64322f4e9c66b50d97548b4af3cd5d22286e2))
* dummpy commit to run semantic release ([9dfcac5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9dfcac52096b2d7e4c83bdfe6705da36feb958f1))
* fix infinite retry for CPIB failure ([7660d21](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7660d212890d9599ad0de3db70023655d1548341))
* fix wrong service name ([6872b06](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/6872b0646ec2b9aa61bbcc58d669e8d553838334))
* increase cpu limit ([4d83df1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4d83df1a322f20c07a20d6705ae3bbe7ab7f23b4))
* increase cpu limit as i received a lot of email for high cpu ([658fa28](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/658fa28fc475e22fdac5cafc22a5849a13934e82))
* increase memory limit for mongo ([f691048](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f691048d6669a1fb82abb88b709246b6a5078f9a))
* move innovation job from staging to production stage ([43c286a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/43c286a91ea9c910b004e709fe68b52349d13b64))
* remove cert from source code and add it as variable in gitlab ci-cd ([5acb722](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5acb7224b07dcee0a2ef051dc389f8d1fbdd1577))
* sync pipeline with guideline ([4853cd7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4853cd716897e9630e5db0f7a0b823d726af4ea5))
* update Kafka event types in configuration ([3105205](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/31052055dfcd77bd00f69faaffb818ea4dac9dd6))


### Features

* update event type and topic for RBC integration ([063766d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/063766d77cc853c7e1e803e14d682a1f2a27c1bb))
* update product inventory specification version ([012285e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/012285efaff5bf699f2217840d813627ed1c629e))

## [1.3.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.6...1.3.7) (2024-07-16)


### Bug Fixes

* move innovation job from staging to production stage ([17e11ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/17e11ed653ab3eeb7cf7c7494eeda766bdde3bed))
* remove cert from source code and add it as variable in gitlab ci-cd ([3cd720a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/3cd720a784d5870eaa5cd0b118f8e960ca16a629))

## [1.3.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.5...1.3.6) (2024-07-09)


### Bug Fixes

* increase cpu limit as i received a lot of email for high cpu ([844c4b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/844c4b0701131ad6e766fbc43ae024c4d46d1f99))

## [1.3.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.4...1.3.5) (2024-07-01)


### Bug Fixes

* increase cpu limit ([b916e43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b916e43e1961027a9764401be609886b49b24dd4))

## [1.3.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.3...1.3.4) (2024-06-26)


### Bug Fixes

* increase memory limit for mongo ([4c5830e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4c5830e6b1d85ad2ff3a56f1ec448ff2513751ef))

## [1.3.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.2...1.3.3) (2024-06-13)


### Bug Fixes

* fix wrong service name ([d6768e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d6768e0a77c4748d47515473e2f6ed573cecaac2))

## [1.3.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.1...1.3.2) (2024-06-12)


### Bug Fixes

* dummpy commit to run semantic release ([3906dca](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/3906dcaee1fdb9544131e5c3cde4c19b917a8453))

## [1.3.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.3.0...1.3.1) (2024-06-12)


### Bug Fixes

* sync pipeline with guideline ([9149c2d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9149c2da1bb6874307737fa8e25f6b8ab51a869a))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.2.1...1.3.0) (2024-06-07)


### Bug Fixes

* address the issues related to sonar. ([92029b3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/92029b39c089a8e5801301385630a32e921044bb))
* fix YAML parse error in deployment.yaml ([2ee58ba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/2ee58ba56f22a5474a7e7bf315634667dc10598b))
* refactor code and update unit tests ([db71400](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/db7140067acaaca44341cdb19ee286b203dca4e1))
* rename KEYCLOAK_ISSUER_URI to KEYCLOAK_URL in deployment.yaml ([ff8f8fb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ff8f8fbb5cf675d20f02ce0987553a1c6f198065))
* revert unintended code reformatting in YAML file ([564c961](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/564c96103683e32181b67b43a3c2535485ee22e3))


### Features

* upgrade process flow library and specs in order-followup ([675d148](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/675d1481c671c5f79ed1d562830c3b92d229b457))

## [1.2.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.2.0...1.2.1) (2024-04-29)


### Bug Fixes

* add job to deploy mock-server on innovation environment ([5806702](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5806702bbf9de454e212dfee269933f6d92f5e54))
* removed semantic image and move it to gitlab variables ([87c776b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/87c776b59be2468ee39f783b14e8427c7432049e))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.1.1...1.2.0) (2024-04-07)


### Bug Fixes

* **CI:** resest semrel and make all helm deploy manual ([daab235](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/daab235c53e8ff685987a447e9726d056ada79b9))
* **devops:** Remove `develop` branch from `.releaserc` ([d81d99d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d81d99de348d3c680548a3164fa58b27a53de013))
* empty variable expansion issue ([dcf21e4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/dcf21e4424170fcad46eefd8e8b74a530e4a554e))
* fix checkstyle violation ([f7faf02](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f7faf02a3e6e0abf402ca40a83cbbb23b64aeab6))


### Features

* add kafka tracing config ([775b3e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/775b3e30fdd8fc92bc2f60986913418c65a4bafe))
* Update CPIB spec version ([8293e43](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/8293e437e82362bccaed32996e66952509236c0c))

## [1.1.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.1.0...1.1.1) (2024-2-20)


### Bug Fixes

* **CI:** resest semrel and make all helm deploy manual ([2348d49](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/2348d49de9f56506fecfcc5123498fc1fb62f99b))

## [1.1.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.1.0...1.1.1) (2024-2-20)


### Bug Fixes

* **CI:** resest semrel and make all helm deploy manual ([2348d49](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/2348d49de9f56506fecfcc5123498fc1fb62f99b))

# 1.0.0 (2024-1-11)


### Bug Fixes

* **CI:** resest semrel and make all helm deploy manual ([2348d49](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/2348d49de9f56506fecfcc5123498fc1fb62f99b))
* enable debug mode ([20209e2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/20209e25ac88df40ea63148baa2c6412d93adf68))
* Exclude 'ProductManagementServiceImpl' from Sonar coverage ([20ab266](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/20ab26699c851ee2cc55527b5f4a0b846b26aede))
* Fix config ([d319efd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d319efd4ec3356c5ed9bd26ef9534aae6506f696))
* fix integration with CPIB ([d6757eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d6757ebbbc9eb04262ebc38144b20c2e5155f077))
* Fix RestTemplate config ([0823105](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/0823105d8686066995a4d28d6c214365b97c2264))
* Fix Sonar issues ([fc450da](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/fc450da4be6ce47f3c41f94c710366c29639803c))
* fix swagger issue ([9fadabf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9fadabf9dd9331d868257df880ed735f62db989e))
* Resolve Kafka config in yml file ([4168bba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4168bba680598ba75730d25c58e195bc6a592939))
* Resolve merge conflicts ([ba91128](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ba91128069d30333454c4d3acc2c7b35a458fcb6))
* Update Node and commons dependencies to the latest versions ([7631343](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/76313436098019c0cb6f31686ea0df87e1522d3b))


### Features

* -keycloak integration ([5158772](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5158772de9fee2e23cb5da96f35ec990795de9d5))
* add async api ([9f09cc7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9f09cc70c8716ed60e3fd9cd5964cd0f18a99bf1))
* Add CPIB spec dependency ([bfe2ede](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/bfe2ededac33c02bb2958e2601aed0b968045dc6))
* Add JaCoCo plugin ([5def462](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5def462b49a5bbba467303c776fc9493b53b8e38))
* add logging configuration ([f8901ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f8901ed4d1ac902dda90fb401acbbbef7c9fc468))
* add test container ([06ec1d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/06ec1d85db201c0e9dcab8193bdfd42da3204e31))
* add test container ([a66586a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/a66586a8fc4a277e44d25642ec4cf25cc797378e))
* Add user task choose operation on contract ([cdd187e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/cdd187e6b6b4f996a86c9ff697182f96fb6dba4f))
* enhance code ([b055b5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/b055b5dcdb56a278af3f5bc3ebf636041945757a))
* Enhance PATCH method for updating products in CPIB ([52d6654](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/52d6654f1f002e99e67851180edc305f77bf28b0))
* Enhance PATCH method for updating products in CPIB ([db615f5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/db615f548b7df7ffecc1a01327be3466d1e44539))
* fix keycloak config ([805476c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/805476c3536a768ebae7143731bbd59b6c6da220))
* fix mvn build ([7c7210e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7c7210eb0012d9fb23bfe33b48cd548cacf2a66c))
* fix sonar issue ([872e1f6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/872e1f66f2cc56bb4eb39aff693578a6a710a954))
* Implement business logic to update CPIB ([7be373f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7be373f05b830f855c21e558ede537eedb187c64))
* implement order follow-up process ([fe81552](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/fe815524502dd7ca14cd47b21f0cf913af331b6b))
* implement order follow-up process ([6bab3bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/6bab3bb2d81f2e307a3680c6d6201fee926b6d9d))
* implement order follow-up process ([ecdca72](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ecdca729f6af2c67c9aab5fe2cee8cd2065f9566))
* Increase timeout ([d0f0e1c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d0f0e1c10b324e0578245c790b37dc0319dcd713))
* Integrate new process flow requirement ([930f561](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/930f561674fd4b7f537c6f72bfdecb1c93f4068f))
* Integrate update CPIB ([4a57b0d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4a57b0da3a50ede8f12a5caf2c099a5d6058ede8))
* Integration with CPIB ([1707af4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/1707af4e7616f552971072384b60c424e0d088e8))
* Update code and imports with commons new version ([a0041ef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/a0041ef5b338769bb39e635b91026ce58c88e7c5))
* update deployement configuration ([d12bfb5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d12bfb51bb96f12829fe35140e5e5d7fe604a5f8))
* update deployement configuration ([a6a7c59](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/a6a7c59753817abcef592aaa43ac54343e1caa34))
* update deployement configuration ([04544b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/04544b9a3ade1463953537b810e0bb227e117f61))
* update_CPIB endpoint ([fdbe798](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/fdbe7989bc791eee1c08ea5ce604faedf3c7b019))
* Upgrade order-followup to SpringBoot 3.1 and Java 17 ([f79939b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f79939b0202cd32c1d2e43bb68ab0e4a1565fec6))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/compare/1.0.0...1.1.0) (2024-1-3)


### Bug Fixes

* Fix config ([d319efd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d319efd4ec3356c5ed9bd26ef9534aae6506f696))
* Fix RestTemplate config ([0823105](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/0823105d8686066995a4d28d6c214365b97c2264))
* fix swagger issue ([9fadabf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9fadabf9dd9331d868257df880ed735f62db989e))
* Resolve Kafka config in yml file ([4168bba](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/4168bba680598ba75730d25c58e195bc6a592939))
* Resolve merge conflicts ([ba91128](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/ba91128069d30333454c4d3acc2c7b35a458fcb6))
* Update Node and commons dependencies to the latest versions ([7631343](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/76313436098019c0cb6f31686ea0df87e1522d3b))


### Features

* -keycloak integration ([5158772](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5158772de9fee2e23cb5da96f35ec990795de9d5))
* add async api ([9f09cc7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/9f09cc70c8716ed60e3fd9cd5964cd0f18a99bf1))
* Add CPIB spec dependency ([bfe2ede](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/bfe2ededac33c02bb2958e2601aed0b968045dc6))
* Add JaCoCo plugin ([5def462](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/5def462b49a5bbba467303c776fc9493b53b8e38))
* add test container ([06ec1d8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/06ec1d85db201c0e9dcab8193bdfd42da3204e31))
* add test container ([a66586a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/a66586a8fc4a277e44d25642ec4cf25cc797378e))
* Enhance PATCH method for updating products in CPIB ([52d6654](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/52d6654f1f002e99e67851180edc305f77bf28b0))
* Enhance PATCH method for updating products in CPIB ([db615f5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/db615f548b7df7ffecc1a01327be3466d1e44539))
* fix keycloak config ([805476c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/805476c3536a768ebae7143731bbd59b6c6da220))
* fix mvn build ([7c7210e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/7c7210eb0012d9fb23bfe33b48cd548cacf2a66c))
* fix sonar issue ([872e1f6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/872e1f66f2cc56bb4eb39aff693578a6a710a954))
* Increase timeout ([d0f0e1c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/d0f0e1c10b324e0578245c790b37dc0319dcd713))
* Update code and imports with commons new version ([a0041ef](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/a0041ef5b338769bb39e635b91026ce58c88e7c5))
* update_CPIB endpoint ([fdbe798](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/fdbe7989bc791eee1c08ea5ce604faedf3c7b019))
* Upgrade order-followup to SpringBoot 3.1 and Java 17 ([f79939b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/order-followup/commit/f79939b0202cd32c1d2e43bb68ab0e4a1565fec6))

# 1.0.0 (2023-10-29)


### Bug Fixes

* enable debug mode ([20209e2](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/20209e25ac88df40ea63148baa2c6412d93adf68))
* Exclude 'ProductManagementServiceImpl' from Sonar coverage ([20ab266](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/20ab26699c851ee2cc55527b5f4a0b846b26aede))
* fix integration with CPIB ([d6757eb](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/d6757ebbbc9eb04262ebc38144b20c2e5155f077))
* Fix Sonar issues ([fc450da](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/fc450da4be6ce47f3c41f94c710366c29639803c))
* fix swagger issue ([9fadabf](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/9fadabf9dd9331d868257df880ed735f62db989e))
* Update Node and commons dependencies to the latest versions ([7631343](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/76313436098019c0cb6f31686ea0df87e1522d3b))


### Features

* add logging configuration ([f8901ed](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/f8901ed4d1ac902dda90fb401acbbbef7c9fc468))
* add test container ([06ec1d8](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/06ec1d85db201c0e9dcab8193bdfd42da3204e31))
* add test container ([a66586a](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/a66586a8fc4a277e44d25642ec4cf25cc797378e))
* Add user task choose operation on contract ([cdd187e](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/cdd187e6b6b4f996a86c9ff697182f96fb6dba4f))
* enhance code ([b055b5d](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/b055b5dcdb56a278af3f5bc3ebf636041945757a))
* Enhance PATCH method for updating products in CPIB ([52d6654](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/52d6654f1f002e99e67851180edc305f77bf28b0))
* Enhance PATCH method for updating products in CPIB ([db615f5](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/db615f548b7df7ffecc1a01327be3466d1e44539))
* fix mvn build ([7c7210e](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/7c7210eb0012d9fb23bfe33b48cd548cacf2a66c))
* fix sonar issue ([872e1f6](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/872e1f66f2cc56bb4eb39aff693578a6a710a954))
* Implement business logic to update CPIB ([7be373f](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/7be373f05b830f855c21e558ede537eedb187c64))
* implement order follow-up process ([fe81552](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/fe815524502dd7ca14cd47b21f0cf913af331b6b))
* implement order follow-up process ([6bab3bb](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/6bab3bb2d81f2e307a3680c6d6201fee926b6d9d))
* implement order follow-up process ([ecdca72](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/ecdca729f6af2c67c9aab5fe2cee8cd2065f9566))
* Increase timeout ([d0f0e1c](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/d0f0e1c10b324e0578245c790b37dc0319dcd713))
* Integrate new process flow requirement ([930f561](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/930f561674fd4b7f537c6f72bfdecb1c93f4068f))
* Integrate update CPIB ([4a57b0d](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/4a57b0da3a50ede8f12a5caf2c099a5d6058ede8))
* Integration with CPIB ([1707af4](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/1707af4e7616f552971072384b60c424e0d088e8))
* update deployement configuration ([d12bfb5](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/d12bfb51bb96f12829fe35140e5e5d7fe604a5f8))
* update deployement configuration ([a6a7c59](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/a6a7c59753817abcef592aaa43ac54343e1caa34))
* update deployement configuration ([04544b9](https://gitlab.tech.orange/disco/disco-order-management/order-followup/commit/04544b9a3ade1463953537b810e0bb227e117f61))
