// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;


class LifeCycleEventHandlerTest extends CatalogApplicationTests {

	@InjectMocks
	LifeCycleEventHandler lifeCycleEventHandler;

	@Mock
	ProductOfferingPriceService productOfferingPriceService;

	// @Test
	// void TestHandleMethodForEntityTypeProDuctOfferingPrice() {
	// 	OffsetDateTime lastUpdate = OffsetDateTime.now();
	// 	LifeCycleStateSelectedEvent event = new LifeCycleStateSelectedEvent("productOfferingPrice_id",
	// 			"produrctOfferingPrice_id", EntityType.PRODUCTOFFERINGPRICE, "unavailable", "launched", lastUpdate,"0.1");
	// 	Mockito.when(productOfferingPriceService.getProductOfferingPriceById(event.getEntityId()))
	// 			.thenReturn(getDummyProductOfferingPrice());
	// 	lifeCycleEventHandler.handle(event);
	// 	verify(productOfferingPriceService).saveProductOfferingPrice(new ProductOfferingPrice()
	// 			.id("produrctOfferingPrice_id").lifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE)
	// 			.description("pop-1").lastUpdate(lastUpdate).version("0.1"));
	// }

	private ProductOfferingPrice getDummyProductOfferingPrice() {
		ProductOfferingPrice productOfferingPrice = new ProductOfferingPrice();
		productOfferingPrice.setId("produrctOfferingPrice_id");
		productOfferingPrice.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		productOfferingPrice.setDescription("pop-1");
		return productOfferingPrice;
	}

}
