// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.productoffering;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Units {
	
	HOURS("Hour(s)"),

	DAYS("Day(s)"),

	WEEKS("Week(s)"),
	
	MONTHS("Month(s)"),
	
	YEARS("Year(s)");

	private final String value;

	Units(String value) {
		this.value = value;
	}

	@JsonCreator
	public static Units fromValue(String text) {
		for (Units b : Units.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
