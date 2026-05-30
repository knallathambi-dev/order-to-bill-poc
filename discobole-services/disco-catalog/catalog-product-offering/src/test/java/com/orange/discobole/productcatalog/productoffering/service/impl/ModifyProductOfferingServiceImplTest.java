// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import static org.junit.Assert.assertNotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.aggregate.ProductOfferingAggregate;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationshipType;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.service.impl.ModifyProductOfferingServiceImpl;

class ModifyProductOfferingServiceImplTest {
	@Mock
	ObjectMapper objectMapper;
	CommandGateway commandGateway;
	MongoEventStoreImpl mongoEventStoreImpl;

	@InjectMocks
	ModifyProductOfferingService modifyproductOfferingService;
	QueryService queryService = Mockito.mock(QueryService.class);

	ApplicationContext appCtx = Mockito.mock(ApplicationContext.class);

	Publisher publisher = Mockito.mock(Publisher.class);

	@Mock
	ProductOfferingAggregate productOfferingAggregate;
	MongoEventStorageEngine engine = Mockito.mock(MongoEventStorageEngine.class);

	ModifyProductOfferingServiceImplTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class); // <-- Added mock
		modifyproductOfferingService = new ModifyProductOfferingServiceImpl(commandGateway,mongoEventStoreImpl);
	}

	@BeforeEach
	void beforeEachSetup() {
		ReflectionTestUtils.setField(modifyproductOfferingService, "publisher", publisher);
		ReflectionTestUtils.setField(modifyproductOfferingService, "mongoEventStoreImpl", mongoEventStoreImpl);
	}

	@Test
	void initiatePOModificationTest() {

		String productOffId = "product_off_id";
		modifyproductOfferingService.initiatePOModification(productOffId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}

	@Test
	void modifyProductOffDescTest() {
		String productOffId = "product_off_id";
		modifyproductOfferingService.modifyProductOffDesc(productOffId,new DefineIdentityData(), new HashSet<String>(), new HashSet<String>(),new HashSet<RelatedParty>()
				,new HashSet<ProductOfferingTerm>(),new TimePeriod(),ProductOfferingType.ATOMICPRODUCTOFFERING,"",null);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}

	@Test
	void modifyProductOfferingCategory() {
		String productOffId = "product_off_id";
		List<String> categories = new ArrayList<>();
		categories.add("BOS_B2B_Classification");
		modifyproductOfferingService.modifyProductOfferingCategory(productOffId, categories);
		assertNotNull(categories);

	}





	@Test
	void modifyProductOfferingOperationTest() {

		String productOffId = "product_off_id";
		CommercialOperation operationSpecification = new CommercialOperation();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		operationSpecification.id("1").name("Create").validFor(new TimePeriod().startDateTime(lastUpdate));
		List<CommercialOperation> operationSpecificationList = new ArrayList<>();
		operationSpecificationList.add(operationSpecification);

		modifyproductOfferingService.modifyProductOfferingOperation(productOffId, operationSpecificationList);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void modifyAssociatePOPtoOperationSpecificationTest() {

		String productOffId = "product_off_id";
		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();

		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

		modifyproductOfferingService.modifyAssociatePOPtoOperationSpecification(associatePOPtoOperationSpecList,
				productOffId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}

	@Test
	void modifyProductOfferingCharacteristicsTest() {
		String productSpecId = "1";
		String productOffId = "product_off_id";
		OffsetDateTime dateTime = OffsetDateTime.now();
		com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod timePeriod = new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod();
		timePeriod.setStartDateTime(dateTime.plusDays(1));
		timePeriod.endDateTime(dateTime.plusDays(9));

		ProductCharValue productCharValue = new ProductCharValue();
		productCharValue.setValue("10");
		productCharValue.setValidFor(timePeriod);
		List<ProductCharValue> productCharValues = new ArrayList<>();
		productCharValues.add(productCharValue);
		PickAtomicProductOfferingCharacteristic pickAtomicProductOfferingCharacteristics = new PickAtomicProductOfferingCharacteristic();
		pickAtomicProductOfferingCharacteristics.setProductSpecCharacteristicValue(productCharValues);
		pickAtomicProductOfferingCharacteristics.setId(productSpecId);
		pickAtomicProductOfferingCharacteristics.setMinCardinality(0);
		pickAtomicProductOfferingCharacteristics.setMaxCardinality(1);
		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);

		modifyproductOfferingService.modifyProductOfferingCharacteristics(productOffId,
				pickAtomicProductOfferingCharacteristic);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}

	@Test
	void modifyProductOfferingRelationshipTest() {

		String productOffId = "product_off_id";
		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
		productOfferingRelationship.setId("rel_1");
		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.REQUIRES);
		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
		productOfferingRelationships.add(productOfferingRelationship);
		

		modifyproductOfferingService.modifyProductOfferingRelationship(productOffId, productOfferingRelationships);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void cancelProductOfferingModificationTest() {

		String productOffId = "product_off_id";
		modifyproductOfferingService.cancelProductOfferingModification(productOffId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	/*
	 * @Test void validateProductOfferingModificationTest () {
	 * 
	 * 
	 * 
	 * String productOffId = "product_off_id"; OffsetDateTime lastUpdate =
	 * OffsetDateTime.now(); ProductOffering po = new
	 * ProductOffering().id(productOffId).name("po1").
	 * lifecycleStatus(ProductOfferingLifecycle.ACTIVE);
	 * 
	 * 
	 * ProductOfferingLifecycle lifecycleStatus = po.getLifecycleStatus();
	 * ProductOfferingModificationInitiatedEvent
	 * productOfferingModificationInitiatedEvent = new
	 * ProductOfferingModificationInitiatedEvent(productOffId, po,
	 * OffsetDateTime.now(), lifecycleStatus);
	 * 
	 * List<Event> eventList = new ArrayList<>();
	 * AtomicProductOfferingModificationValidatedEvent
	 * atomicProductOfferingModificationValidatedEvent = new
	 * AtomicProductOfferingModificationValidatedEvent(productOffId,po,
	 * ProductOfferingLifecycle.INTEST, lastUpdate);
	 * eventList.add(atomicProductOfferingModificationValidatedEvent);
	 * 
	 * 
	 * 
	 * List<DomainEventMessage<Event>> backwardEvent = new ArrayList<>();
	 * DomainEventMessage<Event> messageEvent2 =
	 * Mockito.mock(DomainEventMessage.class);
	 * when(messageEvent2.getPayload()).thenReturn((Event)
	 * atomicProductOfferingModificationValidatedEvent);
	 * when(messageEvent2.getPayloadType()) .thenReturn((Class<Event>)
	 * atomicProductOfferingModificationValidatedEvent.getClass());
	 * backwardEvent.add(messageEvent2);
	 * 
	 * DomainEventMessage<Event> messageEvent =
	 * Mockito.mock(DomainEventMessage.class);
	 * when(messageEvent.getPayload()).thenReturn((Event)
	 * productOfferingModificationInitiatedEvent);
	 * when(messageEvent.getPayloadType()) .thenReturn((Class<Event>)
	 * productOfferingModificationInitiatedEvent.getClass());
	 * backwardEvent.add(messageEvent);
	 * 
	 * 
	 * 
	 * Mockito.when(mongoEventStoreImpl.readEventsBackword(productOffId,
	 * 0)).thenReturn(backwardEvent);
	 * Mockito.doNothing().when(publisher).project(eventList);
	 * 
	 * 
	 * 
	 * modifyproductOfferingService.validateProductOfferingModification(productOffId
	 * ); Mockito.verify(commandGateway,
	 * Mockito.times(1)).sendAndWait(Mockito.any());
	 * 
	 * 
	 * //
	 * modifyproductOfferingService.validateProductOfferingModification(productOffId
	 * );
	 * 
	 * }
	 */

}
