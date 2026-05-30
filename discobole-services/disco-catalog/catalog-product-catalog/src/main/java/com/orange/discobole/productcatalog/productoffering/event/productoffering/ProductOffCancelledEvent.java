// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.productoffering.ProductOffCancelCommand}
 * is triggered.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class ProductOffCancelledEvent implements ProductOfferingEvent {
	
	 private final String productOfferingId;
    private final ProductOffering productOffering;

    private ProductOffCancelledEvent() {
    	this.productOfferingId = null;
        this.productOffering = null;
    }

    public ProductOffCancelledEvent(String productOfferingId,ProductOffering productOffering) {
    	this.productOfferingId = productOfferingId;
        this.productOffering = productOffering;
    }

    @Override
    public String toString() {
        return "ProductOffCancelledEvent{" + "productOffering=" + productOffering +  "productOfferingId=" + productOfferingId +'}';
    }

    public ProductOffering getProductOffering() {
        return productOffering;
    }

	public String getProductOfferingId() {
		return productOfferingId;
	}

}
