// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;

import java.util.List;

public class ModifyDefineIdentityProductSpecCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private IdentityData defineIdentityData;
	private List<RelatedParty> relatedParty;
	private List<RelatedResource> relatedResource;
	private TimePeriod validFor;
	private EntityType type;
	private ProductSpecificationLifecycle lifecycleStatus;

	public ModifyDefineIdentityProductSpecCommand() {
		this.productSpecId = null;
		this.defineIdentityData = null;
		this.relatedParty = null;
		this.relatedResource = null;
		this.validFor = null;
		this.type = null;
		this.lifecycleStatus = null;

	}

	public ModifyDefineIdentityProductSpecCommand(String productSpecId, IdentityData defineIdentityData,
												  List<RelatedParty> relatedParty, List<RelatedResource> relatedResource, TimePeriod validFor,
												  EntityType type, ProductSpecificationLifecycle lifecycleStatus) {
		this.productSpecId = productSpecId;
		this.defineIdentityData = defineIdentityData;
		this.relatedParty = relatedParty;
		this.relatedResource = relatedResource;
		this.validFor = validFor;
		this.type = type;
		this.lifecycleStatus=lifecycleStatus;
	}

	@Override
	public String toString() {
		return "ModifyProductSpecDescribeCommand [productSpecId=" + productSpecId + ", defineIdentityData="
				+ defineIdentityData + ", relatedParty=" + relatedParty + ", relatedResource=" + relatedResource
				+ ", validFor=" + validFor + ", type=" + type + ", lifecycleStatus=" + lifecycleStatus + "]";
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

	public ProductSpecificationLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}
	
}
