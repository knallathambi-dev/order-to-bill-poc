// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongodbCustomConverterConfig {
    private final OffsetDateTimeConverter offsetDateTimeConverter;
    private final InstantConverter instantConverter;

    public MongodbCustomConverterConfig(OffsetDateTimeConverter offsetDateTimeConverter, InstantConverter instantConverter) {
        this.offsetDateTimeConverter = offsetDateTimeConverter;
        this.instantConverter = instantConverter;
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(instantConverter);
        converters.add(offsetDateTimeConverter);
        return new MongoCustomConversions(converters);
    }
}