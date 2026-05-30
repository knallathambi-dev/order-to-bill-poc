// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.saga;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.command.productoffering.modify.ModifyProductOfferingIncompatibleRelationshipCommand;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationshipType;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.AtomicProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.saga.ProductOfferingSaga;

public class ProductOfferingSagaTest extends ProductOfferingApplicationTests {

	@Test
	public void testAtomicProductOfferingRelationshipDefinedEvent() {
		ProductOfferingRelationship relationship = new ProductOfferingRelationship().id("po2")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE);
		List<ProductOfferingRelationship> poRelations = new ArrayList<>();
		poRelations.add(relationship);
		List<PolicyRuleRef> policyRuleRefs = new ArrayList<>();
		SagaTestFixture<ProductOfferingSaga> fixture = new SagaTestFixture<>(ProductOfferingSaga.class);
		fixture.whenPublishingA(new AtomicProductOfferingRelationshipDefinedEvent("productOfferingId", poRelations,
				null))
				.expectDispatchedCommands(new ModifyProductOfferingIncompatibleRelationshipCommand("po2",
						"productOfferingId", true, ProductOfferingType.ATOMICPRODUCTOFFERING));
	}

	@Test
	public void testBundleProductOfferingRelationshipDefinedEvent() {
		ProductOfferingRelationship relationship = new ProductOfferingRelationship().id("po2")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE);
		List<ProductOfferingRelationship> poRelations = new ArrayList<>();
		poRelations.add(relationship);
		SagaTestFixture<ProductOfferingSaga> fixture = new SagaTestFixture<>(ProductOfferingSaga.class);
		fixture.whenPublishingA(new BundleProductOfferingRelationshipDefinedEvent("productOfferingId", poRelations, null))
				.expectDispatchedCommands(new ModifyProductOfferingIncompatibleRelationshipCommand("po2",
						"productOfferingId", true, ProductOfferingType.BUNDLEPRODUCTOFFERING));
	}

	@Test
	public void testContractProductOfferingRelationshipDefinedEvent() {
		ProductOfferingRelationship relationship = new ProductOfferingRelationship().id("po2")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE);
		List<ProductOfferingRelationship> poRelations = new ArrayList<>();
		poRelations.add(relationship);
		SagaTestFixture<ProductOfferingSaga> fixture = new SagaTestFixture<>(ProductOfferingSaga.class);
		fixture.whenPublishingA(new ContractProductOfferingRelationshipDefinedEvent("productOfferingId", poRelations,
				null))
				.expectDispatchedCommands(new ModifyProductOfferingIncompatibleRelationshipCommand("po2",
						"productOfferingId", true, ProductOfferingType.CONTRACT));
	}

	@Test
	void testAtomicProductOfferingRelationshipModifiedEvent() {

		SagaTestFixture<ProductOfferingSaga> fixture = new SagaTestFixture<>(ProductOfferingSaga.class);
		Set<ProductOfferingRelationship> deleteProductOfferingRelationships = new HashSet<>();
		deleteProductOfferingRelationships.add(new ProductOfferingRelationship().id("po3")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE));
		Set<ProductOfferingRelationship> addProductOfferingRelationships = new HashSet<>();
		addProductOfferingRelationships.add(new ProductOfferingRelationship().id("po2")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE));

		fixture.whenPublishingA(new AtomicProductOfferingRelationshipModifiedEvent("productOfferingId",
				addProductOfferingRelationships, deleteProductOfferingRelationships, null))
				.expectDispatchedCommands(
						new ModifyProductOfferingIncompatibleRelationshipCommand("po2", "productOfferingId", true,
								ProductOfferingType.ATOMICPRODUCTOFFERING),
						new ModifyProductOfferingIncompatibleRelationshipCommand("po3", "productOfferingId", false,
								ProductOfferingType.ATOMICPRODUCTOFFERING));
	}

	@Test
	void testBundleProductOfferingRelationshipModifiedEvent() {

		SagaTestFixture<ProductOfferingSaga> fixture = new SagaTestFixture<>(ProductOfferingSaga.class);
		Set<ProductOfferingRelationship> deleteProductOfferingRelationships = new HashSet<>();
		deleteProductOfferingRelationships.add(new ProductOfferingRelationship().id("po3")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE));
		Set<ProductOfferingRelationship> addProductOfferingRelationships = new HashSet<>();
		addProductOfferingRelationships.add(new ProductOfferingRelationship().id("po2")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE));
		

		fixture.whenPublishingA(new BundleProductOfferingRelationshipModifiedEvent("productOfferingId",
				addProductOfferingRelationships, deleteProductOfferingRelationships, null))
				.expectDispatchedCommands(
						new ModifyProductOfferingIncompatibleRelationshipCommand("po2", "productOfferingId", true,
								ProductOfferingType.BUNDLEPRODUCTOFFERING),
						new ModifyProductOfferingIncompatibleRelationshipCommand("po3", "productOfferingId", false,
								ProductOfferingType.BUNDLEPRODUCTOFFERING));
	}

	@Test
	void testContractProductOfferingRelationshipModifiedEvent() {

		SagaTestFixture<ProductOfferingSaga> fixture = new SagaTestFixture<>(ProductOfferingSaga.class);
		Set<ProductOfferingRelationship> deleteProductOfferingRelationships = new HashSet<>();
		deleteProductOfferingRelationships.add(new ProductOfferingRelationship().id("po3")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE));
		Set<ProductOfferingRelationship> addProductOfferingRelationships = new HashSet<>();
		addProductOfferingRelationships.add(new ProductOfferingRelationship().id("po2")
				.relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE));
		
		fixture.whenPublishingA(new ContractProductOfferingRelationshipModifiedEvent("productOfferingId",
				addProductOfferingRelationships, deleteProductOfferingRelationships, null))
				.expectDispatchedCommands(
						new ModifyProductOfferingIncompatibleRelationshipCommand("po2", "productOfferingId", true,
								ProductOfferingType.CONTRACT),
						new ModifyProductOfferingIncompatibleRelationshipCommand("po3", "productOfferingId", false,
								ProductOfferingType.CONTRACT));
	}
}