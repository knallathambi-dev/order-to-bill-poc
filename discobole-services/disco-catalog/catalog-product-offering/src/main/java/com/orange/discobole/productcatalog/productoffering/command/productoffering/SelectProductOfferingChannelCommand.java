// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class SelectProductOfferingChannelCommand {
	
	@TargetAggregateIdentifier
    private final String productofferingId; 
	private final List<String> channel;

	public SelectProductOfferingChannelCommand(String productofferingId,final List<String> channel) {
		this.productofferingId = productofferingId;
		this.channel = channel;
		
	}

	/**
	 * @return the channel
	 */
	public List<String> getChannel() {
		return channel;
	}
	
	public String getProductofferingId() {
		return productofferingId;
	}

}
