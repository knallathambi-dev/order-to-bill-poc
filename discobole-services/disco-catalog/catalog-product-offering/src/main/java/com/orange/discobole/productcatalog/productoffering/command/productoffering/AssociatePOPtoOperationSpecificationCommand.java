// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;

/**
 * This class acts as a command that links POP to Atomic Product Offering
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class AssociatePOPtoOperationSpecificationCommand {
	
	@TargetAggregateIdentifier
    private final String productOfferingId;
    private final List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList;

    /**
     * Constructs a command object that is supplied to
     * {@code ProductOfferingAggregate} that links POP to Atomic Product Offering.
     *
     * @param associatePOPtoOperationSpecList
     */
    public AssociatePOPtoOperationSpecificationCommand(String productOfferingId,List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList) {
        this.productOfferingId = productOfferingId;
    	this.associatePOPtoOperationSpecList = associatePOPtoOperationSpecList;
    }

    public List<AssociatePOPtoOperationSpec> getAssociatePOPtoOperationSpecList() {
        return associatePOPtoOperationSpecList;
    }

    public String getProductOfferingId() {
		return productOfferingId;
	}

	@Override
    public String toString() {
        return "AssociatePOPtoOperationSpecificationCommand{" +
                "associatePOPtoOperationSpecList=" + associatePOPtoOperationSpecList +
                "productOfferingId=" + productOfferingId +'}';
    }

}
