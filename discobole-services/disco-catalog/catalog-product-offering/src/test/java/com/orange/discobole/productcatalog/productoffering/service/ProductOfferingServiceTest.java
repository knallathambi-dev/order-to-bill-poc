// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service; 
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.orange.discobole.productcatalog.productoffering.eventstore.MongoEventStoreImpl;
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
import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.productoffering.aggregate.ProductOfferingAggregate;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOfferingOption;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValueUse;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationshipType;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.service.impl.ProductOfferingServiceImpl;
import com.orange.discobole.productcatalog.productoffering.util.ConverterUtil;

class ProductOfferingServiceTest {
	   @Mock
	    ObjectMapper objectMapper;
	    CommandGateway commandGateway;
	MongoEventStoreImpl mongoEventStoreImpl;



	    
	    @InjectMocks
	    ProductOfferingService productOfferingService;
	    
	    QueryService queryService = Mockito.mock(QueryService.class);
	    
		  ApplicationContext appCtx = Mockito.mock(ApplicationContext.class);
	    
	    @Mock
	    ProductOfferingAggregate productOfferingAggregate;
	    

		MongoEventStorageEngine engine =  Mockito.mock(MongoEventStorageEngine.class);
	    
	    
	    ProductOfferingServiceTest() {
	        commandGateway = Mockito.mock(CommandGateway.class);
			mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class); // <-- Added mock
			productOfferingService = new ProductOfferingServiceImpl(commandGateway, mongoEventStoreImpl);

		}
	    
	    

	    @BeforeEach
	     void setup() {
	         ReflectionTestUtils.setField(productOfferingService, "queryService", queryService);
	     }
	    
	

	

	@Test void triggerInitiateProductOfferingCommand() { 
		
	String productSpecId =
 "product_spec_id"; String productOffId = "product_off_id";
 productOfferingService.createProductOffering(productSpecId,productOffId);
 Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

  
	
	}



	@Test
	void triggerAtomicProductOfferingDescriptionCommand() {
		String productOfferingId = "productOfferingId1";
	
		productOfferingService.defineProductOffDesc(productOfferingId,new DefineIdentityData(), new HashSet<String>(), new HashSet<String>(),new HashSet<RelatedParty>()
		,new HashSet<ProductOfferingTerm>(),new TimePeriod(),ProductOfferingType.ATOMICPRODUCTOFFERING,"");
		 Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	
	}



	@Test
	void triggerDefineAtomicProductOfferingCategoryCommand() {
		
		productOfferingService.defineProductOfferingCategory("1", new ArrayList<>());
		 Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
		
	
		
	}




	@Test
	void triggerUpdateProductOfferingCharacteristics() {

		String productSpecId = "productSpecId17";
		String productOfferingId = "productOfferingId17";
		List<Event> history = new ArrayList<>();
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		OffsetDateTime dateTime = OffsetDateTime.now();
		TimePeriod validFor = new TimePeriod().startDateTime(dateTime).endDateTime(dateTime.plusDays(10));

		ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue = new ProductSpecificationCharacteristicValue()
				.validFor(validFor).valueFrom("1").valueTo("20");
		List<ProductSpecificationCharacteristicValue> productSpecificationCharacteristicValues = new ArrayList<>();
		productSpecificationCharacteristicValues.add(productSpecificationCharacteristicValue);

		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
				.productSpecCharacteristicValue(productSpecificationCharacteristicValues).id(productSpecId)
				.validFor(validFor);
		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = new ArrayList<>();
		productSpecificationCharacteristics.add(productSpecificationCharacteristic);
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
		pickAtomicProductOfferingCharacteristics.setMinCardinality(1);
		pickAtomicProductOfferingCharacteristics.setMaxCardinality(4);
		pickAtomicProductOfferingCharacteristics.setValidFor(timePeriod);
		List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristic = new ArrayList<>();
		pickAtomicProductOfferingCharacteristic.add(pickAtomicProductOfferingCharacteristics);

		ProductSpecificationCharacteristicValueUse productSpecificationCharacteristicValueUse = new ProductSpecificationCharacteristicValueUse();
		productSpecificationCharacteristicValueUse.minCardinality(0).maxCardinality(1)
				.validFor(TimePeriodMapper.toGenerated(timePeriod))
				.productSpecCharacteristicValue(ConverterUtil.convert(productCharValues));
		List<ProductSpecificationCharacteristicValueUse> productSpecCharacteristicValueUseList = new ArrayList<>();
		productSpecCharacteristicValueUseList.add(productSpecificationCharacteristicValueUse);

		productOfferingService.updateProductOfferingCharacteristics(productOfferingId,
				pickAtomicProductOfferingCharacteristic);
		 Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}


	@Test
	void triggerProductOfferingRelationshipCommand() {
		String productSpecId = "productSpecIdr";
		String productOfferingId = "productOfferingIdr";

		ProductOfferingRelationship productOfferingRelationship = new ProductOfferingRelationship();
		productOfferingRelationship.setId("rel_1");
		productOfferingRelationship.setRelationshipType(ProductOfferingRelationshipType.REQUIRES);
		List<ProductOfferingRelationship> productOfferingRelationships = new ArrayList<>();
		productOfferingRelationships.add(productOfferingRelationship);

	productOfferingService.defineProductOfferingEnitityRelationship(productOfferingId, productOfferingRelationships);
	Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}


//	@Test
//	void triggerProductOfferingValidatedCommand() {
//		String productSpecId = "productSpecId9";
//		String productOfferingId = "productOfferingId9";
//		
//		Mockito.when(appCtx.getBean("eventStorageEngine")).thenReturn(engine);
//		OffsetDateTime current = OffsetDateTime.now();
//		when(queryService.fetchProductSpecById(productSpecId)).thenReturn(
//				new ProductSpecification().id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
//						.validFor(new TimePeriod().startDateTime(current).endDateTime(current.plusDays(10))));
//		List<Event> history = new ArrayList<>();
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		history.add(new ProductSpecSelectedEvent("productSpecId"));
//		history.add(new ProductOfferingTypeSelectedEvent(productOfferingId, ProductOfferingLifecycle.INSTUDY,
//				lastUpdate, ProductOfferingType.ATOMICPRODUCTOFFERING));
//		history.add(new ProductSpecStateVerifiedEvent(productSpecId, ProductSpecificationLifecycle.ACTIVE));
//		history.add(new AtomicProductOfferingInitiatedEvent(new ProductSpecificationRef().id(productSpecId),
//				productOfferingId, ProductOfferingLifecycle.INSTUDY, OffsetDateTime.now()));
//		history.add(new AtomicProductOfferingDescribedEvent(productOfferingId, "MobileAccess", "MobileAccess",
//				"MobileAccess", "MobileAccess", ProductOfferingType.ATOMICPRODUCTOFFERING, true, OffsetDateTime.now()));
//		history.add(new AtomicProductOfferingCategoryDefinedEvent(productOfferingId,
//				Set.of(new CategoryRef().id("BOS_B2B_Classification").name("BOS_B2B_Classification")),
//				OffsetDateTime.now()));
//		history.add(new AtomicProductOfferingChannelDefinedEvent(productOfferingId,
//				List.of(new ChannelRef().id("channel1")), OffsetDateTime.now()));
//		history.add(new AtomicProductOfferingMarketDefinedEvent(productOfferingId,
//				List.of(new MarketSegmentRef().id("1266").name("North Region")), OffsetDateTime.now()));
//		history.add(new AtomicProductOfferingRelatedPartyDefinedEvent(productOfferingId,
//				List.of(new RelatedParty().id("SOM1").role("saleBy")), OffsetDateTime.now()));
//		history.add(new AtomicProductOfferingTermDefinedEvent(productOfferingId,
//				List.of(new ProductOfferingTerm().validFor(new TimePeriod().startDateTime(OffsetDateTime.now()))),
//				OffsetDateTime.now()));
//		List<Event> categoryHistory = new ArrayList<>();
//		categoryHistory.add(new AssociatedEntityModifiedEvent("BOS_B2B_Classification",
//				Set.of(new ProductOfferingRef().id(productOfferingId)), null, OffsetDateTime.now()));
//		publisher.publish(productOfferingId, history);
//		publisher.publish("BOS_B2B_Classification", categoryHistory);
	//productOfferingService.validateProductOffering(productOfferingId);
//	Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

//		List<Event> actualEvents = eventStore.fetch(productOfferingId);
//		assertTrue(actualEvents.size() > 8);
//
//		assertTrue(actualEvents.get(10) instanceof AtomicProductOfferingBundleDefinedEvent);
//		AtomicProductOfferingBundleDefinedEvent event = (AtomicProductOfferingBundleDefinedEvent) actualEvents.get(10);
//		assertNotNull(event.getProductOfferingId());
//		assertEquals(productOfferingId, event.getProductOfferingId());
//		assertEquals(false, event.isIsBundle());
//
//		assertTrue(actualEvents.get(11) instanceof AtomicProductOfferingValidatedEvent);
//		AtomicProductOfferingValidatedEvent event1 = (AtomicProductOfferingValidatedEvent) actualEvents.get(11);
//		assertNotNull(event1.getProductOfferingId());
//		assertNotNull(event1.getLifecycleStatus());
//		assertEquals(productOfferingId, event1.getProductOfferingId());
//		assertEquals(ProductOfferingLifecycle.INTEST, event1.getLifecycleStatus());
//
//		assertTrue(actualEvents.get(12) instanceof AtomicProductOfferingVersionCreatedEvent);
//		AtomicProductOfferingVersionCreatedEvent event2 = (AtomicProductOfferingVersionCreatedEvent) actualEvents
//				.get(12);
//		assertEquals(productOfferingId, event2.getProductOfferingId());
//		assertEquals("0.1.0", event2.getProductOfferingVersion());
//
//		assertTrue(actualEvents.get(13) instanceof AtomicProductOfferingCreationCompletedEvent);
//		AtomicProductOfferingCreationCompletedEvent event3 = (AtomicProductOfferingCreationCompletedEvent) actualEvents
//				.get(13);
//		assertEquals(productOfferingId, event3.getProductOffering().getId());
//		assertEquals(ProductOfferingLifecycle.INTEST, event3.getProductOffering().getLifecycleStatus());
//		assertEquals("0.1.0", event3.getProductOffering().getVersion());
//	}

	@Test
	void triggerProductOfferingOperationCommand() {
		String productSpecId = "productSpecId10";
		String productOfferingId = "productOfferingId10";
		OffsetDateTime current = OffsetDateTime.now();
		List<CommercialOperation> operationSpecification = new ArrayList<>();
		CommercialOperation oper = new CommercialOperation();
		oper.id("1").name("Create").validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		operationSpecification.add(oper);

		productOfferingService.defineProductOfferingOperation(productOfferingId, operationSpecification);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());


	}

	@Test
	void triggerCancelProductOffTest() {
		String productSpecId = "productSpecId11";
		String productOfferingId = "productOffId11";
		productOfferingService.cancelProductOff(productOfferingId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}

	@Test
	void triggerAssociatePOPtoOperationSpecificationCommand() {
		String productSpecId = "productSpecId12";
		String productOfferingId = "productOfferingId12";

		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();
		AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
		associatePOPtoOperationSpec.setOperationSpecId("operation_spec_id");
		associatePOPtoOperationSpec.setProductOfferingPriceId("product_offering_price_id");
		associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);

		productOfferingService.associatePOPtoOperationSpecification(associatePOPtoOperationSpecList, productOfferingId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

	}




//	@Test
//	void triggerDeleteProductOfferingTest() {
//		String productOfferingId = UUID.randomUUID().toString();
//		List<Event> history = new ArrayList<>();
//		publisher.publish(productOfferingId, history);
//		String id = productOfferingService.deleteProductOffering(40L, OffsetDateTime.now(), "HOURS");
//		List<Event> actualEvents = eventStore.fetch(id);
//		assertEquals(1, actualEvents.size());
//		assertTrue(actualEvents.get(0) instanceof ProductOfferingDeleteEvent);
//		assertEquals(id, ((ProductOfferingDeleteEvent) actualEvents.get(0)).getAggregateId());
//	}
//
//	/**
//	 * Manage the Bundled Product Offering command and define Bundle Product
//	 * Offerings in product offering.
//	 * 
//	 * @author Varshika Choudhary
//	 */
	@Test

	void triggerManageProductOfferingBundlingCommand() {
		String productOfferingId = "productOfferingId1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		int globalMinCardinality = 0;
		int globalMaxCardinality = 1;
		List<BundledProductOffering> bundleProductOfferings = new ArrayList<>();

		BundledProductOffering bpo1 = new BundledProductOffering();
		bpo1.setId("bpo1");
		bpo1.setName("bpo1");
		BundledProductOfferingOption bundledProductOfferingOption1 = new BundledProductOfferingOption();
		bundledProductOfferingOption1.setNumberRelOfferLowerLimit(0);
		bundledProductOfferingOption1.setNumberRelOfferUpperLimit(1);
		bundledProductOfferingOption1.setNumberRelOfferDefault(1);
		bpo1.bundledProductOfferingOption(bundledProductOfferingOption1);

		BundledProductOffering bpo2 = new BundledProductOffering();
		bpo2.setId("bpo2");
		bpo2.setName("bpo2");
		BundledProductOfferingOption bundledProductOfferingOption2 = new BundledProductOfferingOption();
		bundledProductOfferingOption2.setNumberRelOfferLowerLimit(1);
		bundledProductOfferingOption2.setNumberRelOfferUpperLimit(1);
		bundledProductOfferingOption2.setNumberRelOfferDefault(1);
		bpo2.bundledProductOfferingOption(bundledProductOfferingOption2);

		BundledProductOffering bpo3 = new BundledProductOffering();
		bpo3.setId("bpo3");
		bpo3.setName("bpo3");
		BundledProductOfferingOption bundledProductOfferingOption3 = new BundledProductOfferingOption();
		bundledProductOfferingOption3.setNumberRelOfferLowerLimit(1);
		bundledProductOfferingOption3.setNumberRelOfferUpperLimit(1);
		bundledProductOfferingOption3.setNumberRelOfferDefault(1);
		bpo3.bundledProductOfferingOption(bundledProductOfferingOption3);

		bundleProductOfferings.add(bpo1);
		bundleProductOfferings.add(bpo2);
		bundleProductOfferings.add(bpo3);



		productOfferingService.defineBundleProductOfferings(productOfferingId, bundleProductOfferings,
				globalMinCardinality, globalMaxCardinality);
		
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());


	}
	@Test
	void triggerDeleteProductOfferingTest() {
	productOfferingService.deleteProductOffering(40L, OffsetDateTime.now(), "HOURS");
	Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}
}

