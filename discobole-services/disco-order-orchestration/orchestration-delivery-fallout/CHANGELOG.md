## [2.0.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/compare/2.0.2...2.0.3) (2026-04-23)


### Bug Fixes

* add missing mongo variable that we use it with default value ([e9168d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/e9168d3cbac9458ae23a661f54e32df35779a0e6))
* fix gitleaks issue as it is [secure] positive ([2adbe9a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/2adbe9a46a8c51a96c8112116703975bc7a9b2f9))

## [2.0.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/compare/2.0.1...2.0.2) (2026-04-23)


### Bug Fixes

* add global exception handler ([d371f73](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/d371f73529de492ca6ee7c62896a2f2fd7f7c894))
* **helm:** set appVersion to the current DISCOBOLE release [ci skip] ([65898ff](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/65898ff3d5fd866cd9b8b2588f3773e36722f202))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([23b79b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/23b79b67d84c4d10bc36f3ae811c698ec5daebed))

## [2.0.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/compare/2.0.0...2.0.1) (2026-02-10)


### Bug Fixes

* authorization forbiden response on roles with spaces ([db28afd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/db28afdc07a9470368a4e900dfbd711ba4d48a86))

# [2.0.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/compare/1.1.0...2.0.0) (2025-11-13)


### Bug Fixes

* **987:** update process flow version for security V1 ([53283b6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/53283b67d83dd0c2ae006dd8f211101e1ac4af88))
* **988:** user roles permissions ([f19e2e1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/f19e2e1ef750258f1acad3e29d2f9f06d2501f92))
* add third party file ([36ac71a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/36ac71abbcaea08c464c74b9e45d320cb004ec3c))
* copy values files to helm folder ([3b53dfa](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/3b53dfa729670e7a651088a17110969e994ae7ab))
* enable metrics in mongo database and remove un-used config for istio and promtail ([5532b81](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/5532b810109582ed30870399e79e7ec4befe32c4))
* fallout spec version ([7164e95](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/7164e952a6734878cc604cf157da135f53d4a627))
* fallout swagger endpoint ([72a549f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/72a549f7bd62063df5a15ee2816ceb6ab70b9684))
* fix copy jar ([63bb98d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/63bb98d3ce4d80972ecf45a13bd617566c371565))
* fix gitleaks issue in helm values files ([10610fc](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/10610fc1a9a07d9bfbe83a36847d400bf274735e))
* **IPCEISCOOD-1009:** generate process flow swagger file ([2a46d20](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/2a46d202bf79ab9794416fb786df661fd2dec424))
* **IPCEISCOOD-1011:** Fix Fallout MongoDB metrics not exported due to misconfigured MongoClient ([e251818](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/e251818c0c664859c8eaa0baee92cc33b1e68d86))
* **IPCEISCOOD-870:** [Open source] fallout mvn deploy snapshot doesn't publish packages ([c89c0e2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/c89c0e245c95a851a5c57261876569541fbd70bc))
* regenerate third party ([821fd7d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/821fd7d26151b413bfcec68f5b1dbb86f5d07360))


### Features

* **open-srouce:** refactor group id ([3c18142](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/3c18142ac6ab7d414d78b099cd1f827a3b1676f5))
* **procees-flow:** Upgrade process flow verion to 2.0.0 ([77a322a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/77a322a0b882cb4e7c7cb26615a78aaa6710a667))


### BREAKING CHANGES

* **open-srouce:** The authentication method now uses OAuth2 instead of JWT.

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/compare/1.0.1...1.1.0) (2025-07-16)


### Bug Fixes

* add docker config in common values ([12be577](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/12be5774cf32457c0a195bf8e308080fe2bf7ea8))
* add snapshot to 1.0.1-SNAPSHOT ([165a87b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/165a87b968248713d7f052e6efeb2d9c44006418))
* apply pascal format to md file ([53146c8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/53146c84a5dea31a00b46f73a1836f8be5df18c6))
* fix bump files permission and encoding ([0b9bd72](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/0b9bd726127b7c84a88d68a485035e63de7bda6d))
* fix DEFECTDOJO sonar variables as job is failed ([edb999d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/edb999d68005c690fe47f471a6a1bcc304a670ba))
* fix pipeline variables ([4eaf828](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/4eaf828b2d535bf2ba6a7dc74ea5811f9b725496))
* increase memory limit for mongo ([152fa7f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/152fa7f7f73b4ab94127d1ca1e9681505f4eb0d5))
* innovation configuration for others services ([fab00ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/fab00eda3a4f68071069097ec91635cd8d2003d7))
* move innovation job from staging to production stage ([5a26b39](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/5a26b39a5a88ceb9ac95543ce96bfa154040363a))
* Move node to DLT and fire Create fallout in all DLT consumer- fallout instance was not created ([8aec94f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/8aec94f4fa4999fd4cd11a5f175a96b402209349))
* release api 1.0.1 ([e24782d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/e24782d0c7df7188f35b301058799c0118962de7))
* release api 1.0.1 ([7c8af28](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/7c8af28d325f86101b8ee102b237b5482a522785))
* release sp2 apis ([e8999e7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/e8999e756086e2054c48f9ca12b93ad3828ec46c))
* remove cert from source code and add it as variable in gitlab ci-cd ([078ea48](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/078ea48b1e548b2009d79b8dc2e047c3f2a19a72))
* remove issue of uploading jar to artificatory ([604124b](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/604124b4caf1fb2093f092cc5ecbc120055b5a1b))
* remove un-necessary variables ([f84e06f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/f84e06f13d255457e92231630d80ab02c6594272))
* set sonar prject name as its name is: project.name ([791c5bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/791c5bb1ccfb24e66a1d9ff61836e8bb859e3463))
* skip classes from tests ([fb8e399](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/fb8e399cc900dcc523a5870d0ab1f2d75fa46fe0))
* update readme title ([11b6c53](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/11b6c53ae7652ed5927940ee067583090c0ff281))
* upgrade process flow version, change in getting-started.md and compliance pdf report ([9ad30b4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/9ad30b466051f45b21a6e382cbbdac07b6660847))


### Features

* **IPCEISCOOD-889:** Migrate to G1GC and Update JVM Parameters ([d925345](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/d9253459441ba9998d619db923edbd1fa3c529ec))
* rebasing develop ([7b8e492](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/7b8e492fd3c92f3e7d565831f3cea042e1bc20d3))

<!--
SPDX-FileCopyrightText: 2025 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

## [1.0.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/compare/1.0.0...1.0.1) (2024-06-12)


### Bug Fixes

* add docker config in common values ([bca44bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/bca44bd0205cecdf3a65afacf037ff15f017e2d9))

# 1.0.0 (2024-06-12)


### Bug Fixes

* add presist fallout before user actions ([bfe72ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/bfe72abe8065c8f2aed514ed12767531d84c7d3d))
* add presist fallout before user actions ([2855363](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/285536388954d9bdf22bfd704c9c52b9cf059796))
* change servicename in logback files ([44e806a](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/44e806a7e8c0c1067a5b31d22afdc89fee60c493))
* fix bump file permission ([98f8c25](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/98f8c25091e9f9b3f42d989c60d2e3125407005f))
* fix in held action state, use source instead as... ([96d29b3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/96d29b3deaeae88910f044e0f12127ad5c54a6b9))
* fix pipeline variables ([670bd98](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/670bd989055283050a77e148e114b6062203dfcb))
* **IPCEISCOOD-378:** REST apis, Offset is not as expected, it send wrong value ([5354132](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/5354132a7dc6ded9b7cb9cc47fd3142d36fc8b1d))
* **master:** fix conflicts ([177d1e9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/177d1e9bfa8fbc8e3c8762876d3adc3c248a1138))
* revert actuator port ([a978fa1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/a978fa1b7e25a3dafc6c834edf8dfdbd44267ca6))
* revert code changes ([3e46697](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/3e46697fbffe939fb9c8234b62d33cd08b2869d1))
* revert code changes ([3ef2296](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/3ef22967f37719188e332df97ad97d93427c3ea9))
* set project name in pom file to fix sonar project name ([d248e1d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/d248e1db60e265bc765d0810f26ef76626993ac9))
* update readme title ([23ff80e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/23ff80e8b7069878c7e1f074e5f07eeebc9166a3))


### Features

* add actuator ([b433e24](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/b433e2425d09c2339000d8f97700a9ed7895fe37))
* add missing classes and configs to process flow ([4002a79](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/4002a79cde18b25d14184f5456d9fae22b7f47db))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([aed502f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/aed502f831d9ee12202ac972765acf2b96099da4))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([7129bbe](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/7129bbe9ee48eab55b927e5b871d423928e04239))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([af838ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/af838ec4b6f31b16cfd15a24b3781eebf8e219aa))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([df9637d](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/df9637d68310408541839eb89fae33a7ac9a3d51))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([1c8e8d9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/1c8e8d9f4d718b0b37eafcfb3b4decdc877d9769))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([0cb2eee](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/0cb2eeee443ea83546b440c95f4169cd00bb23c6))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([0d677a5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/0d677a509afd41afa7c6da9d9e60f27665bb2aa5))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([f68dbae](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/f68dbaeef2670db83f9ca623eac6b82f77a57b0e))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([7c003bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/7c003bf92819c067ceff3b173bec60c415a3853f))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([224d852](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/224d852ecb1462bc88c2e9ac7bf6621e76aad8c2))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([8aa0ad3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/8aa0ad316f2b087db37a1bedfd2f7ccda75a14e1))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([6238a92](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/6238a92dffa63b1d6870b8b919d3bdd6d855a4a5))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([3222386](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/3222386322ea36897f6ef3e17afc15eae3d5e5b8))
* **IPCEISCOOD-304-332:** Persist Fallout Entity - To Track Fallout Status ([3b13b42](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/3b13b42aeeaa70ea25daadb0a4b5314b9870d343))
* **IPCEISCOOD-304:** Close Fallout Incident ([84f9104](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/84f91043b2f1e9725a5324199dd653cdcdb9f176))
* **IPCEISCOOD-304:** Close Fallout Incident ([2f16af7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/2f16af762fd26ea3f9a1b7959eb7ba87ef2ae34a))
* **IPCEISCOOD-332:** Persist Fallout Entity - To Track Fallout Status ([fe9179e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/fe9179eadeeb84643d873bc1b66b7883559fcbe3))
* Persist Fallout Entity - To Track Fallout Status ([ffbeeb3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/ffbeeb3332610a616ac1e3a5e58aed2e10702dbd)), closes [#IPCEISCOOD-332](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/issues/IPCEISCOOD-332)
* Persist Fallout Entity - To Track Fallout Status ([b52b986](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/commit/b52b986df001e173a6e63c6c1710aca5e7e47008)), closes [#IPCEISCOOD-332](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-orchestration/orchestration-delivery-fallout/issues/IPCEISCOOD-332)
