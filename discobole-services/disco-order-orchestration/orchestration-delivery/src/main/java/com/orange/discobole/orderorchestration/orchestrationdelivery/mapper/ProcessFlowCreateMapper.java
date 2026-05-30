// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mapper
public interface ProcessFlowCreateMapper {
    String PROCESS_FLOW_SPECIFICATION = "Fallout";
    String PROCESS_FLOW_CHANNEL_ITEM = "Internal";
    FalloutServiceMapper FALLOUT_SERVICE_MAPPER = Mappers.getMapper(FalloutServiceMapper.class);
    FalloutObjectCharacteristicMapper CHARACTERISTIC_MAPPER = Mappers.getMapper(FalloutObjectCharacteristicMapper.class);
    String EVENT_ERROR_NAME = "eventError";
    String EVENT_TRACE_PARENT_NAME = "eventTraceParent";
    String EVENT_TOPIC_NAME = "eventTopic";
    String EVENT_HANDLER_CLASS_NAME = "eventHandlerClass";
    String EVENT_PAYLOAD_NAME = "eventPayload";

    @Mapping(target = "relatedEntity", source = "orchestrationPlan", qualifiedByName = "mapRelatedEntity")
    @Mapping(target = "characteristic", expression = "java(mapCharacteristics(orchestrationPlan, falloutCharacteristicWrapper))")
    ProcessFlowCreate from(OrchestrationPlan orchestrationPlan, FalloutCharacteristicWrapper falloutCharacteristicWrapper);

    @Mapping(target = "relatedEntity", source = "orchestrationPlanNode", qualifiedByName = "mapRelatedEntity")
    @Mapping(target = "characteristic", expression = "java(mapCharacteristics(orchestrationPlanNode, orchestrationPlan))")
    ProcessFlowCreate from(OrchestrationPlan orchestrationPlan, OrchestrationPlanNode orchestrationPlanNode);

    @Mapping(target = "relatedEntity", source = "orchestrationPlanNode", qualifiedByName = "mapRelatedEntity")
    @Mapping(target = "characteristic", expression = "java(mapCharacteristics(orchestrationPlanNode, orchestrationPlan, characteristicWrapper))")
    ProcessFlowCreate from(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan, FalloutCharacteristicWrapper characteristicWrapper);

    @BeforeMapping
    @SuppressWarnings("NP_NULL_ON_SOME_PATH")
    default void constructCommonValues(@MappingTarget ProcessFlowCreate processFlowCreate, OrchestrationPlan orchestrationPlan) {
        if (Objects.nonNull(orchestrationPlan) && !CollectionUtils.isEmpty(orchestrationPlan.getRelatedParty())) {
            processFlowCreate.addRelatedPartyItem(FALLOUT_SERVICE_MAPPER.toRelatedParty(orchestrationPlan.getRelatedParty().get(0)));
        }
        processFlowCreate.setProcessFlowSpecification(PROCESS_FLOW_SPECIFICATION);
        processFlowCreate.addChannelItem(new ChannelRef().name(PROCESS_FLOW_CHANNEL_ITEM));
    }

    @Named("mapRelatedEntity")
    default List<RelatedEntity> mapRelatedEntity(OrchestrationPlan orchestrationPlan) {
        return Objects.nonNull(orchestrationPlan) ? List.of(new RelatedEntity().id(orchestrationPlan.getId()).referredType(OrchestrationPlan.class.getSimpleName())) : List.of();
    }

    @Named("mapRelatedEntity")
    default List<RelatedEntity> mapRelatedEntity(OrchestrationPlanNode node) {
        return Objects.nonNull(node) ? List.of(new RelatedEntity().id(node.getId()).referredType(OrchestrationPlanNode.class.getSimpleName())) : List.of();
    }

    default List<Characteristic> mapFalloutCharacteristicWrapper(FalloutCharacteristicWrapper characteristicWrapper) {
        return new ArrayList<>(List.of(
                CHARACTERISTIC_MAPPER.fromObject(characteristicWrapper.getCoodError(), EVENT_ERROR_NAME),
                CHARACTERISTIC_MAPPER.fromString(characteristicWrapper.getTraceParent(), EVENT_TRACE_PARENT_NAME),
                CHARACTERISTIC_MAPPER.fromString(characteristicWrapper.getTopicName(), EVENT_TOPIC_NAME),
                CHARACTERISTIC_MAPPER.fromString(characteristicWrapper.getHandlerClass(), EVENT_HANDLER_CLASS_NAME),
                CHARACTERISTIC_MAPPER.fromObject(characteristicWrapper.getEventPayload(), EVENT_PAYLOAD_NAME)
        ));
    }

    @Named("mapCharacteristics")
    default List<Characteristic> mapCharacteristics(OrchestrationPlan orchestrationPlan, FalloutCharacteristicWrapper characteristicWrapper) {
        List<Characteristic> characteristics = mapFalloutCharacteristicWrapper(characteristicWrapper);
        characteristics.add(CHARACTERISTIC_MAPPER.from(orchestrationPlan));
        return characteristics;
    }

    @Named("mapCharacteristics")
    default List<Characteristic> mapCharacteristics(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan, FalloutCharacteristicWrapper characteristicWrapper) {
        List<Characteristic> characteristics = mapFalloutCharacteristicWrapper(characteristicWrapper);
        characteristics.add(CHARACTERISTIC_MAPPER.from(orchestrationPlan, orchestrationPlanNode));
        return characteristics;
    }

    @Named("mapCharacteristics")
    default List<Characteristic> mapCharacteristics(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan) {
        List<Characteristic> characteristics = new ArrayList<>(List.of(CHARACTERISTIC_MAPPER.from(orchestrationPlan, orchestrationPlanNode)));
        if (!CollectionUtils.isEmpty(orchestrationPlanNode.getErrorMessage())) {
            orchestrationPlanNode.getErrorMessage().sort(Comparator.comparing(OrchestrationNodeErrorMessage::getTimeStamp).reversed());
            characteristics.add(CHARACTERISTIC_MAPPER.fromObject(orchestrationPlanNode.getErrorMessage().get(0), EVENT_ERROR_NAME));
        }
        return characteristics;
    }
}