// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.action.forstates;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.ErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Component("InitialTaskAction")
@RequiredArgsConstructor
public class InitialTaskAction implements StateMachineStateAction<String, String> {

    private final FalloutRepository falloutRepository;
    private final EventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES);

    @Transactional
    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (!context.getTransition().getSource().getId().equals(State.INITIAL_AUTOMATED_TASK.getValue())) {
            return Mono.empty();
        }
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        Object characteristicObject = StateMachineUtil.getObjectValue(context, "processCharacteristic", new TypeReference<>() {
        });

        FalloutIncident fallout = getObjectFromCharacteristic(characteristicObject, "valueType", "Fallout", FalloutIncident.class);
        fallout.setErrorMessage(getObjectFromCharacteristic(characteristicObject, "name", "eventError", ErrorMessage.class));
        fallout.setId(context.getStateMachine().getUuid().toString());
        fallout.setState(State.INITIAL_AUTOMATED_TASK);
        fallout.setCreationDate(OffsetDateTime.now());
        falloutRepository.save(fallout);
        eventPublisher.publishEvent(CDCEvent.FALLOUT_STATE_CHANGE_EVENT, fallout);
        return Mono.empty();
    }

    private <T> T getObjectFromCharacteristic(Object object, String varName, String value, Class<T> clazz) {
        return objectMapper.convertValue(
                objectMapper.convertValue(((ArrayList) object).stream().filter(o1 -> ((LinkedHashMap) o1).get(varName).equals(value)).findFirst().orElse(null),
                        ObjectCharacteristic.class).getValue(),
                clazz);
    }
}
