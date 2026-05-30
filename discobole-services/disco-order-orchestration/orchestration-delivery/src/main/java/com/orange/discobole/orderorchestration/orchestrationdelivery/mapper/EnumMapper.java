// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.*;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Mapper
public interface EnumMapper {

    default OrchestrationPlanStateEnum orchestrationPlanStateEnumMap(State state) {
        if (Objects.isNull(state)) {
            return null;
        }
        return OrchestrationPlanStateEnum.fromValue(state.value());
    }

    default LocalDateTime localDateTimeMap(Instant instant) {
        if (Objects.isNull(instant)) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    default OrchestrationPlanNodeStateEnum orchestrationPlanNodeStateEnumMap(OrchestrationPlanNodeState state) {
        if (Objects.isNull(state)) {
            return null;
        }
        return OrchestrationPlanNodeStateEnum.fromValue(state.value());
    }

    default RelatedProductRelationTypeEnum relatedProductRelationTypeEnumMap(RelatedProductRelationType relatedProductRelationType) {
        if (Objects.isNull(relatedProductRelationType)) {
            return null;
        }
        return RelatedProductRelationTypeEnum.fromValue(relatedProductRelationType.value());
    }

    default RelatedOrchestrationPlanNodeRelationTypeEnum relatedOrchestrationPlanNodeRelationTypeEnumMap(RelatedOrchestrationPlanNodeRelationshipType relationType) {
        if (Objects.isNull(relationType)) {
            return null;
        }
        return RelatedOrchestrationPlanNodeRelationTypeEnum.fromValue(relationType.value());
    }

    default RelatedProductTypeEnum relatedProductTypeEnumMap(RelatedProductType relatedProductType) {
        if (Objects.isNull(relatedProductType)) {
            return null;
        }
        return RelatedProductTypeEnum.fromValue(relatedProductType.value());
    }
}
