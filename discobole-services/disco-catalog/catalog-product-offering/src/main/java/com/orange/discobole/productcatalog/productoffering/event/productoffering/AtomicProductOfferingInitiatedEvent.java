// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;


import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;

/**
 * Event raised after initiation of product offering
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public final class AtomicProductOfferingInitiatedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final ProductSpecificationRef productSpec;
    private final ProductOfferingLifecycle lifecycleStatus;
    private final OffsetDateTime lastUpdate;




	private AtomicProductOfferingInitiatedEvent() {
        this.productSpec = null;
        this.productOfferingId = null;
        this.lifecycleStatus = null;
        this.lastUpdate = null;
   
    }

    public AtomicProductOfferingInitiatedEvent(ProductSpecificationRef productSpec, String productOfferingId,
                                               ProductOfferingLifecycle lifecycleStatus, OffsetDateTime lastUpdate) {
        this.productSpec = productSpec;
        this.productOfferingId = productOfferingId;
        this.lifecycleStatus = lifecycleStatus;
        this.lastUpdate = lastUpdate;
       
    }

    @Override
    public String toString() {
        return "AtomicProductOfferingInitiatedEvent{" +
                "productSpec=" + productSpec +
                ", productOfferingId='" + productOfferingId + '\'' +
                ", lifecycleStatus=" + lifecycleStatus +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
    
    
 

    public ProductSpecificationRef getProductSpec() {
        return productSpec;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public ProductOfferingLifecycle getLifecycleStatus() {
        return lifecycleStatus;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

}
