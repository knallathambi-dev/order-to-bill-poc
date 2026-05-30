// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.projection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.*;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.ParentCategoryModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

public class CategoryProjectorTest {

	private final CategoryProjector projector;
	private StreamBridge bridge;

	public CategoryProjectorTest() {
		projector = new CategoryProjector();
		bridge = Mockito.mock(StreamBridge.class);
		ReflectionTestUtils.setField(projector, "bridge", bridge);
	}

	private static final String CATEGORY_ID = "categoryId1";

	@Test
	public void handleEntityTypeSelectedEvent() {
		projector.handle(new EntityTypeSelectedEvent(CATEGORY_ID, CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString(),
				OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}


	@Test
	public void CategoryIdentityDataDefinedEvent() {
		
		ProductOfferingRef po = new ProductOfferingRef()
				.id("PO1")
				.type(CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue());
		Set<ProductOfferingRef> productOfferings = new HashSet<>();
		productOfferings.add(po);

	
		projector.handle(new CategoryIdentityDataDefinedEvent(
				CATEGORY_ID,
				"cname",
				"cdescription",
				true,
				null,
				OffsetDateTime.now(),
				List.of(),         
				productOfferings,
				"http://localhost:8080"
		));

		// then
		verify(bridge).send(anyString(), any(Message.class));

	}

	@Test
	public void handleAssociateEntitySelectedEvent() {
		ProductOfferingRef po = new ProductOfferingRef().id("PO1")
				.type(CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue());
		Set<ProductOfferingRef> pos = new HashSet<ProductOfferingRef>();
		pos.add(po);
		projector.handle(new AssociateEntitySelectedEvent(CATEGORY_ID, pos, OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	public void handleCategoryCancelledEvent() {
		projector.handle(new CategoryCancelledEvent(new Category(), CATEGORY_ID, OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	@Test
	public void handleModifyCategoryAssociateEntityEvent() {
		ProductOfferingRef po = new ProductOfferingRef().id("PO1")
				.type(CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue());
		Set<ProductOfferingRef> pos = new HashSet<ProductOfferingRef>();
		pos.add(po);
		projector.handle(new AssociatedEntityModifiedEvent("PO1",Set.of() ,pos, OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}

	
	@Test
	public void handleCategoryIdentityDataModifiedEvent() {
		projector.handle(new CategoryIdentityDataModifiedEvent(CATEGORY_ID, "cname", "cdescription", true, null,
				OffsetDateTime.now(),List.of(),null));
		verify(bridge).send(anyString(), any(Message.class));
	}
	 
	@Test
	public void handleCategoryCreationEvent() {
		projector.handle(new CategoryCreationEvent(CATEGORY_ID, new Category(), OffsetDateTime.now()));
		verify(bridge).send(anyString(), any(Message.class));
	}
	
	@Test
	public void handleCategoryLifeCycleUpdatedEvent() {
		projector.handle(new CategoryLifeCycleUpdatedEvent());
		verify(bridge).send(anyString(), any(Message.class));
	}
	@Test
	public void  handleParentCategoryModifiedEvent() {
		projector.handle(new ParentCategoryModifiedEvent());
		verify(bridge).send(anyString(), any(Message.class));
	}
	@Test
	public void  handleCategoryAssociationDeletedEvent() {
		projector.handle(new CategoryAssociationDeletedEvent());
		verify(bridge).send(anyString(), any(Message.class));
	}
	
	@Test
	public void handleParentCategoryUpdatedEvent() {
		projector.handle(new ParentCategoryUpdatedEvent());
		verify(bridge).send(anyString(), any(Message.class));
	}
}

