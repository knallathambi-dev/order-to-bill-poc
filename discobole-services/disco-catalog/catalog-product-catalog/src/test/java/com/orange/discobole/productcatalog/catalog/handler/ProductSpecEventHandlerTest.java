// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.query.Update;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.DefineIdentityData;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecRelationshipType;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationCharacteristicRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ServiceSpecificationRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.OperationSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.catalog.handler.ProductSpecEventHandler;
import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;
import com.orange.discobole.productcatalog.catalog.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecCancelledEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecCharacteristicsDefinedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecCharacteristicsModifiedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecDefineIdentityModifiedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecIdentityDataEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecInitiatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecModificationValidatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecOpDefinedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecRelationDefinedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecRelationModifiedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecValidatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecVersionCreatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecificationDeleteEvent;

class ProductSpecEventHandlerTest extends CatalogApplicationTests {

	@InjectMocks
	private ProductSpecEventHandler eventHandler;

	@Mock
	private ProductSpecService productSpecService;
	
	@Mock
	private  MongodbDataFilterService mongodbDataFilterService;
	private ProductSpecEvent event;

	private ProductSpecification productSpecification;

	@BeforeEach
	public void init() {
		productSpecification = new ProductSpecification();
		event = new ProductSpecEvent() {
		};
		event.aggregateName();
	}

	@Test
	void saveProductSpecInitiatedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		productSpecification.id("product_spec_id").aggregateId("product_spec_id")
				.lifecycleStatus(ProductSpecificationLifecycle.ACTIVE).supportEntity(SupportEntity.fromValue("CFSSpec"))
				.addServiceSpecificationItem(new ServiceSpecificationRef().id("service_spec_id")).lastUpdate(lastUpdate)
				.isBundle(false);

		ProductSpecInitiatedEvent productSpecInitiatedEvent = new ProductSpecInitiatedEvent("product_spec_id",
				ProductSpecificationLifecycle.ACTIVE, SupportEntity.CFSSPEC.toString(),
				new ServiceSpecificationRef().id("service_spec_id"), lastUpdate, null,null);

		eventHandler.handle(productSpecInitiatedEvent);
		verify(productSpecService).saveProductSpecification(productSpecification);
	}

	@Test
	void updateProductSpecRelationDefinedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecRelationDefinedEvent productSpecRelationDefinedEvent = new ProductSpecRelationDefinedEvent("product_spec_id",
				List.of(new ProductSpecificationRelationship()), lastUpdate, List.of(new PolicyRuleRef()));
		eventHandler.handle(productSpecRelationDefinedEvent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}

	@Test
	void updateProductSpecDescribedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecIdentityDataEvent productSpecIdentityDataEvent = new ProductSpecIdentityDataEvent("product_spec_id",new DefineIdentityData(),new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>(),new TimePeriod() , lastUpdate, EntityType.PRODUCTSPECIFICATION, "http://localhost:8080");
		eventHandler.handle(productSpecIdentityDataEvent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}
	
	@Test
	void updateProductSpecDefineIdentityModifiedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecDefineIdentityModifiedEvent productSpecDefineIdentityModifiedEvent=new ProductSpecDefineIdentityModifiedEvent("product_spec_id",new DefineIdentityData(),new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>(),new TimePeriod(),lastUpdate, EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.INTEST, ProductSpecificationLifecycle.ACTIVE);
		eventHandler.handle(productSpecDefineIdentityModifiedEvent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}
	
	@Test
	void updateProductSpecRelationModifiedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecRelationModifiedEvent productSpecRelationModifiedEvent = new ProductSpecRelationModifiedEvent("product_spec_id", null, null,
				lastUpdate);
		eventHandler.handle(productSpecRelationModifiedEvent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}

	@Test
	void updateProductSpecCharacteristicsModifiedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecCharacteristicsModifiedEvent pSCMevent=new ProductSpecCharacteristicsModifiedEvent("product_spec_id",null,null, lastUpdate);
		eventHandler.handle(pSCMevent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}

	@Test
	void updateProductSpecOpDefinedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecOpDefinedEvent pSODevent = new ProductSpecOpDefinedEvent("product_spec_id",
				List.of(new OperationSpecification()), lastUpdate);
		eventHandler.handle(pSODevent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}

	@Test
	void updateProductSpecCharacteristicsDefinedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecCharacteristicsDefinedEvent pSCDevent = new ProductSpecCharacteristicsDefinedEvent("product_spec_id",
				List.of(new ProductSpecificationCharacteristic()),new ArrayList<UsageSpecification>(), lastUpdate);
		eventHandler.handle(pSCDevent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}


	@Test
	void updateProductSpecValidateEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecValidatedEvent productSpecValidatedEvent = new ProductSpecValidatedEvent("product_spec_id",
				ProductSpecificationLifecycle.INTEST, lastUpdate);

		eventHandler.handle(productSpecValidatedEvent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}

	@Test
	void updateProductSpecVersionCreatedEvent() {
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecVersionCreatedEvent pSVCevent = new ProductSpecVersionCreatedEvent("product_spec_id", "0.1.0",
				lastUpdate);
		eventHandler.handle(pSVCevent);
		verify(productSpecService, times(1)).updateProductSpecification(anyString(), any(Update.class));
	}

	@Test
	void removeProductSpecEvent() {
		String productSpecId = "productSpecId1";
		ProductSpecification productSpec = new ProductSpecification().id(productSpecId)
				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY).lastUpdate(OffsetDateTime.now());
		ProductSpecCancelledEvent pSCevent = new ProductSpecCancelledEvent(productSpecId,productSpec);
		when(productSpecService.fetchProductSpecificationById(productSpecId)).thenReturn(productSpec);
		eventHandler.handle(pSCevent);
		verify(productSpecService).removeProductSpecification(anyString());
	}
	@Test
	void deleteProductSpecEvent() {
		String productSpecId = "productSpecId1";
		ProductSpecificationDeleteEvent pSDevent = new ProductSpecificationDeleteEvent(productSpecId,OffsetDateTime.now(),40L,"HOURS");
		when(productSpecService.fetchProductSpecificationById(productSpecId)).thenReturn(productSpecification);
		eventHandler.handle(pSDevent);
		verify(mongodbDataFilterService).filterProductSpecificsationData(any(OffsetDateTime.class),any(Long.class),any(String.class));
		verify(mongodbDataFilterService).filterProductSpecificationEventsData(any(OffsetDateTime.class),any(Long.class),any(String.class));
	}
	@Test
	void updateProductSpecModificationValidatedEvent() {
		String productSpecId = "productSpecId1";
		when(productSpecService.fetchProductSpecificationById(productSpecId)).thenReturn(productSpecification);
		ProductSpecModificationValidatedEvent pSMVevent=new ProductSpecModificationValidatedEvent(productSpecId, ProductSpecificationLifecycle.INTEST,OffsetDateTime.now(),"1.2.1", getStoredProductSpecification());
		eventHandler.handle(pSMVevent);
		verify(productSpecService).updateProductSpecification(anyString(), any());
	}
	@Test
	void updateProductSpecModificationValidatedEventWithNoVersion() {
		String productSpecId = "productSpecId1";
		when(productSpecService.fetchProductSpecificationById(productSpecId)).thenReturn(productSpecification);
		ProductSpecModificationValidatedEvent pSMVevent=new ProductSpecModificationValidatedEvent(productSpecId, ProductSpecificationLifecycle.INTEST,OffsetDateTime.now(),"1.1.1", getStoredProductSpecification());
		eventHandler.handle(pSMVevent);
		verify(productSpecService).updateProductSpecification(anyString(), any());
	}
	ProductSpecification getStoredProductSpecification() {
		OperationSpecification os3=new OperationSpecification().id(".os1").description("osDescription1").name("osName1");
		OperationSpecification os4=new OperationSpecification().id(".os2").description("osDescription2").name("osName2");
		
		ProductSpecificationRelationship psr3=new ProductSpecificationRelationship().id("psr1").relationshipType(ProductSpecRelationshipType.RELIESON);
		ProductSpecificationRelationship psr4=new ProductSpecificationRelationship().id("psr2").relationshipType(ProductSpecRelationshipType.RELIESON);
		
		RelatedParty rp3=new RelatedParty().id("rp2").name("rpname2").role("rpRole2").referredType("rf2");
		RelatedParty rp4=new RelatedParty().id("rp1").name("rpname1").role("rpRole1").referredType("rf1");
		
		RelatedResource rr3=new RelatedResource().id("rr2").name("rrName2").role("rrRole2").referredType("rf2");
		RelatedResource rr4=new RelatedResource().id("rr1").name("rrName1").role("rrRole1").referredType("rf1");
		
		ProductSpecificationCharacteristicRelationship pscr1=new ProductSpecificationCharacteristicRelationship().id("pscr1").name("pscrName1").relationshipType("pscrRelation1");
		ProductSpecificationCharacteristicRelationship pscr2=new ProductSpecificationCharacteristicRelationship().id("pscr2").name("pscrName2").relationshipType("pscrRelation2");
		
		ProductSpecificationCharacteristicValue pscv1=new ProductSpecificationCharacteristicValue().
				isDefault(true).valueFrom("vf1").rangeInterval("ri1").regex("r1").unitOfMeasure("uom1").valueTo("vt1").valueType("vt1");
		ProductSpecificationCharacteristicValue pscv4=new ProductSpecificationCharacteristicValue().
				isDefault(true).valueFrom("vf2").rangeInterval("ri2").regex("r2").unitOfMeasure("uom2").valueTo("vt2").valueType("vt2");
		
		ProductSpecificationCharacteristic psc3=new ProductSpecificationCharacteristic().id(".psc2").name("pscName2").description("pscDescription2")
				.valueType("string").addProductSpecCharRelationshipItem(pscr2).addProductSpecCharacteristicValueItem(pscv4);
		ProductSpecificationCharacteristic psc4=new ProductSpecificationCharacteristic().id(".psc1").name("pscName1").description("pscDescription1")
				.valueType("string").addProductSpecCharRelationshipItem(pscr1).addProductSpecCharacteristicValueItem(pscv1);
		
		UsageSpecification us1=new UsageSpecification().id(".us1").name("usname1");
		UsageSpecification us2=new UsageSpecification().id(".us2").name("usname2");
		 return new ProductSpecification().aggregateId("aggregate_id").name("ps1").description("description").productNumber("productNumber").
				brand("brand").addOperationSpecificationItem(os3).addOperationSpecificationItem(os4).
				addProductSpecificationRelationshipItem(psr3).addProductSpecificationRelationshipItem(psr4).addRelatedPartyItem(rp3).addRelatedPartyItem(rp4)
				.addRelatedResourceItem(rr3).addRelatedResourceItem(rr4).addProductSpecCharacteristicItem(psc3).addProductSpecCharacteristicItem(psc4).validFor(new TimePeriod()).addProductUsageSpecificationItem(us1).addProductUsageSpecificationItem(us2).version("1.1.1").lifecycleStatus(ProductSpecificationLifecycle.ACTIVE);
	}

}
