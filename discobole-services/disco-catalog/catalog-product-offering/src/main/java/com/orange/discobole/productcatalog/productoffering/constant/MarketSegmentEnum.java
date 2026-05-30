// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MarketSegmentEnum {

	B2C("B2C"),

	B2B("B2B"),

	OPERATOR("Operator"),

	INSTITUTION("Institution");

	private final String segment;

	MarketSegmentEnum(String segment) {
		this.segment = segment;
	}

	@JsonCreator
	public static MarketSegmentEnum from(String text) {
		for (MarketSegmentEnum b : MarketSegmentEnum.values()) {
			if (String.valueOf(b.segment).equals(text)) {
				return b;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return String.valueOf(segment);
	}

	@JsonValue
	public String getSegment() {
		return segment;
	}

}
