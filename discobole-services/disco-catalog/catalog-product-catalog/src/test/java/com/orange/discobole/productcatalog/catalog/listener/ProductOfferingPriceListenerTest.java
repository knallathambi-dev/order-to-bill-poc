// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.listener;

import static org.mockito.Mockito.doNothing;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.catalog.handler.ProductOfferingPriceEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.ProductOfferingPriceListener;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceInitiatedEvent;


class ProductOfferingPriceListenerTest extends CatalogApplicationTests {

	@Mock
	private ProductOfferingPriceEventHandler handler;

	@Test
	void listenProductOfferingPriceTest() {
		ProductOfferingPriceListener productOfferingPriceListener = new ProductOfferingPriceListener();
		ProductOfferingPriceInitiatedEvent offeringPriceInitiatedEvent = new ProductOfferingPriceInitiatedEvent(
				UUID.randomUUID().toString(), ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
				ProductOfferingPriceLifecycle.UNAVAILABLE);
		productOfferingPriceListener.register(ProductOfferingPriceInitiatedEvent.class, handler::handle);
		doNothing().when(handler).handle(offeringPriceInitiatedEvent);
		Assertions.assertNotNull(productOfferingPriceListener.productOfferingPrice());
	}
}