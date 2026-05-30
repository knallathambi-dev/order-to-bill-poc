# Phase 4 Security Seed Data

## Scope

Phase 4 adds local authentication and authorization seed artifacts.

It does not run Discobole `auth-userrole`; that service is introduced in Phase 5. The auth-userrole seed files and script are ready for that phase.

## Keycloak

Realm import:

```text
infrastructure/keycloak/import/discobole-realm.json
```

The realm name is `discobole`.

Seeded users:

| User | Password | Roles |
| --- | --- | --- |
| `customer@otb.com` | `customer` | `OTB_CUSTOMER` |
| `operator@otb.com` | `operator` | `OTB_ORDER_OPERATOR`, `OTB_FALLOUT_OPERATOR` |
| `admin@otb.com` | `admin` | `OTB_ADMIN`, `OTB_ORDER_MANAGER`, `OTB_CATALOG_ADMIN`, `OTB_FALLOUT_OPERATOR`, `OTB_ORDER_OPERATOR` |

Seeded clients:

- `selfcare-ui`
- `admin-ui`
- `poc-gateway`
- `order-capture`
- `order-inventory`
- `orchestration-delivery`
- `orchestration-delivery-management`
- `orchestration-delivery-fallout`
- `auth-userrole`
- `product-catalog`
- `qualification-service`
- `activation-service`
- `billing-service`

Local confidential-client secret:

```text
change-me
```

## Applying Keycloak Import

If Keycloak was already running before the realm file was added, recreate the Keycloak container:

```sh
docker compose --profile infra up -d --force-recreate keycloak
```

Then verify token issuance:

```sh
make security-verify-keycloak
```

## auth-userrole

Seed artifacts:

- `infrastructure/auth-userrole/component-configurations.json`
- `infrastructure/auth-userrole/function-configurations.json`
- `infrastructure/auth-userrole/entitlements.json`
- `infrastructure/auth-userrole/user-roles.json`

Once `auth-userrole` is running in Phase 5:

```sh
make security-seed-auth-userrole
```

