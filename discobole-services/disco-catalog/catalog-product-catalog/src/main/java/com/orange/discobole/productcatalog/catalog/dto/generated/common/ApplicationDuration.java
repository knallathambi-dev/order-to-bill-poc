// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.common;

import jakarta.validation.constraints.NotNull;


/**
 * Pojo as per schema defination.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 *
 */
public class ApplicationDuration {

	@NotNull
	private Integer amount;
	@NotNull
	private String units;

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public ApplicationDuration amount(Integer amount) {
		this.amount = amount;
		return this;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

}
