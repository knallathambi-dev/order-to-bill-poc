// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.listener;

import static org.mockito.Mockito.doNothing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.handler.CategoryProductOfferingEventHandler;
import com.orange.discobole.productcatalog.productoffering.event.category.CategoryProductOfferingAssociationEvent;

public class CategoryProductOfferingListenerTest extends CategoryApplicationTests {


	private final CategoryProductOfferingEventHandler handler;

	public CategoryProductOfferingListenerTest() {
		handler = Mockito.mock(CategoryProductOfferingEventHandler.class);
	}

	@Test
	public void listenProductOfferingEvent() {
		CategoryProductOfferingListener listener = new CategoryProductOfferingListener();
		
		CategoryProductOfferingAssociationEvent event = new CategoryProductOfferingAssociationEvent("categoryId", null, null);
		listener.register(CategoryProductOfferingAssociationEvent.class, handler::handle);
		doNothing().when(handler).handle(event);
		Assertions.assertNotNull(listener.categoryProductOffering());
	}


}
