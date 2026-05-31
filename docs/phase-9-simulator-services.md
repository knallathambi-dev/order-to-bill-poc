# Phase 9 Simulator Services

## Scope

Phase 9 adds three local POC simulator services:

- `qualification-service`
- `activation-service`
- `billing-service`

These services are intentionally auth-free. They do not include Spring Security, JWT validation, Keycloak adapters, or required internal-secret headers. They are local POC services and should stay behind Docker/local networking.

## Runtime URLs

| Service | Local URL | Management URL |
| --- | --- | --- |
| Qualification simulator | `http://localhost:18101` | `http://localhost:19101` |
| Activation simulator | `http://localhost:18102` | `http://localhost:19102` |
| Billing simulator | `http://localhost:18103` | `http://localhost:19103` |

## Endpoints

Each service exposes:

```text
GET  /actuator/health
POST /serviceOrdering/v1/serviceOrder
GET  /simulator/orders/{id}
GET  /simulator/failure-mode
POST /simulator/failure-mode
```

`qualification-service` also exposes:

```text
POST /simulator/serviceability
```

Failure mode accepts a JSON body:

```json
{ "mode": "success" }
```

Supported modes are `success`, `failed`, and `held`.

## Delivery Integration

Discobole Delivery Management is configured with:

```text
CONFIG_SERVICE_ORDERING_URL=http://activation-service:8080/serviceOrdering/v1/serviceOrder
```

`activation-service` handles:

- `fiber-broadband-service`
- `static-ip-service`

When the incoming service specification is `billing-initiation-service`, `activation-service` delegates the same TMF service-order request to `billing-service`.

## Event Contract

Simulator services publish Discobole-compatible service order state events to:

```text
disco.service-order-management.serviceOrderStateChange-event
```

The payload shape is:

```json
{
  "eventId": "...",
  "eventTime": "...",
  "eventType": "ServiceOrderEvent",
  "event": {
    "serviceOrder": {
      "id": "...",
      "state": "Completed",
      "serviceOrderItem": [
        {
          "id": "...",
          "state": "Completed"
        }
      ]
    }
  }
}
```

Delivery Management maps item states as follows:

| Simulator item state | Delivery node result |
| --- | --- |
| `Completed` | completed |
| `Failed` | failed |
| `Held` | held |

## Persistence

Each simulator uses its own MongoDB database:

| Service | Database | Collection |
| --- | --- | --- |
| Qualification | `qualification_service` | `qualification_records` |
| Activation | `activation_service` | `activation_jobs` |
| Billing | `billing_service` | `billing_accounts` |

Records include service order ids, item ids, service specification ids, state, failure reason, timestamps, and an idempotency key derived from the incoming service-order payload.

## Commands

Static verification:

```sh
make verify-phase9
```

Package and build:

```sh
make package-simulator-services
make build-simulator-service-images
```

Run:

```sh
make infra-up
make infra-bootstrap
make simulator-up
make simulator-verify
```

The simulator services are not exposed through the Phase 8 browser gateway.
