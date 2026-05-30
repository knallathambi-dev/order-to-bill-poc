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
 * Enum to select type of alteration
 * 
 * @author Vivek Singh
 * @since 1.0
 */
public enum PriceAlterationType {

	PRICE("Price"),

	PERCENTAGE("Percentage");

	private final String value;

	PriceAlterationType(String value) {
		this.value = value;
	}

	@JsonCreator
	public static PriceAlterationType fromValue(String text) {
		for (PriceAlterationType b : PriceAlterationType.values()) {
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
