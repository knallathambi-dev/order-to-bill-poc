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
 * Gets or Sets ProductOfferingRelationshipType
 */
public enum ProductOfferingRelationshipType {

	PREREQUISITE("prerequisite"),

	INCOMPATIBLE("incompatible"),

	ADD("add"),

	NONPERSISTENTBRINGS("nonPersistentBrings"),

	BRINGS("brings"),

	ONSELECT("onSelect"),

	ONDESELECT("onDeselect"),

	ONLOAD("onLoad"),

	ONRESTORE("onRestore"),

	AGGREGATES("aggregates"),

	RELIESON("reliesOn"),

	RELIESFROM("reliesFrom"),

	DIESWITH("diesWith"),

	REINCARNATES("reincarnates"),

	REQUIRES("requires");

	private final String value;

	ProductOfferingRelationshipType(String value) {
		this.value = value;
	}

	@JsonCreator
	public static ProductOfferingRelationshipType fromValue(String text) {
		for (ProductOfferingRelationshipType b : ProductOfferingRelationshipType.values()) {
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
