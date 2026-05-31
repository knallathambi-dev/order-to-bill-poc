# Phase 5 Discobole Core Services

## Scope

Phase 5 adds a `core` Docker Compose profile for copied Discobole services.

Included services:

- `auth-userrole`
- `order-capture`
- `order-inventory`
- `product-catalog`
- `product-specification`
- `product-offering`
- `product-inventory`
- `orchestration-delivery`
- `orchestration-delivery-management`
- `orchestration-delivery-fallout`

The services are configured to use the Phase 3 infrastructure:

- MongoDB: `mongodb:27017`
- Kafka: `kafka:9092`
- Keycloak issuer: `http://keycloak:8080/realms/discobole`
- auth-userrole API: `http://auth-userrole:8080/userRolePermission/v1/userRole`

## Build

The copied upstream Dockerfiles expect prebuilt JAR files in each module's `target/` directory.

The copied Java services must be packaged with JDK 17. Newer local JDKs can break Lombok annotation processing in these modules and produce misleading compilation errors such as missing `builder()` or getter methods. The package script sources `scripts/use-java17.sh` before invoking the system Maven command, so keep that helper current if the local JDK 17 path changes. The script intentionally skips tests, Javadocs, and license reports because Phase 5 only needs local runtime JARs for the copied services.

Package service artifacts:

```sh
make package-core-services
```

Build local Docker images:

```sh
make build-core-service-images
```

## Run

Start infrastructure and core services:

```sh
make infra-up
make infra-bootstrap
make core-up
make security-seed-auth-userrole
make core-verify
```

Stop core services:

```sh
make core-down
```

## Ports

| Service | API | Management |
| --- | --- | --- |
| auth-userrole | `18085` | `19085` |
| order-capture | `18080` | `19080` |
| order-inventory | `18081` | `19081` |
| product-catalog | `18086` | `19086` |
| product-specification | `18087` | `19087` |
| product-offering | `18088` | `19088` |
| product-inventory | `18089` | `19089` |
| orchestration-delivery | `18082` | `19082` |
| orchestration-delivery-management | `18083` | `19083` |
| orchestration-delivery-fallout | `18084` | `19084` |

## Notes

Phase 5 wires core services but does not seed broadband catalog data or adapt UI content. Those remain Phase 6 and Phase 7 work.
