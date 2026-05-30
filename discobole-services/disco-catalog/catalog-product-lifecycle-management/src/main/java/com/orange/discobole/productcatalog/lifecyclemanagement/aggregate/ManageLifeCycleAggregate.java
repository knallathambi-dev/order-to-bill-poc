// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.aggregate;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle.SelectProductEntityCommand;
import com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle.UpdateLifeCycleCommand;
import com.orange.discobole.productcatalog.lifecyclemanagement.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleEntitySelectEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManager;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Class ManageLifeCycleAggregate handles the business logic of different
 * commands.
 * 
 * @author Vivek Singh
 *
 */
/**
 * @author GMBV8677
 *
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class ManageLifeCycleAggregate {
	@AggregateIdentifier
	private String aggregateId;
	private String entityId;
	private EntityType entityType;
	
	private static final Logger LOGGER = LogManager.getLogger(ManageLifeCycleAggregate.class);
	private static final String DISO_LS_INVALID_ENTITY_ID = "DISO_LS_INVALID_ENTITY_ID";
	private static final String DISO_LS_INVALID_LIFECYCLE_STATUS = "DISO_LS_INVALID_LIFECYCLE_STATUS";

	@Resource
	private ConfigurableProperties configurableProperties;

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;
	private String accessToken;


	public ManageLifeCycleAggregate() {
		
	}
	@Autowired
	public ManageLifeCycleAggregate(AccessTokenInterceptor accessTokenInterceptor) {
		this.accessToken = accessTokenInterceptor.getToken(); // <-- this line runs when Spring instantiates the bean
	}


	/**
	 * Process the select entity request.
	 *
	 * @param command the command
	 * @return the list of events
	 */
	@CommandHandler
	public ManageLifeCycleAggregate(SelectProductEntityCommand command,QueryService queryService) {
		LOGGER.info("In ManageLifeCycleAggregate Constructer : SelectProductEntityCommand {}",command);
		String entityTypeLocal = command.getEntityType();
		this.aggregateId=command.getAggregateId();
		if (EntityType.PRODUCTSPECIFICATION.toString().equals(entityTypeLocal)) {
			ProductSpecification productSpecification = queryService.fetchProductSpecById(command.getEntityId(),accessToken);
			if (null == productSpecification) {
				throw new DiscoManagedClientException(DISO_LS_INVALID_ENTITY_ID, command.getEntityId(),command.getEntityId());
			}
		}

		
 if (EntityType.ATOMICOFFER.getValue().equals(entityTypeLocal)|| EntityType.BUNDLEPRODUCTOFFERING.getValue().equals(entityTypeLocal) || EntityType.CONTRACT.getValue().equals(entityTypeLocal))
		  { ProductOffering
		  productOffering =
		  queryService.fetchProductOfferingById(command.getEntityId(), accessToken);
		  if (null ==  productOffering) { 
			  throw new DiscoManagedClientException(DISO_LS_INVALID_ENTITY_ID, command.getEntityId(),command.getEntityId());
		 
			}
		}
		if (EntityType.PRODUCTOFFERINGPRICE.toString().equals(entityTypeLocal)) {
			ProductOfferingPrice productOfferingPrice = queryService.getProductOfferingPrice(command.getEntityId(),accessToken);
			if (null == productOfferingPrice) {
				throw new  DiscoManagedClientException(DISO_LS_INVALID_ENTITY_ID, command.getEntityId(),command.getEntityId());
				 
				 
			}
		}
		AggregateLifecycle.apply(
				new LifeCycleEntitySelectEvent(command.getAggregateId(),command.getEntityId(), EntityType.fromValue(command.getEntityType())));

	}
	
	/**
	 * updates the state of aggregate after applying LifeCycleEntitySelectEvent.
	 * 
	 * @param event : LifeCycleEntitySelectEvent
	 */
	@EventSourcingHandler
	public void on(LifeCycleEntitySelectEvent event) {
		this.aggregateId=event.getAggregateId();
		this.entityId=event.getEntityId();
		this.entityType=event.getEntityType();
	}
	
	 
	/**
	 * Process the update request for the selected entity.
	 *
	 * @param command the command
	 * @return the list of events
	 */
	@CommandHandler
	public void updateLifeCycle(UpdateLifeCycleCommand command,LifeCycleManager lifeCycleManager) {
		LOGGER.info("In UpdateLifeCycle Method : UpdateLifeCycleCommand {}",command);
		String aggregateIdLocal=this.aggregateId;
		String id = this.entityId;
		EntityType entityTypeLocal = this.entityType;
		List<Event> eventList = lifeCycleManager.changeState(aggregateIdLocal,id, entityTypeLocal, command.getState().toString(),accessToken);
		if(eventList.get(0) instanceof InvalidLifeCycleStateSelectedEvent) {
			InvalidLifeCycleStateSelectedEvent event=(InvalidLifeCycleStateSelectedEvent)eventList.get(0);
			throw new DiscoManagedClientException(DISO_LS_INVALID_LIFECYCLE_STATUS,null,""+event.getNextPossibleStates());
		}
		AggregateLifecycle.apply(eventList.get(0));
	}
}
