// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;

public class ProductSpecRelatedPartySelectedEvent implements ProductSpecEvent {
	
	@TargetAggregateIdentifier
	private final String productSpecId;
	private final List<RelatedParty> relatedParties;

	private ProductSpecRelatedPartySelectedEvent() {
		this.productSpecId = null;
		this.relatedParties = null;
	}

	public ProductSpecRelatedPartySelectedEvent(String productSpecId, List<RelatedParty> parties) {
	this.productSpecId=productSpecId;
	this.relatedParties=parties;
	}

	@Override
	public String toString() {
		return "ProductSpecRelatedPartySelectedEvent{" + "productSpecId='"
				+ productSpecId + '\'' + ", relatedParties=" + relatedParties + '}';
	}

    public String getProductSpecId() {
        return productSpecId;
    }

    public List<RelatedParty> getRelatedParties() {
        return relatedParties;
    }
   
}
