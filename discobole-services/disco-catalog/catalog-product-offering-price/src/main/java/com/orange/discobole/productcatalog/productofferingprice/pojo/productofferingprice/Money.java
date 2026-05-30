// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Pojo as per schema defination.
 *
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class Money {

	private String unit;
	@PositiveOrZero
	private Float value;

	public Money unit(String unit) {
		this.unit = unit;
		return this;
	}

	public Money value(Float value) {
		this.value = value;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Money{" +
				"unit='" + unit + '\'' +
				", value=" + value +
				'}';
	}
}
