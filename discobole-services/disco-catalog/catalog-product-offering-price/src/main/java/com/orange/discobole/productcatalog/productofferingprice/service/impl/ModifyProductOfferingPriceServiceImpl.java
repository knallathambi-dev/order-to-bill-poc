// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;



import java.util.List;


import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.InitiateProductOfferingPriceModificationCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyProductOfferingPriceAlterationIdentityDataCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyProductOfferingPriceChargeIdentityDataCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.POPModificationCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationCancelCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationValidatedCommand;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;

/**
 * Implementation to process Product Offering Price Commands
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
@Service
public class ModifyProductOfferingPriceServiceImpl implements ModifyProductOfferingPriceService {

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingPriceServiceImpl.class);

	@Resource
	private Publisher publisher;

	@Resource
	private QueryService queryService;

	private final CommandGateway commandGateway;

	@Autowired
	public ModifyProductOfferingPriceServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@Override
	public String modifyProductOfferingPrice(String productOfferingPriceId,
											 ProductOfferingPriceType productOfferingPriceType) {
		LOGGER.debug("Triggering InitiateProductOfferingPriceModificationCommand: {}", productOfferingPriceId);
		return commandGateway.sendAndWait(
				new InitiateProductOfferingPriceModificationCommand(productOfferingPriceId, productOfferingPriceType));
	}

	@Override
	public void modifyPOPChargeIdentityData(String productOfferingPriceId,
											DefineProductOfferingPriceChargeIdentityData identityData,
											List<ProductOfferingPriceRelationship> relationships, DefinePOPStatusValidityPeriod validity) {
		LOGGER.debug("Triggering ModifyProductOfferingPriceChargeIdentityDataCommand: {}", productOfferingPriceId);
		commandGateway.sendAndWait(new ModifyProductOfferingPriceChargeIdentityDataCommand(productOfferingPriceId,
				identityData, validity, relationships));
	}

	@Override
	public void modifyPOPAlterationIdentityData(String productOfferingPriceId,
												DefineProductOfferingPriceAlterationIdentityData identityData, DefinePOPStatusValidityPeriod validity) {
		LOGGER.debug("Triggering ModifyProductOfferingPriceAlterationIdentityDataCommand: {}", productOfferingPriceId);
		commandGateway.sendAndWait(new ModifyProductOfferingPriceAlterationIdentityDataCommand(productOfferingPriceId,
				identityData, validity));

	}

	@Override
	public void cancelProductOfferingPriceModification(String productOfferingPriceId) {
		LOGGER.debug("Triggering ProductOfferingPriceModificationCancelCommand: {}", productOfferingPriceId);
		commandGateway.sendAndWait(new ProductOfferingPriceModificationCancelCommand(productOfferingPriceId));

	}

	@Override
	public void initiatePOPModification(String popId) {
		LOGGER.debug("Triggering POPModificationCommand: {}", popId);
		commandGateway.sendAndWait(new POPModificationCommand(popId));
	}

	@Override
	public void validatePOPModification(String popId, String versionType) {
		LOGGER.debug("Triggering ProductOfferingPriceModificationValidatedCommand: {}", popId);
		commandGateway.sendAndWait(new ProductOfferingPriceModificationValidatedCommand(popId, versionType));
	}

}
