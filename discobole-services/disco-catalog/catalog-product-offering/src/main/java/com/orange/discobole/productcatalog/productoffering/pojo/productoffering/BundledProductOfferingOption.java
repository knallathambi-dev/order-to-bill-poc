// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;


import org.springframework.util.ObjectUtils;

import com.orange.discobole.processflow.annotation.DefaultValue;

public class BundledProductOfferingOption {
	@DefaultValue("1")
	private Integer numberRelOfferLowerLimit;
	@DefaultValue("1")
	private Integer numberRelOfferDefault;
	@DefaultValue("1")
	private Integer numberRelOfferUpperLimit;

	public int getNumberRelOfferLowerLimit() {
		return numberRelOfferLowerLimit;
	}

	public void setNumberRelOfferLowerLimit(int numberRelOfferLowerLimit) {
		this.numberRelOfferLowerLimit = numberRelOfferLowerLimit;
	}

	public Integer getNumberRelOfferDefault() {
		return numberRelOfferDefault;
	}

	public void setNumberRelOfferDefault(int numberRelOfferDefault) {
		this.numberRelOfferDefault = numberRelOfferDefault;
	}

	public int getNumberRelOfferUpperLimit() {
		return numberRelOfferUpperLimit;
	}

	public void setNumberRelOfferUpperLimit(int numberRelOfferUpperLimit) {
		
		this.numberRelOfferUpperLimit = numberRelOfferUpperLimit;
	}

}
