// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import java.util.HashSet;
import java.util.Set;


import com.orange.discobole.productcatalog.catalog.constant.CategoryConstants;
import jakarta.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.category.event.category.CategoryCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationValidatedEvent;

import static com.orange.discobole.productcatalog.catalog.constant.CategoryType.PRODUCTOFFERINGCATEGORY;


/**
 * The ModifyCategoryEventHandler handle the events to persist the data in the
 * database.
 *
 * @author Rajan Chauhan
 */

@Component
public class ModifyCategoryEventHandler {

	@Resource
	private CategoryService categoryService;

	@Resource
	private CategoryEntityRelationshipService categoryEntityRelationshipService;

	private static final String ASSOCIATEDENTITY = "Handling AssociatedEntityIndirectModifiedEvent inside for loop - {}";

	private static final Logger LOGGER = LogManager.getLogger(ModifyCategoryEventHandler.class);

	public void handle(final AssociatedEntityModifiedEvent event) {
		LOGGER.info("Handling AssociatedEntityModifiedEvent");
		LOGGER.debug("Handling AssociatedEntityModifiedEvent inside for loop - {}", event);
		Set<ProductOfferingRef> productOfferings = new HashSet<>();
		CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getCategoryId());
		if (null != entity) {
			productOfferings.addAll(entity.getProductOfferings());
		} else {
			entity = new CategoryEntityRelationship();
		}
		if (null != event.getAddProductOfferings()) {
			productOfferings.addAll(event.getAddProductOfferings());
		}
		if (null != event.getDelProductOfferings()) {
			productOfferings.removeAll(event.getDelProductOfferings());
		}
		entity.id(event.getCategoryId()).productOfferings(productOfferings).type(PRODUCTOFFERINGCATEGORY.toString()).setLastUpdate(event.getLastUpdate());
		LOGGER.debug("Handling AssociatedEntityModifiedEvent inside for loop - {}", entity);
		categoryEntityRelationshipService.save(entity);
	}
	 public void handle(final AssociatedEntityIndirectModifiedEvent event) {
		 LOGGER.info("Handling AssociatedEntityModifiedEvent");
		 LOGGER.debug(ASSOCIATEDENTITY, event);
		 Set<ProductOfferingRef> productOfferings = new HashSet<>();
		 CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getCategoryId());
		 if (null != entity) {
			 productOfferings.addAll(entity.getProductOfferings());
		 } else {
			 entity = new CategoryEntityRelationship();
		 }
		 if (null != event.getAddProductOfferings()) {
			 productOfferings.addAll(event.getAddProductOfferings());
		 }
		 if (null != event.getDelProductOfferings()) {
			 productOfferings.removeAll(event.getDelProductOfferings());
		 }
		 entity.id(event.getCategoryId()).productOfferings(productOfferings).type(PRODUCTOFFERINGCATEGORY.toString()).setLastUpdate(event.getLastUpdate());
		 LOGGER.debug(ASSOCIATEDENTITY, entity);
		 categoryEntityRelationshipService.save(entity);

		 if (null != event.getAddProductOfferings()) {
			 productOfferings.addAll(event.getAddProductOfferings());
		 }
		 if (null != event.getDelProductOfferings()) {
			 productOfferings.removeAll(event.getDelProductOfferings());
		 }
		 entity.id(event.getCategoryId()).productOfferings(productOfferings).type(PRODUCTOFFERINGCATEGORY.toString());
		 LOGGER.debug(ASSOCIATEDENTITY, entity);
		 categoryEntityRelationshipService.save(entity);
	 }

	public void handle(final CategoryIdentityDataModifiedEvent event) {
		LOGGER.info("Handling CategoryIdentityDataModifiedEvent");
		Update update = new Update();
		update.set(CategoryConstants.NAME, event.getName());
		update.set(CategoryConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(CategoryConstants.PARENT_ID, event.getParentId());
		update.set(CategoryConstants.DESCRIPTION, event.getDescription());
		update.set(CategoryConstants.IS_ROOT, event.getIsRoot());
		update.set(CategoryConstants.SUB_CATEGORY, event.getSubCategories());

		categoryService.updateCategory(event.getCategoryId(), update);
	}


	public void handle(final CategoryModificationValidatedEvent event) {
		LOGGER.info("Handling CategoryModificationValidatedEvent");
		Update update = new Update();
		update.set(CategoryConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(CategoryConstants.LIFE_CYCLE_STATUS, event.getCategory().getLifecycleStatus());
		update.set(CategoryConstants.VALID_FOR, event.getCategory().getValidFor());

		categoryService.updateCategory(event.getCategoryId(), update);
	}

	public void handle(final CategoryCancelledEvent event) {
		LOGGER.info("Handling CategoryCancelledEvent");
		LOGGER.debug("Handling CategoryCancelledEvent - {}", event);
//		Category category = categoryService.fetchCategoryById(event.getCategoryId());
		categoryService.removeCategory(event.getCategoryId());
	}
}
