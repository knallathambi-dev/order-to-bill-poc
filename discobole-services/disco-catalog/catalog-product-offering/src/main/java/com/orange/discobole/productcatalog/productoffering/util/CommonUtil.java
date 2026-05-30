// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.constant.HttpStatusCodeConstants;
import com.orange.discobole.productcatalog.productoffering.dto.Validation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productoffering.exception.BosInvalidEventException;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.Duration;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;

/**
 * The Class CommonUtil.
 *
 * @since 1.0
 */
public class CommonUtil {

	private CommonUtil() {

	}

	/**
	 * Convert to quantity.
	 *
	 * @param duration the duration
	 * @return the quantity
	 */
	public static com.orange.discobole.productcatalog.productoffering.dto.generated.common.Quantity convertToQuantity(Quantity duration) {
		return new com.orange.discobole.productcatalog.productoffering.dto.generated.common.Quantity().amount(duration.getAmount()).units(duration.getUnits());
	}

	/**
	 * convert duration
	 * @param duration
	 * @return
	 */
	public static com.orange.discobole.productcatalog.productoffering.dto.generated.common.Duration convertToDuration(Duration duration) {
		return new com.orange.discobole.productcatalog.productoffering.dto.generated.common.Duration().amount(duration.getAmount()).units(duration.getUnits());
	}

	/**
	 * * checkNumericRange.
	 *
	 * @author Varshika Choudhary
	 * @param productOfferChar the product offering characteristic
	 * @param productSpecChar  the product spec char
	 * @return true, if successful
	 */
	public static boolean checkNumericRange(PickAtomicProductOfferingCharacteristic productOfferChar,
			ProductSpecificationCharacteristic productSpecChar) {
		boolean check = false;
		Integer prodOffMin = productOfferChar.getMinCardinality();
		Integer prodOffMax = productOfferChar.getMaxCardinality();
		Integer prodSpecMin = productSpecChar.getMinCardinality();
		Integer prodSpecMax = productSpecChar.getMaxCardinality();
		if (prodSpecMin != null && prodSpecMax != null) {
			check=checkMinAndMaxCardinality(prodOffMin,prodOffMax,prodSpecMin,prodSpecMax,productOfferChar,productSpecChar);
		} else if (prodSpecMin != null) {
			if (prodOffMin >= prodSpecMin && prodOffMax >= prodOffMin) {
				if (prodOffMin == 0) {
					productOfferChar.setMaxCardinality(1);
				} else {
					productOfferChar.setMaxCardinality(prodOffMin);
				}
				check = true;
			}
		} else if (prodSpecMax != null) {
			productOfferChar.setMinCardinality(0);
			if (prodOffMax <= prodSpecMax && prodOffMax > 0) {
				check = true;
			}
		} else {
			productOfferChar.setMinCardinality(0);
			productOfferChar.setMaxCardinality(1);
			check = true;
		}
		return check;
	}

	private static boolean checkMinAndMaxCardinality(Integer prodOffMin, Integer prodOffMax, Integer prodSpecMin,
			Integer prodSpecMax,PickAtomicProductOfferingCharacteristic productOfferChar,
			ProductSpecificationCharacteristic productSpecChar) {
		if (prodOffMax == 0) {
			productOfferChar.setMaxCardinality(1);
			prodOffMax = 1;
		}
		if (prodSpecMax == 0) {
			productSpecChar.setMaxCardinality(1);
			prodSpecMax = 1;
		}
		return (prodOffMax >= prodOffMin && prodOffMin >= prodSpecMin && prodOffMin <= prodSpecMax
				&& prodOffMax <= prodSpecMax);

	}

	/**
	 * Check characteristic value range.
	 *
	 * @author Diksha Srivastava
	 * @param productSpecCharacteristicValue the product spec characteristic value
	 * @param minCardinality                 the min cardinality
	 * @param maxCardinality                 the max cardinality
	 * @return true, if successful
	 */
	public static boolean checkCharacteristicValueRange(List<ProductCharValue> productSpecCharacteristicValue,
			Integer minCardinality, Integer maxCardinality) {
		boolean check = true;
		int size = productSpecCharacteristicValue.size();
		if (size < minCardinality || size > maxCardinality) {
			check = false;
		}
		return check;
	}
	

	public static boolean checkCharacteristicValueRange(
			List<ProductCharValue> prodOfferCharValue,
			List<ProductSpecificationCharacteristicValue> prodSpecCharValue,
			Validation validateCharac,
			PickAtomicProductOfferingCharacteristic productOfferChar) {

		Set<String> psCharValue = extractSpecCharacteristicValues(prodSpecCharValue);
		HashMap<String, Boolean> checkRepetition = new HashMap<>();

		int defaultCount = 0;

		for (ProductCharValue poChar : prodOfferCharValue) {
			String mappedValue = mapProductCharValue(poChar, validateCharac);
			if(mappedValue == null && validateCharac.getReason() == null){
				continue;
			}

			if (mappedValue == null) return false;  // Validation already handled

			if (Boolean.TRUE.equals(checkRepetition.get(mappedValue))) {
				throw new BosInvalidEventException(HttpStatusCodeConstants.INVALID_BODY_FIELD,
						"Invalid Product Offering Characteristics Value Selected :",
						"Please enter correct characteristic value, you cannot create multiple values from the same reference value");
			}

			if (!psCharValue.contains(mappedValue)) {
				validateCharac.setReason("CharacteristicValue does not match with ProductSpec CharacteristicValue");
				return false;
			}

			checkRepetition.put(mappedValue, true);
			defaultCount += Boolean.TRUE.equals(poChar.isIsDefault()) ? 1 : 0;
		}

		validateMaxCardinality(defaultCount, productOfferChar.getMaxCardinality());

		return true;
	}

	// Extracts Product Specification Characteristic Values into a Set
	private static Set<String> extractSpecCharacteristicValues(List<ProductSpecificationCharacteristicValue> prodSpecCharValue) {
		return prodSpecCharValue.stream()
				.map(psChar -> (psChar.getCharacteristicReferenceValue() != null)
						? psChar.getCharacteristicReferenceValue() + psChar.getUnitOfMeasure()
						: psChar.getValueFrom() + "-" + psChar.getValueTo())
				.collect(Collectors.toSet());
	}

	// Maps Product Characteristic Values & validates them
	private static String mapProductCharValue(ProductCharValue poChar, Validation validateCharac) {
		if (poChar.getCharacteristicReferenceValue() != null && poChar.getValue() != null) {
			return poChar.getCharacteristicReferenceValue() + poChar.getUnitOfMeasure();
		} else if(poChar.getCharacteristicReferenceValue() == null &&
				poChar.getValue() == null &&
				poChar.getValueFrom() != null &&
				poChar.getValueTo() != null) {
			return poChar.getValueFrom() + "-" + poChar.getValueTo();
		}
		//validity characteristic
		else if(poChar.getTimeRange() != null && (poChar.getTimeRange().getValidFrom() != null || poChar.getTimeRange().getValidTo() != null)) {
			return null;
		}
		validateCharac.setReason("Enter either characteristic value, value range or time range");
		return null;
	}

	// Validates max cardinality condition
	private static void validateMaxCardinality(int defaultCount, int maxCardinality) {
		if (defaultCount > maxCardinality) {
			throw new DiscoClientException("The number of defaults should be less than or equal to maxCardinality");
		}
	}

}
