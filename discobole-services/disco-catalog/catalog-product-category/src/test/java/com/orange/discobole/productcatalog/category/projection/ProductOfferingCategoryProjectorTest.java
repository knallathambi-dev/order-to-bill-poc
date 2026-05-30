// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.projection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

public class ProductOfferingCategoryProjectorTest {

    private final ProductOfferingCategoryProjector projector;
    private StreamBridge bridge;

    public ProductOfferingCategoryProjectorTest() {
        projector = new ProductOfferingCategoryProjector();
        bridge = Mockito.mock(StreamBridge.class);
        ReflectionTestUtils.setField(projector, "bridge", bridge);
    }

    private static final String PRODUCTOFFERINGID = "poId";

    @Test
    public void CategoryIdentityDataDefinedEvent() {
        projector.handle(new
                ProductOfferingCategoryAssociationEvent(PRODUCTOFFERINGID,Set.of(new
                CategoryRef()),true, ProductOfferingType.ATOMICPRODUCTOFFERING.getValue()));
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    public void CategoryIdentityDataDefinedEvent1() {
        projector.handle(new
                ProductOfferingCategoryAssociationDeletedEvent(PRODUCTOFFERINGID,Set.of(new
                CategoryRef()),true));
        verify(bridge).send(anyString(), any(Message.class));
    }
}
