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
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.time.OffsetDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
//import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.CommercialOperation;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationshipType;
//import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingType;
//import com.orange.discobole.productcatalog.catalog.handler.ModifyProductOfferingEventHandler;
//import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
//import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;
//import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
//import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
//import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.DefineContractIdentityData;
//import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
//import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;
//import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//
//class ModifyProductOfferingEventHandlerTest extends CatalogApplicationTests {
//
//	@InjectMocks
//	private ModifyProductOfferingEventHandler eventHandler;
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
//	void saveAtomicProductOfferingCategoryModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		Set<CategoryRef> categories = new HashSet<>();
//		CategoryRef cat = new CategoryRef().id("id").baseType("base").href("href").name("category").type("type");
//		categories.add(cat);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship().id("id").type("type").productOfferings(null).categories(categories);
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		AtomicProductOfferingCategoryModifiedEvent aPOCevent = new AtomicProductOfferingCategoryModifiedEvent(
//				"productOfferingId", categories, categories, OffsetDateTime.now());
//		eventHandler.handle(aPOCevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//	@Test
//	void saveAtomicProductOfferingCategoryIndirectModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		Set<CategoryRef> categories = new HashSet<>();
//		CategoryRef cat = new CategoryRef().id("id").baseType("base").href("href").name("category").type("type");
//		categories.add(cat);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship().id("id").type("type").productOfferings(null).categories(categories);
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		AtomicProductOfferingIndirectCategoryModifiedEvent aPOCIevent = new AtomicProductOfferingIndirectCategoryModifiedEvent(
//				"productOfferingId", categories, categories, OffsetDateTime.now());
//		eventHandler.handle(aPOCIevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//	@Test
//	void saveContractProductOfferingCategoryIndirectModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.CONTRACT);
//		Set<CategoryRef> categories = new HashSet<>();
//		CategoryRef cat = new CategoryRef().id("id").baseType("base").href("href").name("category").type("type");
//		categories.add(cat);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship().id("id").type("type").productOfferings(null).categories(categories);
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		ContractProductOfferingIndirectCategoryModifiedEvent cPOCIevent = new ContractProductOfferingIndirectCategoryModifiedEvent(
//				"productOfferingId", categories, categories, OffsetDateTime.now());
//		eventHandler.handle(cPOCIevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//	@Test
//	void saveBundleProductOfferingCategoryIndirectModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING);
//		Set<CategoryRef> categories = new HashSet<>();
//		CategoryRef cat = new CategoryRef().id("id").baseType("base").href("href").name("category").type("type");
//		categories.add(cat);
//		CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship().id("id").type("type").productOfferings(null).categories(categories);
//		when(categoryEntityService.fetchEntityById("productOfferingId")).thenReturn(categoryEntity);
//		BundleProductOfferingIndirectCategoryModifiedEvent bPOCIevent = new BundleProductOfferingIndirectCategoryModifiedEvent(
//				"productOfferingId", categories, categories, OffsetDateTime.now());
//		eventHandler.handle(bPOCIevent);
//		verify(categoryEntityService).save(categoryEntity);
//	}
//
//	@Test
//	void updateAtomicProductOfferingDescribedModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingIdentityDataModifiedEvent aPOIDMevent = new AtomicProductOfferingIdentityDataModifiedEvent("productOfferingId",
//				new DefineIdentityData(),new ArrayList<ChannelRef>(),
//				new ArrayList<MarketSegmentRef>(),new ArrayList<RelatedParty>(),new ArrayList<ProductOfferingTerm>(),new TimePeriod(),
//				ProductOfferingType.BUNDLEPRODUCTOFFERING,"",
//				OffsetDateTime.now(),ProductOfferingLifecycle.ACTIVE, ProductOfferingLifecycle.ACTIVE);
//		eventHandler.handle(aPOIDMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//	@Test
//	void updateBundleProductOfferingDescribedModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingIdentityDataModifiedEvent bPOIDMevent = new BundleProductOfferingIdentityDataModifiedEvent("productOfferingId",
//				new DefineIdentityData(),new ArrayList<ChannelRef>(),
//				new ArrayList<MarketSegmentRef>(),new ArrayList<RelatedParty>(),new ArrayList<ProductOfferingTerm>(),new TimePeriod(),
//				ProductOfferingType.BUNDLEPRODUCTOFFERING,"",
//				OffsetDateTime.now(),ProductOfferingLifecycle.ACTIVE, ProductOfferingLifecycle.ACTIVE);
//		eventHandler.handle(bPOIDMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateContractProductOfferingDescribedModifiedEvent() {
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingIdentityDataModifiedEvent cPOIDMevent = new ContractProductOfferingIdentityDataModifiedEvent("productOfferingId",
//				new DefineContractIdentityData(),new ArrayList<ChannelRef>(),
//				new ArrayList<MarketSegmentRef>(),new ArrayList<RelatedParty>(),new ArrayList<ProductOfferingTerm>(),new TimePeriod(),
//				ProductOfferingType.BUNDLEPRODUCTOFFERING,"",
//				OffsetDateTime.now(),ProductOfferingLifecycle.ACTIVE, ProductOfferingLifecycle.ACTIVE);
//		eventHandler.handle(cPOIDMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateBundleProductOfferingOperModifiedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOfferingLocal = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.BUNDLEPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOfferingLocal);
//		BundleProductOfferingOperModifiedEvent bPOMevent = new BundleProductOfferingOperModifiedEvent(productOffId,
//				List.of(new CommercialOperation()), OffsetDateTime.now());
//		eventHandler.handle(bPOMevent);
//		verify(productOfferingService).saveProductOffering(productOfferingLocal);
//	}
//
//
//
//	@Test
//	void updateBunldeProductOfferingOperationModifiedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOfferingLocal = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOfferingLocal);
//		BundleProductOfferingOperModifiedEvent bPOOMevent = new BundleProductOfferingOperModifiedEvent(productOffId,
//				List.of(new CommercialOperation()), OffsetDateTime.now());
//		eventHandler.handle(bPOOMevent);
//		verify(productOfferingService).saveProductOffering(productOfferingLocal);
//	}
//	@Test
//	void updateContractProductOfferingOperationModifiedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOfferingLocal = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOfferingLocal);
//		ContractProductOfferingOperModifiedEvent cPOOMevent = new ContractProductOfferingOperModifiedEvent(productOffId,
//				List.of(new CommercialOperation()), OffsetDateTime.now());
//		eventHandler.handle(cPOOMevent);
//		verify(productOfferingService).saveProductOffering(productOfferingLocal);
//	}
//	@Test
//	void updateAtomicProductOfferingOperationModifiedEvent() {
//		String productOffId = "productOffId1";
//		ProductOffering productOfferingLocal = new ProductOffering().id(productOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(productOffId)).thenReturn(productOfferingLocal);
//		AtomicProductOfferingOperationModifiedEvent aPOOMevent = new AtomicProductOfferingOperationModifiedEvent(productOffId,
//				List.of(new CommercialOperation()),null, OffsetDateTime.now());
//		eventHandler.handle(aPOOMevent);
//		verify(productOfferingService).saveProductOffering(productOfferingLocal);
//	}
//
//	@Test
//	void updateLinkPOPtoOperationSpecification() {
//		String prodOffId = "productOffId";
//		ProductOffering productOfferingLocal = new ProductOffering().id(prodOffId)
//				.lifecycleStatus(ProductOfferingLifecycle.ACTIVE).lastUpdate(OffsetDateTime.now())
//				.description("MobileAccess").statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING)
//				.marketSegment(List.of(new MarketSegmentRef().id("1266")))
//				.channel(List.of(new ChannelRef().id("1266")));
//		when(productOfferingService.fetchProductOfferingById(prodOffId)).thenReturn(productOfferingLocal);
//		LinkPOPtoOperModifiedEvent popOMevent = new LinkPOPtoOperModifiedEvent(prodOffId, List.of(new CommercialOperation()),
//				null,OffsetDateTime.now());
//		eventHandler.handle(popOMevent);
//		verify(productOfferingService).saveProductOffering(productOfferingLocal);
//	}
//
//
//
//	@Test
//	void updateAtomicProductOfferingCharacteristicsModifiedEvent() {
//		OffsetDateTime lastUpdate = OffsetDateTime.now();
//		productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//				.productSpecification(new ProductSpecificationRef().id("product_spec_id")).description("MobileAccess")
//				.statusReason("statusReason").name("MobileAccess").brand("MobileAccess")
//				.type(ProductOfferingType.ATOMICPRODUCTOFFERING).lastUpdate(lastUpdate);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingCharacteristicsModifiedEvent aPOCMevent = new AtomicProductOfferingCharacteristicsModifiedEvent(
//				"productOfferingId", List.of(new ProductSpecificationCharacteristicValueUse()), lastUpdate);
//		eventHandler.handle(aPOCMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	@Test
//	void updateAtomicProductOfferingRelationshipModifiedEvent() {
//		Set<ProductOfferingRelationship> deleteProductOfferingRelationships=new HashSet<>();
//		List<ProductOfferingRelationship> productOfferingRelationShips=new ArrayList<>();
//		productOfferingRelationShips.add(new ProductOfferingRelationship().id("123"));
//		productOffering.setProductOfferingRelationship(productOfferingRelationShips);
//		PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
//		PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		AtomicProductOfferingRelationshipModifiedEvent aPORMevent = new AtomicProductOfferingRelationshipModifiedEvent(
//				"productOfferingId", Set.of(new ProductOfferingRelationship().id("123")),deleteProductOfferingRelationships,new ArrayList<>(List.of(policyRef1, policyRef2)), OffsetDateTime.now());
//		eventHandler.handle(aPORMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//	@Test
//	void updateBundleProductOfferingRelationshipModifiedEvent() {
//		Set<ProductOfferingRelationship> deleteProductOfferingRelationships=new HashSet<>();
//		List<ProductOfferingRelationship> productOfferingRelationShips=new ArrayList<>();
//		productOfferingRelationShips.add(new ProductOfferingRelationship().id("123"));
//		PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
//		PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
//		productOffering.setProductOfferingRelationship(productOfferingRelationShips);
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		BundleProductOfferingRelationshipModifiedEvent bPORMevent = new BundleProductOfferingRelationshipModifiedEvent(
//				"productOfferingId", Set.of(new ProductOfferingRelationship().id("123")),deleteProductOfferingRelationships,new ArrayList<>(List.of(policyRef1, policyRef2)), OffsetDateTime.now());
//		eventHandler.handle(bPORMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//	@Test
//	void updateContractProductOfferingRelationshipModifiedEvent() {
//		Set<ProductOfferingRelationship> deleteProductOfferingRelationships=new HashSet<>();
//		List<ProductOfferingRelationship> productOfferingRelationShips=new ArrayList<>();
//		productOfferingRelationShips.add(new ProductOfferingRelationship().id("123"));
//		productOffering.setProductOfferingRelationship(productOfferingRelationShips);
//		PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
//		PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
//		when(productOfferingService.fetchProductOfferingById("productOfferingId")).thenReturn(productOffering);
//		ContractProductOfferingRelationshipModifiedEvent cPORMevent = new ContractProductOfferingRelationshipModifiedEvent(
//				"productOfferingId", Set.of(new ProductOfferingRelationship().id("123")),
//				deleteProductOfferingRelationships, new ArrayList<>(List.of(policyRef1, policyRef2)),
//				OffsetDateTime.now());
//		eventHandler.handle(cPORMevent);
//		verify(productOfferingService).saveProductOffering(productOffering);
//	}
//
//	   @Test
//	    void saveProductOfferingIncompatibleRelationshipModifiedEvent() {
//	    	List<ProductOfferingRelationship> productOfferingsRelations=new ArrayList<>();
//	        OffsetDateTime lastUpdate = OffsetDateTime.now();
//	        PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
//			PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
//	        productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//	                .type(ProductOfferingType.ATOMICPRODUCTOFFERING).lastUpdate(lastUpdate).productOfferingRelationship(productOfferingsRelations);
//	        Mockito.when(productOfferingService.fetchProductOfferingById(anyString())).thenReturn(productOffering);
//			AtomicProductOfferingIncompatibleRelationshipModifiedEvent aPOIRMevent = new AtomicProductOfferingIncompatibleRelationshipModifiedEvent(
//					"productOfferingId", new HashSet<ProductOfferingRelationship>(),
//					Set.of(new ProductOfferingRelationship().id("po2")
//							.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE)),
//					OffsetDateTime.now(), new ArrayList<>(List.of(policyRef1, policyRef2)));
//			eventHandler.handle(aPOIRMevent);
//	        verify(productOfferingService).saveProductOffering(productOffering);
//	    }
//	    @Test
//	    void saveBundleProductOfferingIncompatibleRelationshipModifiedEvent() {
//	    	List<ProductOfferingRelationship> productOfferingsRelations=new ArrayList<>();
//	    	PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
//			PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
//	        OffsetDateTime lastUpdate = OffsetDateTime.now();
//	        productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//	                .type(ProductOfferingType.ATOMICPRODUCTOFFERING).lastUpdate(lastUpdate).productOfferingRelationship(productOfferingsRelations);
//	        Mockito.when(productOfferingService.fetchProductOfferingById(anyString())).thenReturn(productOffering);
//	        BundleProductOfferingIncompatibleRelationshipModifiedEvent bPOIRMevent = new BundleProductOfferingIncompatibleRelationshipModifiedEvent("productOfferingId",
//	                new HashSet<ProductOfferingRelationship>(),
//	                Set.of(new ProductOfferingRelationship().id("po2").relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE)),OffsetDateTime.now(), new ArrayList<>(List.of(policyRef1, policyRef2)));
//	        eventHandler.handle(bPOIRMevent);
//	        verify(productOfferingService).saveProductOffering(productOffering);
//	    }
//	    @Test
//	    void saveContractProductOfferingIncompatibleRelationshipModifiedEvent() {
//	    	List<ProductOfferingRelationship> productOfferingsRelations=new ArrayList<>();
//	        OffsetDateTime lastUpdate = OffsetDateTime.now();
//	        productOffering.id("productOfferingId").lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
//	                .type(ProductOfferingType.ATOMICPRODUCTOFFERING).lastUpdate(lastUpdate).productOfferingRelationship(productOfferingsRelations);
//	        Mockito.when(productOfferingService.fetchProductOfferingById(anyString())).thenReturn(productOffering);
//	        PolicyRuleRef policyRef1 = new PolicyRuleRef().id("1").name("pol1");
//			PolicyRuleRef policyRef2 = new PolicyRuleRef().id("2").name("pol2");
//	        ContractProductOfferingIncompatibleRelationshipModifiedEvent cPOIRMevent = new ContractProductOfferingIncompatibleRelationshipModifiedEvent("productOfferingId",
//	                new HashSet<ProductOfferingRelationship>(),
//	                Set.of(new ProductOfferingRelationship().id("po2").relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE)),OffsetDateTime.now(), new ArrayList<>(List.of(policyRef1, policyRef2)));
//	        eventHandler.handle(cPOIRMevent);
//	        verify(productOfferingService).saveProductOffering(productOffering);
//	    }
//
//}
