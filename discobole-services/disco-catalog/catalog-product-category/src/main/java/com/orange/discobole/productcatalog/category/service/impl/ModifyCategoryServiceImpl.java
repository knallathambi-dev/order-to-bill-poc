// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.command.category.modify.*;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModifiedInitiatedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
import com.orange.discobole.productcatalog.category.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.category.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ModifyCategoryServiceImpl implements ModifyCategoryService {
	private static final Logger LOGGER = LogManager.getLogger(ModifyCategoryServiceImpl.class);

	private final CommandGateway commandGateway;
	
	@Resource
	MongoEventStoreImpl mongoEventStoreImpl;

	@Resource
	private Publisher publisher;

	@Resource
	private QueryService queryService;

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;
	private String accessToken;

	@Autowired
	public ModifyCategoryServiceImpl(CommandGateway commandGateway,AccessTokenInterceptor accessTokenInterceptor) {
		this.commandGateway = commandGateway;
		this.accessToken = accessTokenInterceptor.getToken(); // <-- this line runs when Spring instantiates the bean

	}

	@Override
	public  void  modifyCategoryIdentityData(String categoryId, String name, String description, Boolean isRoot,
			String parentId,List<String> subcategoryIds, List<String> productOfferings) {

		 commandGateway.sendAndWait(new ModifyCategoryIdentityDataCommand(categoryId, name, description, isRoot, parentId,subcategoryIds, productOfferings));

	}
	
	@Override
	public void initiateCategoryModification(String categoryId) {
		commandGateway.sendAndWait(new ModifySelectCategoryCommand(categoryId));
	}

	@Override
	public void modifyAssociatedEntity(String categoryId, List<String> productOfferings) {
		commandGateway.sendAndWait(new ModifyAssociateEntityCommand(categoryId,productOfferings));
	}
	
	@Override
	public void modifyAssociatedEntity(String categoryId, Set<ProductOfferingRef> productOfferings,Boolean isAddition) {
		if(isAddition==Boolean.TRUE) {
		commandGateway.sendAndWait(new ModifyAssociateEntityCommandPOCreation(categoryId,productOfferings));
		} else {
			commandGateway.sendAndWait(new ModifyAssociateEntityCommandPOModification(categoryId,productOfferings));
		}
		
	}

	@Override
	public void validateModifyCategory(String categoryId) {
		commandGateway.sendAndWait(new ModifyValidateCategoryCommand(categoryId));
		List<Event> project = new ArrayList<>();
		Set<String> duplicateCheck = new HashSet<>();
		AssociatedEntityModifiedEvent associatedEntityModifiedEvent=null;
		Set<CategoryRef> categories = new HashSet<>();
		for (DomainEventMessage<Event> eventmsg : mongoEventStoreImpl.readEventsBackword(categoryId, 0)) {
			LOGGER.info("Events to publish - {}", eventmsg.getClass().getName());
			Event event = eventmsg.getPayload();
			if (duplicateCheck.add(eventmsg.getPayloadType().getSimpleName())) {
				if (event instanceof CategoryModifiedInitiatedEvent) {
					break;
				} 
				else if(event instanceof AssociatedEntityModifiedEvent) {
					associatedEntityModifiedEvent=(AssociatedEntityModifiedEvent)event;
				}
				project.add(event);
			}

		}
		if(null!=associatedEntityModifiedEvent) {
			Category category=queryService.fetchCategoryById(associatedEntityModifiedEvent.getCategoryId(), accessToken);
			categories.add(new CategoryRef().id(category.getId()).type(category.getType()).baseType(category.getBaseType())
					.referredType(category.getType()).schemaLocation(category.getSchemaLocation()));
			triggerProductOfferingModifiedEvents(associatedEntityModifiedEvent,categories);
		}
		LOGGER.info("Triggering CatagoryModificationValidatedCommand");
		publisher.project(project);



	}

	private void triggerProductOfferingModifiedEvents(AssociatedEntityModifiedEvent associatedEntityModifiedEvent,Set<CategoryRef> categories) {
		List<Event> productOfferingEvents=new ArrayList<>();
		for (ProductOfferingRef productOffering : associatedEntityModifiedEvent.getDelProductOfferings()) {
			LOGGER.info("associatedEntityModifiedEvent.getDelProductOfferings() :{}", productOffering);
			ProductOfferingCategoryAssociationEvent event=new ProductOfferingCategoryAssociationEvent(productOffering.getId(),categories,false,productOffering.getType());
			productOfferingEvents.add(event);
		}
		for (ProductOfferingRef productOffering : associatedEntityModifiedEvent.getAddProductOfferings()) {
			LOGGER.info("associatedEntityModifiedEvent.getAddProductOfferings() :{}", productOffering);
			ProductOfferingCategoryAssociationEvent event=new ProductOfferingCategoryAssociationEvent(productOffering.getId(),categories,true,productOffering.getType());
			productOfferingEvents.add(event);
		}
		publisher.project(productOfferingEvents);
	}



	@Override
	public void cancelCategoryModification(String categoryId) {
		commandGateway.sendAndWait(new CancelCategoryModificationCommand(categoryId));

	}
}
