// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.projection;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

class ProductSpecProjectorTest extends ProductSpecificationApplicationTests {



	private final ProductSpecProjector projector;

	private StreamBridge bridge;

	ProductSpecProjectorTest() {
		projector = new ProductSpecProjector();
		bridge = Mockito.mock(StreamBridge.class);
		ReflectionTestUtils.setField(projector, "bridge", bridge);
	}

	@Test
	void handleProductSpecInitiatedEvent() {
		projector.handle(new ProductSpecInitiatedEvent("productSpecId", ProductSpecificationLifecycle.ACTIVE,
				SupportEntity.CFSSPEC.toString(), new ServiceSpecificationRef(), OffsetDateTime.now(), null, null));
		verify(bridge).send(anyString(), any(Message.class));

	}

	@Test
	void handleStockItemProductSpecInitiatedEvent() {
		projector.handle(new ProductSpecInitiatedEvent("productSpecId", ProductSpecificationLifecycle.ACTIVE,
				SupportEntity.STOCKITEMTYPE.toString(), null, OffsetDateTime.now(), new StockItemType().id("SIT1"), null));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecDescribedEvent() {

		IdentityData description=new IdentityData();
		description.setBrand("cisco");
		description.setDescription("description_1");
		description.setName("name_1");
		description.setProductNumber("123");
		
		projector.handle(new ProductSpecIdentityDataEvent("productSpecId",description,new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>(),new TimePeriod(),
				OffsetDateTime.now(), EntityType.PRODUCTSPECIFICATION, "http://localhost:8080"));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecOpDefinedEvent() {
		projector.handle(new ProductSpecOpDefinedEvent("productSpecId", List.of(new OperationSpecification()),
				OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecCharacteristicsDefinedEvent() {
		projector.handle(new ProductSpecCharacteristicsDefinedEvent("productSpecId",
				List.of(new ProductSpecificationCharacteristic()),new ArrayList<UsageSpecification>(), OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecRelationDefinedEvent() {
		projector.handle(new ProductSpecRelationDefinedEvent("productSpecId",
				List.of(new ProductSpecificationRelationship()), OffsetDateTime.now(), List.of(new PolicyRuleRef())));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingValidatedEvent() {
		ProductSpecValidatedEvent event = new ProductSpecValidatedEvent("productSpecId",
				ProductSpecificationLifecycle.INTEST, OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingVersionCreatedEvent() {
		ProductSpecVersionCreatedEvent event = new ProductSpecVersionCreatedEvent("productSpecId", "1.0.0",
				OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

    @Test
    @DisplayName(value = "Product Spec. Cancel Projection method called")
     void handleProductSpecCancelledEventTest() {
        ProductSpecCancelledEvent event = new ProductSpecCancelledEvent("productSpecId1",new ProductSpecification().id("productSpecId1")
                .lifecycleStatus(ProductSpecificationLifecycle.INSTUDY).lastUpdate(OffsetDateTime.now()));
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

    @Test
    @DisplayName(value = "Invalid Product Spec. Cancel Projection method called")
     void handleInvalidProductSpecCancelledEventTest() {
        ProductSpecCancelledEvent event = new ProductSpecCancelledEvent("productSpecId1",new ProductSpecification().id("productSpecId1")
                .lifecycleStatus(ProductSpecificationLifecycle.INTEST).lastUpdate(OffsetDateTime.now()));
        projector.handle(event);
        verify(bridge).send(anyString(), any(Message.class));
    }

	@Test
	void handleProductSpecDescriptionModifiedEvent() {
		projector.handle(new ProductSpecDefineIdentityModifiedEvent("productSpecId", new IdentityData(),new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>() ,new TimePeriod(),
				OffsetDateTime.now(), null,ProductSpecificationLifecycle.ACTIVE, ProductSpecificationLifecycle.ACTIVE));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecCharacteristicsModifiedEvent() {
		projector.handle(new ProductSpecCharacteristicsModifiedEvent("productSpecId",
				new ArrayList<ProductSpecificationCharacteristic>(),new ArrayList<UsageSpecification>(), OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecRelationModifiedEvent() {
		projector.handle(
				new ProductSpecRelationModifiedEvent("productSpecId", new ArrayList<ProductSpecificationRelationship>(),
						new ArrayList<PolicyRuleRef>(), OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}


	@Test
	void handleComputeProductConfigurationEvent() {
		projector.handle(new ComputeProductConfigurationEvent(new ArrayList<ProductConfigurationSpec>(), "stockItem1"));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleLinkProductSpecificationToStockItemEvent() {
		projector.handle(new LinkProductSpecificationToStockItemEvent("stockItem1",
				new ArrayList<ProductConfigurationSpec>(), OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecificationDeleteEvent() {
		projector.handle(new ProductSpecificationDeleteEvent("ProductSpec1", OffsetDateTime.now(), 40L, "HOURS"));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecificationTemporaryDeleteEvent() {
		projector.handle(new ProductSpecificationTemporaryDeleteEvent("ProductSpec1"));
		verify(bridge).send(anyString(), any(Message.class));
	}

}
