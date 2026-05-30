// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt


package com.orange.discobole.productcatalog.lifecyclemanagement.aggregate;

import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.*;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.matchers.IgnoreField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle.SelectProductEntityCommand;
import com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle.UpdateLifeCycleCommand;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleEntitySelectEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManager;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

class ManageLifeCycleAggregateTest extends ManageLifeCycleApplicationTests {

	private final String entityId = "entity_id";
	private final String aggregateId = "1";
	private QueryService queryService;

	private LifeCycleManager lifeCycleManager;
	private FixtureConfiguration<ManageLifeCycleAggregate> fixture;
	private final String accessToken = null;

	@BeforeEach
	void setup() {
		queryService = Mockito.mock(QueryService.class);
		lifeCycleManager=Mockito.mock(LifeCycleManager.class);
		AccessTokenInterceptor accessTokenInterceptor = Mockito.mock(AccessTokenInterceptor.class);
		fixture = new AggregateTestFixture<>(ManageLifeCycleAggregate.class);
		fixture.registerFieldFilter(new IgnoreField(LifeCycleStateSelectedEvent.class, "lastUpdate"));
		when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
	}

	@Test
	void selectProductOfferingPriceEntityCommandTest() {

		String entityType = EntityType.PRODUCTOFFERINGPRICE.toString();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId, entityId, entityType);
		when(queryService.getProductOfferingPrice(entityId, accessToken))
				.thenReturn(new ProductOfferingPrice().id(entityId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));

		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand).expectEvents(new LifeCycleEntitySelectEvent(aggregateId, entityId, EntityType.fromValue(entityType)));
	}

	@Test
	void selectProductOfferingPriceEntityCommandTestWithNullPOP() {
		String entityType = EntityType.PRODUCTOFFERINGPRICE.toString();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId, entityId, entityType);
		when(queryService.getProductOfferingPrice(entityId, accessToken))
				.thenReturn(null);

		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void selectProductOfferingEntityAtomicPOEntityTypeCommandTest() {
		String entityType = EntityType.ATOMICOFFER.getValue();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId, entityId, entityType);
		when(queryService.fetchProductOfferingById(entityId, accessToken)).thenReturn(new ProductOffering().id(entityId));
		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectEvents(new LifeCycleEntitySelectEvent(aggregateId, entityId, EntityType.fromValue(entityType)));
	}
	@Test
	void selectProductOfferingEntityAtomicPOEntityTypeCommandTestWithNullPO() {
		String entityType = EntityType.ATOMICOFFER.getValue();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId, entityId, entityType);
		when(queryService.fetchProductOfferingById(entityId, accessToken)).thenReturn(null);
		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void selectProductOfferingEntityPSEntityTypeCommandTest() {
		String entityType = EntityType.PRODUCTSPECIFICATION.toString();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId,entityId, entityType);
		when(queryService.fetchProductSpecById(entityId, accessToken)).thenReturn(new ProductSpecification().id(entityId));
		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectEvents(new LifeCycleEntitySelectEvent(aggregateId, entityId, EntityType.fromValue(entityType)));
	}

	@Test
	void selectProductOfferingEntityPSEntityTypeCommandTestWithNullPS() {
		String entityType = EntityType.PRODUCTSPECIFICATION.toString();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId,entityId, entityType);
		when(queryService.fetchProductSpecById(entityId, accessToken)).thenReturn(null);
		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectException(DiscoManagedClientException.class);
	}



	@Test
	void selectProductOfferingEntityBundlePOEntityTypeCommandTest() {
		String entityType = EntityType.BUNDLEPRODUCTOFFERING.getValue();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId,entityId, entityType);
		when(queryService.fetchProductOfferingById(entityId, accessToken)).thenReturn(new ProductOffering().id(entityId));
		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectEvents(new LifeCycleEntitySelectEvent(aggregateId, entityId, EntityType.fromValue(entityType)));
	}


	@Test
	void selectProductOfferingEntityContractPOEntityTypeCommandTest() {

		String entityType = EntityType.CONTRACT.getValue();
		SelectProductEntityCommand selectProductEntityCommand = new SelectProductEntityCommand(aggregateId,entityId, entityType);
		when(queryService.fetchProductOfferingById(entityId, accessToken)).thenReturn(new ProductOffering().id(entityId));
		fixture.registerInjectableResource(queryService).given().when(selectProductEntityCommand)
				.expectEvents(new LifeCycleEntitySelectEvent(aggregateId, entityId, EntityType.fromValue(entityType)));
	}

	@Test
	void updateLifeCycleSCommandTest() {
		String entityType = EntityType.PRODUCTOFFERINGPRICE.toString();
		List<Event> history = new ArrayList<>();
		history.add(new LifeCycleEntitySelectEvent(aggregateId,entityId, EntityType.fromValue(entityType)));
		UpdateLifeCycleCommand updateLifeCycleSCommand = new UpdateLifeCycleCommand(aggregateId, LifecycleState.LAUNCHED,null);
		LifeCycleStateSelectedEvent event=new LifeCycleStateSelectedEvent(aggregateId,entityId,EntityType.PRODUCTOFFERINGPRICE,"launched","launched",OffsetDateTime.now(),null);
		List<Event>events=new ArrayList<>();
		events.add(event);
		when(lifeCycleManager.changeState(aggregateId, entityId,  EntityType.PRODUCTOFFERINGPRICE,LifecycleState.LAUNCHED.toString(), null))
				.thenReturn(events);

		fixture.registerInjectableResource(lifeCycleManager).registerInjectableResource(queryService).given(history).when(updateLifeCycleSCommand)
				.expectEvents(event);

	}
	@Test
	void updateLifeCycleSCommandTestWithException() {
		String entityType = EntityType.PRODUCTOFFERINGPRICE.toString();
		List<Event> history = new ArrayList<>();
		history.add(new LifeCycleEntitySelectEvent(aggregateId,entityId, EntityType.fromValue(entityType)));
		UpdateLifeCycleCommand updateLifeCycleSCommand = new UpdateLifeCycleCommand(aggregateId, LifecycleState.OBSOLETE,"0.1");

		when(queryService.getProductOfferingPrice(entityId, accessToken))
				.thenReturn(null);
		List<Event> list=new ArrayList<>();
		list.add(new InvalidLifeCycleStateSelectedEvent(aggregateId, entityId, null, entityType, null));
		when(lifeCycleManager.changeState(aggregateId, entityId,  EntityType.PRODUCTOFFERINGPRICE,LifecycleState.OBSOLETE.toString(), null))
				.thenReturn(list);

		fixture.registerInjectableResource(lifeCycleManager).given(history).when(updateLifeCycleSCommand)
				.expectException(DiscoManagedClientException.class);

	}


}
