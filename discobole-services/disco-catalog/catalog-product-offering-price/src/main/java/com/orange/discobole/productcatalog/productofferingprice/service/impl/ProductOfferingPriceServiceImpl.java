// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.*;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceTaxAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineInstallmentChargeIdentityData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;

/**
 * Implementation to process Product Offering Price Commands
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Service
public class ProductOfferingPriceServiceImpl implements ProductOfferingPriceService {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPriceServiceImpl.class);

	private final CommandGateway commandGateway;

	@Autowired
	public ProductOfferingPriceServiceImpl(CommandGateway commandGateway) {
		this.commandGateway=commandGateway;
	}
	@Override
	public String createProductOfferingPrice(ProductOfferingPriceType productOfferingPriceType) {
		String productOfferingPriceId=UUID.randomUUID().toString();
		commandGateway.sendAndWait(new InitiateProductOfferingPriceCommand(productOfferingPriceId,productOfferingPriceType));
		LOGGER.debug("Triggering InitiateProductOfferingPriceCommand: {}",productOfferingPriceId);
		return productOfferingPriceId;
	}

	@Override
	public void defineProductOfferingPriceChargeIdentityData(String productOfferingPriceId,
			DefineProductOfferingPriceChargeIdentityData identityData, List<ProductOfferingPriceRelationship> relationships, DefinePOPStatusValidityPeriod validity) {
		LOGGER.debug(">>Triggering ProductOfferingPriceChargeIdentityDataCommand");
		//sending command on the command bus.
		commandGateway.sendAndWait(new ProductOfferingPriceChargeIdentityDataCommand(productOfferingPriceId, identityData, validity, relationships));
		
		LOGGER.debug("<<Triggering ProductOfferingPriceChargeIdentityDataCommand");
	}

	@Override
	public void defineProductOfferingPriceAlterationIdentityData(String productOfferingPriceId,
			DefineProductOfferingPriceAlterationIdentityData identityData, DefinePOPStatusValidityPeriod validity) {
		LOGGER.debug(">>Triggering ProductOfferingPriceAlterationIdentityDataCommand");
		commandGateway.sendAndWait(new ProductOfferingPriceAlterationIdentityDataCommand(productOfferingPriceId,identityData, validity));
		LOGGER.debug("<<Triggering ProductOfferingPriceAlterationIdentityDataCommand");

	}

	@Override
	public void defineProductOfferingPriceTaxAlterationIdentityData(String productOfferingPriceId, DefineProductOfferingPriceTaxAlterationIdentityData identityData, DefinePOPStatusValidityPeriod validity)  {

		LOGGER.debug(">>Triggering ProductOfferingPriceTaxAlterationIdentityDataCommand");
		commandGateway.sendAndWait(new ProductOfferingPriceTaxAlterationIdentityDataCommand(productOfferingPriceId,identityData, validity));
		LOGGER.debug("<<Triggering ProductOfferingPriceTaxAlterationIdentityDataCommand");
	}




	@Override
	public void defineProductOfferingPriceInstallmentPlanIdentityData(String productOfferingPriceId, DefineInstallmentChargeIdentityData identityData, DefinePOPStatusValidityPeriod validity,List<ProductOfferingPriceRelationship> relationships)  {

		LOGGER.debug(">>Triggering ProductOfferingPriceInstallmentChargeIdentityDataCommand");
		commandGateway.sendAndWait(new ProductOfferingPriceInstallmentChargeIdentityDataCommand(productOfferingPriceId,identityData, validity,relationships));
		LOGGER.debug("<<Triggering ProductOfferingPriceInstallmentChargeIdentityDataCommand");
	}

	@Override
	public void cancelProductOfferingPrice(String productOfferingPriceId) {
		LOGGER.debug(">>Triggering ProductOfferingPriceCancelCommand");
		//sending command on the command bus.
		commandGateway.sendAndWait(new ProductOfferingPriceCancelCommand(productOfferingPriceId));
		LOGGER.debug("<<Triggering ProductOfferingPriceCancelCommand");
	}

	@Override
	public void deleteProductOfferingPrice(Long interval, OffsetDateTime delDate, String intervalUnit) {

		commandGateway.sendAndWait(new ProductOfferingPriceDeleteCommand(delDate, interval, intervalUnit));
		LOGGER.debug("<<Triggering ProductOfferingPriceDeleteCommand");
	}

}
