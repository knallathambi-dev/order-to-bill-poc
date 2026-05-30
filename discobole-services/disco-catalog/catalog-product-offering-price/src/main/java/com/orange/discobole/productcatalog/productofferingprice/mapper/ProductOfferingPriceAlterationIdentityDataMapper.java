// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;

/**
 * This class ProductOfferingPriceAlterationIdentityDataMapper is used to map
 * data from DefineProductOfferingPriceAlterationIdentityData of pojo package to
 * DefineProductOfferingPriceAlterationIdentityData dto.
 * 
 * @author Vivek Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceAlterationIdentityDataMapper {

	private ProductOfferingPriceAlterationIdentityDataMapper() {
	}

	/**
	 * converts pojo into dto
	 * 
	 * @param price : Money
	 * @return Money
	 */
	private static Money priceMapper(com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Money price) {
		if (price == null)
			return null;
		return new Money().unit(price.getUnit()).value(price.getValue());
	}

	/**
	 * converts pojo into dto.
	 * 
	 * @param   unitOfMeasure : Quantity
	 * @return  Quantity
	 */
	private static Quantity unitOfMeasureMapper(
			com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Quantity unitOfMeasure) {
		if (unitOfMeasure == null)
			return null;
		return new Quantity().amount(unitOfMeasure.getAmount()).units(unitOfMeasure.getUnits());
	}

	/**
	 * converts pojo into dto.
	 * 
	 * @param identityData : DefineProductOfferingPriceAlterationIdentityData
	 * @return DefineProductOfferingPriceAlterationIdentityData
	 */
	public static DefineProductOfferingPriceAlterationIdentityData toGenerated(
			com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData identityData) {

		if (identityData == null) {
			return null;
		}
		return new DefineProductOfferingPriceAlterationIdentityData().name(identityData.getName())
				.description(identityData.getDescription()).applicationDuration(identityData.getApplicationDuration())
				.unitOfMeasure(unitOfMeasureMapper(identityData.getUnitOfMeasure()))
				.percentage(identityData.getPercentage()).price(priceMapper(identityData.getPrice()))
				.priceType(identityData.getPriceType()).priority(identityData.getPriority())
				.prorationType(identityData.getProrationType())
				.priceAlterationType(identityData.getPriceAlterationType())
                .applicationOffset(identityData.getApplicationOffset());
	}
}
