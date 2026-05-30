// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


import com.orange.discobole.productcatalog.catalog.config.MongoCustomConverterConfig.DateToOffsetDateTimeConverter;
import com.orange.discobole.productcatalog.catalog.config.MongoCustomConverterConfig.OffsetDateTimeToInstantConverter;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoCustomConverterConfig.class, loader = AnnotationConfigContextLoader.class)
class MongoCustomConverterConfigTest {

	@MockBean
	private MappingMongoConverter mappingMongoConverter;

	@Test
	void converterWriteTest() {
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		Assertions.assertTrue(OffsetDateTimeToInstantConverter.INSTANCE.convert(offsetDateTime) instanceof Instant);
	}

	@Test
	void converterReadTest() {
		Instant instant = Instant.now();
		Date date = Date.from(instant);
		Assertions.assertTrue(DateToOffsetDateTimeConverter.INSTANCE.convert(date) instanceof OffsetDateTime);
	}

}
