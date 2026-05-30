// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationshipType;
import com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Vishal Vachaspati
 * @since 1.0
 *
 */
@Array
public class DefineRelationship {
	@NotBlank
	private String id;
	@NotNull
	private ProductOfferingRelationshipType relationshipType;
	private TimePeriod validFor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ProductOfferingRelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(ProductOfferingRelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

}
