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
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.OperationSpecification;

class ProductSpecOperationCommandTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecOperationCommandTest.class);

	private List<OperationSpecification> operations = new ArrayList<>();
	private final String aggregateId = "1";
	
	@BeforeEach
	public void setUp() {
		operations = List.of(new OperationSpecification().id("1"));
	}

	@Test
	void productSpecOperationCommandTest() {
		ProductSpecOperationCommand productSpecOperationCommand = new ProductSpecOperationCommand(aggregateId,operations);
		Assertions.assertAll("productSpecOperationProcessing",
				() -> assertEquals(productSpecOperationCommand.getOperations(), operations));
		LOGGER.info("productSpecOperationCommand: {}", productSpecOperationCommand);
	}

}
