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
 * This class acts as a command that gets triggered by
 * {@code ProductSpecAggregate} to initiate the creation of
 * {@code ProductSpecification}.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class InitiateStockItemProductSpecCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;
    private final String stockItemTypeId;
    /**
     * Constructs a command object that is supplied to {@code ProductSpecAggregate}
     * to initiate the creation of {@code ProductSpecification}.
     *
     * @param stockItemTypeId stock item id that has been selected for
     *                      creation of {@code ProductSpecification}
     */
    public InitiateStockItemProductSpecCommand(String productSpecId,String stockItemTypeId) {
        this.stockItemTypeId = stockItemTypeId;
		this.productSpecId = productSpecId;
    }

    /**
     * This method represents the {@code stockItemId} associated with
     * {@code InitiateProductSpecCommand}
     *
     * @return string representation of {@code InitiateProductSpecCommand}
     *         containing its data.
     */
	@Override
	public String toString() {
		return "InitiateStockItemProductSpecCommand [productSpecId=" + productSpecId + ", stockItemTypeId="
				+ stockItemTypeId + "]";
	}

    /**
     * This method is used to get the {@code stockItemId} associated with
     * {@code InitiateStockItemProductSpecCommand}
     *
     * @return the {@code stockItemId} associated with
     *         {@code InitiateStockItemProductSpecCommand}
     */
    public String getStockItemTypeId() {
        return stockItemTypeId;
    }

	/**
	 * @return the productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}

}

