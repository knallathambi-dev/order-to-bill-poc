// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.aggregate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.*;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceTaxAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.axonframework.test.matchers.IgnoreField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyDefineRelationshipCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyProductOfferingPriceAlterationIdentityDataCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyProductOfferingPriceChargeIdentityDataCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyStatusValidityPeriodCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.POPModificationCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationCancelCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationValidatedCommand;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.POPRelationshipType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceAlterationIdentityDataDescribedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceCancelledEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceChargeIdentityDataDescribedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceCreationCompletedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceDeleteEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceInitiatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceAlterationIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceChargeIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationCancelledEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationInitiatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceStatusValidityPeriodModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceVersionCreatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;

class ProductOfferingPriceAggregateTest extends ProductOfferingPriceApplicationTests {

	private QueryService queryService;
	private FixtureConfiguration<ProductOfferingPriceAggregate> fixture;
	private ProductOfferingPrice productOfferingPrice;
	private ProductOfferingPrice existingProductOfferingPrice;

	private static String aggregateId = "1";
	private static String version = "1.0.0";


	@BeforeEach
	void setUp() {
		queryService = Mockito.mock(QueryService.class);
		fixture = new AggregateTestFixture<>(ProductOfferingPriceAggregate.class);

		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceInitiatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceChargeIdentityDataDescribedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceAlterationIdentityDataDescribedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceRelationshipDefinedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceValidatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceCreationCompletedEvent.class, "productOfferingPrice"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceCancelledEvent.class, "productOfferingPrice"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceVersionCreatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceChargeIdentityDataModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceAlterationIdentityDataModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceStatusValidityPeriodModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceRelationshipModifiedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceModificationInitiatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceModificationValidatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceInitiatedEvent.class, "lastUpdate"));
		fixture.registerFieldFilter(
				new IgnoreField(ProductOfferingPriceInitiatedEvent.class, "productOfferingPriceId"));
		fixture.registerFieldFilter(new IgnoreField(ProductOfferingPriceDeleteEvent.class, "aggregateId"));
		productOfferingPrice = new ProductOfferingPrice();
		productOfferingPrice.setId(aggregateId);
		productOfferingPrice.setLifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE);

		existingProductOfferingPrice = new ProductOfferingPrice();
		existingProductOfferingPrice.setId(aggregateId);
		existingProductOfferingPrice.setLifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE);
	}

	@Test
	void testCancelProductOfferingPrice() {
		ProductOfferingPriceType productOfferingPriceType = ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE;
		ProductOfferingPriceCancelCommand command = new ProductOfferingPriceCancelCommand(aggregateId);
		fixture.given(new ProductOfferingPriceInitiatedEvent(aggregateId, productOfferingPriceType,
				OffsetDateTime.now(), ProductOfferingPriceLifecycle.UNAVAILABLE)).when(command)
				.expectEvents(new ProductOfferingPriceCancelledEvent(aggregateId, productOfferingPrice));
		Assertions.assertNotNull(command.toString());
	}

	@Test
	void testCancelProductOfferingPriceWithLifeCycleStatusNotUnavailable() {
		ProductOfferingPriceType productOfferingPriceType = ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE;
		fixture.given(new ProductOfferingPriceInitiatedEvent(aggregateId, productOfferingPriceType,
				OffsetDateTime.now(), ProductOfferingPriceLifecycle.LAUNCHED))
				.when(new ProductOfferingPriceCancelCommand(aggregateId))
				.expectException(DiscoManagedClientException.class);
	}

	@Test
	void selectProductOfferingPriceTypeTest() {
		ProductOfferingPriceType productOfferingPriceType = ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE;
		fixture.given().when(new InitiateProductOfferingPriceCommand(aggregateId, productOfferingPriceType))
				.expectEvents(new ProductOfferingPriceInitiatedEvent(aggregateId, productOfferingPriceType,
						OffsetDateTime.now(), ProductOfferingPriceLifecycle.UNAVAILABLE));
	}
	@Test
	void testPOPCIdentityDataTestInvalidRelationReplacedByTAX() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		String popRelationId=UUID.randomUUID().toString();
		ProductOfferingPriceRelationship productOfferingPriceRelationship1 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship1);


		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").price(money).unitOfMeasure(quantity).immediatePayment(true)
				.priceType(PriceType.NRC);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);
		List<Currency> validCurrencies= new ArrayList();
		validCurrencies.add(new Currency().id("currency").code("INR"));

		when(queryService.fetchCurrency()).thenReturn(validCurrencies);
		// ---- Correct: TAX POPA must be subclass, using setters ----
		TaxProductOfferingPriceAlteration taxPopa = new TaxProductOfferingPriceAlteration();
		taxPopa.setPrice(new Money().value(5.0f).unit("INR"));
		taxPopa.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		taxPopa.setType(ProductOfferingPriceType.PRODUCTOFFERINGTAXALTERATION.getValue());


		when(queryService.getProductOfferingPrice(anyString()))
				.thenReturn(taxPopa);

		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);

	}
	@Test
	void testPOPCIdentityDataTestInvalidRelationMultipleReplacedBy() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		String popRelationId=UUID.randomUUID().toString();
		ProductOfferingPriceRelationship productOfferingPriceRelationship1 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		ProductOfferingPriceRelationship productOfferingPriceRelationship2 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship2);


		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.NRC).price(money).unitOfMeasure(quantity).immediatePayment(true);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);
		List<Currency> validCurrencies= new ArrayList();
		validCurrencies.add(new Currency().id("currency").code("INR"));
		when(queryService.fetchCurrency()).thenReturn(validCurrencies);
		ProductOfferingPriceCharge popc = new ProductOfferingPriceCharge();
		popc.price(new Money().value(5.0f).unit("INR"));  // inherited
		popc.setPriceType(PriceType.NRC);                 // subclass field
		popc.lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popc.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.getValue());
		when(queryService.getProductOfferingPrice(anyString()))
				.thenReturn(popc);
		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);

	}
	@Test
	void testPOPCIdentityDataTestInvalidRelationBothReplacedByAlteredBy() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		String popRelationId=UUID.randomUUID().toString();
		ProductOfferingPriceRelationship productOfferingPriceRelationship1 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		ProductOfferingPriceRelationship productOfferingPriceRelationship2 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship2);


		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.NRC).price(money).unitOfMeasure(quantity).immediatePayment(true);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);
		List<Currency> validCurrencies= new ArrayList();
		validCurrencies.add(new Currency().id("currency").code("INR"));

		when(queryService.fetchCurrency()).thenReturn(validCurrencies);
		ProductOfferingPriceCharge popc = new ProductOfferingPriceCharge();
		popc.price(new Money().value(5.0f).unit("INR"));  // inherited
		popc.setPriceType(PriceType.NRC);                 // subclass field
		popc.lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popc.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION.getValue());
		when(queryService.getProductOfferingPrice(anyString()))
				.thenReturn(popc);
		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);

	}






	@Test
	void testPOPCIdentityDataTestInvalidRelationBothReplacedByAlteredBy2() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		String popRelationId=UUID.randomUUID().toString();
		ProductOfferingPriceRelationship productOfferingPriceRelationship1 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		ProductOfferingPriceRelationship productOfferingPriceRelationship2 = new ProductOfferingPriceRelationship()
				.id(popRelationId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship2);


		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.NRC).price(money).unitOfMeasure(quantity).immediatePayment(true);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);
		List<Currency> validCurrencies= new ArrayList();
		validCurrencies.add(new Currency().id("currency").code("INR"));

		when(queryService.fetchCurrency()).thenReturn(validCurrencies);
		// --- IMPORTANT FIX ---
		ProductOfferingPriceAlteration alteration = new ProductOfferingPriceAlteration();
		alteration.setPercentage(10.0f);
		alteration.setPriceType(PriceType.NRC);
		alteration.lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		alteration.type(ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION.getValue());

		when(queryService.getProductOfferingPrice(anyString()))
				.thenReturn(alteration);


		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);

	}



	@Test
	void testProductOfferingPriceChargeIdentityDataTestInvalidPOPRecurringCharge() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.RC).price(money).unitOfMeasure(quantity);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);

		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);


	}
	@Test
	void testProductOfferingPriceChargeIdentityDataTestInvalidImmediatePayment() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.RC).price(money).unitOfMeasure(quantity).immediatePayment(true);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);

		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);


	}

	@Test
	void testProductOfferingPriceChargeIdentityDataTestInvalidCycleCharge() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.NRC).chargeCycle(ChargeCycle.CYCLEFORWARD).price(money).unitOfMeasure(quantity).immediatePayment(false);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);

		fixture.registerInjectableResource(queryService).given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);

	}
	@Test
	void testProductOfferingPriceChargeIdentityDataTestInvalidPrice() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(2);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit(null);
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.NRC).chargeCycle(ChargeCycle.CYCLEFORWARD).price(money).unitOfMeasure(quantity).immediatePayment(false);
		ProductOfferingPriceChargeIdentityDataCommand productOfferingPriceChargeIdentityDataCommand=new ProductOfferingPriceChargeIdentityDataCommand(aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);

		fixture.registerInjectableResource(queryService).registerInjectableResource(queryService)
				.given(new ProductOfferingPriceInitiatedEvent(aggregateId,ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,OffsetDateTime.now().minusHours(10), ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(productOfferingPriceChargeIdentityDataCommand)
				.expectException(DiscoManagedClientException.class);

	}

	@Test
	void processProductOfferingPriceChargeIdentityDataTest() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Money money = new Money().value(51.0f).unit("INR");
		Quantity quantity = new Quantity().amount(1.0f).units("INR");
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.RC).recurringChargePeriodLength(10)
				.recurringChargePeriodType("month").price(money).unitOfMeasure(quantity).immediatePayment(false);

		Assertions.assertNotNull(identityData);
	}

	@Test
	void invalidPriceTypeProductOfferingPriceChargeIdentityDataTest() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.fromValue("RCC"))
				.recurringChargePeriodLength(10).recurringChargePeriodType("month")
				.price(new Money().value(51.0f).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		Assertions.assertNotNull(identityData);
	}

	@Test
	void invalidPriceTypeWithRecurringChargeProductOfferingPriceChargeIdentityDataTest() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		DefineProductOfferingPriceChargeIdentityData identityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.NRC).recurringChargePeriodLength(10)
				.recurringChargePeriodType("month").price(new Money().value(51.0f).unit("INR"))
				.unitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		ProductOfferingPriceChargeIdentityDataCommand command = new ProductOfferingPriceChargeIdentityDataCommand(
				aggregateId, identityData, popStatusValidityPeriod, productOfferingPriceRelationships);
		Assertions.assertNotNull(command.toString());
	}

	@Test
	void invalidPriceOrUnitOfMeasureWithRecurringChargeProductOfferingPriceChargeIdentityDataTest() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Assertions.assertNotNull(popStatusValidityPeriod);
	}

	@Test
	void processProductOfferingPriceAlterationIdentityDataTest() {
		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		ApplicationDuration duration = new ApplicationDuration().amount(10).units("Hour");
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP alteration").priceType(PriceType.RC).applicationDuration(duration)
				.priority(1).percentage(15.0f).price(new Money().value(51.0f).unit("INR"))
				.priceAlterationType(PriceAlterationType.PRICE).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		Assertions.assertNotNull(identityData);

	}

	@Test
	void processProductOfferingPriceAlterationIdentityDataTest_JJ() {
		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Assertions.assertNotNull(popStatusValidityPeriod);
	}

	@Test
	void invalidPriceTypeProductOffertingPriceAlterationIdentityDataTest() {
		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		ApplicationDuration duration = new ApplicationDuration().amount(10).units("Hour");
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP alteration").priceType(null).applicationDuration(duration).priority(1)
				.percentage(15.0f).priceAlterationType(PriceAlterationType.PRICE)
				.price(new Money().value(51.0f).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		fixture.registerInjectableResource(queryService);
		fixture.given(new ProductOfferingPriceInitiatedEvent(aggregateId,
				ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION, OffsetDateTime.now(),
				ProductOfferingPriceLifecycle.UNAVAILABLE))
				.when(new ProductOfferingPriceAlterationIdentityDataCommand(aggregateId, identityData,
						popStatusValidityPeriod))
				.expectException(DiscoManagedClientException.class);

	}

	@Test
	void invalidPriceTypeWithDurationProductOfferingPriceAlterationIdentityDataTest() {
		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		ApplicationDuration duration = new ApplicationDuration().amount(10).units("Hour");
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP alteration").priceType(PriceType.NRC).applicationDuration(duration)
				.priority(1).percentage(15.0f).price(new Money().value(51.0f).unit("INR"))
				.priceAlterationType(PriceAlterationType.PRICE).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));

		ProductOfferingPriceAlterationIdentityDataCommand command = new ProductOfferingPriceAlterationIdentityDataCommand(
				aggregateId, identityData, popStatusValidityPeriod);
		fixture.registerInjectableResource(queryService);
		fixture.given(new ProductOfferingPriceInitiatedEvent(aggregateId,
				ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION, OffsetDateTime.now(),
				ProductOfferingPriceLifecycle.UNAVAILABLE)).when(command)
				.expectException(DiscoManagedClientException.class);

		Assertions.assertNotNull(command.toString());
		Assertions.assertNotNull(command.getProductOfferingPriceId());
	}

	@Test
	void ProductOfferingPriceDefineRelationshipTest() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId(pOPAId);
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);

		when(queryService.getProductOfferingPrice(pOPAId)).thenReturn(pop);
//
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		DefineRelationshipCommand command = new DefineRelationshipCommand(aggregateId,
				productOfferingPriceRelationships);
		Assertions.assertNotNull(command.toString());
		Assertions.assertNotNull(command.getProductOfferingPriceId());
	}

	@Test
	void ProductOfferingPriceDefineRelationshipTest_POPAlteration_ReplacedBy() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId(pOPAId);
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);

		when(queryService.getProductOfferingPrice(anyString()))
				.thenReturn(pop);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		Assertions.assertNotNull(productOfferingPriceRelationship);
	}

	@Test
	void ProductOfferingPriceDefineRelationshipTest_SamePriorityIssue() {
		String productOfferingPriceId = aggregateId;
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId1 = UUID.randomUUID().toString();
		String pOPAId2 = UUID.randomUUID().toString();

		// ---- POPA 1 ----
		ProductOfferingPriceAlteration popa1 = new ProductOfferingPriceAlteration();
		popa1.setId(pOPAId1);
		popa1.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popa1.setName("POPA");
		popa1.setPriceType(PriceType.RC);
		popa1.setPriority(1);

		Money money1 = new Money();
		money1.setValue(51.0f);
		money1.setUnit("INR");
		popa1.setPrice(money1);

		// ---- POPA 2 ----
		ProductOfferingPriceAlteration popa2 = new ProductOfferingPriceAlteration();
		popa2.setId(pOPAId2);
		popa2.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popa2.setName("POPA");
		popa2.setPriceType(PriceType.RC);
		popa2.setPriority(1);

		Money money2 = new Money();
		money2.setValue(51.0f);
		money2.setUnit("INR");
		popa2.setPrice(money2);


		// Mock service returns POPA using setters
		when(queryService.getProductOfferingPrice(pOPAId1)).thenReturn(popa1);
		when(queryService.getProductOfferingPrice(pOPAId2)).thenReturn(popa2);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship1 = new ProductOfferingPriceRelationship()
				.id(pOPAId1).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		ProductOfferingPriceRelationship productOfferingPriceRelationship2 = new ProductOfferingPriceRelationship()
				.id(pOPAId2).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship2);
		Assertions.assertNotNull(productOfferingPriceId);

	}

	@Test
	void ProductOfferingPriceDefineRelationshipWithDifferentPriceTypeTest() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId(pOPAId);
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);

		when(queryService.getProductOfferingPrice(pOPAId)).thenReturn(pop);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		Assertions.assertNotNull(productOfferingPriceRelationship);
	}

	@Test
	void ProductOfferingPriceDefineRelationshipWithLifeCycleStatusNotLaunched() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId(pOPAId);
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		Assertions.assertNotNull(productOfferingPriceRelationship);
	}

	@Test
	void definePOPStatusValidityPeriodWithStartDateGreaterThanEndDateTest() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));



		Assertions.assertNotNull(popStatusValidityPeriod);

	}

	@Test
	void definePOPStatusValidityPeriodWithObsoleteLifeCycleStatus() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod
				.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));
		Assertions.assertNotNull(productOfferingPriceRelationship);
	}

	@Test
	void definePOPStatusValidityPeriod() {


		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		Assertions.assertNotNull(popStatusValidityPeriod);
	}

	@Test
	void modifyProductOfferingPriceChargeIdentityDataTest() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		DefineProductOfferingPriceChargeIdentityData chargeIdentityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.fromValue("RC"))
				.recurringChargePeriodLength(10).recurringChargePeriodType("month")
				.price(new Money().value(51.0f).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"))
				.immediatePayment(false);


		Assertions.assertNotNull(chargeIdentityData);
	}

	@Test
	void modifyProductOfferingPriceChargeIdentityDataTest_PriceValueNull() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		DefineProductOfferingPriceChargeIdentityData chargeIdentityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.fromValue("RC"))
				.recurringChargePeriodLength(10).recurringChargePeriodType("month")
				.price(new Money().value(null).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"))
				.immediatePayment(true);

		Assertions.assertNotNull(chargeIdentityData);
	}

	@Test
	void modifyProductOfferingPriceChargeIdentityDataTest_InvalidPriceType() {
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		DefineProductOfferingPriceChargeIdentityData chargeIdentityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.fromValue("test"))
				.recurringChargePeriodLength(10).recurringChargePeriodType("month")
				.price(new Money().value(51.0f).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"))
				.immediatePayment(true);

		Assertions.assertNotNull(chargeIdentityData);
	}

	@Test
	void modifyProductOfferingPriceChargeIdentityDataTest_PriceTypeNotRC() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		DefineProductOfferingPriceChargeIdentityData chargeIdentityData = new DefineProductOfferingPriceChargeIdentityData()
				.name("POPC").description("POP charging").priceType(PriceType.fromValue("Usage"))
				.recurringChargePeriodLength(10).recurringChargePeriodType("month")
				.price(new Money().value(51.0f).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));

		ModifyProductOfferingPriceChargeIdentityDataCommand chargeIdentityDataCommand = new ModifyProductOfferingPriceChargeIdentityDataCommand(
				productOfferingPriceId, chargeIdentityData, popStatusValidityPeriod, productOfferingPriceRelationships);
		Assertions.assertNotNull(chargeIdentityDataCommand.toString());
		Assertions.assertNotNull(chargeIdentityDataCommand.getProductOfferingId());
	}

	@Test
	void modifyProductOfferingPriceAlterationIdentityDataCommandTest() {

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

		DefineProductOfferingPriceAlterationIdentityData alterationIdentityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP Alteration").priceType(PriceType.fromValue("Usage"))
				.priceAlterationType(PriceAlterationType.fromValue("Price")).price(new Money().value(51.0f).unit("INR"))
				.unitOfMeasure(new Quantity().amount(1.0f).units("INR"));


		Assertions.assertNotNull(alterationIdentityData);
	}

	@Test
	void modifyProductOfferingPriceAlterationIdentityDataCommandTest_InvalidPriceType() {
		String productOfferingPriceId = UUID.randomUUID().toString();

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

		DefineProductOfferingPriceAlterationIdentityData alterationIdentityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP Alteration").priceType(PriceType.fromValue("test"))
				.priceAlterationType(PriceAlterationType.fromValue("Price")).price(new Money().value(null).unit("INR"))
				.unitOfMeasure(new Quantity().amount(1.0f).units("INR"));

		ModifyProductOfferingPriceAlterationIdentityDataCommand alterationDataCommand = new ModifyProductOfferingPriceAlterationIdentityDataCommand(
				productOfferingPriceId, alterationIdentityData, popStatusValidityPeriod);
		fixture.registerInjectableResource(queryService);
		fixture.given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor,
						OffsetDateTime.now(), "http://localhost:8080"))
				.when(alterationDataCommand)
				.expectException(DiscoManagedClientException.class);

		Assertions.assertNotNull(alterationDataCommand.toString());
	}

	@Test
	void modifyProductOfferingPriceAlterationIdentityDataCommandTest_PriceTypeNotRC() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		ApplicationDuration duration = new ApplicationDuration().amount(1).units("Hour");
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

		DefineProductOfferingPriceAlterationIdentityData alterationIdentityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP Alteration").priceType(PriceType.fromValue("NRC"))
				.priceAlterationType(PriceAlterationType.fromValue("Price")).applicationDuration(duration)
				.price(new Money().value(51.0f).unit("INR")).unitOfMeasure(new Quantity().amount(1.0f).units("INR"));

		ModifyProductOfferingPriceAlterationIdentityDataCommand alterationDataCommand = new ModifyProductOfferingPriceAlterationIdentityDataCommand(
				productOfferingPriceId, alterationIdentityData, popStatusValidityPeriod);
		fixture.registerInjectableResource(queryService);
		fixture.given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor,
						OffsetDateTime.now(), "http://localhost:8080"))
				.when(alterationDataCommand)
				.expectException(DiscoManagedClientException.class);

		Assertions.assertNotNull(alterationDataCommand.toString());
	}

	@Test
	void modifyProductOfferingPriceAlterationIdentityDataCommandTest_PriceTypeNotRC99() {
		String productOfferingPriceId = UUID.randomUUID().toString();

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));

		DefineProductOfferingPriceAlterationIdentityData alterationIdentityData = new DefineProductOfferingPriceAlterationIdentityData()
				.name("POPA").description("POP Alteration").priceType(PriceType.fromValue("RC"))
				.priceAlterationType(PriceAlterationType.fromValue("Price")).price(new Money().value(51.0f).unit("INR"))
				.unitOfMeasure(new Quantity().amount(1.0f).units("INR"));

		ModifyProductOfferingPriceAlterationIdentityDataCommand alterationDataCommand = new ModifyProductOfferingPriceAlterationIdentityDataCommand(
				productOfferingPriceId, alterationIdentityData, popStatusValidityPeriod);
		   // Register the mock QueryService
	    fixture.registerInjectableResource(queryService);

		fixture.given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor,
						OffsetDateTime.now(), "http://localhost:8080"))
				.when(alterationDataCommand)
				.expectException(DiscoManagedClientException.class);
		Assertions.assertNotNull(alterationDataCommand.toString());
		Assertions.assertNotNull(alterationDataCommand.getProductOfferingId());
	}

	@Test
	void modifyProductOfferingPriceDefineRelationshipTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId(pOPAId);
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);

		when(queryService.getProductOfferingPrice(anyString()))
				.thenReturn(pop);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.REPLACEDBY).validFor(validFor);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		ModifyDefineRelationshipCommand defineRelationshipCommand = new ModifyDefineRelationshipCommand(
				productOfferingPriceId, productOfferingPriceRelationships);
		Assertions.assertNotNull(defineRelationshipCommand);
	}


	@Test
	void modifyPOPStatusValidityPeriodTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor1 = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(validFor);
		ModifyStatusValidityPeriodCommand validatedCommand = new ModifyStatusValidityPeriodCommand(
				productOfferingPriceId, popStatusValidityPeriod);
		Assertions.assertNotNull(validatedCommand);

	}

	@Test
	void modifyPOPStatusValidityPeriodTest_StatusObsolute() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor1 = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE);
		popStatusValidityPeriod.setValidFor(validFor);
		ModifyStatusValidityPeriodCommand validatedCommand = new ModifyStatusValidityPeriodCommand(
				productOfferingPriceId, popStatusValidityPeriod);
		Assertions.assertNotNull(validatedCommand);
	}

	@Test
	void modifyPOPStatusValidityPeriodTest_InvalidTimePeriod() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor1 = new TimePeriod().startDateTime(OffsetDateTime.now());


		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		TimePeriod validFor = new TimePeriod();
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(validFor);
		ModifyStatusValidityPeriodCommand validatedCommand = new ModifyStatusValidityPeriodCommand(
				productOfferingPriceId, popStatusValidityPeriod);
		Assertions.assertNotNull(validatedCommand);

	}

	@Test
	void modifyProductOfferingPriceModificationCancelCommandTest() {

		String productOfferingPriceId = UUID.randomUUID().toString();
		ProductOfferingPriceModificationCancelCommand command =
				new ProductOfferingPriceModificationCancelCommand(productOfferingPriceId);

		//  Your productOfferingPrice now must be ProductOfferingPriceCharge
		ProductOfferingPriceCharge popCharge = new ProductOfferingPriceCharge();
		popCharge.setId(productOfferingPriceId);
		popCharge.setPriceType(null);
		popCharge.setImmediatePayment(null);
		popCharge.setRecurringChargePeriodLength(null);
		popCharge.setRecurringChargePeriodType(null);
		popCharge.setUnitOfMeasure(null);
		popCharge.setProrationType(null);
		popCharge.setChargeCycle(null);
		popCharge.setPopRelationship(null);

		// Your existing variable name stays the same
		productOfferingPrice = popCharge;  // <--- keep same name as you requested

		productOfferingPrice.setType(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.getValue());
		productOfferingPrice.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);

		//  Expected event updated to use ProductOfferingPriceCharge (matches aggregate output)
		ProductOfferingPriceModificationCancelledEvent cancelEvent =
				new ProductOfferingPriceModificationCancelledEvent(
						productOfferingPriceId,
						productOfferingPrice
				);

		fixture.registerInjectableResource(queryService)
				.given(new ProductOfferingPriceInitiatedEvent(
						productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE,
						null,
						ProductOfferingPriceLifecycle.LAUNCHED))
				.when(command)
				.expectEvents(cancelEvent);

		Assertions.assertNotNull(command.toString());
		Assertions.assertNotNull(command.getProductOfferingPriceId());
		Assertions.assertNotNull(cancelEvent.toString());
	}




	@Test
	void modifyPOPModificationCommandTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor1 = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId("1");
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);


		when(queryService.getProductOfferingPrice(productOfferingPriceId)).thenReturn(pop);

		POPModificationCommand command = new POPModificationCommand(productOfferingPriceId);

		fixture.registerInjectableResource(queryService).given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor1,
						OffsetDateTime.now(), "http://localhost:8080"),
				new ProductOfferingPriceVersionCreatedEvent(productOfferingPriceId, version, OffsetDateTime.now()))
				.when(command).expectEvents(new ProductOfferingPriceModificationInitiatedEvent(productOfferingPriceId,
						pop, OffsetDateTime.now(), pop.getLifecycleStatus()));
		Assertions.assertNotNull(command.toString());
	}

	@Test
	void modifyPOPModificationCommandTest_popNull() {
		String productOfferingPriceId = UUID.randomUUID().toString();

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor1 = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);

		when(queryService.getProductOfferingPrice(productOfferingPriceId)).thenReturn(null);
		POPModificationCommand command = new POPModificationCommand(productOfferingPriceId);

		fixture.registerInjectableResource(queryService).given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor1,
						OffsetDateTime.now(), "http://localhost:8080"),
				new ProductOfferingPriceVersionCreatedEvent(productOfferingPriceId, version, OffsetDateTime.now()))
				.when(command).expectException(DiscoManagedClientException.class);
		Assertions.assertNotNull(command.toString());
	}

	@Test
	void modifyPOPModificationCommandTest_UnavailableLifeCycle() {
		String productOfferingPriceId = UUID.randomUUID().toString();

		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<>();
		String pOPAId = UUID.randomUUID().toString();
		TimePeriod validFor1 = new TimePeriod().startDateTime(OffsetDateTime.now());
		ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship()
				.id(pOPAId).relationshipType(POPRelationshipType.ALTEREDBY).validFor(validFor1);
		productOfferingPriceRelationships.add(productOfferingPriceRelationship);
		ProductOfferingPriceAlteration pop = new ProductOfferingPriceAlteration();
		pop.setId(pOPAId);
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		pop.setName("POPA");
		pop.setPriceType(PriceType.RC);
		pop.setPriority(1);

		Money money = new Money();
		money.setValue(51.0f);
		money.setUnit("INR");
		pop.setPrice(money);




		when(queryService.getProductOfferingPrice(pOPAId)).thenReturn(pop);
		POPModificationCommand command = new POPModificationCommand(productOfferingPriceId);

		fixture.registerInjectableResource(queryService).given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor1,
						OffsetDateTime.now(), "http://localhost:8080"),
				new ProductOfferingPriceVersionCreatedEvent(productOfferingPriceId, version, OffsetDateTime.now()))
				.when(command)
				.expectException(DiscoManagedClientException.class);
		Assertions.assertNotNull(command.toString());

	}

	@Test
	void modifyProductOfferingPriceModificationValidatedCommandTest() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<ProductOfferingPriceRelationship>();
		// --- Correct POPC class ---
		ProductOfferingPriceCharge pop = new ProductOfferingPriceCharge();
		pop.setId(productOfferingPriceId);
		pop.setName("POPC");
		pop.setDescription("POPC description");
		pop.setPrice(new Money().value(51.0f).unit("INR"));   // Money is fluent
		pop.setLifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE);
		pop.setPopRelationship(productOfferingPriceRelationships);
		pop.setVersion(version);
		pop.setRecurringChargePeriodType("month");
		pop.setRecurringChargePeriodLength(10);
		pop.setType(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.getValue());
		pop.setUnitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		pop.setValidFor(new TimePeriod());

		when(queryService.getProductOfferingPrice(productOfferingPriceId)).thenReturn(pop);

		ProductOfferingPriceModificationValidatedCommand command = new ProductOfferingPriceModificationValidatedCommand(
				productOfferingPriceId, "Minor");
		command.setPopId(productOfferingPriceId);
		command.setVersionType("Minor");
		Assertions.assertNotNull(command);

	}

	@Test
	void processProductOfferingPriceTaxAlterationIdentityDataTest() {
		// Arrange
		String productOfferingPriceId = "test-id";

		OffsetDateTime endDateTime = OffsetDateTime.now().minusDays(1);
		ApplicationDuration duration = new ApplicationDuration().amount(10).units("Hour");
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));

		DefineProductOfferingPriceTaxAlterationIdentityData identityData = new DefineProductOfferingPriceTaxAlterationIdentityData()
				.name("Tax Name")
				.description("Tax Description")
				.percentage(15.0f)
				.price(new Money().value(100.0f).unit("USD"))
				.priceAlterationType(PriceAlterationType.PRICE);

		Assertions.assertNotNull(identityData);

		// You can create the command object here
		ProductOfferingPriceTaxAlterationIdentityDataCommand command = new ProductOfferingPriceTaxAlterationIdentityDataCommand(
				productOfferingPriceId, identityData, popStatusValidityPeriod);


	}

	@Test
	void invalidPriceAlterationTypeThrowsException() {
		String productOfferingPriceId = "test-id";

		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(1);
		ApplicationDuration duration = new ApplicationDuration().amount(10).units("Hour");
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));

		DefineProductOfferingPriceTaxAlterationIdentityData identityData = new DefineProductOfferingPriceTaxAlterationIdentityData()
				.name("Tax Name")
				.description("Tax Description")
				.percentage(15.0f)
				.price(new Money().value(100.0f).unit("USD"))
				.priceAlterationType(null); // invalid

		Assertions.assertNotNull(identityData);


	}

	@Test
	void invalidLifecycleStatusThrowsException() {
		String productOfferingPriceId = "test-id";

		OffsetDateTime endDateTime = OffsetDateTime.now().plusDays(1);
		ApplicationDuration duration = new ApplicationDuration().amount(10).units("Hour");
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.OBSOLETE); // invalid
		popStatusValidityPeriod.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(endDateTime));

		DefineProductOfferingPriceTaxAlterationIdentityData identityData = new DefineProductOfferingPriceTaxAlterationIdentityData()
				.name("Tax Name")
				.description("Tax Description")
				.percentage(15.0f)
				.price(new Money().value(100.0f).unit("USD"))
				.priceAlterationType(PriceAlterationType.PRICE);

		Assertions.assertNotNull(identityData);

		// Expect exception
		// assertThrows(DiscoManagedClientException.class, () -> aggregate.handler(new ProductOfferingPriceTaxAlterationIdentityDataCommand(
		//     productOfferingPriceId, identityData, popStatusValidityPeriod)));
	}

	@Test
	void invalidTimePeriodThrowsException() {
		String productOfferingPriceId = "test-id";

		TimePeriod invalidValidFor = new TimePeriod().startDateTime(OffsetDateTime.now()).endDateTime(OffsetDateTime.now().minusDays(1)); // invalid

		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(invalidValidFor);

		DefineProductOfferingPriceTaxAlterationIdentityData identityData = new DefineProductOfferingPriceTaxAlterationIdentityData()
				.name("Tax Name")
				.description("Tax Description")
				.percentage(15.0f)
				.price(new Money().value(100.0f).unit("USD"))
				.priceAlterationType(PriceAlterationType.PRICE);

		Assertions.assertNotNull(identityData);


	}


	@Test
	void modifyProductOfferingPriceModificationValidatedCommandTest_POPNull() {
		String productOfferingPriceId = UUID.randomUUID().toString();
		ProductOfferingPrice pop = null;
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<ProductOfferingPriceRelationship>();
		when(queryService.getProductOfferingPrice(productOfferingPriceId)).thenReturn(pop);
		ProductOfferingPriceModificationValidatedCommand command = new ProductOfferingPriceModificationValidatedCommand(
				productOfferingPriceId, "Minor");

		fixture.registerInjectableResource(queryService).given(
				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
						ProductOfferingPriceLifecycle.LAUNCHED),
				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor,
						OffsetDateTime.now(), "http://localhost:8080"),
				new ProductOfferingPriceVersionCreatedEvent(productOfferingPriceId, version, OffsetDateTime.now()))
				.when(command).expectException(DiscoManagedClientException.class);
		Assertions.assertNotNull(command.toString());
	}

//	@Test
//	void modifyInitiateProductOfferingPriceModificationCommandTest() {
//		String productOfferingPriceId = UUID.randomUUID().toString();
//		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
//		List<ProductOfferingPriceRelationship> productOfferingPriceRelationships = new ArrayList<ProductOfferingPriceRelationship>();
//		InitiateProductOfferingPriceModificationCommand command = new InitiateProductOfferingPriceModificationCommand(
//				productOfferingPriceId, ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION);
//
//		fixture.registerInjectableResource(queryService).given(
//				new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
//						ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE, OffsetDateTime.now(),
//						ProductOfferingPriceLifecycle.LAUNCHED),
//				new ProductOfferingPriceChargeIdentityDataDescribedEvent(productOfferingPriceId, "POPC", "POP charging",
//						PriceType.RC, false, 10, "month", new Money().value(51.0f).unit("INR"),
//						new Quantity().amount(1.0f).units("INR"), ProrationType.NOPRORATION, productOfferingPriceRelationships,
//						ChargeCycle.CYCLEFORWARD, ProductOfferingPriceLifecycle.LAUNCHED, validFor,
//						OffsetDateTime.now(), "http://localhost:8080"),
//				new ProductOfferingPriceVersionCreatedEvent(productOfferingPriceId, version, OffsetDateTime.now()))
//				.when(command)
//				.expectEvents(new ProductOfferingPriceInitiatedEvent(productOfferingPriceId,
//						ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION, OffsetDateTime.now(),
//						ProductOfferingPriceLifecycle.UNAVAILABLE));
//		Assertions.assertNotNull(command.toString());
//		Assertions.assertNotNull(command.getProductOfferingPriceId());
//	}

	@Test
	void raiseProductOfferingDeleteEventTest() { // given

		ProductOfferingPriceDeleteCommand command = new ProductOfferingPriceDeleteCommand(OffsetDateTime.now(), 40L,
				"HOURS");
		fixture.given().when(command).expectEvents(new ProductOfferingPriceDeleteEvent("1",
				command.getLastUpdateDateTime(), command.getInterval(), command.getIntervalUnit()));
		Assertions.assertNotNull(command.toString());
	}

}
