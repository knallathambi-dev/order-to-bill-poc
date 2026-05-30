// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.aggregate.ProductSpecAggregate;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationRelationship;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.productspecification.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

class ProductServiceImplTest {

	@InjectMocks
	ProductSpecService productSpecService;
	QueryService queryService = Mockito.mock(QueryService.class);

	MongoEventStoreImpl mongoEventStoreImpl = Mockito.mock(MongoEventStoreImpl.class);

	private Publisher publisher = Mockito.mock(Publisher.class);

	@Mock
	ProductSpecAggregate productSpecAggregate;
	private static String productSpecId = "prod_spec_1";
	private static String accessToken = "Bearer 123";

	@Mock
	ObjectMapper objectMapper;
	CommandGateway commandGateway;

	public ProductServiceImplTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		productSpecService = new ProductSpecServiceImpl(commandGateway);
	}

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(productSpecService, "queryService", queryService);
		ReflectionTestUtils.setField(productSpecService, "publisher", publisher);
		ReflectionTestUtils.setField(productSpecService, "mongoEventStoreImpl", mongoEventStoreImpl);
	}

	@Test
	void triggerInitiateProductSpecCreation() {
		String serviceSpecId = "service_spec_id";
		productSpecService.initiateProductSpecCreation(serviceSpecId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerInitiateStockItemProductSpecCreation() {
		String stockItemTypeId = "SIT1";
		productSpecService.initiateStockItemProductSpecCreation(stockItemTypeId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerInitiateProductSpecDef() {

		ProductSpecification productSpecification = new ProductSpecification();
		productSpecification.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
				.supportEntity(SupportEntity.CFSSPEC);

		IdentityData description=new IdentityData();
		description.setBrand("cisco");
		description.setDescription("description_1");
		description.setName("name_1");
		description.setProductNumber("123");
		
		when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(productSpecification);
		productSpecService.initiateProductSpecDef(productSpecId, description,new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>(),new TimePeriod(),
				EntityType.PRODUCTSPECIFICATION);
		Mockito.verify(commandGateway, Mockito.times(2)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerInitiateProductSpecDefWhenStockItemType() {

		ProductSpecification productSpecification = new ProductSpecification();
		productSpecification.id(productSpecId).lifecycleStatus(ProductSpecificationLifecycle.ACTIVE)
				.supportEntity(SupportEntity.STOCKITEMTYPE);
		
		IdentityData description=new IdentityData();
		description.setBrand("cisco");
		description.setDescription("description_1");
		description.setName("name_1");
		description.setProductNumber("123");

		when(queryService.fetchProductSpecById(productSpecId, accessToken)).thenReturn(productSpecification);
		productSpecService.initiateProductSpecDef(productSpecId, description,new ArrayList<RelatedParty>(),new ArrayList<RelatedResource>(),new TimePeriod(),
				EntityType.PRODUCTSPECIFICATION);
		Mockito.verify(commandGateway, Mockito.times(2)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerUpdateProductSpecCharacteristics() {

		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = new ArrayList<>();
		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
				.id("1");
		productSpecificationCharacteristics.add(productSpecificationCharacteristic);

		productSpecService.updateProductSpecCharacteristics(productSpecId, productSpecificationCharacteristics,new ArrayList<UsageSpecification>());
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerUpdateStockItemProductSpecCharacteristics() {

		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = new ArrayList<>();
		ProductSpecificationCharacteristic productSpecificationCharacteristic = new ProductSpecificationCharacteristic()
				.id("1");
		productSpecificationCharacteristics.add(productSpecificationCharacteristic);

		productSpecService.updateStockItemProductSpecCharacteristics(productSpecId,
				productSpecificationCharacteristics);
		Mockito.verify(commandGateway, Mockito.times(3)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerUpdateProductSpecRel() {
		List<ProductSpecificationRelationship> relationships = new ArrayList<>();
		List<PolicyRuleRef> policyRuleRefs = new ArrayList<>();
		productSpecService.updateProductSpecRel(productSpecId, relationships, policyRuleRefs);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerValidateProductSpecification() {
		productSpecService.validateProductSpecification(productSpecId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerCancelProductSpec() {
		productSpecService.cancelProductSpec(productSpecId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerinitiateProductSpecModification() {
		productSpecService.initiateProductSpecModification(productSpecId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerModifyProductSpecDescription() {
		productSpecService.modifyProductSpecDefineIdentity(productSpecId, new IdentityData(), new ArrayList<RelatedParty>(), new ArrayList<RelatedResource>(), new TimePeriod(),
				EntityType.PRODUCTSPECIFICATION,ProductSpecificationLifecycle.ACTIVE);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}


	@Test
	void triggerModifyProductSpecRel() {
		productSpecService.modifyProductSpecRel(productSpecId, new ArrayList<ProductSpecificationRelationship>(), new ArrayList<PolicyRuleRef>());
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}


	@Test
	void triggerCancelProductSpecModification() {
		productSpecService.cancelProductSpecModification(productSpecId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void triggerProductSpecModificationValidation() {
		productSpecService.validateProductSpecificationModification(productSpecId);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}
	

	@Test
	void triggerDeleteProductSpecTest() {		
		productSpecService.deleteProductSpecification(OffsetDateTime.now(), 40L, "HOURS");
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}
}
