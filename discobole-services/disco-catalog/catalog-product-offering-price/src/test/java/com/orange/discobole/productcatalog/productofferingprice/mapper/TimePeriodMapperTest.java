// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.productofferingprice.pojo.TimePeriod;

class TimePeriodMapperTest {

	@InjectMocks
	private TimePeriodMapper timePeriodMapper;

	@Test
	void toPojoTest() {
		com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod timePeriodCommon = new com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod();
		timePeriodCommon.startDateTime(OffsetDateTime.now()).endDateTime(OffsetDateTime.now().plusYears(2));
		TimePeriod actual = TimePeriodMapper.toPojo(timePeriodCommon);
		assertEquals(timePeriodCommon.getStartDateTime(), actual.getStartDateTime());
		assertEquals(timePeriodCommon.getEndDateTime(), actual.getEndDateTime());
	}

	@Test
	void toPojoTest_WhenNull() {
		TimePeriod actual = TimePeriodMapper.toPojo(null);
		assertNull(actual);
	}

	@Test
	void toCommonTest() {
		TimePeriod timePeriodCommon = new TimePeriod();
		timePeriodCommon.startDateTime(OffsetDateTime.now()).endDateTime(OffsetDateTime.now().plusYears(2));
		com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod actual = TimePeriodMapper.toGenerated(timePeriodCommon);
		assertEquals(timePeriodCommon.getStartDateTime(), actual.getStartDateTime());
		assertEquals(timePeriodCommon.getEndDateTime(), actual.getEndDateTime());
	}

	@Test
	void toCommonTest_WhenNull() {
		com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod actual = TimePeriodMapper.toGenerated(null);
		assertNull(actual);
	}
}
