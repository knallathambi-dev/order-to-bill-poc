// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Array
public class ProductSpecCharacteristicSpecification {

	@NotBlank
	@JsonProperty("serviceSpecification.serviceSpecCharacteristic.id")
	@ReadOnly(true)
	private String id;
	@NotBlank
	private String name;
	private String description;
	@DefaultValue("string")
	@ReadOnly(true)
	private String valueType;
	private Boolean configurable;
	private Boolean extensible;
	private TimePeriod validFor;
	private int minCardinality;
	private int maxCardinality;
	private Boolean isUnique;
	private List<ProductSpecCharRelationship> externalCharSpecRelationship;
	private List<ServiceSpecificationCharRelationship> internalCharSpecRelationship;
	private List<ProductCharValue> characteristicValueSpecification;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public Boolean getConfigurable() {
		return configurable;
	}

	public void setConfigurable(Boolean configurable) {
		this.configurable = configurable;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

	public int getMinCardinality() {
		return minCardinality;
	}

	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	public int getMaxCardinality() {
		return maxCardinality;
	}

	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	public Boolean getIsUnique() {
		return isUnique;
	}

	public void setIsUnique(Boolean isUnique) {
		this.isUnique = isUnique;
	}

	public List<ProductSpecCharRelationship> getExternalCharSpecRelationship() {
		return externalCharSpecRelationship;
	}

	public void setExternalCharSpecRelationship(List<ProductSpecCharRelationship> externalCharSpecRelationship) {
		this.externalCharSpecRelationship = externalCharSpecRelationship;
	}

	public List<ServiceSpecificationCharRelationship> getInternalCharSpecRelationship() {
		return internalCharSpecRelationship;
	}

	public void setInternalCharSpecRelationship(
			List<ServiceSpecificationCharRelationship> internalCharSpecRelationship) {
		this.internalCharSpecRelationship = internalCharSpecRelationship;
	}

	public List<ProductCharValue> getCharacteristicValueSpecification() {
		return characteristicValueSpecification;
	}

	public void setCharacteristicValueSpecification(List<ProductCharValue> characteristicValueSpecification) {
		this.characteristicValueSpecification = characteristicValueSpecification;
	}

	public Boolean getExtensible() {
		return extensible;
	}

	public void setExtensible(Boolean extensible) {
		this.extensible = extensible;
	}

}
