// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;

public final class DefineIdentityProductSpecCommand {

	@TargetAggregateIdentifier
	private String productSpecId;
	private IdentityData defineIdentityData;
	private List<RelatedParty> relatedParty;
	private List<RelatedResource> relatedResource;
	private TimePeriod validFor;
	private EntityType type;

	public DefineIdentityProductSpecCommand() {
		this.productSpecId = null;
		this.defineIdentityData = null;
		this.relatedParty = null;
		this.relatedResource = null;
		this.validFor = null;
		this.type = null;

	}

	public DefineIdentityProductSpecCommand(String productSpecId, IdentityData defineIdentityData,
			List<RelatedParty> relatedParty, List<RelatedResource> relatedResource, TimePeriod validFor,
			EntityType type) {
		this.productSpecId = productSpecId;
		this.defineIdentityData = defineIdentityData;
		this.relatedParty = relatedParty;
		this.relatedResource = relatedResource;
		this.validFor = validFor;
		this.type = type;
	}

	@Override
	public String toString() {
		return "DefineIdentityProductSpecCommand [productSpecId=" + productSpecId + ", defineIdentityData="
				+ defineIdentityData + ", relatedParty=" + relatedParty + ", relatedResource=" + relatedResource
				+ ", validFor=" + validFor + ", type=" + type + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public IdentityData getDefineIdentityData() {
		return defineIdentityData;
	}

	public List<RelatedParty> getRelatedParty() {
		return relatedParty;
	}

	public List<RelatedResource> getRelatedResource() {
		return relatedResource;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public EntityType getType() {
		return type;
	}

}
