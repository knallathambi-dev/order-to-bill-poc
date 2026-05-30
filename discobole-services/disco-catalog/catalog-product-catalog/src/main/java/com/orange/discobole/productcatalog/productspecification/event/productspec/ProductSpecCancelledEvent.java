// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;

/**
 * Event that will be raised on cancellation of product specification.
 * 
 * @author Jyoti Dheer
 *
 */
public class ProductSpecCancelledEvent implements ProductSpecEvent{

	private final String productSpecId;
    private final ProductSpecification productSpecification;

    private ProductSpecCancelledEvent() {
        productSpecification = null;
		productSpecId = null;
    }

	public ProductSpecCancelledEvent(String productSpecId, ProductSpecification productSpecification) {
		this.productSpecification = productSpecification;
		this.productSpecId = productSpecId;
	}

	@Override
	public String toString() {
		return "ProductSpecCancelledEvent{" + "productSpecId=" + productSpecId + ", productSpecification="
				+ productSpecification + '}';
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public ProductSpecification getProductSpecification() {
        return productSpecification;
    }
}
