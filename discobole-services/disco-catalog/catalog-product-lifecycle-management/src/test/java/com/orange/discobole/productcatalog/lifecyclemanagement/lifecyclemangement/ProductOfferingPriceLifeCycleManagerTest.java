// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemangement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleEntitySelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManagerImpl;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

 class ProductOfferingPriceLifeCycleManagerTest extends ManageLifeCycleApplicationTests {

	private String productOffId = "";
	private String productOffPriceId="";

	@InjectMocks
	private LifeCycleManagerImpl lifeCycleManager;
	@Mock
	private QueryService queryService;

	String accessToken = "Bearer 123";
	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(lifeCycleManager,"queryService",queryService);
	   }
	@Test
	 void testChangePOPState() {
		List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
		List<ProductOfferingPrice> productOfferingPrices=new ArrayList<>();
		when(queryService.getProductOfferingPrice(productOffPriceId, accessToken)).thenReturn(new ProductOfferingPrice().id(productOffPriceId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		when(queryService.fetchProductOfferingsByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferings);
		when(queryService.getProductOfferingPricesByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferingPrices);
		 List<Event> events =
				 lifeCycleManager.changeState("aggregateId",productOffPriceId,EntityType.PRODUCTOFFERINGPRICE,ProductOfferingPriceLifecycle.UNAVAILABLE.getValue(), accessToken);
		assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
		LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
		assertEquals(ProductSpecificationLifecycle.UNAVAILABLE.getValue(),event.getCurrentState());
	}
	@Test
	 void testChangePOPStateWithInvaidNextState() {
		List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
		List<ProductOfferingPrice> productOfferingPrices=new ArrayList<>();
		productOfferingPrices.add(new ProductOfferingPrice().id(productOffId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		when(queryService.getProductOfferingPrice(productOffPriceId, accessToken)).thenReturn(new ProductOfferingPrice().id(productOffPriceId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		when(queryService.fetchProductOfferingsByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferings);
		when(queryService.getProductOfferingPricesByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferingPrices);
		 List<Event> events =
				 lifeCycleManager.changeState("aggregateId",productOffPriceId,EntityType.PRODUCTOFFERINGPRICE,
						 ProductOfferingPriceLifecycle.RETIRED.getValue(), accessToken);
		assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);
	}
	
	@Test
	 void testChangePOPStateWithNullPOP() {
		List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
		List<ProductOfferingPrice> productOfferingPrices=new ArrayList<>();
		productOfferingPrices.add(new ProductOfferingPrice().id(productOffId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		when(queryService.getProductOfferingPrice(productOffPriceId, accessToken)).thenReturn(null);
		when(queryService.fetchProductOfferingsByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferings);
		when(queryService.getProductOfferingPricesByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferingPrices);
		 List<Event> events =
				 lifeCycleManager.changeState("aggregateId",productOffPriceId,EntityType.PRODUCTOFFERINGPRICE,
						 ProductOfferingPriceLifecycle.RETIRED.getValue(), accessToken);
		assertEquals(events.get(0).getClass(),InvalidLifeCycleEntitySelectedEvent.class);
	}
	@Test
	 void testNextPossibleStates() {
		List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
		List<ProductOfferingPrice> productOfferingPrices=new ArrayList<>();
		productOfferingPrices.add(new ProductOfferingPrice().id(productOffId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		when(queryService.getProductOfferingPrice(productOffPriceId, accessToken)).thenReturn(new ProductOfferingPrice().id(productOffPriceId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		when(queryService.fetchProductOfferingsByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferings);
		when(queryService.getProductOfferingPricesByProductOfferingPriceId(productOffPriceId,accessToken)).thenReturn(productOfferingPrices);
		 Set<String> states =
				 lifeCycleManager.getNextPossibleStates(productOffPriceId,EntityType.PRODUCTOFFERINGPRICE,accessToken);
		assertEquals(1,states.size());
	}
}
