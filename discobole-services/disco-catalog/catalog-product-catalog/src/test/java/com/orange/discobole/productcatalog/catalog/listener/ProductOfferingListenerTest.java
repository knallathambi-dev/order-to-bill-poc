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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.handler.ProductOfferingEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.ProductOfferingListener;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.AtomicProductOfferingInitiatedEvent;


class ProductOfferingListenerTest extends CatalogApplicationTests {

	private final ProductOfferingEventHandler handler;

	public ProductOfferingListenerTest() {
		handler = Mockito.mock(ProductOfferingEventHandler.class);
	}

	@Test
	void listenProductOfferingEvent() {
		ProductOfferingListener listener = new ProductOfferingListener();
		AtomicProductOfferingInitiatedEvent event = new AtomicProductOfferingInitiatedEvent(
				new ProductSpecificationRef().id("productSpecId"), "productOfferingId",
				ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now());
		listener.register(AtomicProductOfferingInitiatedEvent.class, handler::handle);
		doNothing().when(handler).handle(event);
		Assertions.assertNotNull(listener.productOffering());
	}

}
