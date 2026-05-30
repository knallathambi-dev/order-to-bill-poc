// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.handler;


import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.productoffering.event.category.CategoryProductOfferingAssociationEvent;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CategoryProductOfferingEventHandler {
    @Resource
    private ModifyCategoryService categoryService;
    
    
    private static final Logger LOGGER = LogManager.getLogger(CategoryProductOfferingEventHandler.class);

    public void handle(final CategoryProductOfferingAssociationEvent event){
    	 LOGGER.info("Handling Message CategoryProductOfferingAssociationEvent");
    	   categoryService.modifyAssociatedEntity(event.getCategoryId(),event.getProductOfferings(),event.getIsAddition());
    }
}
