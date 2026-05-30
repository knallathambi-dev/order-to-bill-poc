// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.util;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;

class TimePeriodValidityUtilTest {

	@Test
	void testTimePeriodValidityUtil_TimePeriodNull() {
		Assertions.assertTrue(TimePeriodValidityUtil.isTimePeriodValid(null));
	}

	@Test
	void testTimePeriodValidityUtil() {
		Assertions.assertTrue(TimePeriodValidityUtil.isTimePeriodValid(
				new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(OffsetDateTime.now().plusYears(1L))));
	}

	@Test
	void testTimePeriodValidityUtil_WhenStartGtEnd() {
		Assertions.assertFalse(TimePeriodValidityUtil.isTimePeriodValid(
				new TimePeriod().startDateTime(OffsetDateTime.now().plusYears(1L)).endDateTime(OffsetDateTime.now())));
	}

	@Test
	void testTimePeriodValidityUtil_StartNull() {
		Assertions.assertFalse(TimePeriodValidityUtil.isTimePeriodValid(
				new TimePeriod().startDateTime(null).endDateTime(OffsetDateTime.now().plusYears(1L))));
	}

	@Test
	void testTimePeriodValidityUtil_EndNull() {
		Assertions.assertTrue(TimePeriodValidityUtil
				.isTimePeriodValid(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(null)));
	}

	@Test
	void testTimePeriodValidityUtil_BothDateNull() {
		Assertions.assertFalse(
				TimePeriodValidityUtil.isTimePeriodValid(new TimePeriod().startDateTime(null).endDateTime(null)));
	}
}
