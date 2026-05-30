// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.CDCEventInterface;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.COOD_TECHNICAL_EXCEPTION;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Slf4j
public class EventPublisher {

    private final EventRepository eventRepository;

    private final List<EventEntityCreator<?, ? extends CDCEventInterface>> eventEntityCreators;

    private final ObjectMapper objectMapper;

    public <T, E extends Enum<E> & CDCEventInterface> void publishEvent(E eventType, T payload) {
        try {
            log.info("Publishing CDC events - Topic: {}, Payload: {}", eventType != null ? eventType.getTopicName() : "N/A", payload);

            // Use the correct EventEntityCreator for the given event type
            EventEntityCreator<T, E> creator = getEventEntityCreator(eventType);
            EventEntity eventEntity = creator.create(payload, Map.of());
            eventRepository.save(eventEntity);
        } catch (JsonProcessingException e) {
            throw new CoodTechnicalException(COOD_TECHNICAL_EXCEPTION, e.getMessage());
        }
    }

    public <T, E extends Enum<E> & CDCEventInterface> void publishEvents(E eventType, List<T> payloads) {
        try {
            int payloadCount = payloads != null ? payloads.size() : 0;
            log.info("Publishing CDC events - Topic: {}, Event count: {},  Payloads: {}", eventType != null ? eventType.getTopicName() : "N/A", payloadCount, payloads);

            EventEntityCreator<T, E> creator = getEventEntityCreator(eventType);
            List<EventEntity> eventEntities = payloads.stream()
                    .map(payload -> {
                        try {
                            return creator.create(payload, Map.of());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            eventRepository.saveAll(eventEntities);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof JsonProcessingException) {
                throw new CoodTechnicalException(COOD_TECHNICAL_EXCEPTION, e.getCause().getMessage());
            } else {
                throw e;
            }
        }
    }


    private <T, E extends Enum<E> & CDCEventInterface> EventEntityCreator<T, E> getEventEntityCreator(E eventType) {
        for (EventEntityCreator<?, ? extends CDCEventInterface> creator : eventEntityCreators) {
            if (eventType != null && eventType.equals(creator.getCDCEvent())) {
                // Safe cast to the correct generic type
                @SuppressWarnings("unchecked")
                EventEntityCreator<T, E> typedCreator = (EventEntityCreator<T, E>) creator;
                return typedCreator;
            }
        }

        throw new CoodTechnicalException(COOD_TECHNICAL_EXCEPTION, "Could not find event creator for event " + eventType);
    }

    public <T, E extends Enum<E> & CDCEventInterface> void publishEvent(E eventType, T payload, Map<String, Object> headers) {

        log.info("Publishing CDC events - Topic: {}, Payload: {}", eventType != null ? eventType.getTopicName() : "N/A", payload);

        try {
            Map<String, String> transformedHeaders = new HashMap<>();
            headers.forEach((key, value) -> {
                try {
                    String mappedValue = value instanceof String castedValue ? castedValue : objectMapper.writeValueAsString(value);
                    transformedHeaders.put(key, mappedValue);
                } catch (JsonProcessingException e) {
                    log.error("Could not serialize header", e);
                }
            });
            EventEntityCreator<T, E> creator = getEventEntityCreator(eventType);
            eventRepository.save(creator.create(payload, transformedHeaders));
        } catch (JsonProcessingException e) {
            throw new CoodTechnicalException(COOD_TECHNICAL_EXCEPTION, e.getMessage());
        }
    }

}
