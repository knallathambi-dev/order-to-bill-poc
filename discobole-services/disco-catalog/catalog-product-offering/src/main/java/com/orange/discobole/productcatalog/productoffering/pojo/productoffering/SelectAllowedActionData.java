// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;

import java.util.List;


@Array
public class SelectAllowedActionData {

	@JsonProperty("action")
	private CommercialOperationRef actionData;

	@JsonProperty("channelRef")
	private List<ChannelRef> channels;

	public CommercialOperationRef getActionData() {
		return actionData;
	}

	public void setActionData(CommercialOperationRef actionData) {
		this.actionData = actionData;
	}

	public List<ChannelRef> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelRef> channels) {
		this.channels = channels;
	}


}
