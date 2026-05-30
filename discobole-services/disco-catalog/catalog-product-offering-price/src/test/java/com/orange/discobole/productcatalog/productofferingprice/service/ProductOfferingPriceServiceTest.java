// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import com.orange.discobole.productcatalog.productofferingprice.service.impl.ProductOfferingPriceServiceImpl;

class ProductOfferingPriceServiceTest extends ProductOfferingPriceApplicationTests {


	CommandGateway commandGateway;
	@InjectMocks
	ProductOfferingPriceServiceImpl productOfferingPriceService;
	private static String productOfferingPriceId = "1";


	@BeforeEach
	void setup() {

		commandGateway = Mockito.mock(CommandGateway.class);
		productOfferingPriceService = new ProductOfferingPriceServiceImpl(commandGateway);
	}
	/*
	 * @Test void triggerDeleteProductOfferingPriceTest() { String
	 * productOfferingPriceId = UUID.randomUUID().toString(); List<Event> history =
	 * new ArrayList<>(); publisher.publish(productOfferingPriceId, history); String
	 * id = productOfferingPriceService.deleteProductOfferingPrice(40L,
	 * OffsetDateTime.now(), "HOURS"); List<Event> actualEvents =
	 * eventStore.fetch(id); assertEquals(1, actualEvents.size());
	 * assertTrue(actualEvents.get(0) instanceof ProductOfferingPriceDeleteEvent);
	 * assertEquals(id, ((ProductOfferingPriceDeleteEvent)
	 * actualEvents.get(0)).getAggregateId()); }
	 */

	@Test
	void testCreateProductOfferingPrice() {
		String aggregateId = productOfferingPriceService
				.createProductOfferingPrice(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE);
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void testDefineProductOfferingPriceChargeIdentityData() {
		productOfferingPriceService.defineProductOfferingPriceChargeIdentityData(productOfferingPriceId,
				new DefineProductOfferingPriceChargeIdentityData(), List.of(new ProductOfferingPriceRelationship()), new DefinePOPStatusValidityPeriod());
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void testDefineProductOfferingPriceAlterationIdentityData() {
		
		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData();
		identityData.setName("POPA");
		identityData.setDescription("POP alteration");
		identityData.setPriceType(PriceType.RC);
		identityData.setApplicationDuration(new ApplicationDuration().amount(10));
		identityData.setPriority(1);
		identityData.setPercentage(15.0f);
		identityData.setPrice(new Money().value(51.0f).unit("INR"));
		identityData.setPriceAlterationType(PriceAlterationType.PRICE);
		identityData.setUnitOfMeasure(new Quantity().amount(1.0f).units("INR"));
		
		productOfferingPriceService.defineProductOfferingPriceAlterationIdentityData(productOfferingPriceId,
				identityData, new DefinePOPStatusValidityPeriod());
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void testCancelProductOfferingPrice() {
		productOfferingPriceService.cancelProductOfferingPrice(productOfferingPriceId);
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void testdeleteProductOfferingPrice() {
		productOfferingPriceService.deleteProductOfferingPrice(40L, OffsetDateTime.now(), "HOURS");
		verify(commandGateway, times(1)).sendAndWait(Mockito.any());
	}
}
