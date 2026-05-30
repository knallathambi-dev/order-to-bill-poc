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

/**
 * The StockItemAttributeValueChangeCommand type initiate to modify the stock item.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItemAttributeValueChangeCommand {

    private final Event event;
    @TargetAggregateIdentifier
    private final String aggregrateId;

    public StockItemAttributeValueChangeCommand(String aggregrateId,Event event) {
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
        return "StockItemAttributeValueChangeCommand{" +
                "event=" + event +
                '}';
    }
}
