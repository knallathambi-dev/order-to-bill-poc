// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.category.service.CategoryService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.messaging.MetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.aggregate.CategoryAggregate;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.AssociateEntitySelectedEvent;
import com.orange.discobole.productcatalog.category.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.category.service.QueryService;

public class CategoryServiceImplTest extends CategoryApplicationTests {

	CommandGateway commandGateway;
	@InjectMocks
	CategoryService categoryService;
	private static String categoryId = "1";
	String name = "test";
	String description = "demo";
	Boolean isRoot = true;
	String parentId = "";
	@Mock
	MongoEventStoreImpl mongoEventStoreImpl;
	@Mock
	QueryService queryService;
	@Mock
	Publisher publisher;
	
	@Mock
	ConfigurableProperties configurableProperties;


	private AccessTokenInterceptor accessTokenInterceptor;

	CategoryServiceImplTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		accessTokenInterceptor= Mockito.mock(AccessTokenInterceptor.class);
		categoryService = new CategoryServiceImpl(commandGateway,accessTokenInterceptor);
	}

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(categoryService,"mongoEventStoreImpl",mongoEventStoreImpl);
		ReflectionTestUtils.setField(categoryService,"publisher",publisher);
		ReflectionTestUtils.setField(categoryService,"queryService",queryService);
		ReflectionTestUtils.setField(categoryService, "configurableProperties", configurableProperties);
		Mockito.when(configurableProperties.getCatprodcaturl()).thenReturn("dummy-url");

	}




	  @Test public void testDefineCategoryIdentityData() {
	  categoryService.defineCategoryIdentityData( name, description,
	  isRoot, parentId,null,null); verify(commandGateway,
	  times(1)).sendAndWait(Mockito.any()); }


	@Test
	public void testAssociateEntity() {
		categoryService.associateEntity(categoryId, null);
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}

	@Test
	public void testCancelCategory() {
		categoryService.cancelCategory(categoryId);
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}

	@Test
	public void testValidateCategory() {
		List<DomainEventMessage<Event>> list=new ArrayList<>();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Set<ProductOfferingRef> pos=new HashSet<ProductOfferingRef>();
		ProductOfferingRef po=new ProductOfferingRef().id("po1").name("po123");
		pos.add(po);
		DomainEventMessage<Event> d1=new DomainEventMessage<Event>() {

			@Override
			public Class<Event> getPayloadType() {
				 return Event.class;
			}

			@Override
			public Event getPayload() {
				return new AssociateEntitySelectedEvent(categoryId,pos, lastUpdate);
			}

			@Override
			public MetaData getMetaData() {
				return null;
			}

			@Override
			public Instant getTimestamp() {
				return null;
			}

			@Override
			public String getIdentifier() {
				return categoryId;
			}

			@Override
			public DomainEventMessage<Event> withMetaData(Map<String, ?> metaData) {
				return null;
			}

			@Override
			public String getType() {
				return CategoryAggregate.class.getTypeName();
			}

			@Override
			public long getSequenceNumber() {
				return 0;
			}

			@Override
			public String getAggregateIdentifier() {
				return categoryId;
			}

			@Override
			public DomainEventMessage<Event> andMetaData(Map<String, ?> metaData) {
				return null;
			}
		};
		list.add(d1);
		Category category=new Category().id("1").baseType("").type("").schemaLocation("");
		Mockito.when(mongoEventStoreImpl.readEventsBackword(categoryId, 0)).thenReturn(list);
		Mockito.when(queryService.fetchCategoryById(category.getId(), null)).thenReturn(category);

		categoryService.validateCategory(categoryId);
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}
}
