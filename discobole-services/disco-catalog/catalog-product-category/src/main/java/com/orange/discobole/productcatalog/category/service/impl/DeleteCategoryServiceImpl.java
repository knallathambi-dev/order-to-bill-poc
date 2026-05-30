// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.command.category.delete.CancelCategoryDeleteCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.DeleteValidateCategoryCommand;
import com.orange.discobole.productcatalog.category.command.category.delete.SelectCategoryDeleteCommand;
import com.orange.discobole.productcatalog.category.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;
import com.orange.discobole.productcatalog.category.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Varshika Choudhary
 */

@Service
public class DeleteCategoryServiceImpl implements DeleteCategoryService {
	private static final Logger LOGGER = LogManager.getLogger(DeleteCategoryServiceImpl.class);

	@Resource
	private Publisher publisher;

	@Resource
	private QueryService queryService;

	private final CommandGateway commandGateway;

	@Resource
	private MongoEventStoreImpl mongoEventStoreImpl;

	@Autowired
	public DeleteCategoryServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;

	}

	@Override
	public void selectCategoryDeletion(String categoryId, String categoryType) {
		LOGGER.debug("Triggerring SelectCategoryDeleteCommand with categoryId : {}", categoryId);
		commandGateway.sendAndWait(new SelectCategoryDeleteCommand(categoryId, categoryType));
	}

	@Override
	public void cancelCategoryDeletion(String categoryId) {
		LOGGER.debug("Triggerring CancelCategoryDeleteCommand with categoryId : {}", categoryId);
		commandGateway.sendAndWait(new CancelCategoryDeleteCommand(categoryId));
	}

	@Override
	public void validateDeleteCategory(String categoryId) {
		LOGGER.debug("Triggerring DeleteValidateCategoryCommand with categoryId : {}", categoryId);
		commandGateway.sendAndWait(new DeleteValidateCategoryCommand(categoryId));
	}

}
