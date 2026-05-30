package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface NodeDeliveryTimeRepository extends MongoRepository<NodeDeliveryTimeRepository.NodeDeliveryTime, String> {

    List<NodeDeliveryTime> findByNodeIdIn(Collection<String> nodeIds);

    void deleteByDeliveryTimeBefore(Instant deliveryTimeBefore);

    @Builder
    record NodeDeliveryTime(@Id String nodeId, Instant deliveryTime) {
    }
}
