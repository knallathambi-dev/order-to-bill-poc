// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOffering;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingEvent;

import java.time.OffsetDateTime;
import java.util.List;

public class ChildPOInfoModifiedEvent implements BundleProductOfferingEvent {
    @TargetAggregateIdentifier
	private final String productOfferingId;
    private final List<BundledProductOffering> bundleProductOffering;
    private final OffsetDateTime lastUpdate;
    private final Integer globalMinCardinality;
    private final Integer globalMaxCardinality;

    /**
     * @param bundleProductOffering
     * @param lastUpdate
     * @param globalMinCardinality
     * @param globalMaxCardinality
     */
    public ChildPOInfoModifiedEvent(String productOfferingId, List<BundledProductOffering> bundleProductOffering, OffsetDateTime lastUpdate, Integer globalMinCardinality,
                                    Integer globalMaxCardinality) {
    	this.productOfferingId = productOfferingId;
        this.bundleProductOffering = bundleProductOffering;
        this.lastUpdate = lastUpdate;
        this.globalMinCardinality = globalMinCardinality;
        this.globalMaxCardinality = globalMaxCardinality;
    }

    public ChildPOInfoModifiedEvent() {
        this.productOfferingId = null;
        this.bundleProductOffering = null;
        this.lastUpdate = null;
        this.globalMinCardinality = null;
        this.globalMaxCardinality = null;
    }

	@Override
    public String toString() {
        return "BundleProductOfferingSelectedEvent [" + ", bundleProductOffering="
                + bundleProductOffering + ", lastUpdate=" + lastUpdate + ", globalMinCardinality="
                + globalMinCardinality + ", globalMaxCardinality=" + globalMaxCardinality + "]";
    }

    public List<BundledProductOffering> getBundleProductOffering() {
        return bundleProductOffering;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public Integer getGlobalMinCardinality() {
        return globalMinCardinality;
    }

    public Integer getGlobalMaxCardinality() {
        return globalMaxCardinality;
    }
    public String getProductOfferingId() {
		return productOfferingId;
	}

}