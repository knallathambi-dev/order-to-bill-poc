// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.aggregate.CategoryAggregate;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.category.service.QueryService;

class ModifyCategoryServiceImplTest {
	@Mock
	ObjectMapper objectMapper;
	CommandGateway commandGateway;

	@InjectMocks
	ModifyCategoryService modifycategoryService;
	QueryService queryService = Mockito.mock(QueryService.class);

	ApplicationContext appCtx = Mockito.mock(ApplicationContext.class);

	Publisher publisher = Mockito.mock(Publisher.class);
	MongoEventStoreImpl mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class);

	private AccessTokenInterceptor accessTokenInterceptor;



	@Mock
	CategoryAggregate categoryAggregate;
	MongoEventStorageEngine engine = Mockito.mock(MongoEventStorageEngine.class);

	ModifyCategoryServiceImplTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		accessTokenInterceptor= Mockito.mock(AccessTokenInterceptor.class);
		modifycategoryService = new ModifyCategoryServiceImpl(commandGateway,accessTokenInterceptor);
	}

	@BeforeEach
	void beforeEachSetup() {
		ReflectionTestUtils.setField(modifycategoryService, "publisher", publisher);
		ReflectionTestUtils.setField(modifycategoryService, "mongoEventStoreImpl", mongoEventStoreImpl);
	}

	@Test
	void initiatePOModificationTest() {

		String categoryId = "category_id";
		modifycategoryService.initiateCategoryModification(categoryId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}

	@Test
	void modifyAssociatedEntityTest1() {

		String categoryId = "category_id";
		Set<ProductOfferingRef> productOfferings = new HashSet<>();
		Boolean isAddition = true;
		modifycategoryService.modifyAssociatedEntity(categoryId, productOfferings, isAddition);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void modifyAssociatedEntityTest2() {

		String categoryId = "category_id";
		Set<ProductOfferingRef> productOfferings = new HashSet<>();
		Boolean isAddition = false;
		modifycategoryService.modifyAssociatedEntity(categoryId, productOfferings, isAddition);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void modifyAssociatedEntity() {

		String categoryId = "category_id";
		List<String> productOfferings = new ArrayList<>();
		productOfferings.add("1269");
		modifycategoryService.modifyAssociatedEntity(categoryId, productOfferings);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void cancelCategoryModificationTest() {

		String categoryId = "category_id";
		modifycategoryService.cancelCategoryModification(categoryId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}
}