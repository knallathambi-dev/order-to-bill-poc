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

public enum CategoryAssociateEntity {

	PRODUCTSPECIFICATION("ProductSpecification"),

	PRODUCTOFFERING("ProductOffering"),

	PRODUCTOFFERINGPRICE("ProductOfferingPrice");

	private final String value;

	
	CategoryAssociateEntity(String value) {
		this.value = value;
	}

	@JsonCreator
	public static CategoryAssociateEntity fromValue(String text) {
		for (CategoryAssociateEntity b : CategoryAssociateEntity.values()) {
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
