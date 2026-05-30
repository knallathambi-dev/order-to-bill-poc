// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import java.util.List;

import com.orange.discobole.processflow.annotation.DefaultValue;

public class EntityRelationships {

	@DefaultValue("0")
	private Integer policyRuleMinCardinality = 0;
	@DefaultValue("1")
	private Integer policyRuleMaxCardinality = 1;
	private List<DefineRelationship> defineRelationship;

	@DefaultValue("0")
	private Integer associatePolicyRuleRefMinCardinality = 0;
	@DefaultValue("1")
	private Integer associatePolicyRuleRefMaxCardinality = 1;
	private List<AssociatePolicyRuleRef> associatePolicyRuleRef;

	public Integer getPolicyRuleMinCardinality() {
		return policyRuleMinCardinality;
	}

	public void setPolicyRuleMinCardinality(Integer policyRuleMinCardinality) {
		this.policyRuleMinCardinality = policyRuleMinCardinality;
	}

	public Integer getPolicyRuleMaxCardinality() {
		return policyRuleMaxCardinality;
	}

	public void setPolicyRuleMaxCardinality(Integer policyRuleMaxCardinality) {
		this.policyRuleMaxCardinality = policyRuleMaxCardinality;
	}

	public List<DefineRelationship> getDefineRelationship() {
		return defineRelationship;
	}

	public void setDefineRelationship(List<DefineRelationship> defineRelationship) {
		this.defineRelationship = defineRelationship;
	}

	public Integer getSssociatePolicyRuleRefMinCardinality() {
		return associatePolicyRuleRefMinCardinality;
	}

	public void setSssociatePolicyRuleRefMinCardinality(Integer sssociatePolicyRuleRefMinCardinality) {
		this.associatePolicyRuleRefMinCardinality = sssociatePolicyRuleRefMinCardinality;
	}

	public Integer getSssociatePolicyRuleRefMaxCardinality() {
		return associatePolicyRuleRefMaxCardinality;
	}

	public void setSssociatePolicyRuleRefMaxCardinality(Integer sssociatePolicyRuleRefMaxCardinality) {
		this.associatePolicyRuleRefMaxCardinality = sssociatePolicyRuleRefMaxCardinality;
	}

	public List<AssociatePolicyRuleRef> getAssociatePolicyRuleRef() {
		return associatePolicyRuleRef;
	}

	public void setAssociatePolicyRuleRef(List<AssociatePolicyRuleRef> associatePolicyRuleRef) {
		this.associatePolicyRuleRef = associatePolicyRuleRef;
	}

}
