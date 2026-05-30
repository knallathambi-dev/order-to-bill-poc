// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;

public class ProductSpecDefineIdentityModifiedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private IdentityData defineIdentityData;
	private List<RelatedParty> relatedParty;
	private List<RelatedResource> relatedResource;
	private TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
	private final EntityType type;
	private final ProductSpecificationLifecycle lifecycleStatus;
	private final ProductSpecificationLifecycle currentLifecycleStatus;


	public ProductSpecDefineIdentityModifiedEvent() {
		this.productSpecId = null;
		this.defineIdentityData = null;
		this.relatedParty = null;
		this.relatedResource = null;
		this.validFor = null;
		this.lastUpdate = null;
		this.type = null;
		this.lifecycleStatus=null;
		this.currentLifecycleStatus = null;
	}

	public ProductSpecDefineIdentityModifiedEvent(String productSpecId, IdentityData defineIdentityData,
										List<RelatedParty> relatedParty, List<RelatedResource> relatedResource,
										TimePeriod defineEntityValidityPeriod, OffsetDateTime lastUpdate, EntityType type,ProductSpecificationLifecycle lifecycleStatus, ProductSpecificationLifecycle currentLifecycleStatus) {

		this.productSpecId = productSpecId;
		this.defineIdentityData = defineIdentityData;
		this.relatedParty = relatedParty;
		this.relatedResource = relatedResource;
		this.validFor = defineEntityValidityPeriod;
		this.lastUpdate = lastUpdate;
		this.type = type;
		this.lifecycleStatus = lifecycleStatus;
		this.currentLifecycleStatus = currentLifecycleStatus;
	}


	@Override
	public String toString() {
		return "ProductSpecDefineIdentityModifiedEvent{" +
				"productSpecId='" + productSpecId + '\'' +
				", defineIdentityData=" + defineIdentityData +
				", relatedParty=" + relatedParty +
				", relatedResource=" + relatedResource +
				", validFor=" + validFor +
				", lastUpdate=" + lastUpdate +
				", type=" + type +
				", lifecycleStatus=" + lifecycleStatus +
				", currentLifecycleStatus=" + currentLifecycleStatus +
				'}';
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

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public EntityType getType() {
		return type;
	}

	public ProductSpecificationLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public ProductSpecificationLifecycle getCurrentLifecycleStatus() {
		return currentLifecycleStatus;
	}
}
