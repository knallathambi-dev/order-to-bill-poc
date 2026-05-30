// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import com.orange.discobole.processflow.annotation.DefaultValue;



/**
 * Pojo as per schema defination.
 *
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class Quantity {

	@DefaultValue("1.0f")
	private Float amount;

	private String units;

	public Quantity amount(Float amount) {
		this.amount = amount;
		return this;
	}

	public Quantity units(String units) {
		this.units = units;
		return this;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
}
