// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import com.orange.discobole.ordermanagement.orderinventory.codec.*;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories("com.orange.discobole.ordermanagement.orderinventory.repository")
@Import(value = MongoAutoConfiguration.class)
public class DatabaseConfiguration {

    private final DateToZonedDateTimeConverter dateToZonedDateTimeConverter;
    private final ZonedDateTimeToDateConverter zonedDateTimeToDateConverter;

    public DatabaseConfiguration(DateToZonedDateTimeConverter dateToZonedDateTimeConverter, ZonedDateTimeToDateConverter zonedDateTimeToDateConverter) {
        this.dateToZonedDateTimeConverter = dateToZonedDateTimeConverter;
        this.zonedDateTimeToDateConverter = zonedDateTimeToDateConverter;
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(dateToZonedDateTimeConverter);
        converters.add(zonedDateTimeToDateConverter);

        converters.add(new ItemActionTypeReaderConverter());
        converters.add(new ItemActionTypeWriterConverter());

        converters.add(new ProductOrderItemRelationshipTypeReaderConverter());
        converters.add(new ProductOrderItemRelationshipTypeWriterConverter());

        converters.add(new ProductOrderItemStateTypeReaderConverter());
        converters.add(new ProductOrderItemStateTypeWriterConverter());

        converters.add(new ProductOrderStateTypeReaderConverter());
        converters.add(new ProductOrderStateTypeWriterConverter());

        converters.add(new ProductStatusTypeReaderConverter());
        converters.add(new ProductStatusTypeWriterConverter());

        converters.add(new ProductOfferingPriceLifecycleReaderConverter());
        converters.add(new ProductOfferingPriceLifecycleWriterConverter());

        return new MongoCustomConversions(converters);
    }
}