package com.otb.poc.simulator.qualification;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("qualification_records")
public record QualificationRecord(
    @Id String id,
    String idempotencyKey,
    String serviceOrderId,
    String serviceOrderItemId,
    String serviceSpecificationId,
    String state,
    String qualificationResult,
    String failureReason,
    Instant createdAt,
    Instant updatedAt) {
}
