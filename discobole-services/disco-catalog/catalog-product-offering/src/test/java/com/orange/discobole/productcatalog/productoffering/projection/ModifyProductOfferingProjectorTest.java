// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.projection;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOffCancelledEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.projection.ModifyProductOfferingProjector;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

public class ModifyProductOfferingProjectorTest extends ProductOfferingApplicationTests {

    @InjectMocks
    private final ModifyProductOfferingProjector projector;
    private StreamBridge bridge;



    ModifyProductOfferingProjectorTest() {
        projector = new ModifyProductOfferingProjector();
        bridge = Mockito.mock(StreamBridge.class);
        ReflectionTestUtils.setField(projector, "bridge", bridge);
    }

    @Test
    void handleAtomicProductOfferingCategoryModifiedEvent() {
        AtomicProductOfferingCategoryModifiedEvent event = new AtomicProductOfferingCategoryModifiedEvent();
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingIdentityDataModifiedEvent() {
        AtomicProductOfferingIdentityDataModifiedEvent event = new AtomicProductOfferingIdentityDataModifiedEvent();
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingMarketModifiedEvent() {
        AtomicProductOfferingMarketModifiedEvent event = new AtomicProductOfferingMarketModifiedEvent(null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingRelatedPartyModifiedEvent() {
        AtomicProductOfferingRelatedPartyModifiedEvent event = new AtomicProductOfferingRelatedPartyModifiedEvent("productSpecId", null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingOperationModifiedEvent() {
        AtomicProductOfferingOperationModifiedEvent event = new AtomicProductOfferingOperationModifiedEvent("productSpecId", null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleLinkPOPtoOperModifiedEvent() {
        LinkPOPtoOperModifiedEvent event = new LinkPOPtoOperModifiedEvent("productSpecId", null,null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingTermModifiedEvent() {
        AtomicProductOfferingTermModifiedEvent event = new AtomicProductOfferingTermModifiedEvent("productSpecId", null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingCharacteristicsModifiedEvent() {
        AtomicProductOfferingCharacteristicsModifiedEvent event = new AtomicProductOfferingCharacteristicsModifiedEvent("productSpecId", null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingRelationshipModifiedEvent() {
        AtomicProductOfferingRelationshipModifiedEvent event = new AtomicProductOfferingRelationshipModifiedEvent("productSpecId",null,null,null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingValidForModifiedEvent() {
        AtomicProductOfferingValidForModifiedEvent event = new AtomicProductOfferingValidForModifiedEvent("productSpecId", null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleProductOffCancelledEvent() {
        ProductOffCancelledEvent event = new ProductOffCancelledEvent("productSpecId", new ProductOffering().id("fv"));
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleAtomicProductOfferingIncompatibleRelationshipModifiedEvent() {
        AtomicProductOfferingIncompatibleRelationshipModifiedEvent event = new AtomicProductOfferingIncompatibleRelationshipModifiedEvent("productSpecId",null,null,null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleBundleProductOfferingIncompatibleRelationshipModifiedEvent() {
        BundleProductOfferingIncompatibleRelationshipModifiedEvent event = new BundleProductOfferingIncompatibleRelationshipModifiedEvent("productSpecId",null,null,null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingIncompatibleRelationshipModifiedEvent() {
        ContractProductOfferingIncompatibleRelationshipModifiedEvent event = new ContractProductOfferingIncompatibleRelationshipModifiedEvent("productSpecId",null,null,null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingCategoryModifiedEvent() {
        ContractProductOfferingCategoryModifiedEvent event = new ContractProductOfferingCategoryModifiedEvent("productSpecId", null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingIdentityDataModifiedEvent() {
        ContractProductOfferingIdentityDataModifiedEvent event = new ContractProductOfferingIdentityDataModifiedEvent("productSpecId", null, null, null, null, null, null, null, null, null,ProductOfferingLifecycle.ACTIVE, ProductOfferingLifecycle.ACTIVE);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingModificationValidatedEvent() {
        ContractProductOfferingModificationValidatedEvent event = new ContractProductOfferingModificationValidatedEvent("productSpecId", null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingOperModifiedEvent() {
        ContractProductOfferingOperModifiedEvent event = new ContractProductOfferingOperModifiedEvent("productSpecId", null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingSelectedModifiedEvent() {
        ContractProductOfferingSelectedModifiedEvent event = new ContractProductOfferingSelectedModifiedEvent("productSpecId", null, null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleChildPOInfoModifiedEvent() {
        ChildPOInfoModifiedEvent event = new ChildPOInfoModifiedEvent("productSpecId", null, null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleContractProductOfferingRelationshipModifiedEvent() {
        ContractProductOfferingRelationshipModifiedEvent event = new ContractProductOfferingRelationshipModifiedEvent("productSpecId",null,null,null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleBundleProductOfferingOperModifiedEvent() {
        BundleProductOfferingOperModifiedEvent event = new BundleProductOfferingOperModifiedEvent("productSpecId", null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleBundleProductOfferingCategoryModifiedEvent() {
        BundleProductOfferingCategoryModifiedEvent event = new BundleProductOfferingCategoryModifiedEvent("productSpecId", null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleBundleProductOfferingIdentityDataModifiedEvent() {
        BundleProductOfferingIdentityDataModifiedEvent event = new BundleProductOfferingIdentityDataModifiedEvent("productSpecId", null, null, null, null, null, null, null, null, null, ProductOfferingLifecycle.ACTIVE, ProductOfferingLifecycle.ACTIVE);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleBundleProductOfferingSelectedModifiedEvent() {
        BundleProductOfferingSelectedModifiedEvent event = new BundleProductOfferingSelectedModifiedEvent("productSpecId", null, null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    void handleBundleProductOfferingModificationValidatedEvent() {
        BundleProductOfferingModificationValidatedEvent event = new BundleProductOfferingModificationValidatedEvent("productSpecId", null, null, null);
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

}
