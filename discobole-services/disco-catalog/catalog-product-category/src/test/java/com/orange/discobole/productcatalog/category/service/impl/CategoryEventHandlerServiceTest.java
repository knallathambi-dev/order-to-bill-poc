// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCreationEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.category.event.category.EntityTypeSelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationValidatedEvent;

public class CategoryEventHandlerServiceTest extends CategoryApplicationTests {

	private CategoryEventHandlerService service;

	private Publisher publisher;
	private static String categoryId = "1";
	String categoryType = CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue();
	OffsetDateTime lastUpdate = OffsetDateTime.now();
	String name = "test";
	String description = "demo";
	Boolean isRoot = true;
	String parentId = "";

	@BeforeEach
	public void setup() {
		service = new CategoryEventHandlerService();
		publisher = Mockito.mock(Publisher.class);
		ReflectionTestUtils.setField(service, "publisher", publisher);
	}

	@Test
	void handleEntityTypeSelectedEvent() {
		EntityTypeSelectedEvent event = new EntityTypeSelectedEvent(categoryId, categoryType, lastUpdate);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	public void handleCategoryIdentityDataDefinedEvent() {
		Set<ProductOfferingRef> productOfferings = new HashSet<>();
		CategoryIdentityDataDefinedEvent event = new CategoryIdentityDataDefinedEvent(categoryId, name, description,
				isRoot, parentId, lastUpdate,null, productOfferings, "http://localhost:8080");
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleCategoryCancelledEvent() {
		CategoryCancelledEvent event = new CategoryCancelledEvent(new Category(), categoryId, lastUpdate);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleCategoryCreationEvent() {

		CategoryCreationEvent event = new CategoryCreationEvent(categoryId, new Category(), lastUpdate);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleCategoryModificationCancelledEvent() {
		CategoryModificationCancelledEvent event = new CategoryModificationCancelledEvent(categoryId, new Category(),
				lastUpdate);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleAssociatedEntityModifiedEvent() {
		Set<ProductOfferingRef> pos = new HashSet<>();
		AssociatedEntityIndirectModifiedEvent event = new AssociatedEntityIndirectModifiedEvent(categoryId, null, pos, lastUpdate);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

	@Test
	void handleCategoryModificationValidatedEvent() {
		CategoryModificationValidatedEvent event = new CategoryModificationValidatedEvent(categoryId, new Category(),
				lastUpdate);
		List<Event> eventList = List.of(event);
		doNothing().when(publisher).project(eventList);
		service.handle(event);
		verify(publisher, times(1)).project(Mockito.anyList());
	}

}
