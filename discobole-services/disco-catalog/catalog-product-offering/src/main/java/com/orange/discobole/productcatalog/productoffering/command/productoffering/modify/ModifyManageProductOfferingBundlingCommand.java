// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOffering;

import java.util.List;

public class ModifyManageProductOfferingBundlingCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<BundledProductOffering> bundleProductOfferings;
	private final Integer globalMinCardinality;
	private final Integer globalMaxCardinality;

	/**
	 * @param bundleProductOfferings
	 * @param globalMinCardinality
	 * @param globalMaxCardinality
	 */
	public ModifyManageProductOfferingBundlingCommand(String productOfferingId, List<BundledProductOffering> bundleProductOfferings,
                                                      Integer globalMinCardinality, Integer globalMaxCardinality) {
		this.productOfferingId = productOfferingId;
		this.bundleProductOfferings = bundleProductOfferings;
		this.globalMinCardinality = globalMinCardinality;
		this.globalMaxCardinality = globalMaxCardinality;
	}

	@Override
	public String toString() {
		return "ManageProductOfferingBundlingCommand [bundleProductOfferings=" + bundleProductOfferings
				+ ", globalMinCardinality=" + globalMinCardinality + ", globalMaxCardinality=" + globalMaxCardinality
				+ ",productOfferingId=" + productOfferingId + "]";
	}

	public List<BundledProductOffering> getBundleProductOfferings() {
		return bundleProductOfferings;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Integer getGlobalMinCardinality() {
		return globalMinCardinality;
	}

	public Integer getGlobalMaxCardinality() {
		return globalMaxCardinality;
	}

}
