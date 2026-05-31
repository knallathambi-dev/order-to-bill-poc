package com.otb.poc.simulator.billing;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BillingRecordRepository extends MongoRepository<BillingRecord, String> {
  Optional<BillingRecord> findByIdempotencyKey(String idempotencyKey);
}
