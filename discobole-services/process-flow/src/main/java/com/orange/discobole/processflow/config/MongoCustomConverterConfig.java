// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.config;
/*
 * package com.orange.disco.processflow.config;
 * 
 * import org.springframework.context.annotation.Configuration; import
 * java.time.Instant; import java.time.OffsetDateTime; import
 * java.time.ZoneOffset; import java.util.ArrayList; import java.util.Date;
 * import java.util.List;
 * 
 * import org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.core.convert.converter.Converter; import
 * org.springframework.data.convert.ReadingConverter; import
 * org.springframework.data.convert.WritingConverter; import
 * org.springframework.data.mongodb.core.convert.MongoCustomConversions;
 * 
 * @Configuration public class MongoCustomConverterConfig {
 * 
 * 
 * @Bean public MongoCustomConversions customConversions() { List<Converter<?,
 * ?>> converters = new ArrayList<>();
 * converters.add(OffsetDateTimeToInstantConverter.INSTANCE);
 * converters.add(DateToOffsetDateTimeConverter.INSTANCE); return new
 * MongoCustomConversions(converters); }
 * 
 * @WritingConverter enum OffsetDateTimeToInstantConverter implements
 * Converter<OffsetDateTime, Instant> { INSTANCE;
 * 
 * @Override public Instant convert(OffsetDateTime source) { return source ==
 * null ? null : source.toInstant(); } }
 * 
 * @ReadingConverter enum DateToOffsetDateTimeConverter implements
 * Converter<Date, OffsetDateTime> { INSTANCE;
 * 
 * @Override public OffsetDateTime convert(Date source) { return source == null
 * ? null : source.toInstant().atOffset(ZoneOffset.UTC); }
 * 
 * }
 * 
 * 
 * }
 */