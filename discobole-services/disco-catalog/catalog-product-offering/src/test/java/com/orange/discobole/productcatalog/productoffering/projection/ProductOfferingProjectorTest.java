// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.projection;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.dto.InvalidCharacteristics;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.projection.ProductOfferingProjector;
import com.orange.discobole.productcatalog.productoffering.util.ConverterUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

class ProductOfferingProjectorTest extends ProductOfferingApplicationTests {

	@InjectMocks
	private final ProductOfferingProjector projector;
	private StreamBridge bridge;

	ProductOfferingProjectorTest() {
		projector = new ProductOfferingProjector();
		bridge = Mockito.mock(StreamBridge.class);
		ReflectionTestUtils.setField(projector, "bridge", bridge);
	}

	@Test
	void handleProductSpecSelectedEvent() {
		ProductSpecSelectedEvent event = new ProductSpecSelectedEvent("productSpecId");
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductSpecStateVerifiedEvent() {
		ProductSpecStateVerifiedEvent event = new ProductSpecStateVerifiedEvent("productOfferingId", "productSpecId",
				ProductSpecificationLifecycle.ACTIVE);
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductOfferingInitiatedEvent() {
		AtomicProductOfferingInitiatedEvent event = new AtomicProductOfferingInitiatedEvent(
				new ProductSpecificationRef().id("product_spec_id"), "productOfferingId",
				ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidProductSpecStatusEvent() {
		InvalidProductSpecStatusEvent event = new InvalidProductSpecStatusEvent("productSpecId",
				ProductSpecificationLifecycle.UNAVAILABLE);
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingIdentityDataDefinedEvent() {
		AtomicProductOfferingIdentityDataDefinedEvent event = new AtomicProductOfferingIdentityDataDefinedEvent("poId1",
				new DefineIdentityData(), new ArrayList<ChannelRef>(), new ArrayList<MarketSegmentRef>(),
				new ArrayList<RelatedParty>(), new ArrayList<ProductOfferingTerm>(), new TimePeriod(),
				ProductOfferingType.BUNDLEPRODUCTOFFERING, null, OffsetDateTime.now(), "http://localhost:8080");
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleBundleProductOfferingIdentityDataDefinedEvent() {
		BundleProductOfferingIdentityDataDefinedEvent event = new BundleProductOfferingIdentityDataDefinedEvent("poId1",
				new DefineIdentityData(), new ArrayList<ChannelRef>(), new ArrayList<MarketSegmentRef>(),
				new ArrayList<RelatedParty>(), new ArrayList<ProductOfferingTerm>(), new TimePeriod(),
				ProductOfferingType.BUNDLEPRODUCTOFFERING, null, OffsetDateTime.now(), "http://localhost:8080");
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleContractProductOfferingIdentityDataDefinedEvent() {
		ContractProductOfferingIdentityDataDefinedEvent event = new ContractProductOfferingIdentityDataDefinedEvent(
				"poId1", new DefineContractIdentityData(), new ArrayList<ChannelRef>(),
				new ArrayList<MarketSegmentRef>(), new ArrayList<RelatedParty>(), new ArrayList<ProductOfferingTerm>(),
				new TimePeriod(), ProductOfferingType.BUNDLEPRODUCTOFFERING, null, OffsetDateTime.now(),
				"http://localhost:8080");
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductOfferingCategoryDefinedEvent() {
		AtomicProductOfferingCategoryDefinedEvent event = new AtomicProductOfferingCategoryDefinedEvent(
				"productOfferingId", Set.of(), OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingCharacteristicsDefinedEvent() {
		OffsetDateTime dateTime = OffsetDateTime.now();
		com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod timePeriod = new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod();
		timePeriod.setStartDateTime(dateTime.plusDays(1));
		timePeriod.endDateTime(dateTime.plusDays(9));
		ProductCharValue productCharValue = new ProductCharValue();
		productCharValue.setValue("10");
		productCharValue.setValidFor(timePeriod);
		List<ProductCharValue> productCharValues = new ArrayList<>();
		productCharValues.add(productCharValue);
		AtomicProductOfferingCharacteristicsDefinedEvent event = new AtomicProductOfferingCharacteristicsDefinedEvent(
				"productOfferingId",
				List.of(new ProductSpecificationCharacteristicValueUse().minCardinality(1).maxCardinality(4)
						.validFor(TimePeriodMapper.toGenerated(timePeriod))
						.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues))),
				OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidAtomicProductOfferingCharacteristicsSelectedEvent() {
		OffsetDateTime dateTime = OffsetDateTime.now();
		com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod timePeriod = new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod();
		timePeriod.setStartDateTime(dateTime.plusDays(1));
		timePeriod.endDateTime(dateTime.plusDays(9));
		ProductCharValue productCharValue = new ProductCharValue();
		productCharValue.setValue("30");
		productCharValue.setValidFor(timePeriod);
		List<ProductCharValue> productCharValues = new ArrayList<>();
		productCharValues.add(productCharValue);
		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristic = new PickAtomicProductOfferingCharacteristic();
		pickAtomicProductOfferingCharacteristic.setProductSpecCharacteristicValue(productCharValues);
		pickAtomicProductOfferingCharacteristic.setMinCardinality(1);
		pickAtomicProductOfferingCharacteristic.setMaxCardinality(4);
		pickAtomicProductOfferingCharacteristic.setValidFor(timePeriod);
		Set<InvalidCharacteristics> invalidCharacteristics = new HashSet<>();
		InvalidCharacteristics invalidCharacteristic = new InvalidCharacteristics();
		invalidCharacteristic.setPickAtomicProductOfferingCharacteristic(pickAtomicProductOfferingCharacteristic);
		invalidCharacteristic.setReason("Value Does not match");
		invalidCharacteristics.add(invalidCharacteristic);

		InvalidAtomicProductOfferingCharacteristicsSelectedEvent event = new InvalidAtomicProductOfferingCharacteristicsSelectedEvent(
				"productOfferingId", invalidCharacteristics);
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingRelationshipSelectedEvent() {
		AtomicProductOfferingRelationshipSelectedEvent event = new AtomicProductOfferingRelationshipSelectedEvent(
				"productOffId", Map.of("123", "brings"));
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingRelationshipDefinedEvent() {
		AtomicProductOfferingRelationshipDefinedEvent event = new AtomicProductOfferingRelationshipDefinedEvent(
				"productOffId", List.of(new ProductOfferingRelationship().id("123")), OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidProductOfferingRelationshipSelectedEvent() {
		InvalidProductOfferingRelationshipSelectedEvent event = new InvalidProductOfferingRelationshipSelectedEvent(
				"productOfferingId", List.of("128"));
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingBundleDefinedEvent() {
		AtomicProductOfferingBundleDefinedEvent event = new AtomicProductOfferingBundleDefinedEvent("productOffId",
				false, OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingValidatedEvent() {
		AtomicProductOfferingValidatedEvent event = new AtomicProductOfferingValidatedEvent("productOffId",
				ProductOfferingLifecycle.INTEST, OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAtomicProductOfferingVersionCreatedEvent() {
		AtomicProductOfferingVersionCreatedEvent event = new AtomicProductOfferingVersionCreatedEvent("productOffId",
				"0.1.0", OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductOfferingOperationDefinedEvent() {
		AtomicProductOfferingOperDefinedEvent event = new AtomicProductOfferingOperDefinedEvent("productOffId",
				List.of(new CommercialOperation()), null, OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test

	@DisplayName(value = "Product Off. Cancel Projection method called")
	void handleProductOffCancelledEventTest() {
		ProductOffCancelledEvent event = new ProductOffCancelledEvent("productOfferingId",
				new ProductOffering().id("productOffId1").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
						.lastUpdate(OffsetDateTime.now()));
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test

	@DisplayName(value = "Invalid Product Off. Cancel Projection method called")
	void handleInvalidProductOffCancelledEventTest() {
		InvalidProductOffCancelledEvent event = new InvalidProductOffCancelledEvent("ProductOffId1",
				ProductOfferingLifecycle.INTEST, ProductOfferingLifecycle.INSTUDY);
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleAssociatePOPtoOperationSpecEvent() {
		LinkPOPtoOperEvent event = new LinkPOPtoOperEvent("ProductOfferingId", List.of(new CommercialOperation()),
				null, OffsetDateTime.now());
		projector.handle(event);
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidAssociatePOPtoOperationSpecEvent() {
		InvalidLinkPOPtoOperEvent event = new InvalidLinkPOPtoOperEvent("ProductOfferingId",
				Set.of("InvalidOperationSpecId"));
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidPOPInAssociatePOPtoOperationSpecEvent() {
		InvalidPopIdSelectedEvent event = new InvalidPopIdSelectedEvent("ProductOfferingId", Set.of("InvalidPOPId"));
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidPOPStatusSelectedEvent() {
		InvalidPOPStatusSelectedEvent event = new InvalidPOPStatusSelectedEvent("ProductOfferingId",
				Map.of("ProductOfferingPriceId", ProductOfferingPriceLifecycle.LAUNCHED.toString()));
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleInvalidProductOfferingOperationsSelectedEvent() {
		InvalidProductOfferingOperationsSelectedEvent event = new InvalidProductOfferingOperationsSelectedEvent(
				"ProductOfferingId", Set.of("InvalidOperationSelected"));
		assertThrows(DiscoManagedClientException.class, () -> projector.handle(event));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	void handleProductOfferingDeleteEvent() {
		projector.handle(new ProductOfferingDeleteEvent("ProductOff1", OffsetDateTime.now(), 40L, "HOURS"));
		verify(bridge).send(anyString(), any(Message.class));
	}
}
