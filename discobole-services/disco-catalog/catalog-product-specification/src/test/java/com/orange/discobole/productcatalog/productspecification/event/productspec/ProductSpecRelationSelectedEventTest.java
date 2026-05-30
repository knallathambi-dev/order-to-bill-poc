// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecRelationSelectedEvent;

public class ProductSpecRelationSelectedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecRelationSelectedEventTest.class);

	private ProductSpecRelationSelectedEvent productSpecRelationSelectedEvent;
	private final Set<String> serviceSpecRelationships = new HashSet<>();

	@BeforeEach
	void setUp() {
		String productSpecId = "PROD1";
		serviceSpecRelationships.add("1");
		productSpecRelationSelectedEvent = new ProductSpecRelationSelectedEvent(productSpecId,
				serviceSpecRelationships);
	}

	@Test
	void productSpecRelationSelectedEventTest() {
		Assertions.assertAll("productSpecRelationSelectedEvent",
				() -> Assertions.assertEquals(productSpecRelationSelectedEvent.getProductSpecId(), "PROD1"),
				() -> Assertions.assertEquals(productSpecRelationSelectedEvent.getServiceSpecRelationships(),
						serviceSpecRelationships));
		LOGGER.info("productSpecRelationSelectedEvent: {}", productSpecRelationSelectedEvent);
	}
}
