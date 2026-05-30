// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Array
public class AssociatePOPtoOperationSpecification {
	@NotBlank
	@JsonProperty("operationSpecification.id")
	@ReadOnly(true)
	private String operationspecid;
	@NotBlank
	@JsonProperty("productOfferingPrice.id")
	@ReadOnly(true)
	private String productofferingpriceid;
	private AssociatePOPtoTerm productOfferingTerm;

	public String getOperationspecid() {
		return operationspecid;
	}

	public void setOperationspecid(String operationspecid) {
		this.operationspecid = operationspecid;
	}

	public String getProductofferingpriceid() {
		return productofferingpriceid;
	}

	public void setProductofferingpriceid(String productofferingpriceid) {
		this.productofferingpriceid = productofferingpriceid;
	}

	public AssociatePOPtoTerm getProductOfferingTerm() {
		return productOfferingTerm;
	}

	public void setProductOfferingTerm(AssociatePOPtoTerm productOfferingTerm) {
		this.productOfferingTerm = productOfferingTerm;
	}
}
