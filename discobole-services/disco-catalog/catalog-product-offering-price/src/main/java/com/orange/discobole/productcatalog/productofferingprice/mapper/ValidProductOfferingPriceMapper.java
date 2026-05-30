// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;

/**
 * This class ValidProductOfferingPriceMapper is used to map data from
 * DefinePOPStatusValidityPeriod of pojo package to
 * DefinePOPStatusValidityPeriod dto.
 * 
 * @author Vivek Singh
 * @since 1.0
 *
 */
public class ValidProductOfferingPriceMapper {

	/**
	 * constructor.
	 */
	private ValidProductOfferingPriceMapper() {
	}

	/**
	 * This convert DefinePOPStatusValidityPeriod of pojo to dto.
	 * 
	 * @param validate : pojo DefinePOPStatusValidityPeriod
	 * @return dto DefinePOPStatusValidityPeriod 
	 */
	public static DefinePOPStatusValidityPeriod toGenerate(
			com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefinePOPStatusValidityPeriod validate) {
		return new DefinePOPStatusValidityPeriod().lifecycleStatus(validate.getLifecycleStatus())
				.validFor(TimePeriodMapper.toGenerated(validate.getValidFor()));
	}

}
