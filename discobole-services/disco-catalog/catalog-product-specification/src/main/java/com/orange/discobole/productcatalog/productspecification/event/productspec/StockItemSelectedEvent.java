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

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.InitiateStockItemProductSpecCommand}
 * is triggered and denotes that stock item has been selected for
 * product specification creation.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public final class StockItemSelectedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
    private final String stockItemId;

    private StockItemSelectedEvent() {
        stockItemId = null;
        productSpecId = null;
    }

    /**
     * Constructs an object of StockItemSelectedEvent
     *
     * @param stockItemId service specification id
     */
    public StockItemSelectedEvent(String productSpecId,String stockItemId) {
        this.stockItemId = stockItemId;
		this.productSpecId = productSpecId;
    }

    /**
     * This method represents the {@code StockItemSelectedEvent} object with its
     * data.
     *
     * @return returns the string representation of {@code StockItemSelectedEvent}
     *         data
     */
	@Override
	public String toString() {
		return "StockItemSelectedEvent{" + "productSpecId=" + productSpecId + "stockItemId='" + stockItemId + '\''
				+ '}';
	}

    /**
     * This method is used to get stockItemId
     *
     * @return returns stockItemId
     */
    public String getStockItemId() {
        return stockItemId;
    }

    /**
	 * @return the productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}
}