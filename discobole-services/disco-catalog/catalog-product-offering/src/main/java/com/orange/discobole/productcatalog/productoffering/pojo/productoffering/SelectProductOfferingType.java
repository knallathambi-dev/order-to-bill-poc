// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;


import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

import jakarta.validation.constraints.NotNull;

public class SelectProductOfferingType {
	@NotNull
	private ProductOfferingType productOfferingType;
	private Boolean isSellable;
	private Boolean isBundle;
	private Boolean isInstallable;

	public ProductOfferingType getProductOfferingType() {
		return productOfferingType;
	}

	public void setProductOfferingType(ProductOfferingType productOfferingType) {
		this.productOfferingType = productOfferingType;
	}

	public Boolean getIsSellable() {
		return isSellable;
	}

	public void setIsSellable(Boolean isSellable) {
		this.isSellable = isSellable;
	}

	public Boolean getIsBundle() {
		return isBundle;
	}

	public void setIsBundle(Boolean isBundle) {
		this.isBundle = isBundle;
	}

	public Boolean getIsInstallable() {
		return isInstallable;
	}

	public void setIsInstallable(Boolean isInstallable) {
		this.isInstallable = isInstallable;
	}

}
