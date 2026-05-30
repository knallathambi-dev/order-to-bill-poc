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

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;

/**
 * Utility class for Time period parameters
 *
 * @author Vivek Singh
 * @since 1.0
 */
public class TimePeriodValidityUtil {

	/**
	 * Instantiates a new TimePeriodValidityUtil.
	 */
	private TimePeriodValidityUtil() {

	}

	/**
	 * this method checks the time period, if it is valid then it returns true else return false
	 * @param  validFor
	 * @return boolean
	 */
	public static boolean isTimePeriodValid(TimePeriod validFor) {
		if (validFor == null) {
			return true;
		}

		OffsetDateTime startDateTime = validFor.getStartDateTime();
		OffsetDateTime endDateTime = validFor.getEndDateTime();
		return (startDateTime != null && endDateTime == null
				|| null != startDateTime && startDateTime.compareTo(endDateTime) < 0);
	}
}
