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
import com.orange.discobole.productcatalog.catalog.constant.CategoryType;
import com.orange.discobole.productcatalog.catalog.handler.CategoryEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.CategoryListener;
import com.orange.discobole.productcatalog.category.event.category.EntityTypeSelectedEvent;

/**
 * CategoryListenerTest represents  test class for Category Listener
 *
 * @author Varshika Choudhary
 */

class CategoryListenerTest extends CatalogApplicationTests {

    private final CategoryEventHandler handler;

    public CategoryListenerTest() {
        handler = Mockito.mock(CategoryEventHandler.class);
    }

    @Test
    void listenCategoryEvent() {
        CategoryListener listener = new CategoryListener();
        EntityTypeSelectedEvent event = new EntityTypeSelectedEvent("category1", CategoryType.PRODUCTOFFERINGCATEGORY.toString(), OffsetDateTime.now());
        listener.register(EntityTypeSelectedEvent.class, handler::handle);
        doNothing().when(handler).handle(event);
        listener.category();
        Assertions.assertNotNull(listener.category());
    }

}
