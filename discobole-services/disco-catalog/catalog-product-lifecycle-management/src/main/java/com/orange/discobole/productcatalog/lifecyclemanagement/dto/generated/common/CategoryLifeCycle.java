// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryLifeCycle {

	ACTIVE("active"),
	LAUNCHED("launched"),
	RETIRED("retired");

	private final String value;

	CategoryLifeCycle(String value) {
		this.value = value;
	}

	@JsonCreator
	public static CategoryLifeCycle fromValue(String text) {
		for (CategoryLifeCycle b : CategoryLifeCycle.values()) {
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
