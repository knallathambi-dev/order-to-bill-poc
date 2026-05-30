// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import jakarta.validation.constraints.NotNull;

import java.util.List;


public class ManageProductOfferingBundling {
	@NotNull
	private Integer globalMinCardinality;
	@NotNull
	private Integer globalMaxCardinality;
	
	private List<BundledProductOfferings> bundledProductOffering;

	public Integer getGlobalMinCardinality() {
		return globalMinCardinality;
	}

	public void setGlobalMinCardinality(int globalMinCardinality) {
		this.globalMinCardinality = globalMinCardinality;
	}

	public Integer getGlobalMaxCardinality() {
		return globalMaxCardinality;
	}

	public void setGlobalMaxCardinality(int globalMaxCardinality) {
		this.globalMaxCardinality = globalMaxCardinality;
	}

	public List<BundledProductOfferings> getBundledProductOffering() {
		return bundledProductOffering;
	}

	public void setBundledProductOffering(List<BundledProductOfferings> bundledProductOffering) {
		this.bundledProductOffering = bundledProductOffering;
	}
}
