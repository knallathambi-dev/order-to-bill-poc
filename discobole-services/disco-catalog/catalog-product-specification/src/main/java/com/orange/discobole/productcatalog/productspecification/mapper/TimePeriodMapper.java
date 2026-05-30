// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.mapper;

import com.orange.discobole.productcatalog.productspecification.pojo.TimePeriod;

/**
 * This class TimePeriodMapper is used to map data of time period class from
 * pojo package to time period class of common dto.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class TimePeriodMapper {
	private TimePeriodMapper() {
	}

	public static TimePeriod toPojo(com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod timePeriod) {
		if (timePeriod == null) {
			return null;
		}
		return new TimePeriod().startDateTime(timePeriod.getStartDateTime()).endDateTime(timePeriod.getEndDateTime());
	}

	public static com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod toGenerated(
			TimePeriod timePeriod) {
		if (timePeriod == null) {
			return null;
		}
		return new com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod()
				.startDateTime(timePeriod.getStartDateTime()).endDateTime(timePeriod.getEndDateTime());
	}
}
