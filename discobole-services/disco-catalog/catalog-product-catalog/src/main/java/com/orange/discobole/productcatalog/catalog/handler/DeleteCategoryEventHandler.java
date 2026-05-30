// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DeleteCategoryEventHandler {
@Resource
private CategoryService categoryService;
@Resource
private CategoryEntityRelationshipService categoryEntityRelationshipService;

private static final Logger LOGGER = LogManager.getLogger(DeleteCategoryEventHandler.class);


public void handle(CategoryLifeCycleUpdatedEvent event) {
	LOGGER.info("CategoryLifeCycleUpdatedEvent");
	LOGGER.debug("Handling CategoryLifeCycleUpdatedEvent:{}",event);
	Category category=categoryService.fetchCategoryById(event.getCategoryId());
	if(null!=category) {
		category.setLifecycleStatus(event.getLifeCycleStatus());
		categoryService.saveCategory(category);
	}	
}

}
