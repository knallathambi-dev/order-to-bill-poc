// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.mappings;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * EnumReadingConverters should have static inner classes that implement Converter for each enum
 */

public class EnumReadingConverters {

    private EnumReadingConverters() {
    }


    /**
     * Static class that annotated with @ReadingConverter and implements
     * Convert<valueTypeInsideDocument, Enum class>,
     * in here we have Converter<String, State> means that we take String value inserted in the document
     * and convert it to State
     */
    @ReadingConverter
    public static class OrchestrationPlanStateReadingConverter implements Converter<String, State> {
        @Override
        public State convert(String source) {
            return State.fromValue(source);
        }
    }

    //RelatedProductRelationType
    @ReadingConverter
    public static class RelatedProductRelationTypeReadingConverter implements Converter<String, RelatedProductRelationType> {
        @Override
        public RelatedProductRelationType convert(String source) {
            return RelatedProductRelationType.fromValue(source);
        }
    }

    @ReadingConverter
    public static class RelatedProductTypeReadingConverter implements Converter<String, RelatedProductType> {
        @Override
        public RelatedProductType convert(String source) {
            return RelatedProductType.fromValue(source);
        }
    }

    //OrchestrationPlanNodeState
    @ReadingConverter
    public static class OrchestrationPlanNodeStateReadingConverter implements Converter<String, OrchestrationPlanNodeState> {
        @Override
        public OrchestrationPlanNodeState convert(String source) {
            return OrchestrationPlanNodeState.fromValue(source);
        }
    }

    //RelatedOrchestrationPlanNodeRelationshipType
    @ReadingConverter
    public static class RelatedOrchestrationPlanNodeRelationshipTypeReadingConverter implements Converter<String, RelatedOrchestrationPlanNodeRelationshipType> {
        @Override
        public RelatedOrchestrationPlanNodeRelationshipType convert(String source) {
            return RelatedOrchestrationPlanNodeRelationshipType.fromValue(source);
        }
    }

    @ReadingConverter
    public static class DeliveryFactoryEnumReadingConverter implements Converter<String, DeliveryFactoryRef.DeliveryFactoryEnum> {
        @Override
        public DeliveryFactoryRef.DeliveryFactoryEnum convert(String source) {
            return DeliveryFactoryRef.DeliveryFactoryEnum.fromValue(source);
        }
    }

    @ReadingConverter
    public static class DeliveryStatusEnumReadingConverter implements Converter<String, DeliveryStatusMapping.DeliveryStatusEnum> {
        @Override
        public DeliveryStatusMapping.DeliveryStatusEnum convert(String source) {
            return DeliveryStatusMapping.DeliveryStatusEnum.fromValue(source);
        }
    }

    @ReadingConverter
    public static class NodeStatusEnumReadingConverter implements Converter<String, DeliveryStatusMapping.NodeStatusEnum> {
        @Override
        public DeliveryStatusMapping.NodeStatusEnum convert(String source) {
            return DeliveryStatusMapping.NodeStatusEnum.fromValue(source);
        }
    }

    @ReadingConverter
    public static class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Date date) {
            return date.toInstant().atOffset(ZoneOffset.UTC);
        }

    }
}

