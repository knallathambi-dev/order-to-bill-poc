// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;

/**
 * This class ProductOfferingPriceChargeIdentityDataMapper is used to map data
 * from DefineProductOfferingPriceChargeIdentityData of pojo package to
 * DefineProductOfferingPriceChargeIdentityData dto.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceChargeIdentityDataMapper {
	private ProductOfferingPriceChargeIdentityDataMapper() {
	}

	/**
	 * this method converts pojo Money into dto Money.
	 * 
	 * @param  price  : pojo Money
	 * @return Money  : dto Money
	 */
	private static Money priceMapper(com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Money price) {
		if (price == null)
			return null;
		return new Money().unit(price.getUnit()).value(price.getValue());
	}

	/**
	 * this method converts pojo Quantity from dto Quantity
	 * 
	 * @param   unitOfMeasure : pojo Quantity
	 * @return  dto Quantity
	 */
	private static Quantity unitOfMeasureMapper(
			com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Quantity unitOfMeasure) {
		if (unitOfMeasure == null)
			return null;
		return new Quantity().amount(unitOfMeasure.getAmount()).units(unitOfMeasure.getUnits());
	}

	/**
	 * this method converts pojo DefineProductOfferingPriceChargeIdentityData into
	 * dto.
	 * 
	 * @param   identityData : DefineProductOfferingPriceChargeIdentityData
	 * @return  DefineProductOfferingPriceChargeIdentityData
	 */
	public static DefineProductOfferingPriceChargeIdentityData toGenerated(
			com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData identityData) {
		if (identityData == null) {
			return null;
		}
		return new DefineProductOfferingPriceChargeIdentityData().name(identityData.getName())
				.description(identityData.getDescription()).price(priceMapper(identityData.getPrice()))
				.priceType(identityData.getPriceType())
				.prorationType(identityData.getProrationType())
				.immediatePayment(identityData.getImmediatePayment())
				.recurringChargePeriodLength(identityData.getRecurringChargePeriodLength())
				.recurringChargePeriodType(identityData.getRecurringChargePeriodType())
				.chargeCycle(identityData.getChargeCycle())
				.unitOfMeasure(unitOfMeasureMapper(identityData.getUnitOfMeasure()));
	}

}
