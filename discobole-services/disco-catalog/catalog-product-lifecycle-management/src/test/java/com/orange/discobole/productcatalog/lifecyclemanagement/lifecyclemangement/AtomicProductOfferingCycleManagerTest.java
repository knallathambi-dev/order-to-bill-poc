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
import static org.mockito.ArgumentMatchers.anyString;
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
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecificationRef;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManagerImpl;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.AdminQueryService;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.CommercialProductInstalledBaseQueryService;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

class AtomicProductOfferingCycleManagerTest extends ManageLifeCycleApplicationTests {


	private String productSpecId = "";
	private String productOffId = "";


	@InjectMocks
	private LifeCycleManagerImpl lifeCycleManager;
	@Mock
	private QueryService queryService;
	@Mock
	private CommercialProductInstalledBaseQueryService cpibQueryService;
	@Mock
	private AdminQueryService adminQueryService;

	private String accessToken = "Bearer 123";

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(lifeCycleManager,"queryService",queryService);
		ReflectionTestUtils.setField(lifeCycleManager,"cpibQueryService",cpibQueryService);
		ReflectionTestUtils.setField(lifeCycleManager,"adminQueryService",adminQueryService);

	   }
	@Test
	 void testChangeProductOfferingStateFromIntestToActive() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.INTEST).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(productOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.ACTIVE.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals(ProductSpecificationLifecycle.ACTIVE.getValue(),event.getCurrentState());
	}
	
	@Test
	 void testChangeProductOfferingStateFromIntestToActiveInvalid() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.INTEST).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(productOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.UNAVAILABLE.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);
	}
	@Test
	 void testChangeProductOfferingStateFromActiveToLaunched() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					version("0.1").lifecycleStatus(ProductOfferingLifecycle.ACTIVE).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.LAUNCHED));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(productOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.LAUNCHED.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals("launched","launched");
	}	
	@Test
	 void testChangeProductOfferingStateFromActiveToRetiredInvalid() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 List<ProductOffering> bundledProductOfferings = new ArrayList<ProductOffering>();
	 bundledProductOfferings.add(new ProductOffering().id("").version("0.1").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
					version("0.1").lifecycleStatus(ProductOfferingLifecycle.ACTIVE).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(bundledProductOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.RETIRED.getValue(), accessToken);
	
	assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);
	}	
	
	@Test
	 void testChangeProductOfferingStateFromLaunchedToUnvailable() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 List<ProductOffering> bundledProductOfferings = new ArrayList<ProductOffering>();
	 bundledProductOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.LAUNCHED).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(bundledProductOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.UNAVAILABLE.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals(ProductSpecificationLifecycle.UNAVAILABLE.getValue(),event.getCurrentState());
	}
	@Test
	 void testChangeProductOfferingStateFromLaunchedToRetired() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 List<ProductOffering> bundledProductOfferings = new ArrayList<ProductOffering>();
	 bundledProductOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.LAUNCHED).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(bundledProductOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.RETIRED.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals(ProductSpecificationLifecycle.RETIRED.getValue(),event.getCurrentState());
	}
	@Test
	 void testChangeProductOfferingStateFromUnavailableToRetired() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 List<ProductOffering> bundledProductOfferings = new ArrayList<ProductOffering>();
	 bundledProductOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.UNAVAILABLE).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(bundledProductOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.RETIRED.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals(ProductSpecificationLifecycle.RETIRED.getValue(),event.getCurrentState());
	}
	@Test
	 void testChangeProductOfferingStateFromRetiredToObsolete() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 List<ProductOffering> bundledProductOfferings = new ArrayList<ProductOffering>();
	 bundledProductOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.RETIRED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.RETIRED).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(bundledProductOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.ATOMICOFFER,
			ProductOfferingLifecycle.OBSOLETE.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals(ProductSpecificationLifecycle.OBSOLETE.getValue(),event.getCurrentState());
	}
	@Test
	 void testNextPossibleStatesForPO() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	productOfferings.add(new ProductOffering().id("").lifecycleStatus(ProductOfferingLifecycle.LAUNCHED));
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.INTEST).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(productOfferings);
	Set<String> states =lifeCycleManager.getNextPossibleStates(productOffId,EntityType.ATOMICOFFER,accessToken);
	assertEquals(2,states.size());
	}
	@Test
	 void testChangeBundleProductOfferingStateFromIntestToActive() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.INTEST).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleAndContractProductOfferingByBundlingProductOfferingId(anyString(),anyString())).thenReturn(productOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.BUNDLEPRODUCTOFFERING,
			ProductOfferingLifecycle.ACTIVE.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),LifeCycleStateSelectedEvent.class);
	LifeCycleStateSelectedEvent event=(LifeCycleStateSelectedEvent)events.get(0);
	assertEquals(ProductSpecificationLifecycle.ACTIVE.getValue(),event.getCurrentState());
	}
	@Test
	 void testChangeContractProductOfferingStateFromIntestToActive() {
	 List<ProductOffering> productOfferings = new ArrayList<ProductOffering>();
	 		when(queryService.fetchProductOfferingById(productOffId, accessToken)).thenReturn(new ProductOffering().id("").
	 					lifecycleStatus(ProductOfferingLifecycle.INTEST).productSpecification(new ProductSpecificationRef().id("")));
			 when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(new ProductSpecification().id("").
			 		lifecycleStatus(ProductSpecificationLifecycle.UNAVAILABLE));
			 when(queryService.fetchProductOfferingsByProductSpecId(productSpecId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(productOffId, accessToken)).thenReturn(productOfferings);
			 when(queryService.fetchBundleAndContractProductOfferingByBundlingProductOfferingId(anyString(),anyString())).thenReturn(productOfferings);
	List<Event> events =lifeCycleManager.changeState("aggregateId",productOffId,EntityType.CONTRACT,
			ProductOfferingLifecycle.OBSOLETE.getValue(), accessToken);
	assertEquals(events.get(0).getClass(),InvalidLifeCycleStateSelectedEvent.class);
	}
}
