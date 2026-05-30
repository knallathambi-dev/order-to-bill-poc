// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import java.util.List;

import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.productcatalog.productoffering.pojo.AssociatePolicyRuleRef;

public class PolicyRuleAssociation {

	@DefaultValue("0")
	private Integer associatePolicyRuleRefMinCardinality = 0;
	@DefaultValue("1")
	private Integer associatePolicyRuleRefMaxCardinality = 1;
	private List<AssociatePolicyRuleRef> associatePolicyRuleRef;
	
	public Integer getAssociatePolicyRuleRefMinCardinality() {
		return associatePolicyRuleRefMinCardinality;
	}
	public void setAssociatePolicyRuleRefMinCardinality(Integer associatePolicyRuleRefMinCardinality) {
		this.associatePolicyRuleRefMinCardinality = associatePolicyRuleRefMinCardinality;
	}
	public Integer getAssociatePolicyRuleRefMaxCardinality() {
		return associatePolicyRuleRefMaxCardinality;
	}
	public void setAssociatePolicyRuleRefMaxCardinality(Integer associatePolicyRuleRefMaxCardinality) {
		this.associatePolicyRuleRefMaxCardinality = associatePolicyRuleRefMaxCardinality;
	}
	public List<AssociatePolicyRuleRef> getAssociatePolicyRuleRef() {
		return associatePolicyRuleRef;
	}
	public void setAssociatePolicyRuleRef(List<AssociatePolicyRuleRef> associatePolicyRuleRef) {
		this.associatePolicyRuleRef = associatePolicyRuleRef;
	}
	@Override
	public String toString() {
		return "PolicyRuleAssociation [associatePolicyRuleRefMinCardinality=" + associatePolicyRuleRefMinCardinality
				+ ", associatePolicyRuleRefMaxCardinality=" + associatePolicyRuleRefMaxCardinality
				+ ", associatePolicyRuleRef=" + associatePolicyRuleRef + "]";
	}
	

	

	

}
