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
import jakarta.validation.constraints.NotBlank;


/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Array
public class SelectRelatedResource {
	@NotBlank
	private String role;
	@JsonProperty("@referredType")
	private String referredType;
	private String name;
	@ReadOnly(true)
	private String id;

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

	public String getReferredType() {
		return referredType;
	}

	public void setReferredType(String referredType) {
		this.referredType = referredType;
	}
}
