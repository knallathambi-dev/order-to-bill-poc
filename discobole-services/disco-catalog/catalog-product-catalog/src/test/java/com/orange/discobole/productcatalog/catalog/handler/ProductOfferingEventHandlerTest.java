// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

//package com.orange.discobole.productcatalog.catalog.handler;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.time.OffsetDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
//import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.CommercialOperation;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingType;
//import com.orange.discobole.productcatalog.catalog.handler.ProductOfferingEventHandler;
//import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
//import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;
//import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingCategoryDefinedEvent;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingOperDefinedEvent;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelationshipDefinedEvent;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingSelectedEvent;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingValidatedEvent;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingVersionCreatedEvent;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.CreateBundleProductOfferingEvent;
//import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.*;
//import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
//import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingModificationValidatedEvent;
//
//class ProductOfferingEventHandlerTest extends CatalogApplicationTests {
//
//	@InjectMocks
//	private ProductOfferingEventHandler eventHandler;
//
//	@Mock
//	private ProductOfferingService productOfferingService;
//
//	@Mock
//	private MongodbDataFilterService mongodbDataFilterService;
//
//	@Mock
//	private CategoryEntityRelationshipService categoryEntityService;
//
//	private ProductOfferingEvent event;
//
//	private ProductOffering productOffering;
//
//	@BeforeEach
//	public void init() {
//		productOffering = new ProductOffering();
//		event = new ProductOfferingEvent() {
//		};
//		event.aggregateName();
//	}
//
//	@Test
//	void saveProductOfferingTypeSelectedEvent() {
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING).lastUpdate(lastUpdate);
//		ProductOfferingTypeSelectedEvent pOTSevent = new ProductOfferingTypeSelectedEvent("productOfferingId", lastUpdate,
//				false, false, false, ProductOfferingType.ATOMICPRODUCTOFFERING, ProductOfferingLifecycle.ACTIVE);
//		eventHandler.handle(pOTSevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void saveProductOfferingInitiatedEvent() {
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).lastUpdate(lastUpdate);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingInitiatedEvent aPOIevent = new AtomicProductOfferingInitiatedEvent(
//				new ProductSpecificationRef().id("product_spec_id"), "productOfferingId",
//				ProductOfferingLifecycle.INSTUDY, lastUpdate);
//		eventHandler.handle(aPOIevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateProductOfferingForIdentityDataEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id"));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingIdentityDataDefinedEvent aPOIDDevent = new AtomicProductOfferingIdentityDataDefinedEvent("productOfferingId",
//				new DefineIdentityData(),new ArrayList<ChannelRef>(),
//				new ArrayList<MarketSegmentRef>(),new ArrayList<RelatedParty>(),new ArrayList<ProductOfferingTerm>(),new TimePeriod(),
//				ProductOfferingType.BUNDLEPRODUCTOFFERING,"",
//				OffsetDateTime.now(), "http://localhost:8080");
//		eventHandler.handle(aPOIDDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateContractProductOfferingForDescribedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id"));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingIdentityDataDefinedEvent cPOIDDevent = new ContractProductOfferingIdentityDataDefinedEvent("productOfferingId",
//				new DefineContractIdentityData(),new ArrayList<ChannelRef>(),
//				new ArrayList<MarketSegmentRef>(),new ArrayList<RelatedParty>(),new ArrayList<ProductOfferingTerm>(),new TimePeriod(),
//				ProductOfferingType.BUNDLEPRODUCTOFFERING,"",
//				OffsetDateTime.now(), "http://localhost:8080");
//		eventHandler.handle(cPOIDDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateProductOfferingForCategoryDefinedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship();
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingCategoryDefinedEvent aPOCDevent = new AtomicProductOfferingCategoryDefinedEvent(
//				"productOfferingId", Set.of(), OffsetDateTime.now());
//		eventHandler.handle(aPOCDevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//
//	@Test
//	void updateContractProductOfferingForCategoryDefinedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship().id("id").type("type")
//				.productOfferings(null).categories(null);
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingCategoryDefinedEvent cPOCDevent = new ContractProductOfferingCategoryDefinedEvent(
//				"productOfferingId", Set.of(), OffsetDateTime.now());
//		eventHandler.handle(cPOCDevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//
//
//	@Test
//	void updateProductOfferingCharacteristicsDefinedEvent() {
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING).lastUpdate(lastUpdate);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingCharacteristicsDefinedEvent aPOCDevent = new AtomicProductOfferingCharacteristicsDefinedEvent(
//				"productOfferingId", List.of(new ProductSpecificationCharacteristicValueUse()), lastUpdate);
//		eventHandler.handle(aPOCDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateProductOfferingForRelationshipEvent() {
//		productOffering.setProductOfferingRelationship(List.of(new ProductOfferingRelationship().id("123")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingRelationshipDefinedEvent aPORDevent = new AtomicProductOfferingRelationshipDefinedEvent(
//				"productOfferingId", List.of(new ProductOfferingRelationship().id("123")), OffsetDateTime.now());
//		eventHandler.handle(aPORDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateContractProductOfferingForRelationshipEvent() {
//		productOffering.setProductOfferingRelationship(List.of(new ProductOfferingRelationship().id("123")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingRelationshipDefinedEvent cPORDevent = new ContractProductOfferingRelationshipDefinedEvent(
//				"productOfferingId", List.of(new ProductOfferingRelationship().id("123")), OffsetDateTime.now());
//		eventHandler.handle(cPORDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateProductOfferingForBundleDefinedEvent() {
//		productOffering.id("productOfferingId").description("MobileAccess").statusReason("statusReason")
//				.name("MobileAccess").brand("MobileAccess").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingBundleDefinedEvent aPOBDevent = new AtomicProductOfferingBundleDefinedEvent("productOfferingId",
//				false, OffsetDateTime.now());
//		eventHandler.handle(aPOBDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//
//
//	@Test
//	void updateProductOfferingForValidatedEvent() {
//		productOffering.id("productOfferingId").description("MobileAccess").statusReason("statusReason")
//				.name("MobileAccess").brand("MobileAccess").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266"))).channel(List.of(new ChannelRef().id("1266")))
//				.relatedParty(List.of(new RelatedParty().id("SOM1").role("saleBy")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingValidatedEvent aPOVevent = new AtomicProductOfferingValidatedEvent("productOfferingId",
//				ProductOfferingLifecycle.INTEST, OffsetDateTime.now());
//		eventHandler.handle(aPOVevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateContractProductOfferingForValidatedEvent() {
//		productOffering.id("productOfferingId").description("MobileAccess").statusReason("statusReason")
//				.name("MobileAccess").brand("MobileAccess").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266"))).channel(List.of(new ChannelRef().id("1266")))
//				.relatedParty(List.of(new RelatedParty().id("SOM1").role("saleBy")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingValidatedEvent cPOVevent = new ContractProductOfferingValidatedEvent("productOfferingId",
//				ProductOfferingLifecycle.INTEST, OffsetDateTime.now());
//		eventHandler.handle(cPOVevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateProductOfferingForVersionCreatedEvent() {
//		productOffering.id("productOfferingId").description("MobileAccess").statusReason("statusReason")
//				.name("MobileAccess").brand("MobileAccess").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266"))).channel(List.of(new ChannelRef().id("1266")))
//				.relatedParty(List.of(new RelatedParty().id("SOM1").role("saleBy")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingVersionCreatedEvent aPOVCevent = new AtomicProductOfferingVersionCreatedEvent(
//				"productOfferingId", "0.1.0", OffsetDateTime.now());
//		eventHandler.handle(aPOVCevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateContractProductOfferingForVersionCreatedEvent() {
//		productOffering.id("productOfferingId").description("MobileAccess").statusReason("statusReason")
//				.name("MobileAccess").brand("MobileAccess").type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266"))).channel(List.of(new ChannelRef().id("1266")))
//				.relatedParty(List.of(new RelatedParty().id("SOM1").role("saleBy")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingVersionCreatedEvent cPOVCevent = new ContractProductOfferingVersionCreatedEvent(
//				"productOfferingId", "0.1.0", OffsetDateTime.now());
//		eventHandler.handle(cPOVCevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void removeProductOffEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOff = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.INSTUDY).lastUpdate(OffsetDateTime.now());
//		ProductOffCancelledEvent pOCevent = new ProductOffCancelledEvent("1", productOff);
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOff);
//		eventHandler.handle(pOCevent);
//		verify(productOfferingService).removeProductOffering(anyString());
//	}
//
//	@Test
//	void updateProductOfferingOperDefinedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOff = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOff);
//		AtomicProductOfferingOperDefinedEvent aPOODevent = new AtomicProductOfferingOperDefinedEvent(productOffId,
//				List.of(new CommercialOperation()), null, OffsetDateTime.now());
//		eventHandler.handle(aPOODevent);
//		verify(productOfferingService).saveProductOffering(productOff);
//	}
//
//	@Test
//	void updateContractProductOfferingOperDefinedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOff = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOff);
//		ContractProductOfferingOperDefinedEvent cPOODevent = new ContractProductOfferingOperDefinedEvent(productOffId,
//				List.of(new CommercialOperation()), OffsetDateTime.now());
//		eventHandler.handle(cPOODevent);
//		verify(productOfferingService).saveProductOffering(productOff);
//	}
//
//	@Test
//	void updateContractProductOfferingSelectedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOff = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.CONTRACT).marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOff);
//		ContractProductOfferingSelectedEvent cPOSevent = new ContractProductOfferingSelectedEvent(productOffId,
//				List.of(new BundledProductOffering()), OffsetDateTime.now(), 1, 1);
//		eventHandler.handle(cPOSevent);
//		verify(productOfferingService).saveProductOffering(productOff);
//	}
//
//	@Test
//	void updateLinkPOPtoOperationSpecification() {
//		String prodOffId = "productOffId";
//		ProductOffering productOff = new ProductOffering().id(prodOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(prodOffId)).thenReturn(productOff);
//		LinkPOPtoOperEvent lPOPevent = new LinkPOPtoOperEvent(prodOffId, List.of(new CommercialOperation()),
//				null, OffsetDateTime.now());
//		eventHandler.handle(lPOPevent);
//		verify(productOfferingService).saveProductOffering(productOff);
//	}
//
//	@Test
//	void deleteProductOfferingEvent() {
//		String productOfferingId = "productOfferingId1";
//		ProductOfferingDeleteEvent pODevent = new ProductOfferingDeleteEvent(productOfferingId, OffsetDateTime.now(), 40L,
//				"HOURS");
//		eventHandler.handle(pODevent);
//		verify(mongodbDataFilterService).filterProductOfferingData(any(OffsetDateTime.class), any(Long.class),
//				any(String.class));
//		verify(mongodbDataFilterService).filterProductOfferingEventsData(any(OffsetDateTime.class), any(Long.class),
//				any(String.class));
//	}
//
//	@Test
//	void updateBundleProductOfferingCategoryDefinedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship().id("id").type("type")
//				.productOfferings(Set.of(new ProductOfferingRef().id("id"))).categories(Set.of(new CategoryRef().id("id")));
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingCategoryDefinedEvent bPOCDevent = new BundleProductOfferingCategoryDefinedEvent(
//				"productOfferingId", Set.of(), OffsetDateTime.now());
//		eventHandler.handle(bPOCDevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//
//
//
//
//	@Test
//	void updateBundleProductOfferingRelationshipDefinedEvent() {
//		productOffering.setProductOfferingRelationship(List.of(new ProductOfferingRelationship().id("123")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingRelationshipDefinedEvent bPORDevent = new BundleProductOfferingRelationshipDefinedEvent(
//				"productOfferingId", List.of(new ProductOfferingRelationship().id("123")), OffsetDateTime.now());
//		eventHandler.handle(bPORDevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//
//	@Test
//	void updateBundleProductOfferingValidatedEvent() {
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).lastUpdate(lastUpdate);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingValidatedEvent bPOVevent = new BundleProductOfferingValidatedEvent("productOfferingId",
//				ProductOfferingLifecycle.ACTIVE, lastUpdate);
//		eventHandler.handle(bPOVevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//
//	@Test
//	void updateBundleProductOfferingVersionCreatedEvent() {
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).lastUpdate(lastUpdate);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingVersionCreatedEvent bPOVCevent = new BundleProductOfferingVersionCreatedEvent(
//				"productOfferingId", "1.1.1", lastUpdate);
//		eventHandler.handle(bPOVCevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateCreateBundleProductOfferingEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id"));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		CreateBundleProductOfferingEvent cBPOevent = new CreateBundleProductOfferingEvent("productOfferingId",
//				OffsetDateTime.now(), false, false, false, ProductOfferingType.BUNDLEPRODUCTOFFERING);
//		eventHandler.handle(cBPOevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateBundleProductOfferingOperDefinedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOff = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOff);
//		BundleProductOfferingOperDefinedEvent bPOODevent = new BundleProductOfferingOperDefinedEvent(productOffId,
//				List.of(new CommercialOperation()), OffsetDateTime.now());
//		eventHandler.handle(bPOODevent);
//		verify(productOfferingService).saveProductOffering(productOff);
//	}
//
//	@Test
//	void updateBundleProductOfferingSelectedEvent() {
//		productOffering.id("productOfferingId").description("MobileAccess").statusReason("statusReason")
//				.name("MobileAccess").brand("MobileAccess").type(ProductOfferingType.BUNDLEPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingSelectedEvent bPOSevent = new BundleProductOfferingSelectedEvent("productOfferingId",
//				List.of(new BundledProductOffering().baseType("base").id("id")), OffsetDateTime.now(), 1, 1);
//		eventHandler.handle(bPOSevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateProductOfferingTemporaryDeleteEvent() {
//		when(mongodbDataFilterService.filterTemporaryProductOfferingData()).thenReturn(10L);
//		ProductOfferingTemporaryDeleteEvent pOTDevent = new ProductOfferingTemporaryDeleteEvent("aggregate_Id");
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		eventHandler.handle(pOTDevent);
//		verify(mongodbDataFilterService).filterTemporaryProductOfferingData();
//	}
//
//	@Test
//	void updateAtomicProductOfferingModificationValidatedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOff = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING).version("1.0")
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOff);
//		AtomicProductOfferingModificationValidatedEvent aPOMVevent = new AtomicProductOfferingModificationValidatedEvent(
//				"productOffId", productOff, ProductOfferingLifecycle.ACTIVE, OffsetDateTime.now(), "1.1");
//		eventHandler.handle(aPOMVevent);
//		verify(productOfferingService).updateProductOffering(anyString(), any());
//	}
//
//}
