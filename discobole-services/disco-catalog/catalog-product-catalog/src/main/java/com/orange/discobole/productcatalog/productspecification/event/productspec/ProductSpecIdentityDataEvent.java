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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.DefineIdentityData;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.RelatedResource;

public final class ProductSpecIdentityDataEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private DefineIdentityData defineIdentityData;
	private List<RelatedParty> relatedParty;
	private List<RelatedResource> relatedResource;
	private TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
	private final EntityType type;
	private final String href;

	public ProductSpecIdentityDataEvent() {
		this.productSpecId = null;
		this.defineIdentityData = null;
		this.relatedParty = null;
		this.relatedResource = null;
		this.validFor = null;
		this.lastUpdate = null;
		this.type = null;
		this.href = null;
	}

	public ProductSpecIdentityDataEvent(String productSpecId, DefineIdentityData defineIdentityData,
			List<RelatedParty> relatedParty, List<RelatedResource> relatedResource,
			TimePeriod defineEntityValidityPeriod, OffsetDateTime lastUpdate, EntityType type, String href) {

		this.productSpecId = productSpecId;
		this.defineIdentityData = defineIdentityData;
		this.relatedParty = relatedParty;
		this.relatedResource = relatedResource;
		this.validFor = defineEntityValidityPeriod;
		this.lastUpdate = lastUpdate;
		this.type = type;
		this.href = href;
	}

	@Override
	public String toString() {
		return "ProductSpecIdentityDataEvent [productSpecId=" + productSpecId + ", defineIdentityData="
				+ defineIdentityData + ", relatedParty=" + relatedParty + ", relatedResource=" + relatedResource
				+ ", validFor=" + validFor + ", lastUpdate=" + lastUpdate + ", type=" + type + ", href=" + href + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public DefineIdentityData getDefineIdentityData() {
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

	public String getHref() {
		return href;
	}

}
