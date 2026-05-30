// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProrationType {

	SUBSCRIPTION("subscription"), SUBSCRIPTIONANDTERMINATION("subscriptionAndTermination"), NOPRORATION("noProration"),

	TERMINATION("termination");

	private final String value;

	ProrationType(String value) {
		this.value = value;
	}

	@JsonCreator
	public static ProrationType fromValue(String text) {
		for (ProrationType b : ProrationType.values()) {
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
