// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.handler;


import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;

@Component
public class ProductOfferingCategoryEventHandler {
	@Resource
	private ModifyProductOfferingService productOfferingService;
	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingCategoryEventHandler.class);

	public void handle(ProductOfferingCategoryAssociationEvent event) {
		LOGGER.info("handle ProductOfferingCategoryAssociationEvent {}", event);
		productOfferingService.callPOAggregateForModification(event.getProductOfferingId(), event.getCategories(),
				event.isIsAddition(),event.getProductOfferingType());
	}

	public void handle(ProductOfferingCategoryAssociationDeletedEvent event) {
		LOGGER.info("handle ProductOfferingCategoryAssociationDeletedEvent: {}",event);
	}
}
