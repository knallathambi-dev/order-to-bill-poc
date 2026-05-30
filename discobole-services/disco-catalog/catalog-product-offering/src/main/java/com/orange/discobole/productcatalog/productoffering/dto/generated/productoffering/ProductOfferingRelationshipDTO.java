// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * @author BMKJ8547
 *
 */
public class ProductOfferingRelationshipDTO {
	private  String productOfferingId;
	private  ProductOfferingRelationship addProductOfferingRelationships;
	private  ProductOfferingRelationship deleteProductOfferingRelationships;
	private  OffsetDateTime lastUpdate;
	public String getProductOfferingId() {
		return productOfferingId;
	}
	public ProductOfferingRelationship getAddProductOfferingRelationships() {
		return addProductOfferingRelationships;
	}
	public void setAddProductOfferingRelationships(ProductOfferingRelationship addProductOfferingRelationships) {
		this.addProductOfferingRelationships = addProductOfferingRelationships;
	}
	public ProductOfferingRelationship getDeleteProductOfferingRelationships() {
		return deleteProductOfferingRelationships;
	}
	public void setDeleteProductOfferingRelationships(ProductOfferingRelationship deleteProductOfferingRelationships) {
		this.deleteProductOfferingRelationships = deleteProductOfferingRelationships;
	}
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(OffsetDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public void setProductOfferingId(String productOfferingId) {
		this.productOfferingId = productOfferingId;
	}
	@Override
	public String toString() {
		return "ProductOfferingRelationshipDTO [productOfferingId=" + productOfferingId
				+ ", addProductOfferingRelationships=" + addProductOfferingRelationships
				+ ", deleteProductOfferingRelationships=" + deleteProductOfferingRelationships + ", lastUpdate="
				+ lastUpdate + "]";
	}
	
	
	
}
