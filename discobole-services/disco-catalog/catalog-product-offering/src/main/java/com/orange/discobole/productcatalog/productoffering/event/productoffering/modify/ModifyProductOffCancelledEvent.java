// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;


	
	public class ModifyProductOffCancelledEvent implements ProductOfferingEvent {
	    
		@TargetAggregateIdentifier
		 private final String productOfferingId;
	    private final ProductOffering productOffering;

	    private ModifyProductOffCancelledEvent() {
	    	this.productOfferingId = null;
	        this.productOffering = null;
	    }

	    public ModifyProductOffCancelledEvent(String productOfferingId,ProductOffering productOffering) {
	        this.productOfferingId = productOfferingId;
	    	this.productOffering = productOffering;
	    }

	    @Override
	    public String toString() {
	        return "ProductOffCancelledEvent{" + "productOffering=" + productOffering + "productOfferingId=" 
	        		+ productOfferingId +'}';
	    }

	    public ProductOffering getProductOffering() {
	        return productOffering;
	    }

		public String getProductOfferingId() {
			return productOfferingId;
		}


}
