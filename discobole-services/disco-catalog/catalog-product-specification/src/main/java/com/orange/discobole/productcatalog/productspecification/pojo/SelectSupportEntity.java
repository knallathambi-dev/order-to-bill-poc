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
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
public class SelectSupportEntity {

	@JsonProperty("supportEntitySpecification.id")
	@NotBlank
	@ReadOnly(true)
	private String id;
	@NotNull
	private SupportEntity supportEntityType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SupportEntity getSupportEntityType() {
		return supportEntityType;
	}

	public void setSupportEntityType(SupportEntity supportEntityType) {
		this.supportEntityType = supportEntityType;
	}

}
