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


import com.orange.discobole.productcatalog.category.event.category.delete.CategoryDeletedEvent;
import jakarta.annotation.Resource;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.ParentCategoryModifiedEvent;

@Service
public class DeleteCategoryHandlerServiceImpl {

	@Resource
	Publisher publisher;

	@EventHandler
	public void handle(CategoryLifeCycleUpdatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ParentCategoryModifiedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(CategoryAssociationDeletedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(CategoryDeletedEvent event) {
		publisher.project(List.of(event));
	}

}
