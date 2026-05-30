// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.common;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import jakarta.annotation.Generated;

@ApiModel(description = "The category resource is used to group product offerings, service and resource candidates in logical containers. Categories can contain other categories and/or product offerings, resource or service candidates.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class CategoryEntityRelationship {
	@JsonProperty("id")
	private String id = null;
	
	@JsonProperty("productOfferings")
	private Set<ProductOfferingRef> productOfferings=null;
	
	@JsonProperty("categories")
	private Set<CategoryRef> categories=null;
	
	@JsonProperty("@type")
	private String type;
	
	@JsonProperty("lastUpdate")
	private OffsetDateTime lastUpdate = null;

	public CategoryEntityRelationship id(String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}

	public Set<ProductOfferingRef> getProductOfferings() {
		return productOfferings;
	}

	public void setProductOfferings(Set<ProductOfferingRef> productOfferings) {
		this.productOfferings = productOfferings;
	}

	
	public Set<CategoryRef> getCategories() {
		return categories;
	}

	public void setCategories(Set<CategoryRef> categories) {
		this.categories = categories;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public CategoryEntityRelationship productOfferings(Set<ProductOfferingRef> productOfferings) {
		this.productOfferings = productOfferings;
		return this;
	}
	public CategoryEntityRelationship categories(Set<CategoryRef> categories) {
		this.categories = categories;
		return this;
	}
	public CategoryEntityRelationship type(String type) {
		this.type = type;
		return this;
	}
	
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(OffsetDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "CategoryEntityRelationship [id=" + id + ", productOfferings=" + productOfferings + ", categories="
				+ categories + ", type=" + type + ", lastUpdate" + lastUpdate + "]";
	}

	
}
