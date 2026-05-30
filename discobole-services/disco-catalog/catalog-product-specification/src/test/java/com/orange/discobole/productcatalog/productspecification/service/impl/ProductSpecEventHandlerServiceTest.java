// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ServiceSpecificationRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ComputeProductConfigurationEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.LinkProductSpecificationToStockItemEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecCancelledEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecCharacteristicsDefinedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecCreationCompletedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecIdentityDataEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecInitiatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecOpDefinedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecRelResourceSelectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecRelatedPartySelectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecRelationDefinedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecUsageSelectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecValidForSelectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecValidatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecVersionCreatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ServiceSpecSelectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ServiceSpecStateVerifiedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.StockItemSelectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.StockItemStateVerifiedEvent;
import com.orange.discobole.productcatalog.productspecification.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;


class ProductSpecEventHandlerServiceTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	ProductSpecEventHandlerService productSpecEventHandlerService;
	private static String productSpecId = "1";
	private static String stockItemId = "2";
	private static String serviceSpecId = "spec1";
	private static String test = "test";

	@Mock
	Publisher publisher;
	
	MongoEventStoreImpl mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class);
	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(productSpecEventHandlerService, "mongoEventStoreImpl", mongoEventStoreImpl);
	}
	@Test
	void productSpecInitiatedEventHandlerTest() {
		ProductSpecInitiatedEvent event = new ProductSpecInitiatedEvent(productSpecId,
				ProductSpecificationLifecycle.INSTUDY, "CFSspec", new ServiceSpecificationRef(), OffsetDateTime.now(),
				null,null);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void productSpecDescribedEventHandlerTest() {
		ProductSpecIdentityDataEvent event = new ProductSpecIdentityDataEvent(productSpecId,new IdentityData(), new ArrayList<RelatedParty>(),
				new ArrayList<RelatedResource>(),null,null,EntityType.PRODUCTSPECIFICATION, "http://localhost:8080");

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void stockItemSelectedEventHandlerTest() {
		StockItemSelectedEvent event = new StockItemSelectedEvent(productSpecId,stockItemId);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void serviceSpecSelectedEventHandlerTest() {
		ServiceSpecSelectedEvent event = new ServiceSpecSelectedEvent(productSpecId, serviceSpecId);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void stockItemStateVerifiedEventHandlerTest() {
		StockItemStateVerifiedEvent event = new StockItemStateVerifiedEvent(productSpecId, stockItemId,
				StockItemLifeCycleEnum.ACTIVE);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void serviceSpecStateVerifiedEventHandlerTest() {
		ServiceSpecStateVerifiedEvent event = new ServiceSpecStateVerifiedEvent(productSpecId, serviceSpecId, "active");

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void productSpecCharacteristicsDefinedEventHandlerTest() {
		ProductSpecCharacteristicsDefinedEvent event = new ProductSpecCharacteristicsDefinedEvent(productSpecId, null,
				null,null);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void productSpecOpDefinedEventHandlerTest() {
		ProductSpecOpDefinedEvent event = new ProductSpecOpDefinedEvent(productSpecId, null, null);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void ProductSpecRelationDefinedEventTest() {
		ProductSpecRelationDefinedEvent event = new ProductSpecRelationDefinedEvent(productSpecId, new ArrayList<>(),
				OffsetDateTime.now(), new ArrayList<>());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}


	@Test
	void ProductSpecRelatedPartySelectedEventTest() {
		ProductSpecRelatedPartySelectedEvent event = new ProductSpecRelatedPartySelectedEvent(productSpecId,
				new ArrayList<>());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void productSpecValidForSelectedEventTest() {
		ProductSpecValidForSelectedEvent event = new ProductSpecValidForSelectedEvent(productSpecId, new TimePeriod());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}


	@Test
	void productSpecValidatedEventTest() {
		ProductSpecValidatedEvent event = new ProductSpecValidatedEvent(productSpecId,
				ProductSpecificationLifecycle.ACTIVE, OffsetDateTime.now());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecificationId());
	}

	@Test
	void productSpecVersionCreatedEventTest() {
		ProductSpecVersionCreatedEvent event = new ProductSpecVersionCreatedEvent(productSpecId, "1.0.0",
				OffsetDateTime.now());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecificationId());
	}

	@Test
	void ProductSpecCreationCompletedEventTest() {
		ProductSpecCreationCompletedEvent event = new ProductSpecCreationCompletedEvent(productSpecId,
				new ProductSpecification().id("1"));

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecificationId());
	}

	@Test
	void ProductSpecUsageSelectedEventTest() {
		ProductSpecUsageSelectedEvent event = new ProductSpecUsageSelectedEvent(productSpecId, new ArrayList<>());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void productSpecCancelledEventTest() {
		ProductSpecCancelledEvent event = new ProductSpecCancelledEvent(productSpecId, new ProductSpecification());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}

	@Test
	void productSpecRelResourceSelectedEventTest() {
		ProductSpecRelResourceSelectedEvent event = new ProductSpecRelResourceSelectedEvent(productSpecId,
				new ArrayList<>());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecId());
	}


	@Test
	void computeProductConfigurationEventTest() {
		ComputeProductConfigurationEvent event = new ComputeProductConfigurationEvent(new ArrayList<>(), productSpecId);

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProdSpecId());
	}

	@Test
	void linkProductSpecificationToStockItemEventTest() {
		LinkProductSpecificationToStockItemEvent event = new LinkProductSpecificationToStockItemEvent(productSpecId,
				new ArrayList<>(), OffsetDateTime.now());

		Mockito.doNothing().when(publisher).project(List.of(event));
		productSpecEventHandlerService.handle(event);
		assertEquals("1", event.getProductSpecificationId());
	}
}
