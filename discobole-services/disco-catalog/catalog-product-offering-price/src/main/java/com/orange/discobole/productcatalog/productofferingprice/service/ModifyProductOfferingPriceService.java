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

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;


/**
 * Contract to process Product Offering price Commands.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
public interface ModifyProductOfferingPriceService {

	/**
	 * Function to handle product offering price creation command
	 * 
	 * @param productOfferingPriceType
	 * @return product offering price id
	 */
	String modifyProductOfferingPrice(String productOfferingPriceId,ProductOfferingPriceType productOfferingPriceType);

	/**
	 * Function to handle product offering price charge define command
	 * 
	 * @param productOfferingPriceId
	 * @param identityData
	 * @param definePOPStatusValidityPeriod 
	 * @param relationships 
	 */
	void modifyPOPChargeIdentityData(String productOfferingPriceId,
			DefineProductOfferingPriceChargeIdentityData identityData, List<ProductOfferingPriceRelationship> relationships, DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod);

	/**
	 * Function to handle product offering price altered price command
	 * 
	 * @param productOfferingPriceId
	 * @param identityData
	 * @param definePOPStatusValidityPeriod 
	 */
	void modifyPOPAlterationIdentityData(String productOfferingPriceId,
			DefineProductOfferingPriceAlterationIdentityData identityData, DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod);

	/**
	 * Function to handle product offering price process cancel command
	 * 
	 * @param productOfferingPriceId
	 */
	void cancelProductOfferingPriceModification(String productOfferingPriceId);

	/**
	 * Function to initiate POP Modification Process
	 * 
	 * @param popId
	 */
	void initiatePOPModification(String popId);

	/**
	 * Function to validate POP Modification Process
	 * 
	 * @param popId
	 * @param versionType
	 */
	void validatePOPModification(String popId, String versionType);
}
