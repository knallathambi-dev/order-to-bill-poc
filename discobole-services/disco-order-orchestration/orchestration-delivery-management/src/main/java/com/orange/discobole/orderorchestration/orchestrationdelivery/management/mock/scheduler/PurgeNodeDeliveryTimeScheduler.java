package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.scheduler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PurgeNodeDeliveryTimeScheduler {

    private final NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @Scheduled(fixedDelayString = "${mocks.purgeNodeDeliveryTimeScheduler.jobDelayDuration}")
    public void purge() {
        nodeDeliveryTimeRepository.deleteByDeliveryTimeBefore(Instant.now());
    }
}
