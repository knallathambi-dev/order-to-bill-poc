// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationLifecycle;

/**
 * Event raised if Product Spec state is incorrect
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public class InvalidProductSpecStatusEvent implements ProductOfferingEvent {

    private final String productSpecId;
	private final ProductSpecificationLifecycle resourceState;

    private InvalidProductSpecStatusEvent() {
        this.productSpecId = null;
        this.resourceState = null;
    }

	public InvalidProductSpecStatusEvent(String productSpecId, ProductSpecificationLifecycle resourceState) {
        this.productSpecId = productSpecId;
        this.resourceState = resourceState;
    }

    @Override
    public String toString() {
        return "InvalidProductSpecStatusEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                ", resourceState=" + resourceState +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }

	public ProductSpecificationLifecycle getResourceState() {
        return resourceState;
    }

}
