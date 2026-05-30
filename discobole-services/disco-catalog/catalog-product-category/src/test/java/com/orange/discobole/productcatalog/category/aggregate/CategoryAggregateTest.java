// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.aggregate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.matchers.IgnoreField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.command.category.CancelCategoryCommand;
import com.orange.discobole.productcatalog.category.command.category.DefineCategoryIdentityDataCommand;
import com.orange.discobole.productcatalog.category.command.category.SelectAssociateEntityCommand;
import com.orange.discobole.productcatalog.category.command.category.ValidateCategoryCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.CancelCategoryDeleteCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.DeleteValidateCategoryCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.SelectCategoryDeleteCommand;
import com.orange.discobole.productcatalog.category.command.category.modify.CancelCategoryModificationCommand;
import com.orange.discobole.productcatalog.category.command.category.modify.ModifyAssociateEntityCommand;
import com.orange.discobole.productcatalog.category.command.category.modify.ModifyAssociateEntityCommandPOCreation;
import com.orange.discobole.productcatalog.category.command.category.modify.ModifyAssociateEntityCommandPOModification;
import com.orange.discobole.productcatalog.category.command.category.modify.ModifyCategoryIdentityDataCommand;
import com.orange.discobole.productcatalog.category.command.category.modify.ModifySelectCategoryCommand;
import com.orange.discobole.productcatalog.category.command.category.modify.ModifyValidateCategoryCommand;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryLifeCycle;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.category.event.category.AssociateEntitySelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCreationEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.category.event.category.EntityTypeSelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CancelCategoryDeleteEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.SelectCategoryDeleteEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.*;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
import com.orange.discobole.productcatalog.category.service.QueryService;

class CategoryAggregateTest extends CategoryApplicationTests {
	private QueryService queryService;
	private FixtureConfiguration<CategoryAggregate> fixture;
	private Category category;
	private String catType = CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue();
	private String name = "test";
	private String description = "demo";
	private Boolean isRoot = true;
	private String parentId = "";
	private String ProductOfferingId = "po1";
	private String categoryId = "cat1";
	private Publisher publisher;

	@BeforeEach
	public void setup() {
		queryService = Mockito.mock(QueryService.class);
		publisher = Mockito.mock(Publisher.class);

		fixture = new AggregateTestFixture<>(CategoryAggregate.class);
		fixture.registerFieldFilter(new IgnoreField(EntityTypeSelectedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryIdentityDataDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AssociateEntitySelectedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryCancelledEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryCreationEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryCreationEvent.class, "category"));
		fixture.registerFieldFilter(new IgnoreField(SelectCategoryDeleteEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CancelCategoryDeleteEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryIdentityDataModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryCreationEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryModifiedInitiatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryModificationValidatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AssociatedEntityIndirectModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(CategoryModificationCancelledEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(AssociatedEntityModifiedEvent.class, "lastUpdate"));
		fixture.registerInjectableResource(queryService);
		fixture.registerInjectableResource(publisher);
		category = new Category();
		category.setId(categoryId);
		category.setType(catType);

	}



	@Test
	void testCategoryModifiedInitiatedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category().id(categoryId).lifecycleStatus(CategoryLifeCycle.ACTIVE.getValue());
		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ModifySelectCategoryCommand(categoryId)).expectEvents(
				new CategoryModifiedInitiatedEvent(categoryId, category, lastUpdate, CategoryLifeCycle.ACTIVE));

	}


	@Test
	void testCategoryModifiedInitiatedEventWithNullCategory() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = null;
		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ModifySelectCategoryCommand(categoryId)).expectException(DiscoManagedClientException.class);

	}


	@Test
	void testCategoryIdentityDataDefinedEvent() {
		Category parentCategory = new Category().id("pa1").isRoot(true).type(catType);
		Mockito.when(queryService.fetchCategoryById("pa1", null)).thenReturn(parentCategory);
		Assertions.assertNotNull(parentCategory);

	}

	@Test
	void testCategoryIdentityDataDefinedEventWithNullPArentId() {
		Category parentCategory = new Category().id("pa1").isRoot(true).type(catType);
		Mockito.when(queryService.fetchCategoryById("pa1", null)).thenReturn(parentCategory);
		Assertions.assertNotNull(parentCategory);
	}

	@Test
	void testCategoryIdentityDataDefinedEventWithDifferntType() {
		Category parentCategory = new Category().id("pa1").isRoot(true)
				.type(CategoryEntityType.PRODUCTSPECIFICATIONCATEGORY.toString());
		Mockito.when(queryService.fetchCategoryById("pa1", null)).thenReturn(parentCategory);
		Assertions.assertNotNull(parentCategory);
	}

	@Test
	void testCategoryIdentityDataDefinedEventWithNullDescriptionAndName() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		List<String> subCategoryIds = new ArrayList<String>();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new DefineCategoryIdentityDataCommand(categoryId, null, null, isRoot, parentId, subCategoryIds,null, category.getHref()))
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void testCategoryIdentityDataDefinedEventWithNullName() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		List<String> subCategoryIds = new ArrayList<String>();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate)).when(
				new DefineCategoryIdentityDataCommand(categoryId, null, description, isRoot, parentId, subCategoryIds,null, category.getHref()))
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void categoryIdentityDataModifiedEvent () {
	    OffsetDateTime now = OffsetDateTime.now();
	    List<CategoryRef> subCategories = List.of(new CategoryRef().id("sub1"));
	    Set<ProductOfferingRef> productOfferings = Set.of(new ProductOfferingRef().id("po1"));

	    CategoryIdentityDataModifiedEvent event =
	            new CategoryIdentityDataModifiedEvent(
	                    "cat1",
	                    "name",
	                    "desc",
	                    true,
	                    "parent1",
	                    now,
	                    subCategories,
	                    productOfferings
	            );

	    Assertions.assertEquals("cat1", event.getCategoryId());
	    Assertions.assertEquals("name", event.getName());
	    Assertions.assertEquals("desc", event.getDescription());
	    Assertions.assertTrue(event.getIsRoot());
	    Assertions.assertEquals("parent1", event.getParentId());
	    Assertions.assertEquals(now, event.getLastUpdate());
	    Assertions.assertEquals(subCategories, event.getSubCategories());
	    Assertions.assertEquals(productOfferings, event.getProductOfferings());
	}


	@Test
	void testAtomicProductOfferingCategoryModifiedEvent() {


		ProductOffering productOffering = new ProductOffering();
		productOffering.id(ProductOfferingId).lifecycleStatus(ProductOfferingLifecycle.ACTIVE)
				.type(ProductOfferingType.ATOMICPRODUCTOFFERING).isSellable(true);

		Set<ProductOfferingRef> productOfferingRefSet = new HashSet<ProductOfferingRef>();
		ProductOfferingRef productOfferingRef = new ProductOfferingRef();
		productOfferingRef.id(productOffering.getId()).type(productOffering.getType().getValue()).referredType(null)
				.baseType(productOffering.getBaseType()).href(productOffering.getHref())
				.schemaLocation(productOffering.getSchemaLocation());
		productOfferingRefSet.add(productOfferingRef);

		Set<CategoryRef> addCategories = new HashSet<>();
		addCategories.add(new CategoryRef().id(categoryId).type(ProductOfferingType.ATOMICPRODUCTOFFERING.toString())
				.baseType(this.category.getBaseType()).href(this.category.getHref())
				.schemaLocation(this.category.getSchemaLocation()).referredType("category"));

		List<String> ProductOfferingIds = new ArrayList<String>();
		ProductOfferingIds.add(ProductOfferingId);

		Mockito.when(queryService.fetchProductOfferingById(ProductOfferingId, null)).thenReturn(productOffering); //
		Mockito.doNothing().when(publisher).project(
				List.of(new ProductOfferingCategoryAssociationEvent(ProductOfferingId, addCategories, isRoot,ProductOfferingType.ATOMICPRODUCTOFFERING.getValue())));

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new SelectAssociateEntityCommand(categoryId, ProductOfferingIds))
				.expectEvents(new AssociateEntitySelectedEvent(categoryId, productOfferingRefSet, lastUpdate));

	}

	@Test
	void testAtomicProductOfferingCategoryModifiedEvent2() {

		ProductOffering productOffering = new ProductOffering();
		productOffering.id(ProductOfferingId).lifecycleStatus(ProductOfferingLifecycle.ACTIVE)
				.type(ProductOfferingType.ATOMICPRODUCTOFFERING).isSellable(true);

		Set<ProductOfferingRef> productOfferingRefSet = new HashSet<ProductOfferingRef>();
		ProductOfferingRef productOfferingRef = new ProductOfferingRef();
		productOfferingRef.id(productOffering.getId()).type(productOffering.getType().getValue()).referredType(null)
				.baseType(productOffering.getBaseType()).href(productOffering.getHref())
				.schemaLocation(productOffering.getSchemaLocation());
		productOfferingRefSet.add(productOfferingRef);

		Set<CategoryRef> addCategories = new HashSet<>();
		addCategories.add(new CategoryRef().id(categoryId).type(ProductOfferingType.ATOMICPRODUCTOFFERING.toString())
				.baseType(this.category.getBaseType()).href(this.category.getHref())
				.schemaLocation(this.category.getSchemaLocation()).referredType("category"));

		List<String> ProductOfferingIds = new ArrayList<String>();
		ProductOfferingIds.add(ProductOfferingId);

		Category category = new Category();
		category.id(categoryId);
		category.setType(catType);
		category.setProductOffering(Set.of());
		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		Mockito.when(queryService.fetchProductOfferingById(ProductOfferingId, null)).thenReturn(productOffering); //
		Mockito.doNothing().when(publisher).project(
				List.of(new ProductOfferingCategoryAssociationEvent(ProductOfferingId, addCategories, isRoot,ProductOfferingType.ATOMICPRODUCTOFFERING.getValue())));

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ModifyAssociateEntityCommand(categoryId, ProductOfferingIds))
				.expectEvents(new AssociatedEntityModifiedEvent(categoryId, productOfferingRefSet,
						new HashSet<ProductOfferingRef>(), lastUpdate));

	}

	@Test
	void testBundleProductOfferingCategoryDefinedEvent() {


		ProductOffering productOffering = new ProductOffering();
		productOffering.id(ProductOfferingId).lifecycleStatus(ProductOfferingLifecycle.ACTIVE)
				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING).isSellable(true);

		Set<ProductOfferingRef> productOfferingRefSet = new HashSet<ProductOfferingRef>();
		ProductOfferingRef productOfferingRef = new ProductOfferingRef();
		productOfferingRef.id(productOffering.getId()).type(productOffering.getType().getValue()).referredType(null)
				.baseType(productOffering.getBaseType()).href(productOffering.getHref())
				.schemaLocation(productOffering.getSchemaLocation());
		productOfferingRefSet.add(productOfferingRef);

		List<String> ProductOfferingIds = new ArrayList<String>();
		ProductOfferingIds.add(ProductOfferingId);

		Mockito.when(queryService.fetchProductOfferingById(ProductOfferingId, null)).thenReturn(productOffering); //

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new SelectAssociateEntityCommand(categoryId, ProductOfferingIds))
				.expectEvents(new AssociateEntitySelectedEvent(categoryId, productOfferingRefSet, lastUpdate));

	}

	@Test
	void testCategoryCancelledEvent() {
		category.setType(catType);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, null))
				.when(new CancelCategoryCommand(category.getId()))
				.expectEvents(new CategoryCancelledEvent(category, category.getId(), null));
	}


	@Test
	void testSelectCategoryDeleteCommand() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.lifecycleStatus("active").id(categoryId);
		category.setType(catType);

		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new SelectCategoryDeleteCommand(categoryId,
						CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString()))
				.expectEvents(
						new SelectCategoryDeleteEvent(categoryId, category, lastUpdate));
	}

	@Test
	void testValidateCategoryCommand() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.lifecycleStatus("launched").id(categoryId);
		category.setType(catType);

		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ValidateCategoryCommand(categoryId))
				.expectEvents(new CategoryCreationEvent(categoryId, category, lastUpdate));

	}


	@Test
	void testModifyValidateCategoryCommand() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.id(categoryId);
		category.setType(catType);
		category.lastUpdate(lastUpdate);
		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ModifyValidateCategoryCommand(categoryId))
				.expectEvents(new CategoryModificationValidatedEvent(categoryId, category, lastUpdate));

	}


	@Test
	void testSelectCategoryDeleteCommand_CategoryNull() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(null);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new SelectCategoryDeleteCommand(categoryId,
						CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString()))
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void testCancelCategoryDeleteCommand() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.lifecycleStatus("active").id(categoryId);
		category.setType(catType);

		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new CancelCategoryDeleteCommand(categoryId))
				.expectEvents(new CancelCategoryDeleteEvent(categoryId, category, lastUpdate));
	}

	@Test
	void testCancelCategoryDeleteCommand_CategoryNull() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(null);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new CancelCategoryDeleteCommand(categoryId)).expectException(DiscoManagedClientException.class);
	}

	@Test
	void testDeleteValidateCategoryCommand() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.lifecycleStatus("active").id(categoryId);
		category.setType(catType);
		category.setIsRoot(false);

		List<Category> subCatList = new ArrayList<>();
		Category category2 = new Category();
		category2.lifecycleStatus("active").id("2");
		category2.setType(catType);
		category2.setIsRoot(true);
		subCatList.add(category2);

		CategoryRef categoryRef = new CategoryRef().id(category.getId()).type(category.getType());

		Set<ProductOfferingRef> poSet = new HashSet<>();
		ProductOfferingRef productOfferingRef = new ProductOfferingRef();
		productOfferingRef.id(ProductOfferingId);
		productOfferingRef.setType(ProductOfferingType.ATOMICPRODUCTOFFERING.getValue());
		poSet.add(productOfferingRef);

		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(category);
		Mockito.when(queryService.fetchCategoryEntityBySubCategoryId(categoryId, null)).thenReturn(subCatList);

		Mockito.when(queryService.fetchCategoryEntityById(categoryId, null))
				.thenReturn(new CategoryEntityRelationship().id("1").productOfferings(poSet));
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new DeleteValidateCategoryCommand(categoryId))
				.expectEvents(
						 new CategoryDeletedEvent(categoryId, subCatList),
						new CategoryAssociationDeletedEvent(categoryId));
	}

	@Test
	void testDeleteValidateCategoryCommand_CategoryNull() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.setType(catType);

		Mockito.when(queryService.fetchCategoryById(categoryId, null)).thenReturn(null);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new DeleteValidateCategoryCommand(categoryId)).expectException(DiscoManagedClientException.class);
	}

	@Test
	void testModifyAssociateEntityCommandPOCreation() {
		Set<ProductOfferingRef> pos = new HashSet<>();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ModifyAssociateEntityCommandPOCreation(categoryId, pos))
				.expectEvents(new AssociatedEntityIndirectModifiedEvent(categoryId, pos, null, lastUpdate));
	}

	@Test
	void testModifyAssociateEntityCommandPOModification() {
		Set<ProductOfferingRef> pos = new HashSet<>();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new ModifyAssociateEntityCommandPOModification(categoryId, pos))
				.expectEvents(new AssociatedEntityIndirectModifiedEvent(categoryId, null, pos, lastUpdate));
	}

	@Test
	void testCancelCategoryModificationCommand() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		Category category = new Category();
		category.id(categoryId);
		category.setType(catType);
		category.lastUpdate(lastUpdate);
		fixture.given(new EntityTypeSelectedEvent(categoryId, catType, lastUpdate))
				.when(new CancelCategoryModificationCommand(categoryId))
				.expectEvents(new CategoryModificationCancelledEvent(categoryId, category, lastUpdate));
	}
}
