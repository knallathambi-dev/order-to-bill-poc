// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.bundleproductoffering;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelationshipDefinedEvent;

class BundleProductOfferingRelationshipDefinedEventTest extends CatalogApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingRelationshipDefinedEventTest.class);

	private BundleProductOfferingRelationshipDefinedEvent bundleProductOfferingRelationshipDefinedEvent;

	private final List<ProductOfferingRelationship> characteristicList = new ArrayList<>();
	
	List<PolicyRuleRef> policyRuleRefs = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductOfferingRelationship relations = new ProductOfferingRelationship();
		relations.setId("rel1");
		characteristicList.add(relations);
		PolicyRuleRef policyRuleRef = new PolicyRuleRef();
		policyRuleRef.id("1").name("policy");
		policyRuleRefs.add(policyRuleRef);
		bundleProductOfferingRelationshipDefinedEvent = new BundleProductOfferingRelationshipDefinedEvent(productOffId,
				characteristicList, lastUpdate);
	}

	@Test
	void bundleProductOfferingOperDefinedEventTest() {
		Assertions.assertAll("bundleProductOfferingRelationshipDefinedEvent",
				() -> Assertions.assertEquals("PROD1",
						bundleProductOfferingRelationshipDefinedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(
						bundleProductOfferingRelationshipDefinedEvent.getProductOfferingRelationships(),
						characteristicList));
		LOGGER.debug("bundleProductOfferingRelationshipDefinedEvent: {}", bundleProductOfferingRelationshipDefinedEvent);
	}
}
