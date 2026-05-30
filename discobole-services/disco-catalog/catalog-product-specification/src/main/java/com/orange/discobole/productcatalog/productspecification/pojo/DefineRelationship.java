// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;


import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecRelationshipType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Array
public class DefineRelationship {

	@NotBlank
	private String id;
	@NotNull
	private ProductSpecRelationshipType relationshipType;
	private TimePeriod validFor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ProductSpecRelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(ProductSpecRelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

}
