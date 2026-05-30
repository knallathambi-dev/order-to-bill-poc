// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.event.category.AssociateEntitySelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCreationEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.category.event.category.EntityTypeSelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.ParentCategoryUpdatedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.ParentCategoryModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.category.projection.CategoryProjector;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class CategoryInitializer implements CommandLineRunner {
	@Resource
	private Publisher publisher;
	
	@Resource
	private CategoryProjector projector;
	
	@Override
	public void run(String... args) throws Exception {
		publisher.register(EntityTypeSelectedEvent.class,projector::handle);
		publisher.register(CategoryIdentityDataDefinedEvent.class, projector::handle);
		publisher.register(AssociateEntitySelectedEvent.class,projector::handle );
		publisher.register(CategoryCancelledEvent.class,projector::handle );
		publisher.register(CategoryCreationEvent.class,projector::handle);
		publisher.register(ParentCategoryUpdatedEvent.class,projector::handle);
		publisher.register(AssociatedEntityModifiedEvent.class, projector::handle);
		publisher.register(CategoryIdentityDataModifiedEvent.class, projector::handle);
		publisher.register(CategoryLifeCycleUpdatedEvent.class,projector::handle);
		publisher.register(CategoryAssociationDeletedEvent.class,projector::handle);
		publisher.register(ParentCategoryModifiedEvent.class,projector::handle);
		publisher.register(AssociatedEntityIndirectModifiedEvent.class,projector::handle);
		publisher.register(CategoryDeletedEvent.class,projector::handle);
	}
}
