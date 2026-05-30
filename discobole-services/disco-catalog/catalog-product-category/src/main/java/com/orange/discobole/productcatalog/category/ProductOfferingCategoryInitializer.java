// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
import com.orange.discobole.productcatalog.category.projection.ProductOfferingCategoryProjector;

import jakarta.annotation.Resource;


@Component
public class ProductOfferingCategoryInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;
	
	@Resource
	private ProductOfferingCategoryProjector projector;
	@Override
	public void run(String... args) throws Exception {
		publisher.register(ProductOfferingCategoryAssociationEvent.class, projector::handle);
		publisher.register(ProductOfferingCategoryAssociationDeletedEvent.class, projector::handle);
	}

}
