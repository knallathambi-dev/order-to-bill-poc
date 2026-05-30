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

public enum OrgnizationRole {

	MARKETING_ORGANIZATION("marketingOrganization"), EXTERNAL_PARTNER("externalPartner"),
	INTERNAL_SUPPORT("internalSupport"), SALES_ORGANIZATION("salesOrganization");

	private final String role;

	OrgnizationRole(String orgnizationRole) {
		this.role = orgnizationRole;
	}

	@Override
	public String toString() {
		return String.valueOf(role);
	}

	public String getOrgnizationRole() {
		return role;
	}

	public static OrgnizationRole fromValue(String value) {
		for (OrgnizationRole orgnizationRole : OrgnizationRole.values()) {
			if (String.valueOf(orgnizationRole.role).equals(value))
				return orgnizationRole;
		}
		return null;
	}
	@JsonValue
	public String getValue() {
		return role;
	}
}
