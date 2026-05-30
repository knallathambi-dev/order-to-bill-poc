// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecRelationshipType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationRelationship;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;


class ProductSpecRelationDefinedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecRelationDefinedEventTest.class);

	private ProductSpecRelationDefinedEvent productSpecRelationDefinedEvent;
	List<ProductSpecificationRelationship> productSpecRelationships = new ArrayList<>();
	List<PolicyRuleRef> policyRuleRefs = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String productSpecId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductSpecificationRelationship productSpecificationRelationship = new ProductSpecificationRelationship();
		productSpecificationRelationship.id("1").relationshipType(ProductSpecRelationshipType.RELIESON)
				.validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		productSpecRelationships.add(productSpecificationRelationship);
		PolicyRuleRef policyRuleRef = new PolicyRuleRef();
		policyRuleRef.setId("1");
		policyRuleRef.setName("policy");
		policyRuleRefs.add(policyRuleRef);
		productSpecRelationDefinedEvent = new ProductSpecRelationDefinedEvent(productSpecId, productSpecRelationships,
				lastUpdate, policyRuleRefs);
	}

	@Test
	void productSpecRelationDefinedEventTest() {
		Assertions.assertAll("productSpecRelationDefinedEvent",
				() -> Assertions.assertEquals("PROD1", productSpecRelationDefinedEvent.getProductSpecId()),
				() -> Assertions.assertEquals(productSpecRelationDefinedEvent.getProductSpecRelationships(),
						productSpecRelationships));
		LOGGER.info("productSpecRelationDefinedEvent: {}", productSpecRelationDefinedEvent);
	}
}
