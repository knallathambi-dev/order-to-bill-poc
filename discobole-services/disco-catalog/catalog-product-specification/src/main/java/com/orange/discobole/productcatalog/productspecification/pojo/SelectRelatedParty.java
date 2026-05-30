// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.PartyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Array
public class SelectRelatedParty {
	@NotNull
	@JsonProperty("@referredType")
	private PartyType referredType;
	@NotBlank
	private String role;
	private String name;
	@ReadOnly(true)
	private String id;

	public PartyType getReferredType() {
		return referredType;
	}

	public void setReferredType(PartyType referredType) {
		this.referredType = referredType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
