// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.projection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;

import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.POPRelationshipType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceAlterationIdentityDataDescribedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceCancelledEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceChargeIdentityDataDescribedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceCreationCompletedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceDeleteEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceInitiatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceVersionCreatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;

class ProductOfferingPriceProjectorTest extends ProductOfferingPriceApplicationTests {

	private final ProductOfferingPriceProjector productOfferingPriceProjector;
	private StreamBridge bridge;

	// private final MessageChannel productOfferingPrice;

	ProductOfferingPriceProjectorTest() {
		bridge = Mockito.mock(StreamBridge.class);
		productOfferingPriceProjector = new ProductOfferingPriceProjector(bridge);
		//ReflectionTestUtils.setField(productOfferingPriceProjector, "bridge", bridge);
	}

	@Test
	void handleProductOfferingPriceInitiatedEvent() {
		ProductOfferingPriceInitiatedEvent offeringPriceInitiatedEvent = new ProductOfferingPriceInitiatedEvent(
				UUID.randomUUID().toString(), ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
				ProductOfferingPriceLifecycle.UNAVAILABLE);
		productOfferingPriceProjector.handle(offeringPriceInitiatedEvent);
		// Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void handleProductOfferingPriceChargeIdentityDataDescribedEventTest() {
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
				.endDateTime(OffsetDateTime.now().plusYears(2));
		String productOfferingPriceId = UUID.randomUUID().toString();
		ProductOfferingPriceChargeIdentityDataDescribedEvent event = new ProductOfferingPriceChargeIdentityDataDescribedEvent(
				productOfferingPriceId, "POPC", "POP charging", PriceType.RC, false, 10, "month",
				new Money().unit("INR").value(51.0f), new Quantity().units("INR").amount(1.0f),
				ProrationType.NOPRORATION, new ArrayList<>(), ChargeCycle.CYCLEFORWARD,
				ProductOfferingPriceLifecycle.LAUNCHED, validFor, OffsetDateTime.now(), "http://localhost:8080");
		productOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void handleProductOfferingPriceRelationshipDefinedEventTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		ProductOfferingPriceRelationship productOfferingRelationship1 = new ProductOfferingPriceRelationship()
				.id(UUID.randomUUID().toString()).relationshipType(POPRelationshipType.REPLACEDBY);
		ProductOfferingPriceRelationship productOfferingRelationship2 = new ProductOfferingPriceRelationship()
				.id(UUID.randomUUID().toString()).relationshipType(POPRelationshipType.REPLACEDBY);
		productOfferingPriceRelationships.addAll(List.of(productOfferingRelationship1, productOfferingRelationship2));
		ProductOfferingPriceRelationshipDefinedEvent event = new ProductOfferingPriceRelationshipDefinedEvent(
				productOfferingPriceId, productOfferingPriceRelationships, OffsetDateTime.now());
		productOfferingPriceProjector.handle(event);
		// Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void handleProductOfferingPriceValidatedEvent() {
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
				.endDateTime(OffsetDateTime.now().plusYears(2));
		ProductOfferingPriceValidatedEvent validatedEvent = new ProductOfferingPriceValidatedEvent(
				UUID.randomUUID().toString(), ProductOfferingPriceLifecycle.LAUNCHED, validFor, OffsetDateTime.now());
		productOfferingPriceProjector.handle(validatedEvent);
		// Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void handleProductOfferingPriceVersionCreatedEvent() {
		ProductOfferingPriceVersionCreatedEvent versionCreatedEvent = new ProductOfferingPriceVersionCreatedEvent(
				UUID.randomUUID().toString(), "1.0.1", OffsetDateTime.now());
		productOfferingPriceProjector.handle(versionCreatedEvent);
		// Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void handleProductOfferingPriceCreationCompletedEventTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		ProductOfferingPrice pop = new ProductOfferingPrice().id(productOfferingPriceId);
		ProductOfferingPriceCreationCompletedEvent event = new ProductOfferingPriceCreationCompletedEvent(
				productOfferingPriceId, pop);
		productOfferingPriceProjector.handle(event);
		// Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
		assertEquals(productOfferingPriceId, event.getProductOfferingPriceId());
	}

	@Test
	void handleProductOfferingPriceAlterationIdentityDataDescribedEventTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		ApplicationDuration duration = new ApplicationDuration().amount(10);
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
				.endDateTime(OffsetDateTime.now().plusYears(2));
		ProductOfferingPriceAlterationIdentityDataDescribedEvent event = new ProductOfferingPriceAlterationIdentityDataDescribedEvent(
				productOfferingPriceId, "POPA", "POP alteration", PriceType.RC, duration, 1, ProrationType.NOPRORATION,
				15.0f, new Money().unit("INR").value(51.0f), new Quantity().units("INR").amount(1.0f),
				ProductOfferingPriceLifecycle.LAUNCHED, validFor, OffsetDateTime.now(), "http://localhost:8080",0);
		productOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void handleProductOfferingPriceCancelledEventTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		ProductOfferingPrice pop = new ProductOfferingPrice().id(productOfferingPriceId);
		ProductOfferingPriceCancelledEvent event = new ProductOfferingPriceCancelledEvent(productOfferingPriceId, pop);
		productOfferingPriceProjector.handle(event);
		// Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
		assertEquals(productOfferingPriceId, event.getProductOfferingPriceId());
	}

	@Test
	void handleProductOfferingPriceDeleteEvent() {
		OffsetDateTime updateTime = OffsetDateTime.now();
		ProductOfferingPriceDeleteEvent event = new ProductOfferingPriceDeleteEvent("ProductOffPrice1", updateTime, 40L,
				"HOURS");
		productOfferingPriceProjector.handle(event);
		// verify(productOfferingPrice).send(any(Message.class));
		verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
		assertNotNull(event.getInterval());
		assertEquals("HOURS", event.getIntervalUnit());
		assertEquals(updateTime, event.getLastUpdateDateTime());
	}
}