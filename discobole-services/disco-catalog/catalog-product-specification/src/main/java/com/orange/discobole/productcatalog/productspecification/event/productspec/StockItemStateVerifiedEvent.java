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

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.SelectProductSpecCharacteristicCommand}
 * is triggered and denotes that Stock item state has been verified that
 * means its in active or launched state.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

public class StockItemStateVerifiedEvent implements ProductSpecEvent {
	
	@TargetAggregateIdentifier
	private final String productSpecId;
    private final String stockItemId;
    private final StockItemLifeCycleEnum stockItemLifeCycleStatus;
    
    public StockItemStateVerifiedEvent() {
        this.stockItemId = null;
        this.stockItemLifeCycleStatus = null;
		this.productSpecId = null;
    }

    /**
     * Constructs an object of StockItemStateVerifiedEvent
     *
     * @param stockItemId              stock item id
     * @param stockItemLifeCycleStatus stock item lifecycle status
     */

    public StockItemStateVerifiedEvent(String productSpecId,String stockItemId, StockItemLifeCycleEnum stockItemLifeCycleStatus) {
        super();
        this.stockItemId = stockItemId;
        this.stockItemLifeCycleStatus = stockItemLifeCycleStatus;
		this.productSpecId = productSpecId;
    }

    @Override
    public String toString() {
        return "StockItemStateVerifiedEvent [productSpecId="+productSpecId+",stockItemId=" + stockItemId + ", stockItemLifeCycleStatus="
                + stockItemLifeCycleStatus + "]";
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public StockItemLifeCycleEnum getStockItemLifeCycleStatus() {
        return stockItemLifeCycleStatus;
    }

	/**
	 * @return the productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}
    
   
}

