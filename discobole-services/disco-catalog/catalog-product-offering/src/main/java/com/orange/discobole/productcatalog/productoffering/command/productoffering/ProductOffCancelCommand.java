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
 * This class acts as a command that gets triggered to cancel the
 * {@code ProductOffering}.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class ProductOffCancelCommand {
    
	@TargetAggregateIdentifier
	private final String productOffId;

	/**
	 * Constructs a command object that is supplied to
	 * {@code ProductOfferingAggregate} to cancel the incomplete
	 * {@code ProductOffering}.
	 *
	 * @param productOffId product offering id
	 */
	public ProductOffCancelCommand(String productOffId) {
		this.productOffId = productOffId;
	}

	@Override
	public String toString() {
		return "ProductOffCancelCommand{" + "productOffId='" + productOffId + '\'' + '}';
	}

	public String getProductOffId() {
		return productOffId;
	}

}
