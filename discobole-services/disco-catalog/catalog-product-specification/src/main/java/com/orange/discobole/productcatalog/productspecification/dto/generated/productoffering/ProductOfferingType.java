// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ProductOfferingType
 */
public enum ProductOfferingType {

	CONTRACT("Contract"),

	BUNDLEPRODUCTOFFERING("BundleProductOffering"),

	ATOMICPRODUCTOFFERING("AtomicProductOffering");

	private final String value;

	ProductOfferingType(String value) {
		this.value = value;
	}

	@JsonCreator
	public static ProductOfferingType fromValue(String text) {
		for (ProductOfferingType b : ProductOfferingType.values()) {
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
