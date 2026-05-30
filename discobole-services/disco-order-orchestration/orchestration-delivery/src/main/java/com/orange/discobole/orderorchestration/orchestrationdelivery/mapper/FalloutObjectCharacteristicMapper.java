// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FalloutObjectCharacteristicMapper {

    @Mapping(target = "name", constant = "orchestrationPlan")
    @Mapping(target = "type", expression = "java(ObjectCharacteristic.class.getSimpleName())")
    @Mapping(target = "valueType", constant = "Fallout")
    @Mapping(target = "value", source = ".", qualifiedByName = "convert")
    ObjectCharacteristic from(OrchestrationPlan orchestrationPlan);

    @Mapping(target = "name", constant = "orchestrationPlanNode")
    @Mapping(target = "type", expression = "java(ObjectCharacteristic.class.getSimpleName())")
    @Mapping(target = "valueType", constant = "Fallout")
    @Mapping(target = "value", source = ".", qualifiedByName = "convertWithNode")
    ObjectCharacteristic from(OrchestrationPlan orchestrationPlan, @Context OrchestrationPlanNode orchestrationPlanNode);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", expression = "java(ObjectCharacteristic.class.getSimpleName())")
    @Mapping(target = "valueType", expression = "java(Object.class.getSimpleName())")
    @Mapping(target = "value", expression = "java(object)")
    ObjectCharacteristic fromObject(Object object, String name);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", expression = "java(StringCharacteristic.class.getSimpleName())")
    @Mapping(target = "valueType", expression = "java(String.class.getSimpleName())")
    @Mapping(target = "value", expression = "java(value)")
    StringCharacteristic fromString(String value, String name);


    @Named("convert")
    default FalloutIncident convert(OrchestrationPlan orchestrationPlan) {
        return Mappers.getMapper(OrchestrationPlanFalloutMapper.class).from(orchestrationPlan);
    }

    @Named("convertWithNode")
    default FalloutIncident convertWithNode(OrchestrationPlan orchestrationPlan, @Context OrchestrationPlanNode orchestrationPlanNode) {
        return Mappers.getMapper(OrchestrationPlanFalloutMapper.class).from(orchestrationPlanNode, orchestrationPlan);
    }
}
