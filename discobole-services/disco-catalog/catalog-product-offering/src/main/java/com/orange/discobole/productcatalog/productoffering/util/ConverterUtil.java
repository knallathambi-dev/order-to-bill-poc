// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import java.util.ArrayList;
import java.util.List;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicRelationship;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.mapper.TimeRangeMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.CharacteristicValueSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.pojo.ServiceSpecificationCharRelationship;

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
	 * Method to convert CharacteristicValueSpecification to
	 * StockItemCharacteristicValue
	 *
	 * @param characteristicValueSpecification
	 * @return ProductSpecificationCharacteristicValue
	 */
	public static ProductSpecificationCharacteristicValue convert(CharacteristicValueSpecification characteristicValueSpecification) {
		return new ProductSpecificationCharacteristicValue()
				.validFor(TimePeriodMapper.toGenerated(characteristicValueSpecification.getValidFor()))
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
		return new ProductSpecificationCharacteristicValue().isDefault(serviceSpecCharacteristicValue.isIsDefault() != null ? serviceSpecCharacteristicValue.isIsDefault() : false)
				.rangeInterval(serviceSpecCharacteristicValue.getRangeInterval())
				.regex(serviceSpecCharacteristicValue.getRegex())
				.unitOfMeasure(serviceSpecCharacteristicValue.getUnitOfMeasure())
				.timeRange(TimeRangeMapper.toGenerated(serviceSpecCharacteristicValue.getTimeRange()))
				.validFor(TimePeriodMapper.toGenerated(serviceSpecCharacteristicValue.getValidFor()))
				.valueType(serviceSpecCharacteristicValue.getValueType())
				.valueTo(serviceSpecCharacteristicValue.getValueTo())
				.valueFrom(serviceSpecCharacteristicValue.getValueFrom())
				.value(serviceSpecCharacteristicValue.getValue())
				.characteristicReferenceValue(serviceSpecCharacteristicValue.getCharacteristicReferenceValue());
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
