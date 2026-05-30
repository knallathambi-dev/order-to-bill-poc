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

public class EntityRelationships {

	@DefaultValue("0")
	private  Integer entityRelationshipMinCardinality = 0;

	@DefaultValue("1")
	private  Integer entityRelationshipMaxCardinality = 1;

	public Integer getEntityRelationshipMinCardinality() {
		return entityRelationshipMinCardinality;
	}

	private List<DefineRelationship> defineRelationship;
	public void setEntityRelationshipMinCardinality(Integer entityRelationshipMinCardinality) {
		this.entityRelationshipMinCardinality = entityRelationshipMinCardinality;
	}

	public Integer getEntityRelationshipMaxCardinality() {
		return entityRelationshipMaxCardinality;
	}

	public void setEntityRelationshipMaxCardinality(Integer entityRelationshipMaxCardinality) {
		this.entityRelationshipMaxCardinality = entityRelationshipMaxCardinality;
	}

	public List<DefineRelationship> getDefineRelationship() {
		return defineRelationship;
	}

	public void setDefineRelationship(List<DefineRelationship> defineRelationship) {
		this.defineRelationship = defineRelationship;
	}

	@Override
	public String toString() {
		return "EntityRelationships{" +
				"entityRelationshipMinCardinality=" + entityRelationshipMinCardinality +
				", entityRelationshipMaxCardinality=" + entityRelationshipMaxCardinality +
				", defineRelationship=" + defineRelationship +
				'}';
	}
}
