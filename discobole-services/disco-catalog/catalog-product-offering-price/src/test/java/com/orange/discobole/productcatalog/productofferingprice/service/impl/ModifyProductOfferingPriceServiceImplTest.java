// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.POPRelationshipType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.RecurringChargePeriodType;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;

class ModifyProductOfferingPriceServiceImplTest {

	@InjectMocks
	private ModifyProductOfferingPriceServiceImpl modifyProductOfferingPriceServiceImpl;
	QueryService queryService = Mockito.mock(QueryService.class);
	private Publisher publisher = Mockito.mock(Publisher.class);
	MongoEventStoreImpl mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class);

	private static String productOfferingPriceId = "1";
	CommandGateway commandGateway;

	public ModifyProductOfferingPriceServiceImplTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		modifyProductOfferingPriceServiceImpl = new ModifyProductOfferingPriceServiceImpl(commandGateway);

	}

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(modifyProductOfferingPriceServiceImpl, "queryService", queryService);
		ReflectionTestUtils.setField(modifyProductOfferingPriceServiceImpl, "publisher", publisher);
//		ReflectionTestUtils.setField(modifyProductOfferingPriceServiceImpl, "mongoEventStoreImpl", mongoEventStoreImpl);
	}

	@Test
	void triggerModifyProductOfferingPrice() {
		modifyProductOfferingPriceServiceImpl.modifyProductOfferingPrice(productOfferingPriceId,
				ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerModifyPOPChargeIdentityData() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.RC).recurringChargePeriodLength(10)
				.recurringChargePeriodType("month").price(money).unitOfMeasure(quantity);

		modifyProductOfferingPriceServiceImpl.modifyPOPChargeIdentityData(productOfferingPriceId, identityData,
				productOfferingPriceRelationships, popStatusValidityPeriod);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerModifyPOPAlterationIdentityData() {
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		ApplicationDuration duration = new ApplicationDuration().amount(10);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP alteration").priceType(PriceType.RC).applicationDuration(duration).priority(1)
				.percentage(15.0f).price(new Money().value(51.0f).unit("INR"))
				.priceAlterationType(PriceAlterationType.PRICE).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));

		modifyProductOfferingPriceServiceImpl.modifyPOPAlterationIdentityData(productOfferingPriceId, identityData, popStatusValidityPeriod);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerCancelProductOfferingPriceModification() {
		modifyProductOfferingPriceServiceImpl.cancelProductOfferingPriceModification(productOfferingPriceId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerInitiatePOPModification() {
		modifyProductOfferingPriceServiceImpl.initiatePOPModification(productOfferingPriceId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Test
	 */
	void triggerValidatePOPModification() {
		
		modifyProductOfferingPriceServiceImpl.validatePOPModification(productOfferingPriceId,"minor");
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
		/*
		 * 
		 * ProductOfferingPrice pop = new
		 * ProductOfferingPrice().id("1").name("POPA").priceType(PriceType.RC).priority(
		 * 1) .price(new Money().value(51.0f).unit("INR")).lifecycleStatus(
		 * ProductOfferingPriceLifecycle.UNAVAILABLE);
		 * 
		 * ProductOfferingPriceModificationInitiatedEvent
		 * productOfferingPriceModificationInitiatedEvent = new
		 * ProductOfferingPriceModificationInitiatedEvent( productOfferingPriceId, pop,
		 * OffsetDateTime.now(), pop.getLifecycleStatus());
		 * 
		 * List<Event> eventList = new ArrayList<>();
		 * ProductOfferingPriceModificationValidatedEvent
		 * productOfferingPriceModificationValidatedEvent = new
		 * ProductOfferingPriceModificationValidatedEvent( productOfferingPriceId, pop,
		 * pop.getLifecycleStatus(), OffsetDateTime.now());
		 * eventList.add(productOfferingPriceModificationValidatedEvent);
		 * 
		 * List<DomainEventMessage<Event>> backwardEvent = new ArrayList<>();
		 * DomainEventMessage<Event> messageEvent2 =
		 * Mockito.mock(DomainEventMessage.class);
		 * when(messageEvent2.getPayload()).thenReturn((Event)
		 * productOfferingPriceModificationValidatedEvent);
		 * when(messageEvent2.getPayloadType()) .thenReturn((Class<Event>)
		 * productOfferingPriceModificationValidatedEvent.getClass());
		 * backwardEvent.add(messageEvent2);
		 * 
		 * 
		 * DomainEventMessage<Event> messageEvent =
		 * Mockito.mock(DomainEventMessage.class);
		 * when(messageEvent.getPayload()).thenReturn((Event)
		 * productOfferingPriceModificationInitiatedEvent);
		 * when(messageEvent.getPayloadType()) .thenReturn( (Class<Event>)
		 * productOfferingPriceModificationInitiatedEvent.getClass());
		 * backwardEvent.add(messageEvent);
		 * 
		 * 
		 * Mockito.when(mongoEventStoreImpl.readEventsBackword(productOfferingPriceId,
		 * 0)).thenReturn(backwardEvent);
		 * Mockito.doNothing().when(publisher).project(eventList);
		 * 
		 * modifyProductOfferingPriceServiceImpl.validatePOPModification(
		 * productOfferingPriceId); Mockito.verify(commandGateway,
		 * Mockito.times(1)).sendAndWait(Mockito.any());
		 */}
}
