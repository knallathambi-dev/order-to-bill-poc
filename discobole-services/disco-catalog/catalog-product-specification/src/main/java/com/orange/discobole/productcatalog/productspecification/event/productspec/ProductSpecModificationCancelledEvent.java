// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.productspec.ProductSpecCancelModificationCommand}
 * is triggered.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ProductSpecModificationCancelledEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
    private final ProductSpecification productSpecification;

    private ProductSpecModificationCancelledEvent() {
        productSpecification = null;
        productSpecId=null;
    }

    /**
     * Instantiates a new Product spec cancelled event.
     *
     * @param productSpecification the product specification
     */
    public ProductSpecModificationCancelledEvent(String productSpecId,ProductSpecification productSpecification) {
		this.productSpecId = productSpecId;
        this.productSpecification = productSpecification;
    }

	@Override
	public String toString() {
		return "ProductSpecCancelledEvent{ productSpecId=" + productSpecId + ", productSpecification="
				+ productSpecification + '}';
	}

    /**
     * Gets product specification.
     *
     * @return the product specification
     */
    public ProductSpecification getProductSpecification() {
        return productSpecification;
    }

	public String getProductSpecId() {
		return productSpecId;
	}
}
