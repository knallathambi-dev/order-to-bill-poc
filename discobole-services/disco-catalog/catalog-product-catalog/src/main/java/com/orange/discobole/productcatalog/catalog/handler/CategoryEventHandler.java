// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;


import com.orange.discobole.productcatalog.catalog.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryDeletedEvent;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.category.event.category.AssociateEntitySelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCreationEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.category.event.category.EntityTypeSelectedEvent;




/**
 * The CategoryEventHandler handle the events to persist the data in the database.
 *
 * @author Varshika Choudhary
 */

@Component
public class CategoryEventHandler {

    @Resource
    private CategoryService categoryService;

    @Resource
    private CategoryEntityRelationshipService categoryEntityRelationshipService;

    private static final Logger LOGGER = LogManager.getLogger(CategoryEventHandler.class);

    public void handle(final EntityTypeSelectedEvent event) {
        LOGGER.info("Handling EntityTypeSelectedEvent");
        Category category = new Category();
        category.id(event.getCategoryId())
                .type(event.getCategoryType())
                .lastUpdate(event.getLastUpdate());
        categoryService.saveCategory(category);
    }

    public void handle(final CategoryIdentityDataDefinedEvent event) {
        LOGGER.info("Handling CategoryIdentityDataDefinedEvent");
        Update update = new Update();
        update.set(CategoryConstants.NAME, event.getName());
        update.set(CategoryConstants.LAST_UPDATE, event.getLastUpdate());
        update.set(CategoryConstants.HREF, event.getHref());
        update.set(CategoryConstants.PARENT_ID, event.getParentId());
        update.set(CategoryConstants.DESCRIPTION, event.getDescription());
        update.set(CategoryConstants.IS_ROOT, event.getIsRoot());
        update.set(CategoryConstants.SUB_CATEGORY, event.getSubCategories());

        categoryService.updateCategory(event.getCategoryId(), update);
    }

    public void handle(final AssociateEntitySelectedEvent event) {
        LOGGER.info("Handling AssociateEntitySelectedEvent");
        Category category = categoryService.fetchCategoryById(event.getCategoryId());
        CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getCategoryId());
        if (null == entity) {
            entity = new CategoryEntityRelationship();
        }
        entity.id(event.getCategoryId()).productOfferings(event.getProductOfferings()).type(category.getType()).setLastUpdate(event.getLastUpdate());
        categoryEntityRelationshipService.save(entity);
    }


    public void handle(final CategoryCreationEvent event) {
        LOGGER.info("Handling CategoryCreationEvent");
        Update update = new Update();
        update.set(CategoryConstants.LAST_UPDATE, event.getLastUpdate());
        update.set(CategoryConstants.LIFE_CYCLE_STATUS, event.getCategory().getLifecycleStatus());
        update.set(CategoryConstants.VALID_FOR, event.getCategory().getValidFor());

        categoryService.updateCategory(event.getCategoryId(), update);
    }


    public void handle(CategoryDeletedEvent event) {
        LOGGER.info("CategoryDeletedEvent");
        LOGGER.debug("CategoryDeletedEvent:{}", event);
        Category category = categoryService.fetchCategoryById(event.getCategoryId());
        if (null != category) {
            categoryService.removeCategory(category.getId());
            LOGGER.debug("CategoryDeletedEventDeleted: {}", category.getId());



            if (Boolean.FALSE.equals(category.isIsRoot())) {


            for (Category category2 : event.getCategoryList()) {
                if (category2 != null && category2.getSubCategory() != null) {

                    category2.getSubCategory().removeIf(sub ->
                            sub.getId().equals(event.getCategoryId())
                    );


                    categoryService.saveCategory(category2);
                }

            }
         }
        }
    }


        public void handle (CategoryAssociationDeletedEvent event){
            LOGGER.info("CategoryAssociationDeletedEvent");
            LOGGER.debug("CategoryAssociationDeletedEvent:{}", event);
            String categoryId = event.getCategoryId();
            CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getCategoryId());
            if (null != entity) {
                categoryEntityRelationshipService.deleteCategory(entity.getId());
                LOGGER.debug("CategoryAssociationDeleted");

                categoryEntityRelationshipService.cleanupCategoryFromEntityRelationships(categoryId);
            }
        }

        public void handle ( final CategoryCancelledEvent event){
            LOGGER.info("Handling CategoryCancelledEvent");
            categoryService.removeCategory(event.getCategoryId());
        }

    }

