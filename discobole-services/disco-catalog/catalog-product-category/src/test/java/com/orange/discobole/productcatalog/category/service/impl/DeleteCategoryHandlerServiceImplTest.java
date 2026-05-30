// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.ParentCategoryModifiedEvent;


class DeleteCategoryHandlerServiceImplTest extends CategoryApplicationTests {

	@InjectMocks
	DeleteCategoryHandlerServiceImpl deleteCategoryHandlerServiceImpl;
	private String categoryId = "1";
	private CategoryRef subCategory;
	private final String lifeCycleStatus = "Active";

	@Mock
	Publisher publisher;
	
	
	@Test
	void ParentCategoryModifiedEventHandlerTest() {
		ParentCategoryModifiedEvent event = new ParentCategoryModifiedEvent(categoryId,subCategory);

		Mockito.doNothing().when(publisher).project(List.of(event));
		deleteCategoryHandlerServiceImpl.handle(event);
		assertEquals("1", event.getCategoryId());
	}
	@Test
	void CategoryLifeCycleUpdatedEventTest() {
		CategoryLifeCycleUpdatedEvent event = new CategoryLifeCycleUpdatedEvent(categoryId,lifeCycleStatus);

		Mockito.doNothing().when(publisher).project(List.of(event));
		deleteCategoryHandlerServiceImpl.handle(event);
		assertEquals("1", event.getCategoryId());
	}
	@Test
	void CategoryAssociationDeletedEventTest() {
		CategoryAssociationDeletedEvent event = new CategoryAssociationDeletedEvent(categoryId);
		Mockito.doNothing().when(publisher).project(List.of(event));
		deleteCategoryHandlerServiceImpl.handle(event);
		assertEquals("1", event.getCategoryId());
	}
}
