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
import com.orange.discobole.productcatalog.category.command.category.*;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.AssociateEntitySelectedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
import com.orange.discobole.productcatalog.category.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.category.service.CategoryService;
import com.orange.discobole.productcatalog.category.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
/**
 * The Class CategoryServiceImpl handles the business logic of different
 * steps involved  in category creation.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
@Service
public class CategoryServiceImpl implements CategoryService {

	private static final Logger LOGGER = LogManager.getLogger(CategoryServiceImpl.class);

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
	public CategoryServiceImpl(CommandGateway commandGateway,AccessTokenInterceptor accessTokenInterceptor) {
		this.commandGateway = commandGateway;
		this.accessToken = accessTokenInterceptor.getToken(); // <-- this line runs when Spring instantiates the bean
	}



	@Resource
	ConfigurableProperties configurableProperties;

	@Override
	public String defineCategoryIdentityData(
			String name,
			String description,
			Boolean isRoot,
			String parentId,
			List<String> subcategoryIds,List<String> productOfferingIds) {

		String aggregateId = UUID.randomUUID().toString();

		return commandGateway.sendAndWait(
				new DefineCategoryIdentityDataCommand(
						aggregateId,
						name,
						description,
						isRoot,
						parentId,
						subcategoryIds,
						productOfferingIds,
						this.configurableProperties.getCatprodcaturl()
				)
		);
	}




	@Override
	public void associateEntity(String categoryId, List<String> productOfferings) {
		
		commandGateway.sendAndWait(new SelectAssociateEntityCommand(categoryId, productOfferings));

	}

	@Override
	public void cancelCategory(String categoryId) {

		commandGateway.sendAndWait(new CancelCategoryCommand(categoryId));
	}

	@Override
	public void validateCategory(String categoryId) {
		commandGateway.sendAndWait(new ValidateCategoryCommand(categoryId));
		Set<String> duplicateCheck = new HashSet<>();
		Set<CategoryRef> categories=new HashSet<>();
		AssociateEntitySelectedEvent associateEntitySelectedEvent=null;
		for (DomainEventMessage<Event> eventmsg : mongoEventStoreImpl.readEventsBackword(categoryId, 0)) {
			LOGGER.info("Events to publish - {}", eventmsg.getClass().getName());
			Event event = eventmsg.getPayload();
			if (duplicateCheck.add(eventmsg.getPayloadType().getSimpleName()) && event instanceof AssociateEntitySelectedEvent) {
					associateEntitySelectedEvent = (AssociateEntitySelectedEvent)event;
					break;
			}
		}
		if(!ObjectUtils.isEmpty(associateEntitySelectedEvent)){
			Category category=queryService.fetchCategoryById(associateEntitySelectedEvent.getCategoryId(), accessToken);
			categories.add(new CategoryRef().id(category.getId()).type(category.getType()).baseType(category.getBaseType())
					.referredType(category.getType()).schemaLocation(category.getSchemaLocation()));
			triggerProductOfferingModifiedEvents(associateEntitySelectedEvent,categories);
		}
		publisher.project(List.of(associateEntitySelectedEvent));
	}

	private void triggerProductOfferingModifiedEvents(AssociateEntitySelectedEvent associateEntitySelectedEvent,Set<CategoryRef> categories) {
		List<Event> productOfferingEvents=new ArrayList<>();
		for (ProductOfferingRef productOffering : associateEntitySelectedEvent.getProductOfferings()) {
			LOGGER.info("associateEntitySelectedEvent.getProductOfferings() :{}", productOffering);
			productOfferingEvents.add(new ProductOfferingCategoryAssociationEvent(productOffering.getId(),categories,true,productOffering.getType()));
		}
		publisher.project(productOfferingEvents);
	}
}
