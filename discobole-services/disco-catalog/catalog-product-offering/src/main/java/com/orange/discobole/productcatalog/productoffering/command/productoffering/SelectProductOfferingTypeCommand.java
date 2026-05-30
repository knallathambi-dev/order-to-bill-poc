// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

/**
 * Command to trigger Product Offering creation
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public final class SelectProductOfferingTypeCommand {
	
	@TargetAggregateIdentifier
    public final String productOfferingId;
	public final ProductOfferingType productOfferingType;
	public final Boolean isSellable;
	public final Boolean isBundle;
	public final Boolean isInstallable;

	/**
	 * @param productOfferingType
	 * @param isSellable
	 * @param isBundle
	 * @param isInstallable
	 */
	public SelectProductOfferingTypeCommand(String productOfferingId,ProductOfferingType productOfferingType, Boolean isSellable, Boolean isBundle,
			Boolean isInstallable) {
		this.productOfferingType = productOfferingType;
		this.isSellable = isSellable;
		this.isBundle = isBundle;
		this.isInstallable = isInstallable;
		this.productOfferingId = productOfferingId;
	}

	public String getproductOfferingId() {
		return productOfferingId;
	}

	public ProductOfferingType getProductOfferingType() {
		return productOfferingType;
	}

	public Boolean getIsSellable() {
		return isSellable;
	}

	public Boolean getIsBundle() {
		return isBundle;
	}

	public Boolean getIsInstallable() {
		return isInstallable;
	}

	@Override
	public String toString() {
		return "InitiateProductOfferingTypeCommand [productOfferingType=" + productOfferingType + ", isSellable="
				+ isSellable + ", isBundle=" + isBundle + ", isInstallable=" + isInstallable + ", productOfferingId=" + productOfferingId +"]";
	}

}
