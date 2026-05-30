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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ServiceSpecificationRef;
import com.orange.discobole.productcatalog.catalog.handler.ProductSpecEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.ProductSpecListener;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecInitiatedEvent;

class ProductSpecListenerTest extends CatalogApplicationTests {

	private final ProductSpecEventHandler handler;

	public ProductSpecListenerTest() {
		handler = Mockito.mock(ProductSpecEventHandler.class);
	}

	@Test
	void listenProductOfferingEvent() {
		ProductSpecListener listener = new ProductSpecListener();
		ProductSpecInitiatedEvent event = new ProductSpecInitiatedEvent("productSpecId",
				ProductSpecificationLifecycle.ACTIVE, "CFSSpec", new ServiceSpecificationRef(), OffsetDateTime.now(),
				null,null);
		listener.register(ProductSpecInitiatedEvent.class, handler::handle);
		doNothing().when(handler).handle(event);
		Assertions.assertNotNull(listener.productSpec());
	}

}
