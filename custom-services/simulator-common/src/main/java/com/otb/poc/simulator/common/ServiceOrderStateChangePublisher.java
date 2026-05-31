package com.otb.poc.simulator.common;

import com.otb.poc.simulator.common.model.ServiceOrder;
import com.otb.poc.simulator.common.model.ServiceOrderEvent;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceOrderStateChangePublisher {
  private final KafkaTemplate<String, ServiceOrderEvent> kafkaTemplate;
  private final String topic;

  public ServiceOrderStateChangePublisher(
      KafkaTemplate<String, ServiceOrderEvent> kafkaTemplate,
      @Value("${simulator.kafka.service-order-state-topic:disco.service-order-management.serviceOrderStateChange-event}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
  }

  public void publish(ServiceOrder serviceOrder) {
    ServiceOrderEvent event = new ServiceOrderEvent(
        UUID.randomUUID().toString(),
        Instant.now(),
        "ServiceOrderEvent",
        new ServiceOrderEvent.Payload(serviceOrder));
    kafkaTemplate.send(topic, serviceOrder.id(), event);
  }
}
