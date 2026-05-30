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
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.handler.ModifyCategoryEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.CategoryListener;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;

/**
 * CategoryListenerTest represents test class for Category Listener
 *
 * @author Rajan Chauhan
 */

class ModifyCategoryListenerTest extends CatalogApplicationTests {

	private final ModifyCategoryEventHandler handler;

	ModifyCategoryListenerTest() {
		handler = Mockito.mock(ModifyCategoryEventHandler.class);
	}

	@Test
	void listenCategoryEvent() {
		CategoryListener listener = new CategoryListener();
		CategoryIdentityDataModifiedEvent event = new CategoryIdentityDataModifiedEvent("category1", "cloud services",
				"A category to hold all cloud service offers", true, null, OffsetDateTime.now(),List.of(),null);
		listener.register(CategoryIdentityDataModifiedEvent.class, handler::handle);
		doNothing().when(handler).handle(event);
		Assertions.assertNotNull(listener.category());
	}

}
