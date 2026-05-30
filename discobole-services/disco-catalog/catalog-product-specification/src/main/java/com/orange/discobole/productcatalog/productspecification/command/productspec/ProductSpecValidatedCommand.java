// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * The Class ProductSpecValidatedCommand validates the product
 * specification and sets its version.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public final class ProductSpecValidatedCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;

	public ProductSpecValidatedCommand(String productSpecId) {
		this.productSpecId = productSpecId;
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	@Override
	public String toString() {
		return "ProductSpecValidatedCommand [productSpecId =" + productSpecId + "]";
	}
	
}
