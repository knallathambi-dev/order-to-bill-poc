package com.otb.poc.simulator.activation;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("activation_jobs")
public record ActivationRecord(
    @Id String id,
    String idempotencyKey,
    String serviceOrderId,
    String serviceOrderItemId,
    String serviceSpecificationId,
    String activationType,
    String state,
    String failureReason,
    Instant createdAt,
    Instant updatedAt) {
}
