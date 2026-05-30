// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class ProductSpecRelCommandTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecRelCommandTest.class);

	private final List<ProductSpecificationRelationship> relationships = new ArrayList<>();
    private final String aggregateId = "1";
    List<PolicyRuleRef> policyRuleRefs = new ArrayList<>();
	
    @BeforeEach
	public void setUp() {
		relationships.add(new ProductSpecificationRelationship().type(ProductSpecRelationshipType.RELIESON.getValue()));
		relationships
				.add(new ProductSpecificationRelationship().type(ProductSpecRelationshipType.RELIESON.getValue()));
		PolicyRuleRef policyRuleRef = new PolicyRuleRef();
		policyRuleRef.setId("1");
		policyRuleRef.setName("policy");
		policyRuleRefs.add(policyRuleRef);
	}

	@Test
	public void productSpecRelCommandTest() {
		ProductSpecRelCommand productSpecRelCommand = new ProductSpecRelCommand(aggregateId,relationships, policyRuleRefs);
		Assertions.assertAll("RelationSpecProcessing",
				() -> assertEquals(productSpecRelCommand.getProductSpecificationRelationships(), relationships));
		LOGGER.info("productSpecRelCommand: {}", productSpecRelCommand);
	}
}
