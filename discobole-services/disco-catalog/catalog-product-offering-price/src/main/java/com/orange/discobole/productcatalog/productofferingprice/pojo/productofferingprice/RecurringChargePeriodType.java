// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

public enum RecurringChargePeriodType {
	HOUR("hour"), DAY("day"), WEEK("week"), MONTH("month"), TRIMESTER("trimester"), YEAR("year");

	final String periodType;

	RecurringChargePeriodType(String periodType) {
		this.periodType = periodType;
	}

	public String getPeriodType() {
		return periodType;
	}

	@Override
	public String toString() {
		return String.valueOf(periodType);
	}
}
