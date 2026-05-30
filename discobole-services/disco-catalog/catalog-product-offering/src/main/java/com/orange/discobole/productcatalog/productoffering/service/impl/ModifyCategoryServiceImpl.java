// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import java.util.List;
import java.util.Set;


import jakarta.annotation.Resource;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.command.category.modify.ModifyAssociateEntityCommandPOCreation;
import com.orange.discobole.productcatalog.productoffering.command.category.modify.ModifyAssociateEntityCommandPOModification;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.productoffering.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productoffering.service.ModifyCategoryService;

@Service
public class ModifyCategoryServiceImpl implements ModifyCategoryService {

	private final CommandGateway commandGateway;
	

	MongoEventStoreImpl mongoEventStoreImpl;

	@Resource
	private Publisher publisher;

	@Autowired
	public ModifyCategoryServiceImpl(CommandGateway commandGateway,	MongoEventStoreImpl mongoEventStoreImpl) {
		this.commandGateway = commandGateway;
		this.mongoEventStoreImpl = mongoEventStoreImpl;

	}

	
	public void modifyAssociatedEntity(String categoryId, Set<ProductOfferingRef> productOfferings, boolean creation) {
		if(creation) {
		commandGateway.sendAndWait(new ModifyAssociateEntityCommandPOCreation(categoryId,productOfferings));
		} else {
			commandGateway.sendAndWait(new ModifyAssociateEntityCommandPOModification(categoryId,productOfferings));
		}
		
	}


	@Override
	public void modifyAssociatedEntity(String categoryId, List<String> productOfferings) {

		//implemented in future if needed
	}



}
