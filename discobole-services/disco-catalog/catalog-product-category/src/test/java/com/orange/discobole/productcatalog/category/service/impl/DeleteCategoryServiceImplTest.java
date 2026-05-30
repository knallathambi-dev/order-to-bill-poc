// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;
import com.orange.discobole.productcatalog.category.service.QueryService;
import com.orange.discobole.productcatalog.category.service.impl.DeleteCategoryServiceImpl;

class DeleteCategoryServiceImplTest {

	@InjectMocks
	DeleteCategoryService deleteCategoryService;
	QueryService queryService = Mockito.mock(QueryService.class);
	MongoEventStoreImpl mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class);

	CommandGateway commandGateway;

	private static String categoryId = "cat1";

	@BeforeEach
	public void setup() {
		commandGateway = Mockito.mock(CommandGateway.class);
		deleteCategoryService = new DeleteCategoryServiceImpl(commandGateway);

	}

	@Test
	void triggerInitiateProductSpecCreation() {

		deleteCategoryService.selectCategoryDeletion(categoryId, CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString());
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggercancelCategoryDeletion() {

		deleteCategoryService.cancelCategoryDeletion(categoryId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggervalidateDeleteCategory() {

		deleteCategoryService.validateDeleteCategory(categoryId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

}
