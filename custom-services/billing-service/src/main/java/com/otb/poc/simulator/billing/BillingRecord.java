package com.otb.poc.simulator.billing;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("billing_accounts")
public record BillingRecord(
    @Id String id,
    String idempotencyKey,
    String serviceOrderId,
    String serviceOrderItemId,
    String accountId,
    String invoiceId,
    String state,
    String failureReason,
    Instant createdAt,
    Instant updatedAt) {
}
