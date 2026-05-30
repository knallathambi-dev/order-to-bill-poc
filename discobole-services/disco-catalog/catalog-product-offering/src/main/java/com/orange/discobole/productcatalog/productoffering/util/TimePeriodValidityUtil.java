// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.productoffering.dto.Validation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

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
	 * Method to check input time period range is within a restriction timperiod
	 * passed as a second paramter.
	 *
	 * @param input
	 * @param restriction
	 * @return boolean true if input timeperiod is in restricted range
	 */
	public static Validation checkTimePeriodRestriction(TimePeriod input, TimePeriod restriction) {
		Validation validation = new Validation();
		validation.setValid(true);
		if (input == null || restriction == null) {
			validation.setReason("Invalid Input or Restriction");
			validation.setValid(false);
		}
		OffsetDateTime inputStartDate = null;
		OffsetDateTime inputEndDate = null;
		if (input != null) {
			inputStartDate = input.getStartDateTime();
			inputEndDate = input.getEndDateTime();
		}
		OffsetDateTime resStartDate = null;
		OffsetDateTime resEndDate = null;
		if (restriction != null) {
			resStartDate = restriction.getStartDateTime();
			resEndDate = restriction.getEndDateTime();
		}
		if (null != inputStartDate && null != inputEndDate && inputStartDate.compareTo(inputEndDate) > 0) {
			validation.setReason(
					"Enter valid StartDate : StartDate " + inputStartDate + " is greater than EndDate " + inputEndDate);
			validation.setValid(false);
		} else {
			if (null != resStartDate && (inputStartDate == null || inputStartDate.compareTo(resStartDate) < 0)) {
				validation.setReason("Enter valid StartDate :Input StartDate " + inputStartDate
						+ " must be greater than Restricted StartDate " + resStartDate);
				validation.setValid(false);
			}
			if (null != resEndDate && (inputEndDate == null || inputEndDate.compareTo(resEndDate) > 0)) {
				validation.setReason("Enter valid EndDate :Input EndDate " + inputEndDate
						+ " must be lesser than Restricted EndDate " + resEndDate);
				validation.setValid(false);
			}
		}
		return validation;
	}

	public static boolean isTimePeriodValid(TimePeriod validFor) {
		if (validFor == null) {
			return true;
		}

		OffsetDateTime startDateTime = validFor.getStartDateTime();
		OffsetDateTime endDateTime = validFor.getEndDateTime();
		return startDateTime != null && endDateTime == null || null != startDateTime && startDateTime.compareTo(endDateTime) < 0;
	}
}
