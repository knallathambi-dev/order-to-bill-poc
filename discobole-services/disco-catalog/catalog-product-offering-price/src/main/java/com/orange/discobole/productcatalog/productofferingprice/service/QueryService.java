// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service;

import java.util.List;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Currency;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Frequency;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
/**
 * This interface corresponds to declare the operations related to
 * {@code ServiceSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public interface QueryService {
	/**
	 * Function to get POP by id.
	 * 
	 * @param productOfferingPriceId
	 * @return
	 */
	ProductOfferingPrice getProductOfferingPrice(String productOfferingPriceId);
	
	List<ProductOffering> fetchProductOfferingsByProductOfferingPriceId(String productOfferingPriceId);

	List<Frequency> fetchFrequency();

	List<Currency> fetchCurrency();
}
