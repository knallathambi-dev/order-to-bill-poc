// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;

import io.swagger.annotations.ApiModel;
import jakarta.annotation.Generated;

import java.util.List;

@ApiModel(description = "Represents User roles from the provider of the catalog, this resource includes entitlement information.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRole {

	@JsonProperty(ProductSpecConstants.ID)
	private String id = null;

	@JsonProperty("involvementRole")
	private String involvementRole = null;

	@JsonProperty("@type")
	private String type = null;

	@JsonProperty("entitlement")
	private List<Entitlement> entitlement = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInvolvementRole() {
		return involvementRole;
	}

	public void setInvolvementRole(String involvementRole) {
		this.involvementRole = involvementRole;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Entitlement> getEntitlement() {
		return entitlement;
	}

	public void setEntitlement(List<Entitlement> entitlement) {
		this.entitlement = entitlement;
	}

	@Override
	public String toString() {
		return "UserRole [id=" + id + ", involvementRole=" + involvementRole + ", type=" + type + ", entitlement="
				+ entitlement + "]";
	}

}
