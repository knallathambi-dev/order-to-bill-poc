// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import java.util.List;

import com.orange.discobole.processflow.annotation.DefaultValue;

public class PickCharacteristicSpecification {

	private List<ProductSpecCharacteristicSpecification> characteristicSpecification;

	private List<ProductSpecUsageSpecification> usageSpecification;

	@DefaultValue("0")
	private Integer characteristicSpecificationMinCardinality = 0;
	@DefaultValue("1")
	private Integer characteristicSpecificationMaxCardinality = 1;

	@DefaultValue("0")
	private Integer usageSpecificationMinCardinality = 0;
	@DefaultValue("1")
	private Integer usageSpecificationMaxCardinality = 1;

	public List<ProductSpecCharacteristicSpecification> getCharacteristicSpecification() {
		return characteristicSpecification;
	}

	public void setCharacteristicSpecification(
			List<ProductSpecCharacteristicSpecification> pickCharacteristicSpecification) {
		this.characteristicSpecification = pickCharacteristicSpecification;
	}

	public List<ProductSpecUsageSpecification> getUsageSpecification() {
		return usageSpecification;
	}

	public void setUsageSpecification(List<ProductSpecUsageSpecification> pickUsageSpecification) {
		this.usageSpecification = pickUsageSpecification;
	}

	public Integer getCharacteristicSpecificationMinCardinality() {
		return characteristicSpecificationMinCardinality;
	}

	public void setCharacteristicSpecificationMinCardinality(
			Integer pickCharacteristicSpecificationMinCardinality) {
		this.characteristicSpecificationMinCardinality = pickCharacteristicSpecificationMinCardinality;
	}

	public Integer getCharacteristicSpecificationMaxCardinality() {
		return characteristicSpecificationMaxCardinality;
	}

	public void setCharacteristicSpecificationMaxCardinality(
			Integer pickCharacteristicSpecificationMaxCardinality) {
		this.characteristicSpecificationMaxCardinality = pickCharacteristicSpecificationMaxCardinality;
	}

	public Integer getUsageSpecificationMinCardinality() {
		return usageSpecificationMinCardinality;
	}

	public void setUsageSpecificationMinCardinality(Integer pickUsageSpecificationMinCardinality) {
		this.usageSpecificationMinCardinality = pickUsageSpecificationMinCardinality;
	}

	public Integer getUsageSpecificationMaxCardinality() {
		return usageSpecificationMaxCardinality;
	}

	public void setUsageSpecificationMaxCardinality(Integer pickUsageSpecificationMaxCardinality) {
		this.usageSpecificationMaxCardinality = pickUsageSpecificationMaxCardinality;
	}

}
