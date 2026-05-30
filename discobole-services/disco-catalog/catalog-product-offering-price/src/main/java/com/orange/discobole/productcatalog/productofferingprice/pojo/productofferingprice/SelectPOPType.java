// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;

import jakarta.validation.constraints.NotNull;
/**
 * Pojo as per schema defination.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class SelectPOPType {
	@NotNull
	private ProductOfferingPriceType pOPType;

	public ProductOfferingPriceType getpOPType() {
		return pOPType;
	}

	public void setpOPType(ProductOfferingPriceType pOPType) {
		this.pOPType = pOPType;
	}
}

