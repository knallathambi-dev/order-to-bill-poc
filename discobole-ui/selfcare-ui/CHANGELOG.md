# [2.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/2.1.0...2.2.0) (2026-04-27)


### Bug Fixes

* add hadolint ignore for SC2016 single quotes in envsubst ([d171499](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d171499a9deb327be818c392b59eb04ba8f2abac))
* add nginx dependency on init script to resolve 503 startup race ([d683170](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d683170767134124f55e2c55666f4e14ad31b741))
* add pipefail shell option and shellcheck disable for envsubst ([9efdd87](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9efdd874bf0a8e5d6b7007a4c264a8a35b6559cb))
* **auth:** resolve token refresh flow and authentication state synchronization ([d89a996](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d89a996f1a08886249a74b2539e701b2ca893eee))
* **build:** update APK cache to resolve nginx module conflict ([8805975](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8805975572a72383735afcdcb2d681021ed83a17))
* **ConfigItemGroup:** keep SingleItem accordion always expanded ([c684810](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c684810b858a282e2bdc693de48516c589d3ccfd))
* configure axios with corporate proxy support and standardize currency formatting ([0de836f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0de836f1bf34901d7b2333d6b0be86e2c7f63fe4))
* disable TLS strict validation to support internal self-signed certificates ([2c58147](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2c58147c1741d08a029c0732ba9d00763988289f))
* display discount value excluding tax ([f76aa45](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f76aa450a611d49c03f5045becc0e16360808b68))
* **docker:** remove nginx-mod-http-headers-more and fix s6-overlay architecture detection ([c652134](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c652134d8d5f8afdc906c5f07a1530fb84562ae2))
* **docker:** use nginx:1.24.0-alpine to resolve module conflicts ([512e017](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/512e01770fc06a4e6c28fb4d9a726b6fee56cbfa))
* **eligibility:** load existing configuration for migration flow ([0f23128](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0f2312873c6ebb992f3bec0d4e71582b821bfaa6))
* empty screen when purchasing offers requiring eligibility check ([f1d11ab](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f1d11ab1f2e48e39e2162df19c5b6353ce29c0b9))
* enable cancel order in Migration and Modification ([fc4674f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/fc4674fd086d17b5b1a6ca91beea82cb8e84d421))
* ensure consistent billingAccountId across acquisition and modification flows ([2bf922c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2bf922c1efe14a1611e2adf65df4c1febbdf12c9))
* fix gitleaks issue ([8d98f5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8d98f5c180c27512c67dceeb427ac02b4228d2b0))
* fix nginx security issue ([254f28e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/254f28e0cc0d8085459a5b93205a718c66130e5f))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([3823f55](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3823f551f43df93626499a0ccea88f15cd7c8194))
* **helm:** set version based on latest commits, set appVersion to the current DISCOBOLE release ([0d616b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0d616b0ca06a2bcfb9db837efec5309c0cd08995))
* **helm:** upgrade version only for the chart's version [ci skip] ([05513b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/05513b54328793214575796f1ddb460277b85f62))
* hide nginx server identity by removing Server header entirely ([4a656d2](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/4a656d21b7f39d506eec57423ca5ff0d2df8712f))
* hide reload button tooltip on click to prevent it from staying visible ([9538ae8](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9538ae807089164ef078633ed6b2d07ec82be5bd))
* include non-visible items in shipping configuration processing ([cb48c5c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/cb48c5cd99a8f31a1fcbbe8bc4d799af312dae8b))
* **nginx:** harden security and switch to Alpine 3.19 ([3d15d8f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3d15d8f1cb772f59c4c4418240838b5c845cce2b))
* partner change issue click ([aee6ab9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/aee6ab9743242235a0c6aa737c2d03f4d22e0d05))
* pass missing onConfigurationChange to addFirstSelectableItem and keep multi-purchase accordions always open ([a983974](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a983974723ec408d1ac69c83fe77b7b76f2429f1))
* pre-create nginx temp dirs and use 777 for OpenShift arbitrary UIDs ([2f51c73](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2f51c73e176e3330eafbddbc477d316b172d3196))
* **PricingDisplay:** replace taxExcluded label with taxIncluded in InstallmentPaymentTabs ([f1dc558](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f1dc5585278d53dde5ea74a2fb9bb14cbffa805e))
* replace MemoryStore with file-based sessions and silence s6-overlay root warning ([c4687f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c4687f4217389fbc4f649ab3347e88f72e2cf1b7))
* resolve GitLeaks issues ([9c57d48](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9c57d48c11a3211fe1d50e33908cb938d4a2cd3f))
* resolve GitLeaks issues ([246a275](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/246a27523a929eb20168e259f56c34508fb8725e))
* resolve GitLeaks issues ([5840212](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/584021228c52d5f1a230d58acc4f02e931a1e449))
* resolve GitLeaks issues ([7ee6a26](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/7ee6a26df949cbd77a87db33d83f3f287533b1c5))
* resolve undefined variable errors in Plan components ([9df3027](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9df3027edb41fcba16c354538059fe7f44c3908f))
* **security:** implement BFF pattern to mitigate V11 account takeover and token reuse vulnerabilities ([81da334](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/81da334c727708752163f7d0a79dcf2340f60b3a))
* **security:** sanitize API responses to prevent internal infrastructure disclosure (v5) ([6f01245](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/6f0124545ecbd826dfde9a5f54747e07e9048855))
* set 777 on sessions directory for file-store write access ([13b2d8f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/13b2d8f0f3804bda72b33812de5c3ff262ea9e33))
* set project version based on latest commits ([3af016a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3af016a05035537fde49b55fd65ee826eea1ab31))
* split config loading effects to prevent infinite getDefaultProductConfiguration loop on acquisition ([e860a1a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/e860a1a8c2c91e5b538eec26910767dd243dee63))
* treat non-empty description in task flow responses as error ([6643788](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/66437884af52fe9202d4820e27b65030db685a2b))


### Features

* display applicationOffset in plan planPrivew ([e81e989](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/e81e9890996875acda9821a3a8d4fc2e95f1151a))
* filter homepage offers by allowedAction instead of channelRef ([cd665f0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/cd665f051e645dcd9754a63c1065c3e0422e2276))
* **helm:** add SESSION_SECRET support and refactor chart configuration ([0d4489c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0d4489c2fe4177abb941c0b9d46d94ed06fd3b49))
* **i18n:** capitalize first letter in applicationOffset translations ([b91b8dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b91b8ddc40d1d7d9066a8e1db60f9dcc411a49e0))
* rename relationship requires for shipping ([a155806](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a155806e0a1b422a85688b03e527fd53eb8b3088))

# [2.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/2.0.1...2.1.0) (2025-12-23)


### Features

* upgrade package-lock.json ([c34f880](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c34f88030122ccdb2fc1724b4189c657da0b42b2))

## [2.0.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/2.0.0...2.0.1) (2025-12-02)


### Bug Fixes

* add missing id parameter in countSelectedItemsByOfferingName call ([784d160](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/784d160eefa146570d67b3b0bed0b870275df5a1))
* add roundAmount and apply rounding to all price calculations ([dcb072f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/dcb072f1c3dfe1ac2e5ee9e409d3a529049fbb31))
* add tokenEncryption.js generic-api-key to .gitleaksignore [ci skip] ([55f6b17](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/55f6b171aa9990bd9c20f920cd22dc8b18e0cc48))
* align checkbox to right in accordion nested bundles ([0ffadd6](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0ffadd6e31f3948faa2e3fba7ebfefb12fe123e4))
* **auth,csrf:** stop CSRF 403 loop and stabilize cookies ([95f9fe5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/95f9fe5c8c9230866c5d406ac82e60bf4fd1c177))
* centralize error handling to show migration failure toast ([e665293](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/e6652932a466705a79cdb65f1123381c975291f8))
* correct action determination for address configuration ([f04e691](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f04e691610e883d935de62bbf76efa769a465c28))
* correct configuration action for characteristic modification ([443517d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/443517d2a9476dd55266ec68c511b79b97cf09e2))
* **nginx:** increase proxy buffer sizes for large JWT tokens ([ce57b84](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ce57b84f8379fe40baeb84644fca11d541b0750a))
* resolve missing checkboxes for unnamed color characters ([86f7ec1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/86f7ec178acd63de955912e9976532f776ff8965))
* scope selected item counting to parent bundle context ([59d2456](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/59d2456af8c680685b802cd94f86b301bf2d19e3))
* skip API request when no configuration items existRetry ([d6776ac](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d6776ac23df095cbcd88baa189af7282f2adc94f))
* sync package-lock.json files for server and client ([33bd053](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/33bd0531168207d895a496bb2ba019b0151b1cea))
* unify price presentation across multiple screens for clearer, more consistent amounts ([2debfd3](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2debfd379278500ba0cd716e24044bbaa1e069b2))
* update migration routing to check fiber/convergent category for both source and target plans ([951a18a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/951a18aeb3aff8f9d02b9c823fa8341ca24ffc71))

# [2.0.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.10.1...2.0.0) (2025-11-13)


* feat!: implemented express proxy to hide env secrets ([b00966c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b00966cb79b152b4ab7364d2328c6a5fc56c90bf))
* Merge branch 'fix/credentail-leak' into 'develop' ([9d5e2b5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9d5e2b5704f5a556d17e1cc6cc37c836f00c6fcf))


### Bug Fixes

* add gitleaks ignore for [secure] positive secrets ([ed7c333](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ed7c33360df47448625bcaf395dc62eb13d33908))
* **auth:** add form validation with error toast notifications ([f808e5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f808e5d3c47ddedc84a8561f5cea3e1ad153c584))
* breadcrumb multilanguage ([ea62594](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ea625944aaa2ac41e220aa29f000b3379ad5963e))
* breadcrumb multilanguage ([5742d83](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/5742d837eb663f9a958afe0fb2ad0beffbf39d13))
* change message description for non eligible address modal ([93533f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/93533f9705b81787b9dda30b131e84ec8efd3515))
* correct display of commitment term on setup plan screen ([0e7ccce](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0e7ccce04c41be0b0ba6dd30bcb0615790c5e413))
* DeviceSetUp & loading offers improve calls ([ce14a1a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ce14a1a65bba1912149d5d7e3b1a23451b6639d6))
* disable edit and change plan buttons during pending migration to prevent invalid operations ([106b7cb](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/106b7cbfe412b0b7c9c1228bd6a6cfca89c68b31))
* disable product charateristics for noChange items ([37a2dbf](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/37a2dbffbbce57e944294574b4d274ac7df14a06))
* disable product charateristics for noChange items only ([924b0a7](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/924b0a7626f910a3ebecd7e6ec93ec3e2b31c3c7))
* display the price on the commitment card ([b716688](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b71668830612a27ddb84981f1dba002babb9dd48))
* display translated title in plan preview ([1b2391e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/1b2391e22fadd8eb842aae0614f7e9e59d5b36dd))
* **edit-plan:** show bundles without children when terminate action is selectable ([4573828](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/4573828c36a2d46a5375cc4f47d86003d4147418))
* fix the breadcrumb display over migration scenario ([ddc473a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ddc473a4de6a20b2839116c3bdb95044f0633e4c))
* format total amount on complete order screen ([521b9a7](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/521b9a71373f22e3d4ab3c6e5a5ba02c4ec0a00d))
* improve authentication flow and API error handling ([542fba7](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/542fba7b5499eb3b49f1f1a12ea028522d1045a6))
* include tax in discount calculations ([12d9539](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/12d9539c519f9bf4f7c3963d910ef31cd1d22074))
* prevent OfferCard footer divider from disappearing at zoom levels ([f325b41](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f325b416a01ce6b28caf9cae2e1cd0a24ad90c6f))
* prevent past date selection in appointment booking ([3fe6b23](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3fe6b237fa1cc9f757a8ab864504c987831594f2))
* properly disable buttons during pending migration state ([0cdc4c4](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0cdc4c4822f0765e469ad61f7498ea5e88632c47))
* refactor useTitlePage hook for edit/terminate plan and add i18n support ([71ab97c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/71ab97c447489c5a73b882eb9914cb0adfefcad7))
* resolve gitleaks issues ([74429dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/74429dd60699c945983ba69f3e6dd3b625928101))
* scope security middleware and routers to /api prefix to resolve 404s ([e3406f9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/e3406f94384fcd56db4e60824aa6e0356bc3ee27))
* **security:** prevent IDOR vulnerability in selfcare user data access ([0e7b4fd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0e7b4fd7e45b0be9dc58cc0e25d67cdfcc60c473))
* **shipping:** prevent selection of past dates for delivery date ([28cf3a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/28cf3a632663edf328917c2f35193ec901289a86))
* sum tax amounts separately before adding to original price ([a7dab65](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a7dab658c5f8b21ea8e0700aee3030082a09a753))
* support both email and mobile number validation in EWallet form ([cd39a3c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/cd39a3ca26aa77ef0449c57122cfa1779e83fb7e))


### Features

* add commitment display in setUpPlan screen across all levels ([ac1f5e9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ac1f5e9b90a67351b7b449744965ba795679c667))
* add custom dateTime formatting and remove moment.js dependency ([2f12c47](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2f12c47c08d53e5ad0850cc15b469071f88ac448))
* add support for terminating bundles and displaying commitments on edit plan screen ([6b1e436](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/6b1e4363971c07e649af29f34701a3e115947a59))
* assign single billing account ID to all items ([c390598](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c3905987aa4035aedd1bea0878f4596dbdb283db))
* display commitment term in order summary ([93f42dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/93f42dd1d1b2446b0c324ef9817bebe33129e150))
* display price before discount ([4e00877](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/4e00877d2341fe9c395c4faba90e35f2f87fa8ba))
* display the "before discount" amounts with tax included ([a67647d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a67647d9ccae6613bd21eef2d0746becfafff420))
* format total values for cleaner output ([ad33492](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ad334928e25a71c68c63751a54b155aeb9363792))
* handle non-eligible fiber address ([3b25cdc](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3b25cdc5e1fdc85506e0f515ebbd207cbf747e75))
* hide bookAppointment screen when address is eligible ([b0e797d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b0e797d5dbffa68a3d1aef043138f72dfedd265a))
* implement dynamic currency display across all screens ([971e96b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/971e96b977e32c546849879664231ab43284b225))
* improve order summary to display address characteristics ([c39429b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c39429b6581934f9fdea5ebd8df518bc41e3fe79))
* integrate i18n translations across multiple components and pages ([745a204](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/745a204c493083a476e8add7e86c1ab7e23c81fd))
* introduce addressChar in setup and plan editing, allowing address updates and integration with Product Configurator ([2a06acd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2a06acd8718903df1885e723ce1d84628f2be124))
* introduce appointment booking step prior to payment and split order confirmation into two stages ([6dbd3c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/6dbd3c7c4c75b8af83151737ab15fffc29f6ea1a))
* support migration across all screens ([5ae6236](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/5ae623647d8376d8e14d8a43f1d7ca44c2ffd228))
* update product eligibility display to be dynamically sourced from catalog in migration scenario ([cfda91b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/cfda91b153d733a5f16e76893cfd41776fa191d9))


### BREAKING CHANGES

* API requests now route through express proxy server instead of direct client calls. Update client configuration to use new proxy endpoints.

See merge request disco/disco-oda-components/disco-ui-portals/selfcare-ui!328
* API requests now route through express proxy server instead of direct client calls. Update client configuration to use new proxy endpoints.

## [1.10.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.10.0...1.10.1) (2025-07-17)


### Bug Fixes

* correct contract ID in Configurator request during Basic offer termination ([20c09f3](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/20c09f3f1ed9d2b997e38cffd45b4f9062d0d100))

# [1.10.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.9.0...1.10.0) (2025-07-16)


### Bug Fixes

* add check for null or empty orderTotalPrice in calculation ([3942db5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3942db52576f4866a709f24fe246d607636015a0))
* address display issues on setup and edit plan screens by refining visibility logic handling ([93c6a79](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/93c6a7958e476458696f618939b67b87f1a5f3e3))
* correct calculation of priceDisplay for percentage alterations to ensure accurate adjustment amount ([8c592d1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8c592d1ef935ec2cf2f9a579c368f6501a96ba40))
* correct device image display issue ([ddae8a9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ddae8a9431430d107cc7dbbc4f10766e000313d4))
* correct shipping patch in product configurator for modification use case ([2a7d58a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2a7d58ac36aa3b4b744facb9e4e907e84bd404c8))
* corrected discount calculation for item to ensure accurate pricing ([fae1953](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/fae1953c84e39894ea112c10d5762e8153153fce))
* ensure beforeDiscount display renders correctly ([1836823](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/183682307ccb4181382ffa5f2ebbba07d6ff4f7d))
* ensure bundles with isVisible set to [secure] are not displayed with their children in setup and edit plan screens ([53e46ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/53e46eca11d0eb7709c0fc57196ad9c99ae52205))
* ensure delivery price updates when selecting "Home Delivery" ([eefa6eb](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/eefa6eb9b0d4daaff26d0668c2d9b5982d4c8fab))
* prevent bundle from displaying when isVisible is [secure] on edit plan screen ([046e8be](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/046e8be350522d0f531eb26343563450181e55eb))
* resolve order processing issues and correct request payloads for option deselection ([0e0e582](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0e0e582b8013f886acf55b4e90f3d53f38515adf))
* update prop types and ensure data consistency ([17acdae](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/17acdaea84caf0e0652c87be472836aace9bacc2))


### Features

* display application duration across relevant screens ([8b6b629](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8b6b629df914801787bef5d8ce2b7d7eb11b2e72))
* implement useTitlePage hook across multiple screens for consistent title management ([b4f5586](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b4f558635ed595ef84bc8b6bb0ca8c6db4e2914d))
* revamp Edit Plan screen ([499ff32](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/499ff324e082735321b85ad5344c8e02333bcabb))

<!--
SPDX-FileCopyrightText: 2025 - 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# [1.9.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.8.0...1.9.0) (2025-06-05)

### Features

* remove copyright from
  footer ([195ccfd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/195ccfdf462a0b43e8b0f5ea0906f09c403091df))

# [1.8.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.7.0...1.8.0) (2025-06-04)

### Bug Fixes

* group products by name and status in My Plans screen; group orders by productOffering name and state in My Orders
  screen ([9646416](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/964641670bb7d4a9f57244eceb876795f6bd2bec))

### Features

* update documentation, regenerate CONTRIBUTING.md and adjust deployment
  configurations ([c307483](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c3074832f5470463c8447dcf0adca21dd07ab115))

# [1.7.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.6.0...1.7.0) (2025-06-03)

### Bug Fixes

* add filtering for active offers/devices in integration and review
  environments ([f5eeb47](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f5eeb4741f5b37441b0b2f95cd8a1f534df03e37))
* adjust Gitleaks configuration to reduce [secure]
  positives ([f900fe4](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f900fe417a802cb21c1ec8bb7abeb6fa63a777df))
* correct item activation in multi-added bundle after deselection when default cardinality is
  correct ([2adc3ec](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2adc3eceab2a6b0a7db48d01e37c9f7e432a94f9))
* correct price display for multi-price items in my orders
  screen ([a4259bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a4259bf05ac09907608c5e13c257fac1d687ef6c))
* correct total calculation for recurring items by grouping charges with same period and including non-recurring
  charges ([01ab1d9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/01ab1d9443283edcad863b83d1c66c0949ca7dec))
* corrected getConfigurationProperty calls to use groupItem.configItem?.productConfiguration, ensuring proper access to
  configItem in NestedBundles
  component ([5a5a8a4](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/5a5a8a45f97761d60a40c78deb527bf9f4bb3d67))
* display new sub-bundles as optional instead of included in setup plan screen for multi-purchased
  sub-bundles ([3bb6836](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3bb6836e57312a085350d70266abda0457186b06))
* enable terminate button and handle RelatedParty error in Edit
  Plan ([e9691c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/e9691c5228ce8d5cd2e46bd1bc6dbf35125e2e78))
* enforce mutual exclusivity between conflicting options in tangible
  offers ([6f69838](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/6f698381e01b22349297e9940107cb09ec26cd7c))
* ensure filter includes only selected product configurations in includedBundles on Plan Preview set up plan
  screen ([a8591bd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a8591bdde4e2f58d7a40b7f6f8d87532fc83e003))
* ensure isSelected is correctly set to [secure] when canceling termination or modification actions in Edit
  Plan ([deb60f4](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/deb60f4672419f027514d72d1b3594dc347b9bcf))
* ensure multi-purchased items are displayed correctly in order
  summary ([269897c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/269897cd7364ba74490a3ce72182f7cf1e1049be))
* handle 'RelatedParty not supported' error in configurator for consistent
  modifications ([f8597cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f8597cfa2fc5858d41489844a78fbc24695546cc))
* handle null unitOfMeasure in characteristic display for Ring Back Tone on order summary
  page ([7b655f1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/7b655f18cb6d54db20ed5df3cfea895980f348b8))
* move PlanItem.css styles to
  index.css ([a83c8ad](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a83c8adf144da834d856bada476e403e1b921799))
* revert check
  partyMangement ([8f69eea](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8f69eeac78c87d080f5f12688d05c9e1bd3eceff))
* update createRequestBodyShipping to handle multiple shipping items with dynamic
  characteristics ([92d47f7](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/92d47f76db27abc5ac6f13afe539e73fd5d1f7a7))

### Features

* add support for filtering offers/devices with lifecycleStatus 'active' in integration and review
  environments ([634e137](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/634e137aa6c4c90e5c9f610e3df4d7889e48a2fd))
* check party management using oc in the shipping
  screen ([59e9a5f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/59e9a5f3abcb88b9054083e38e008985e3954885))
* display the products based on
  isCustomerVisible ([dad1f36](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/dad1f36b9d2b1d9748626d62f7c14f28e84735e4))
* enhance "Set up your plan" screen to support optional bundle selection and visual fading of unselected
  item/bundle ([d767a61](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d767a61ba55b3d5f7d27c644ea90e2eadb54eb86))
* group plan items by product offering and optimize order item
  presentation ([4bbb985](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/4bbb98503809051d45481f87fbfb945400032bd3))
* implement multi-purchase support for offers and
  bundles ([1f16d78](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/1f16d7844e8896df7b4da2cf992abf41359910f4))
* improve total amount calculations across multiple screens to include all applicable charges and
  discounts ([fc00a1e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/fc00a1e0712c9ff604b0fa1bc26170fb32824e6a))
* **opensource:** move to Open Source
  Sofware ([5d48853](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/5d48853a2b000cab39d7ebc22772515429957af3))
* upgrade keycloak-js dependency to version 26 for enhanced compatibility and
  features ([b24fb83](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b24fb83852b27cfab4368ce7bf1581f2f5b2f530))

# [1.6.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.5.1...1.6.0) (2025-03-05)

### Bug Fixes

* activate marker moovment in the
  map ([6d47489](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/6d474896615f6bb9da1ad12c39d7c5d0e3cc6711))
* ensure shipping-required accessory items are retrieved and displayed on shipping
  screen ([800f702](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/800f7021900a63fbf869b966d04d81d84f890d4a))
* fix ui after
  review ([6571afd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/6571afd2214fef32e4023f9063e0751ffe7959e6))
* guard shipping config item in setup plan screen order click
  handler ([d21785b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d21785b63a22ec73e0828e3e0a937090d4b57b7e))
* handle null checks and errors for configuration data in edit plan
  screen ([833155b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/833155b70997412866b1a30410546ff5581cd7e3))
* keep optional bundles always expanded in setup
  screen ([bf35838](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/bf358384ca886fea7d52e67f3274c9ae66f5cc64))
* properly reference productConfiguration.isSelected for optional
  sub-bundles ([590447e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/590447eeb7ef1d2507ebb453f7f42ab4ffd98495))
* resolve issue where subscribed options were not displayed due to incomplete action
  checks ([79e719d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/79e719df7cef7fe1008537d8dacccc3e9bac1be5))
* resolve state management bug in Options and Included components in edit plan
  screen ([305d268](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/305d268dcc3f85fe1ac8ad9ec98c07915dcf884b))
* sort bundles, direct items and nested sub-bundles alphabetically in setup plan
  screen ([9d032d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9d032d3d9e3bc7af178d63a9860b251e717789f4))
* update optional item accordion logic to prevent unintended
  selection ([9e78358](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9e78358d472dc6ec117a6eedb336188388accee4))
* update set up screen price
  display ([c797ecc](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c797ecc7f68d13fef85b2bbbcc4da84a16d74ccf))

### Features

* add support for sub-bundles in setup
  screen ([ef549d3](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ef549d30a3b87ca2be8b972065dbf860837c1b3e))
* add technical eligibility screen for fiber
  offers ([42548e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/42548e8586e42d05d7ff4296cb63f3b5f96c00a6))
* delay opening eligibility modal until after 2-second loading
  spinner ([174d161](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/174d161cd2c92ae12912e0a23b169da0f31d595f))

## [1.5.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.5.0...1.5.1) (2025-02-12)

### Bug Fixes

* display the current value when items are radio
  boxes ([44abf58](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/44abf58f7f3cc9fb5df54d2214575533ef5fe944))
* handle moreOptions in case of unothorized modification
  screen ([0973b38](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/0973b38e742691af0eef482675701cbfb2659db8))

# [1.5.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.4.0...1.5.0) (2025-02-10)

### Bug Fixes

* correct last step condition to check direct optional
  items ([55b5580](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/55b55804d9fd8971d127727d511fb255c8e63205))
* correct styling and selection logic for optional
  characteristics ([66431c9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/66431c990d7c1178bb74dbdabda30dd8a7db78a8))
* display missing "Next" button for optional
  items ([16a1453](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/16a145360403d513979357a3d28ddbf711f705d9))
* exclude shipping item from display in track order
  screen ([2911952](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2911952d67ebfe6c20612f2dcc52af754b152ef5))
* fix logo for devices
  display ([fdf3d7e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/fdf3d7e5367bee7683f4477d32c2362f0e85f17a))
* improve item sorting, display issues and UI
  consistency ([f4e94fa](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f4e94faca8edaa8a94592020f4196ced7da2a975))
* replace character values above 9999 with "Unlimited" in setup
  screen ([d19bf39](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d19bf3920ba1c2317e57645d4b10adcce3391b13))
* resolve logo path issue in index.html by moving
  image ([a6fb6dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a6fb6dd3faa0a724c3316b6dd3ac85c8464a8ad5))
* resolve regression in pricing and discount
  calculations ([8dcbd43](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8dcbd437fa11617f5a5474d94d80d672d7e66f82))

### Features

* add search input in home
  screen ([4bc0cf9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/4bc0cf96723c753dbed86b6ad34d83f9134d4003))
* display contract level in plan preview and prices for bundle and contract in setup
  screen ([cc736a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/cc736a276a8f8971832ec59a1eb2627ca161a335))
* manage caroussel for A55 device to handle multiple image
  views ([bb01dc5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/bb01dc59710584719e5ed82b8e53b2e1a5bf546b))
* support bundle level display in set up
  plan ([eed1249](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/eed1249de5fa4efba0fae43bc809babfd7a0d951))

# [1.4.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/compare/1.3.0...1.4.0) (2025-01-28)

### Bug Fixes

* adjust MP Relax offer display for updated configuration
  changes ([2487abd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2487abd9698e372eae21261e2f05f66a7aad5216))
* change display of discount in
  setUpPlan ([ce0926b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ce0926b093e6891f6bba5c7f859695ab8cbe340f))
* conditionally render optional items in edit plan and set up plan
  screens ([2308c8e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2308c8e93ea30751b0b023f498001ce616213343))
* correct relatedParty mapping to ensure id, name and role are properly
  set ([79ff465](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/79ff46575d18ea89971e2b35380f9f5e3b6272d6))
* display "Aborted" and "Terminated" states in My Orders and Track
  Order ([85219c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/85219c5fb1922854ff03d59a2951aa4d823a5cfc))
* display all the needed configuration characteristics per item on the plan preview in setup and edit
  plan ([3e4a409](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3e4a40930554481813f1241a2b8f3008e9ee4cda))
* display price only when product configuration is selected in set up
  screen ([91700b8](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/91700b8fcf0e06fc6448692eaac95154ce341691))
* display requested completion date in order summary
  screen ([823e8bf](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/823e8bfded9cfae497b587a3f5fc0068bfaee8cc))
* fix configuration shipping request
  body ([9eed203](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/9eed203fad101e27c44f9c2e87147b0837ec986a))
* fix display of current
  value ([7d92013](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/7d920135066ff459fbf0f55bc89a565845a8ba04))
* fix the review
  comments ([bf45a5d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/bf45a5d420bb12db05dffa6c105c43e4b37b1249))
* fix the ui after
  merge ([7ab23f8](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/7ab23f85721be273ee00bdab33d432e86acf8f06))
* hide cards without
  actions ([c7f94e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/c7f94e0b47254d7748f62f7954502897d24a091b))
* normalize units to lowercase for billable charge period
  formatting ([b5406dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/b5406dd0c68230420abe2ebb35bfdec483179374))
* resolve GitLeaks issues by removing duplicate entries and redundant
  fingerprints ([d89af61](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/d89af619fe9b97cadc2b8747ed569711593f951b))
* resolve issue in getPersistedCharacteristic to ensure accurate data
  retrieval ([ed404d0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ed404d0b81e3f202e9f7b1c10e46f5c10a8cd7a1))
* resolve issue preventing Terminate Plan button from triggering termination
  logic ([3d288aa](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3d288aa1a2822dd227eccbaf87145ceb807306ec))
* set 128 GB as default memory size for handset in Edit Plan
  screen ([f55cece](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/f55cece2f193ddf81538fbc5ab0299ed674b9c95))
* update discount format to use slash in PlanPreview of set up
  screen ([a210baf](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/a210bafa3260af1236a5aa92d8f44031720edab8))
* update isActiveContract condition in My Plans tab on account screen to require both plan status and operational status
  as '
  Active' ([2d43e6d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/2d43e6da4e125a324d9c5c511858c745968e359b))

### Features

* adapt display for MP Relax
  offer ([5a8d470](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/5a8d4702d5a7889d9bad3303bd6b0886b0ef68f1))
* add cancel terminate
  functionality ([3a497c7](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/3a497c7a74818777a0b0470dae1877fb059d4911))
* add channel name and market segment filter field for contract
  offer ([cf0fe5b](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/cf0fe5bd15a603c2488c20816bb12b1bad30a5c6))
* add requested completion date to order
  screens ([ff77d31](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/ff77d3153505aeda6ec37df011fc50c384be205f))
* implement cancel modify on characteristics
  items ([64bcca0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/64bcca0e3e4d13f99c27b809b7f62a15a6b9440c))
* refactor Set Up screen layout, update button behavior and revise global
  notifications ([8e76527](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/selfcare-ui/commit/8e76527cbac997305ec6659de79479d7f3668bb2))

# [1.3.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/compare/1.2.1...1.3.0) (2024-10-14)

### Bug Fixes

* add missing
  configService.js ([e1b1cb0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/e1b1cb061deba3b6b39b6143f7b020b3a1bea73d))
* change the error message of unqualified
  offer ([f05143a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/f05143ac23e571a4f32441921388af8f7eb017a2))
* complete add party to process flow request when customer is identified for
  accessories ([01d9d6d](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/01d9d6d5ccf284ac90afedd7487f9e45afb3ac85))
* ensure SMS price is displayed as 3 euros in all relevant
  screens ([7c9d844](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/7c9d8443038fc5a9a7979cafc23fd55260166ca4))
* exclude zero-value discounts from recurring
  charges ([9903766](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/9903766f7e75996e8f75723efaf0381acf937d06))
* fix discount price calculation and display for multiple charge types in PlanPreview and
  OrderPreview ([6517201](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/6517201f7f7fbc99d013a2ff8701b05bb1be0785))
* fix keycloak password
  value ([83896da](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/83896da9974e3a9d31b77e6d855686267f0eeeeb))
* fix offers images width to be
  responsive ([dbe97fe](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/dbe97fe5e10fd71580b0b43d9b71f5ae434c3ff2))
* hide zero discounts in setup/shipping and resolve NaN in order
  summary ([6f769a3](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/6f769a31956a4ab35d4f80cf461f25a798728d5d))
* rename variable `configurable` to
  `isConfigurable` ([e82582f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/e82582f51af3ecc24df634ae55d3009d23fec8a8))
* replace the orange logo by the disco icon and fix the dispatch of images in Devices &
  Electronics ([3b35e3e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/3b35e3e05cbe57f047124f1062b09c93c77a7971))

### Features

* display discount on all plan & Order
  Previews ([a0931e6](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/a0931e6b5ad7c8bbd847cdc583e72f5ae064cb62))
* display discount on all plan&Order Previews and prevent the case of 100 %
  discount ([3cf004c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/3cf004c81f16de5a20e68b8258255c2522594385))
* display discount on setup plan
  screen ([46de3a9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/46de3a981a76f3e8bac837f6256a759c070e0a5d))
* handle incompatible
  offers ([6149573](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/61495738e1ff20211749091b8f5b35d14654d63b))
* revamp setup screen with gauge
  implementation ([dd22144](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/dd221445adcd1a0900d1249ac8ee756aac59aa0c))
* set up dynamic environment config with ConfigMap, update Dockerfile and API
  endpoint ([06895a2](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/06895a2f0e62ebfe01e0948e8fcdd0483a370c09))

## [1.2.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/compare/1.2.0...1.2.1) (2024-09-09)

### Bug Fixes

* adapt logic for displaying options in setup plan to handle variable upper
  limit ([d6cf050](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/d6cf0505f7d50ec04a0d73fdd5f5da6f22d94b6f))

# [1.2.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/compare/1.1.6...1.2.0) (2024-09-04)

### Bug Fixes

* add config for image pull
  secret ([29b4722](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/29b4722ff25742a770298a15c2fec8e2aeafd968))
* add image pull secret discoregistry-imagepuller to can pull
  from ([13ea141](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/13ea14134fcb2a30a3344b44e95ac5483f8eae2c))
* correct display of configuration characteristics and
  names ([08202a6](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/08202a6d5e42d9ec22e54ddc4fd7270580eadbec))
* correct display of selected item on Plan Preview in Set Up Plan screen and fix total price calculation in Order
  Summary
  screen ([a2d48e0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/a2d48e0c366ab08d8b5e8c6ebb1dc0d36d39d0e3))
* correct NaN price display for shipping on Shipping
  screen ([d43ffcd](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/d43ffcdd4a899fb9447c458d56b7fd8e847bfb2b))
* correct total price calculation and display of ring back tone prices for "My Options" in PlanPreview
  component ([20ea67f](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/20ea67f6cf5a6aff920868c589e030f23523566b))
* disable 'Edit Plan' button for inactive contracts in PlanItem
  component ([da487b0](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/da487b0c90d8946bf28def8d766bb8dd00a8e591))
* display "Terminated" for items with status "Terminated" on edit plan
  screen ([0c9984a](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/0c9984ab9b030ff2519e6fedd804539435f3847a))
* display tangible products dynamically on the shipping screen; breadcrumb issue
  resolved ([648cb57](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/648cb57d1b65f031c8afa37989ce57cb77e6dc3c))
* filter and display only selected tangible items on the shipping details
  screen ([8f5ff87](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/8f5ff8774fa7866ef68a981a987527c814c50ef6))
* fix config for docker
  image ([57ec3b9](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/57ec3b90c9b5e49db8179dc5520201b7034f91f5))
* fix ingress
  config ([13ca20c](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/13ca20ced6ed8fa3e162b4da892fcb6220a90a2f))
* fix total price calculation and display of selected tone in PlanPreview
  component ([57620c6](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/57620c64648ffeccdb33097591c1d9bea5b8f226))
* move innovation job from staging to production
  stage ([ad6ee43](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/ad6ee43c27954ce878abfb11ef5bb6efa4024154))
* remove characteristic names from recurring or purchased items on the complete order screen, reduce total size on
  multiple screens, and fix issue with patching characteristics in setup
  plans ([b67a7c5](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/b67a7c53d8f9655d1c6d45bf2a249671e88d3721))
* sync pipeline with
  guideline ([c2b9f4e](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/c2b9f4e9133b5b21acc88b9e22e5ed288bc4e4fc))
* update display names, pricing, and
  patterns ([a3259ac](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/a3259acc99837439b6141a600f385a01f5bd60c4))

### Features

* adapt ordering FE to support fusion of
  PS/PO ([18ff945](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/18ff945ce66dbbce93af62b1a7fce3f3f51e26ad))
* enhance Set Up Plan screen: add non-selectable items, unit of measure and update
  names ([f836b11](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/f836b11095488902f5aab169e6356fec4eb3aebb))
* evolve payment screen to complete order screen, add BA info, items to be charged with details and items to be paid
  with
  details ([26cd4e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/26cd4e347492f5a7f4dc44404058e587598fefbb))
* update screens to display recurring charges with frequency
  details ([0babe88](https://gitlab.tech.orange/disco/disco-oda-components/disco-ui-portals/orange-online-self-care/commit/0babe88085440c4cc78e7be2d3f38b4b0115f8a0))

## [1.1.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.1.5...1.1.6) (2024-07-16)

### Bug Fixes

* move innovation job from staging to production
  stage ([d2eb304](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/d2eb304b0ab3600278dec80177930d6a168836e2))

## [1.1.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.1.4...1.1.5) (2024-06-13)

### Bug Fixes

* fix ingress
  config ([4b32c4e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/4b32c4e6f7f27520535ff6468ef67da7bce9c6d5))

## [1.1.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.1.3...1.1.4) (2024-06-12)

### Bug Fixes

* add image pull secret discoregistry-imagepuller to can pull
  from ([451241f](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/451241f805859ad6803a3700766015c989395acb))

## [1.1.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.1.2...1.1.3) (2024-06-12)

### Bug Fixes

* add config for image pull
  secret ([a708098](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/a708098bc310900591c6d77f912fe547e15505fe))

## [1.1.2](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.1.1...1.1.2) (2024-06-12)

### Bug Fixes

* fix config for docker
  image ([406a1c4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/406a1c45d2f5a49c7b9a8fd141e55181c39c8d66))

## [1.1.1](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.1.0...1.1.1) (2024-06-12)

### Bug Fixes

* sync pipeline with
  guideline ([5741d38](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/5741d3801ee38b4a1516a1a90d2408510590ed11))

# [1.1.0](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.8...1.1.0) (2024-06-07)

### Bug Fixes

* conditional edit plan activation for Active Contract
  products ([cc4b7ed](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/cc4b7ed4f31cf33e206b5a46b4b304aa0198afdd))
* correct display of handset device on order summary
  screen ([51ac6bb](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/51ac6bbf2d8ececb627e9821f40f6da972e9b2d1))
* fix permisson of folder
  /var/cache/nginx ([4304870](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/4304870bc843e6ed06492aaa5cb0dd30e057d817))

### Features

* integrate new Mobile Package Basic FO for acquisition use
  case ([5e3f9dd](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/5e3f9dd031c57aa7ce466bd73d979b4800469e33))
* update the term 'Shipment' to '
  Shipping' ([34014e8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/34014e81189e67b50c9d93e03e683d7588cad93d))

## [1.0.8](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.7...1.0.8) (2024-05-14)

### Bug Fixes

* add image tag
  variable ([4edf5cf](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/4edf5cf9ba26e1ded458f124f46890ad4637a87e))

## [1.0.7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.6...1.0.7) (2024-05-14)

### Bug Fixes

* remove suffix production from
  keycloak ([5ee275e](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/5ee275e927605e7e5bbd026d5e043e5410016a9d))

## [1.0.6](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.5...1.0.6) (2024-05-14)

### Bug Fixes

* set image tag per
  environment ([686c0d9](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/686c0d9ce22e204c01e33f69f928d4610ad4f425))

## [1.0.5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.4...1.0.5) (2024-05-14)

### Bug Fixes

* publish helm chart on
  repos.tech.orange ([af75ed5](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/af75ed52f3fe89f96a9d9e1daaf29bc54c2ff596))

## [1.0.4](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.3...1.0.4) (2024-05-14)

### Bug Fixes

* the sed expression used to change the version and appVersion
  in ([3ba3ca7](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/3ba3ca7b2cfef4654134b8bc8757d7d8518a0421))

## [1.0.3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/compare/1.0.2...1.0.3) (2024-05-14)

### Bug Fixes

* enable job semantic release to run
  automatic ([c3d55e3](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/c3d55e3eda5c0e5e483138382adec29489c0dc18))
* enable production helm when semantic release create
  pipeline ([96afd0c](https://gitlab.tech.orange/disco/disco-oda-components/disco-order-management/orange-online-self-care/commit/96afd0cc890f05d2676306a90b1615e8cbd900af))
