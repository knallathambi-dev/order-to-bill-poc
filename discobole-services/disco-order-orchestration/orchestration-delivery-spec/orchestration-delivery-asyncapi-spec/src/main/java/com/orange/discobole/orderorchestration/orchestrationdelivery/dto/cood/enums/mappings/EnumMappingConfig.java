// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.mappings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for setting enum converter inside mongo custom conversion
 * each enum model should have reading/writing converter that convert enum from document value (String)
 * to pojo value and vice verse
 */

@Configuration
public class EnumMappingConfig {

    private static final List<Converter<?,?>> ENUM_CONVERTERS = List.of(
            new EnumReadingConverters.OrchestrationPlanStateReadingConverter(),
            new EnumReadingConverters.OrchestrationPlanNodeStateReadingConverter(),
            new EnumReadingConverters.RelatedOrchestrationPlanNodeRelationshipTypeReadingConverter(),
            new EnumReadingConverters.RelatedProductRelationTypeReadingConverter(),
            new EnumReadingConverters.RelatedProductTypeReadingConverter(),
            new EnumReadingConverters.DeliveryFactoryEnumReadingConverter(),
            new EnumReadingConverters.DeliveryStatusEnumReadingConverter(),
            new EnumReadingConverters.NodeStatusEnumReadingConverter(),
            new EnumWritingConverter.OffsetDateTimeWriteConverter(),
            new EnumReadingConverters.OffsetDateTimeReadConverter(),
            new EnumWritingConverter<>()
    );
    @Bean
    public MongoCustomConversions customConversions() {
        final List<Converter<?, ?>> converterList = new ArrayList<>(ENUM_CONVERTERS);
        return new MongoCustomConversions(converterList);
    }

}
