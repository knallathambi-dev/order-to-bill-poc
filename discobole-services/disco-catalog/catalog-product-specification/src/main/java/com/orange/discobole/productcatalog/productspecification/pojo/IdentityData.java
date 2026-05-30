// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import com.orange.discobole.processflow.annotation.DefaultValue;
import jakarta.validation.constraints.NotBlank;


/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
public class IdentityData {

	@NotBlank
	private String name;
	@NotBlank
	private String description;
	/** Brand associated with this Product specification. */
	@DefaultValue("Orange")
	private String brand;
	private String productNumber;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
