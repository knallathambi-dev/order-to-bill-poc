// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.util;

import java.util.ArrayList;
import java.util.List;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristicRelationship;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productspecification.mapper.TimeRangeMapper;
import com.orange.discobole.productcatalog.productspecification.pojo.CharacteristicValueSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productspecification.pojo.ServiceSpecificationCharRelationship;

/**
 * Utility class to convert from ServiceSpecification components to
 * ProductSpecification components
 *
 * @author Vivek Singh
 * @since 1.0
 */
public class ConverterUtil {

	/**
	 * Instantiates a new ConverterUtil.
	 */
	private ConverterUtil() {

	}

	/**
	 * Method to convert ServiceSpecCharacteristic to
	 * ProductSpecificationCharacteristic
	 *
	 * @param serviceSpecCharacteristic
	 * @return ProductSpecificationCharacteristic
	 */
	public static ProductSpecificationCharacteristic convert(CharacteristicSpecification serviceSpecCharacteristic) {
		return new ProductSpecificationCharacteristic().configurable(serviceSpecCharacteristic.isConfigurable())
				.extensible(serviceSpecCharacteristic.isExtensible()).name(serviceSpecCharacteristic.getName())
				.type(serviceSpecCharacteristic.getType()).id(serviceSpecCharacteristic.getId())
				.isUnique(serviceSpecCharacteristic.isIsUnique())
				.description(serviceSpecCharacteristic.getDescription())
				.maxCardinality(serviceSpecCharacteristic.getMaxCardinality())
				.minCardinality(serviceSpecCharacteristic.getMinCardinality())
				.regex(serviceSpecCharacteristic.getRegex()).validFor(serviceSpecCharacteristic.getValidFor())
				.schemaLocation(serviceSpecCharacteristic.getSchemaLocation());
	}

	/**
	 * Method to convert CharacteristicValueSpecification to
	 * StockItemCharacteristicValue
	 *
	 * @param characteristicValueSpecification
	 * @return ProductSpecificationCharacteristicValue
	 */
	public static ProductSpecificationCharacteristicValue convert(CharacteristicValueSpecification characteristicValueSpecification) {
		return new ProductSpecificationCharacteristicValue()
				.validFor(TimePeriodMapper.toGenerated(characteristicValueSpecification.getValidFor()))
				.isSelectable(characteristicValueSpecification.getIsSelectable())
				.value(characteristicValueSpecification.getValue())
				.characteristicReferenceValue(characteristicValueSpecification.getStockItemCharacteristicValueReference());
	}

	/**
	 * Method to convert ServiceSpecCharacteristicValue to
	 * ProductSpecificationCharacteristicValue
	 *
	 * @param serviceSpecCharacteristicValue
	 * @return ProductSpecificationCharacteristicValue
	 */
	public static ProductSpecificationCharacteristicValue convert(ProductCharValue serviceSpecCharacteristicValue) {
		return new ProductSpecificationCharacteristicValue().isDefault(serviceSpecCharacteristicValue.isIsDefault())
				.isSelectable(serviceSpecCharacteristicValue.getIsSelectable())
				.rangeInterval(serviceSpecCharacteristicValue.getRangeInterval())
				.regex(serviceSpecCharacteristicValue.getRegex())
				.unitOfMeasure(serviceSpecCharacteristicValue.getUnitOfMeasure())
				.timeRange(TimeRangeMapper.toGenerated(serviceSpecCharacteristicValue.getTimeRange()))
				.validFor(TimePeriodMapper.toGenerated(serviceSpecCharacteristicValue.getValidFor()))
				.valueType(serviceSpecCharacteristicValue.getValueType())
				.valueTo(serviceSpecCharacteristicValue.getValueTo())
				.valueFrom(serviceSpecCharacteristicValue.getValueFrom())
				.value(serviceSpecCharacteristicValue.getValue())
				.characteristicReferenceValue(serviceSpecCharacteristicValue.getServiceSpecCharacteristicReferenceValue());
	}

	public static List<ProductSpecificationCharacteristicValue> convert(List<ProductCharValue> characteristicValue) {
		List<ProductSpecificationCharacteristicValue> psCharValue = new ArrayList<>();
		for (ProductCharValue charVal : characteristicValue) {
			psCharValue.add(convert(charVal));
		}
		return psCharValue;
	}

	public static ProductSpecificationCharacteristicRelationship convert(
			ServiceSpecificationCharRelationship serviceSpecRel) {
		return new ProductSpecificationCharacteristicRelationship().id(serviceSpecRel.getId())
				.relationshipType(serviceSpecRel.getRelationshipType())
				.validFor(TimePeriodMapper.toGenerated(serviceSpecRel.getValidFor()));
	}

}
