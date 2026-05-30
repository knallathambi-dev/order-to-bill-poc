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

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationshipType;
import com.orange.discobole.productcatalog.catalog.handler.ModifyProductOfferingEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.ModifyProductOfferingListener;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingIncompatibleRelationshipModifiedEvent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class ModifyProductOfferingListenerTest {

    private final ModifyProductOfferingEventHandler handler;

    public ModifyProductOfferingListenerTest() {
        handler = Mockito.mock(ModifyProductOfferingEventHandler.class);
    }

    @Test
    void listenProductOfferingEvent() {
        ModifyProductOfferingListener listener = new ModifyProductOfferingListener();
        AtomicProductOfferingIncompatibleRelationshipModifiedEvent event = new AtomicProductOfferingIncompatibleRelationshipModifiedEvent("productOfferingId",
                new HashSet<>(),
                Set.of(new ProductOfferingRelationship().id("po2").relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE)), OffsetDateTime.now(), new ArrayList<>());
        listener.register(AtomicProductOfferingIncompatibleRelationshipModifiedEvent.class, handler::handle);
        doNothing().when(handler).handle(event);
        Assertions.assertNotNull(event);
    }
}
