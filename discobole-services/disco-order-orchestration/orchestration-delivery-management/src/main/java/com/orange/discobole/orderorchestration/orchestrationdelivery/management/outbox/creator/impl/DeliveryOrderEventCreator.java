package com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.creator.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class DeliveryOrderEventCreator implements EventEntityCreator<DeliveryOrderPayloadEvent, CDCEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(DeliveryOrderPayloadEvent deliveryOrderPayloadEvent, Map<String, String> headers) throws JsonProcessingException {
        return EventEntity.create(
                deliveryOrderPayloadEvent.getDeliveryOrder().getId(),
                getCDCEvent().getType(), getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(getDeliveryOrderEvent(deliveryOrderPayloadEvent, getCDCEvent())),
                headers
        );
    }

    private DeliveryOrderEvent getDeliveryOrderEvent(DeliveryOrderPayloadEvent deliveryStartPayloadEvent, CDCEvent cdcEvent) {
        return DeliveryOrderEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(cdcEvent.getType())
                .event(deliveryStartPayloadEvent)
                .build();
    }

    @Override
    public CDCEvent getCDCEvent() {
        return CDCEvent.DELIVERY_ORDER_DLT_EVENT;
    }
}
