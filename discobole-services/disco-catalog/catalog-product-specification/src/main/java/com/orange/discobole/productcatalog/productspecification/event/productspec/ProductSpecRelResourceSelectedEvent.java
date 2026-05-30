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

import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;

public class ProductSpecRelResourceSelectedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
    private final String productSpecId;
    private final List<RelatedResource> relatedResources;

    private ProductSpecRelResourceSelectedEvent() {
        this.productSpecId=null;
        this.relatedResources=null;
    }

    public ProductSpecRelResourceSelectedEvent(String productSpecId, List<RelatedResource> resources) {
        this.productSpecId=productSpecId;
        this.relatedResources=resources;
    }

    @Override
	public String toString() {
		return "ProductSpecRelResourceSelectedEvent{" + "productSpecId='"
				+ productSpecId + '\'' + ", relatedResources=" + relatedResources + '}';
	}

    public String getProductSpecId() {
        return productSpecId;
    }

    public List<RelatedResource> getRelatedResources() {
        return relatedResources;
    }

}
