// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingType;

public class CreateBundleProductOfferingEvent implements BundleProductOfferingEvent{

	private final String productOfferingId;
	private final OffsetDateTime lastUpdate;
	private final Boolean isSellable;
	private final Boolean isBundle;
	private final Boolean isInstallable;
	private final ProductOfferingType type;

	/**
	 * @param productOfferingId
	 * @param lastUpdate
	 * @param isSellable
	 * @param isBundle
	 * @param isInstallable
	 * @param type
	 */
	public CreateBundleProductOfferingEvent(String productOfferingId, OffsetDateTime lastUpdate, Boolean isSellable,
			Boolean isBundle, Boolean isInstallable, ProductOfferingType type) {
		super();
		this.productOfferingId = productOfferingId;
		this.lastUpdate = lastUpdate;
		this.isSellable = isSellable;
		this.isBundle = isBundle;
		this.isInstallable = isInstallable;
		this.type = type;
	}

	@Override
	public String toString() {
		return "CreateBundleProductOfferingEvent [productOfferingId=" + productOfferingId + ", lastUpdate=" + lastUpdate
				+ ", isSellable=" + isSellable + ", isBundle=" + isBundle + ", isInstallable=" + isInstallable
				+ ", type=" + type + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
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

	public ProductOfferingType getType() {
		return type;
	}

}
