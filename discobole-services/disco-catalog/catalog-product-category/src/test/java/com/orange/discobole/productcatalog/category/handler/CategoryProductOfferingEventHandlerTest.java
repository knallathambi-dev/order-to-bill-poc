// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.handler;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;
import com.orange.discobole.productcatalog.category.handler.CategoryProductOfferingEventHandler;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.productoffering.event.category.CategoryProductOfferingAssociationEvent;

public class CategoryProductOfferingEventHandlerTest extends CategoryApplicationTests{
	@InjectMocks
	private CategoryProductOfferingEventHandler eventHandler;

	@Mock
	private  ModifyCategoryService categoryService;

	private CategoryEvent event;

	private Category category;
	
	@BeforeEach
	public void init() {
		category = new Category();
		event = new CategoryEvent() {
		};
		event.aggregateName();
	}
	@Test
	public void testHandle() {
		eventHandler.handle(new CategoryProductOfferingAssociationEvent("categoryId",Set.of(new ProductOfferingRef()), true));
 	   Mockito.verify(categoryService).modifyAssociatedEntity("categoryId",Set.of(new ProductOfferingRef()), true);
 
	}

}
