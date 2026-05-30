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
 * The Class ProductSpecCreationCompletedEvent shows the completed
 * product specification created.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public final class ProductSpecCreationCompletedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private String productSpecificationId;
	private final ProductSpecification productSpecification;

	public ProductSpecCreationCompletedEvent() {
		productSpecification = null;
		productSpecificationId=null;
	}

	public ProductSpecCreationCompletedEvent(String productSpecificationId,ProductSpecification productSpecification) {
		this.productSpecificationId = productSpecificationId;
		this.productSpecification = productSpecification;
	}

	@Override
	public String toString() {
		return "ProductSpecCreationCompletedEvent [productSpecificationId=" + productSpecificationId
				+ ", productSpecification=" + productSpecification + "]";
	}

	public ProductSpecification getProductSpecification() {
		return productSpecification;
	}

	public String getProductSpecificationId() {
		return productSpecificationId;
	}

}
