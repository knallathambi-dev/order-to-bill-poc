// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.POPRelationshipType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
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

class ProductOfferingPriceEventHandlerServiceTest extends ProductOfferingPriceApplicationTests {

	private ProductOfferingPriceEventHandlerServiceImpl service;

	private Publisher publisher;
	private static String productOfferingPriceId = "1";

	@BeforeEach
	void setup() {
		service = new ProductOfferingPriceEventHandlerServiceImpl();
		publisher = Mockito.mock(Publisher.class);
		ReflectionTestUtils.setField(service, "publisher", publisher);
	}

	@Test
	void handleProductOfferingPriceInitiatedEvent() {
		ProductOfferingPriceInitiatedEvent event = new ProductOfferingPriceInitiatedEvent("1",
				ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
				ProductOfferingPriceLifecycle.LAUNCHED);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceChargeIdentityDataDescribedEvent() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id("12").relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		ProductOfferingPriceChargeIdentityDataDescribedEvent event = new ProductOfferingPriceChargeIdentityDataDescribedEvent(
				productOfferingPriceId, "POPC", "POP charging", PriceType.RC, false, 10, "month",
				new Money().value(51.0f).unit("INR"), new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION,
				productOfferingPriceRelationships, ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED,
				validFor, OffsetDateTime.now(), "http://localhost:8080");
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceAlterationIdentityDataDescribedEvent() {
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ApplicationDuration duration = new ApplicationDuration().amount(10);
		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP alteration").priceType(PriceType.RC).applicationDuration(duration).priority(1)
				.percentage(15.0f).price(new Money().value(51.0f).unit("INR"))
				.priceAlterationType(PriceAlterationType.PRICE).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		ProductOfferingPriceAlterationIdentityDataDescribedEvent event = new ProductOfferingPriceAlterationIdentityDataDescribedEvent(
				productOfferingPriceId, identityData.getName(), identityData.getDescription(),
				identityData.getPriceType(), identityData.getApplicationDuration(), identityData.getPriority(), ProrationType.NOPRORATION,
				identityData.getPercentage(), identityData.getPrice(), identityData.getUnitOfMeasure(),ProductOfferingPriceLifecycle.LAUNCHED, validFor,
				OffsetDateTime.now(), "http://localhost:8080",0);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceRelationshipDefinedEvent() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id("12").relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		ProductOfferingPriceRelationshipDefinedEvent event = new ProductOfferingPriceRelationshipDefinedEvent(
				productOfferingPriceId, productOfferingPriceRelationships, OffsetDateTime.now());
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceVersionCreatedEvent() {

		ProductOfferingPriceVersionCreatedEvent event = new ProductOfferingPriceVersionCreatedEvent(
				productOfferingPriceId, "1.0.0", OffsetDateTime.now());
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceValidatedEvent() {
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceValidatedEvent event = new ProductOfferingPriceValidatedEvent(productOfferingPriceId,
				ProductOfferingPriceLifecycle.LAUNCHED, validFor, OffsetDateTime.now());
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceCreationCompletedEvent() {

		ProductOfferingPriceCreationCompletedEvent event = new ProductOfferingPriceCreationCompletedEvent(
				productOfferingPriceId, new ProductOfferingPrice());
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(0)).project(Mockito.anyList());
	}

	@Test
	void handleProductOfferingPriceCancelledEvent() {

		ProductOfferingPriceCancelledEvent event = new ProductOfferingPriceCancelledEvent(productOfferingPriceId,
				new ProductOfferingPrice());
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}
	
	@Test
	void handleProductOfferingPriceDeleteEvent() {

		ProductOfferingPriceDeleteEvent event = new ProductOfferingPriceDeleteEvent(productOfferingPriceId,
				OffsetDateTime.now(), 1L, "HOUR");
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}
}
