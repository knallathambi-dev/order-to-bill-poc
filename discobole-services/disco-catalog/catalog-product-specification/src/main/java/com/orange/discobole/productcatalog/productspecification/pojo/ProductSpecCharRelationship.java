// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
public class ProductSpecCharRelationship {

	@JsonProperty("productSpecificationCharacteristicId")
	@NotBlank
	private String id;
	private String parentSpecificationId;
	@NotBlank
	private String relationshipType;
	private TimePeriod validFor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentSpecificationId() {
		return parentSpecificationId;
	}

	public void setParentSpecificationId(String parentSpecificationId) {
		this.parentSpecificationId = parentSpecificationId;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

}
