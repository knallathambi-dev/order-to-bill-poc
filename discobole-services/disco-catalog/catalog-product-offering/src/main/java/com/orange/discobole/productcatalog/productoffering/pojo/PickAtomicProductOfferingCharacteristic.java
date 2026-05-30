// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.constraints.NotBlank;

import java.util.List;



@Array
public class PickAtomicProductOfferingCharacteristic {
	@NotBlank
	@JsonProperty("productSpecification.productSpecCharacteristic.id")
	@ReadOnly(true)
	private String id;
	private String name;
	private String description;
	private TimePeriod validFor;
	@DefaultValue("string")
	@ReadOnly(true)
	private String valueType;
	private Integer minCardinality;
	private Integer maxCardinality;
	private List<ProductCharValue> productSpecCharacteristicValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public Integer getMinCardinality() {
		return minCardinality;
	}

	public void setMinCardinality(Integer minCardinality) {
		this.minCardinality = minCardinality;
	}

	public Integer getMaxCardinality() {
		return maxCardinality;
	}

	public void setMaxCardinality(Integer maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	public List<ProductCharValue> getProductSpecCharacteristicValue() {
		return productSpecCharacteristicValue;
	}

	public void setProductSpecCharacteristicValue(List<ProductCharValue> productSpecCharacteristicValue) {
		this.productSpecCharacteristicValue = productSpecCharacteristicValue;
	}
}
