// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Vishal Vachaspati
 * @since 1.0
 */

public enum BillingType {

	PREPAID("prepaid"),

	POSTPAID("postpaid"),

	HYBRID("hybrid");

	private final String value;

	BillingType(String value) {
		this.value = value;
	}

	@JsonCreator
	public static BillingType fromValue(String text) {
		for (BillingType b : BillingType.values()) {
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
