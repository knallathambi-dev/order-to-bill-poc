// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefineInstallmentChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;



/**
 * This class ProductOfferingPriceInstallmentChargeIdentityDataMapper is used to map
 * data from DefineProductOfferingPriceInstallmentChargeIdentityData of pojo package to
 * DefineProductOfferingPriceAlterationIdentityData dto.
 * 
 * @author Vivek Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceInstallmentChargeIdentityDataMapper {

	private ProductOfferingPriceInstallmentChargeIdentityDataMapper() {
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


	/**
	 * converts pojo into dto.
	 *
	 * @param identityData : DefineProductOfferingPriceAlterationIdentityData
	 * @return DefineProductOfferingPriceAlterationIdentityData
	 */

	public static DefineInstallmentChargeIdentityData toGenerated(
			com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineInstallmentChargeIdentityData identityData) {

		if (identityData == null) {
			return null;
		}
		return new DefineInstallmentChargeIdentityData().name(identityData.getName())
				.description(identityData.getDescription())
				.partner(identityData.getPartner())
				.downPayment(identityData.getDownPayment())
				.applicationDuration(identityData.getApplicationDuration())
				.externalId(identityData.getExternalId())
				.price(priceMapper(identityData.getPrice()))
                .interestRate((identityData.getInterestRate()));
	}
}
