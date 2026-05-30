---
title: Security
summary:
authors:
- Ayatullah Abdulhakim Mousa
---

## Authentication and Authorization

The Authentication and Authorization components are now implemented in COOD. For service-to-service communication, we utilize `grant_type: client_credentials`. The implementation includes the following roles to authorize calls to the product catalog:

- LifecycleAdmin
- Product Cat
- ProductCatalogAdmin
- ProductOfferingAdmin
- ProductOfferingPriceAdmin
- ProductSpecAdmin

Additionally, our role has been integrated into the user role creation/retrieval service using the following configuration:

```json
{
  "involvementRole": "Orchestration Plans Admin",
  "@type": "UserRole",
  "entitlement": [
    {
      "id": "x200",
      "action": "Consultation",
      "function": "Orchestration plan",
      "@type": "Entitlement"
    }
  ]
}
```

!!! abstract "Security in Customer Order Orchestration and Distribution"
    For detailed information, please refer to the following link [DISCOBOLE Security](https://discobole.ow2.io/doc/architecture/security/)
