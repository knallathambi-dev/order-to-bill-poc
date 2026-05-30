// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ModifyProductOfferingChannelCommand {
    
	@TargetAggregateIdentifier
	private String productOfferingId;
	private final List<String> channel;

	/**
	 * @param channel
	 */
	public ModifyProductOfferingChannelCommand(String productOfferingId,List<String> channel) {
		super();
		this.productOfferingId = productOfferingId;
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingChannelCommand [channel=" + channel + "]";
	}

	/**
	 * @return the productOfferingId
	 */
	public String getProductOfferingId() {
		return productOfferingId;
	}


	public List<String> getChannel() {
		return channel;
	}

}
