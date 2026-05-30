// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;


import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
import com.orange.discobole.productcatalog.catalog.service.ProductSpecService;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;

/**
 * handler for Life cycle events.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Component
public class LifeCycleEventHandler {

	private static final Logger LOGGER = LogManager.getLogger(DeleteCategoryEventHandler.class);

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

	public void handle(final LifeCycleStateSelectedEvent event) {
		LOGGER.info("Handling LifeCycleStateSelectedEvent");
		if (EntityType.PRODUCTSPECIFICATION == event.getEntityType()) {
			ProductSpecification productSpecification = productSpecService
					.fetchProductSpecificationById(event.getEntityId());
			productSpecification.lifecycleStatus(ProductSpecificationLifecycle.fromValue(event.getCurrentState()))
					.lastUpdate(event.getLastUpdate()).version(event.getVersion());
			productSpecService.saveProductSpecification(productSpecification);
		}

		if ((EntityType.ATOMICOFFER == event.getEntityType()) || (EntityType.BUNDLEPRODUCTOFFERING == event.getEntityType()) || (EntityType.CONTRACT == event.getEntityType()))
		{
			ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getEntityId());
			productOffering.lifecycleStatus(ProductOfferingLifecycle.fromValue(event.getCurrentState()))
					.lastUpdate(event.getLastUpdate()).version(event.getVersion());
			productOfferingService.saveProductOffering(productOffering);
		}

		if (EntityType.PRODUCTOFFERINGPRICE == event.getEntityType()) {
			ProductOfferingPrice productOfferingPrice = productOfferingPriceService
					.getProductOfferingPriceById(event.getEntityId());
			productOfferingPrice.lifecycleStatus(ProductOfferingPriceLifecycle.fromValue(event.getCurrentState()))
					.lastUpdate(event.getLastUpdate()).version(event.getVersion());

			productOfferingPriceService.saveProductOfferingPrice(productOfferingPrice);
		}
	}

}