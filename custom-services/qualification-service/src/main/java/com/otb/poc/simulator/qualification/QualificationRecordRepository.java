package com.otb.poc.simulator.qualification;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QualificationRecordRepository extends MongoRepository<QualificationRecord, String> {
  Optional<QualificationRecord> findByIdempotencyKey(String idempotencyKey);
}
