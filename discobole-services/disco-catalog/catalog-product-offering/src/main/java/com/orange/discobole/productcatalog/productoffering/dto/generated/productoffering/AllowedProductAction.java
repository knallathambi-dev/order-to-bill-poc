// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.CommercialOperationRef;

import java.util.ArrayList;
import java.util.List;


@Array
public class AllowedProductAction {

	@JsonProperty("action")
	private CommercialOperationRef action;

	@JsonProperty("channelRef")
	private List<ChannelRef> channelRef  = new ArrayList<>();

	@JsonProperty("@type")
	private String type;

	@JsonProperty("@baseType")
	private String baseType;

	@JsonProperty("@schemaLocation")
	private String schemaLocation;

	public AllowedProductAction() {
	}

	/** Copy constructor */
	public AllowedProductAction(AllowedProductAction other) {
		this.action = other.action;
		this.channelRef = other.channelRef != null
				? new ArrayList<>(other.channelRef)
				: null;
		this.type = other.type;
		this.baseType = other.baseType;
		this.schemaLocation = other.schemaLocation;

	}

	/* ---------- Fluent setters ---------- */

	public AllowedProductAction action(CommercialOperationRef action) {
		this.action = action;
		return this;
	}

	public AllowedProductAction channelRef(List<ChannelRef> channelRef) {
		this.channelRef = channelRef;
		return this;
	}


	public CommercialOperationRef getAction() {
		return action;
	}

	public void setAction(CommercialOperationRef action) {
		this.action = action;
	}

	public List<ChannelRef> getChannelRef() {
		return channelRef;
	}

	public void setChannelRef(List<ChannelRef> channelRef) {
		this.channelRef = channelRef;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
}
