// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Command to trigger Product Offering initiation
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public final class InitiateProductOfferingCommand {
	
	

 
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final String productSpecId;

    public InitiateProductOfferingCommand(String productOfferingId,String productSpecId) {
        this.productOfferingId = productOfferingId;
    	this.productSpecId = productSpecId;
      
    }

    @Override
    public String toString() {
        return "InitiateProductOfferingCommand{" +
                "productSpecId='" + productSpecId + '\'' +
                "aggregateId='" + productOfferingId +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }

	public String getProductOfferingId() {
		return productOfferingId;
	}
    


}
