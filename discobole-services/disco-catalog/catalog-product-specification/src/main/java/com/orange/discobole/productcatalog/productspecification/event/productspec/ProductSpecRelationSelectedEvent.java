// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.Set;

public class ProductSpecRelationSelectedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final Set<String> serviceSpecRelationships;

	private ProductSpecRelationSelectedEvent() {
		productSpecId = null;
		serviceSpecRelationships = null;
	}

	public ProductSpecRelationSelectedEvent(String productSpecId, Set<String> serviceSpecRelationships) {
		this.productSpecId = productSpecId;
		this.serviceSpecRelationships = serviceSpecRelationships;
	}

	@Override
	public String toString() {
		return "ProductSpecRelationSelectedEvent [productSpecId=" + productSpecId + ", serviceSpecRelationships="
				+ serviceSpecRelationships + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public Set<String> getServiceSpecRelationships() {
		return serviceSpecRelationships;
	}

}
