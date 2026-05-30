// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IndividualRole {

	MARKETING_CONTACT("marketingContact"), TECHNICAL_CONTACT("technicalContact"), PARTNER_CONTACT("partnerContact"),
	COMMERCIAL_CONTACT("commercialContact");

	private final String role;

	IndividualRole(String individualRole) {
		this.role = individualRole;
	}

	@Override
	public String toString() {
		return String.valueOf(role);
	}

	public String getIndividualRole() {
		return role;
	}

	public static IndividualRole fromValue(String value) {
		for (IndividualRole individualRole : IndividualRole.values()) {
			if (String.valueOf(individualRole.role).equals(value))
				return individualRole;
		}
		return null;
	}

	@JsonValue
	public String getValue() {
		return role;
	}
}
