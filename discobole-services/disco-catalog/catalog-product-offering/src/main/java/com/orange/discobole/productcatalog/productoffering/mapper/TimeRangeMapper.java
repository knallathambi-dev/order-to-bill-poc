// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.mapper;


import com.orange.discobole.productcatalog.productoffering.pojo.TimeRange;

/**
 * This class TimePeriodMapper is used to map data of time period class from
 * pojo package to time period class of common dto.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class TimeRangeMapper {
	private TimeRangeMapper() {
	}

	public static TimeRange toPojo(
			com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimeRange timeRange) {
		if (timeRange == null) {
			return null;
		}
		return new TimeRange().validFrom(timeRange.getValidFrom()).validTo(timeRange.getValidTo());
	}

	public static com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimeRange toGenerated(
			TimeRange timeRange) {
		if (timeRange == null) {
			return null;
		}
		return new com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimeRange()
				.validFrom(timeRange.getValidFrom()).validTo(timeRange.getValidTo());
	}
}
