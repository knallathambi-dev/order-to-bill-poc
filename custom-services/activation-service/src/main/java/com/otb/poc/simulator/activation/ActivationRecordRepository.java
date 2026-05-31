package com.otb.poc.simulator.activation;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivationRecordRepository extends MongoRepository<ActivationRecord, String> {
  Optional<ActivationRecord> findByIdempotencyKey(String idempotencyKey);
}
