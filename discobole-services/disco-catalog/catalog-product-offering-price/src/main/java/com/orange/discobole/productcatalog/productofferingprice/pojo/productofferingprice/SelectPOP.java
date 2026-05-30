// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.ReadOnly;

import jakarta.validation.constraints.NotBlank;

public class SelectPOP {

	@JsonProperty("productOfferingPrice.id")
	@NotBlank
	@ReadOnly(true)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
