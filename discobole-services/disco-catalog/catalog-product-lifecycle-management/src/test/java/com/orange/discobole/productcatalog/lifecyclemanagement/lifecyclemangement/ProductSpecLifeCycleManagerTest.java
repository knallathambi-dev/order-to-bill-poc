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
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManagerImpl;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.AdminQueryService;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.CommercialProductInstalledBaseQueryService;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

class ProductSpecLifeCycleManagerTest extends ManageLifeCycleApplicationTests {

	private String productSpecId = "";

	@InjectMocks
	private LifeCycleManagerImpl lifeCycleManager;
	@Mock
	private QueryService queryService;
	@Mock
	private CommercialProductInstalledBaseQueryService cpibQueryService;
	@Mock
	private AdminQueryService adminQueryService;
	String accessToken = "Bearer 123";

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(lifeCycleManager,"queryService",queryService);
		ReflectionTestUtils.setField(lifeCycleManager,"cpibQueryService",cpibQueryService);
		ReflectionTestUtils.setField(lifeCycleManager,"adminQueryService",adminQueryService);
	}
	
	@Test
	 void raiseEventsWhenProcessProductSpecStateChangeFromActiveToActive() {
		List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
		when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
		when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(
				new ProductSpecification().id(productSpecId).version("0.1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
		 List<Event> events =
				 lifeCycleManager.changeState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,"active", accessToken);
		assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);
	}
	
 @Test
	 void testChangeProductSpecificationStateFromActiveToLaunched() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			version("0.1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
	when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
	List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
			ProductSpecificationLifecycle.LAUNCHED.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
 }
 @Test
 void testChangeProductSpecificationStateFromActiveToRetiredWithLinkedProductOfferingStatusAsObsolete() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").version("0.1").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		version("0.1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.RETIRED.getValue(), accessToken);
assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);

}
 @Test
 void testChangeProductSpecificationStateFromIntestToActive() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.INTEST));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.ACTIVE.getValue(), accessToken);
assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
assertEquals(ProductSpecificationLifecycle.ACTIVE.getValue(),event.getCurrentState());
}
 @Test
 void testChangeProductSpecificationStateFromLaunchedToUnavailable() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.UNAVAILABLE.getValue(), accessToken);
assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
assertEquals(ProductSpecificationLifecycle.UNAVAILABLE.getValue(),event.getCurrentState());
}
 @Test
 void testChangeProductSpecificationStateFromLaunchedToRetired() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.RETIRED.getValue(), accessToken);
assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
assertEquals(ProductSpecificationLifecycle.RETIRED.getValue(),event.getCurrentState());
}
 @Test
 void testChangeProductSpecificationStateFromLaunchedToRetiredWithPOSAsLaunched() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.RETIRED.getValue(), accessToken);
assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);

}
 @Test
 void testChangeProductSpecificationStateFromUnavailableToRetiredWithPOSAsLaunched() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.RETIRED.getValue(), accessToken);
assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);

}
 @Test
 void testChangeProductSpecificationStateFromUnavailableToRetired() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.RETIRED.getValue(), accessToken);
		assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
		LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
		assertEquals(ProductSpecificationLifecycle.RETIRED.getValue(),event.getCurrentState());

}
 @Test
 void testChangeProductSpecificationStateFromRetiredToObsolete() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.OBSOLETE));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.RETIRED));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.OBSOLETE.getValue(), accessToken);
		assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
		LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
		assertEquals(ProductSpecificationLifecycle.OBSOLETE.getValue(),event.getCurrentState());

}
 @Test
 void testChangeProductSpecificationStateFromRetiredToObsoleteWithPOSLaunched() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.RETIRED));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.OBSOLETE.getValue(), accessToken);
assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);

}
 @Test
 void testChangeProductSpecificationStateFromRejectedToObsoleteWithPOSLaunched() {
 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
 productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
		lifecycleStatus(ProductSpecificationLifecycle.REJECTED));
when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
List<Event> events =lifeCycleManager.changeProductSpecState("aggregateId",productSpecId,EntityType.PRODUCTSPECIFICATION,
		ProductSpecificationLifecycle.OBSOLETE.getValue(), accessToken);
assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);

}
 @Test
 void testnextPossibleStatesForProductSpecification() {
	List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
	when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(
			new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE));
	 Set<String> states =
			 lifeCycleManager.getNextPossibleStates(productSpecId,EntityType.PRODUCTSPECIFICATION,accessToken);
	assertEquals(2,states.size());
}

}
