// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import java.util.List;


import jakarta.annotation.Resource;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.event.category.CategoryCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryCreationEvent;
import com.orange.discobole.productcatalog.category.event.category.CategoryIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.category.event.category.EntityTypeSelectedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationCancelledEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationValidatedEvent;

/**
 * The Class CategoryEventHandlerService handles the business logic of different
 * EventHandler for category.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */

@Service
public class CategoryEventHandlerService {
	@Resource
	Publisher publisher;
	
	@EventHandler
	public void handle(EntityTypeSelectedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(CategoryIdentityDataDefinedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(CategoryCancelledEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(CategoryCreationEvent event) {
		publisher.project(List.of(event));
	}
	
	
	@EventHandler
	public void handle(CategoryModificationCancelledEvent event) {
		publisher.project(List.of(event));
	}
	
	@EventHandler
	public void handle(AssociatedEntityIndirectModifiedEvent event) {
		publisher.project(List.of(event));
	}

	
	@EventHandler
	public void handle(CategoryModificationValidatedEvent event) {
		publisher.project(List.of(event));
	}


}
