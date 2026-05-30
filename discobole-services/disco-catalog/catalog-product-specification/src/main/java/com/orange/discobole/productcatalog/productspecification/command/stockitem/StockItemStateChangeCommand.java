// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.stockitem;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;

/*
*The StockItemStateChangeCommand instructs to catalog to initiate an active stockItem.
*
* @author Varshika Choudhary
* @since 1.0
*/
public final class StockItemStateChangeCommand {

    private final Event event;
    @TargetAggregateIdentifier
    private final String aggregrateId;
    /**
     * Instantiates a new stock item state change command.
     *
     * @param event the event
     */
    public StockItemStateChangeCommand(String aggregrateId,Event event) {
        this.event = event;
        this.aggregrateId = aggregrateId;
    }

    public Event getEvent() {
        return event;
    }

    public String getAggregrateId() {
		return aggregrateId;
	}



	@Override
    public String toString() {
        return "StockItemStateChangeCommand{" +
                "event=" + event +
                '}';
    }
}
