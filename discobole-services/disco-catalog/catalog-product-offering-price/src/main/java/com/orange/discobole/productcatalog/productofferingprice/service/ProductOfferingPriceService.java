// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceTaxAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineInstallmentChargeIdentityData;


/**
 * Contract to process Product Offering price Commands.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public interface ProductOfferingPriceService {

	/**
	 * Function to handle product offering price creation command
	 * 
	 * @param productOfferingPriceType
	 * @return product offering price id
	 */
	String createProductOfferingPrice(ProductOfferingPriceType productOfferingPriceType);

	/**
	 * Function to handle product offering price charge define command
	 * 
	 * @param productOfferingPriceId
	 * @param identityData
	 * @param relationships
	 * @param definePOPStatusValidityPeriod
	 */
	void defineProductOfferingPriceChargeIdentityData(String productOfferingPriceId,
			DefineProductOfferingPriceChargeIdentityData identityData,
			List<ProductOfferingPriceRelationship> relationships,
			DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod);

	/**
	 * Function to handle product offering price altered price command
	 * 
	 * @param productOfferingPriceId
	 * @param identityData
	 * @param definePOPStatusValidityPeriod
	 */
	void defineProductOfferingPriceAlterationIdentityData(String productOfferingPriceId,
			DefineProductOfferingPriceAlterationIdentityData identityData, DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod);

	/**
	 * Function to handle product offering price tax altered price command
	 *
	 * @param productOfferingPriceId
	 * @param identityData
	 * @param definePOPStatusValidityPeriod
	 */
	void defineProductOfferingPriceTaxAlterationIdentityData(String productOfferingPriceId,
															 DefineProductOfferingPriceTaxAlterationIdentityData identityData, DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod);


	void defineProductOfferingPriceInstallmentPlanIdentityData(String productOfferingPriceId,
                                                               DefineInstallmentChargeIdentityData identityData, DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod,	List<ProductOfferingPriceRelationship> relationshipsrelationships);



	/**
	 * Function to handle product offering price process cancel command
	 * 
	 * @param productOfferingPriceId
	 */
	void cancelProductOfferingPrice(String productOfferingPriceId);
	
	/**
	 * Function to handle delete product offering price command
	 * 
	 * @param interval
	 * @param delDate
	 * @param intervalUnit
	 */
	void deleteProductOfferingPrice(Long interval,OffsetDateTime delDate,String intervalUnit);
}
