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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;

public final class ProductSpecPartyAndRelResourceCommandTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecPartyAndRelResourceCommandTest.class);

	private final List<RelatedParty> relatedParties = new ArrayList<>();
	private final Map<String, String> realtedResources = new HashMap<>();

	@BeforeEach
	public void setUp() {
		relatedParties.add(new RelatedParty().id("CUS").role("adminBy"));
		realtedResources.put("SOM1", "Service Delivery");
	}

	@Test
	public void productSpecPartyAndRelResourceCommandTest() {
		ProductSpecPartyAndRelResourceCommand productSpecPartyAndRelResourceCommand = new ProductSpecPartyAndRelResourceCommand(relatedParties,realtedResources);
		Assertions.assertAll("PartyAndRelResourceSpecProcessing",
				() -> assertEquals(productSpecPartyAndRelResourceCommand.getRelatedParties(), relatedParties),
		        () -> assertEquals(productSpecPartyAndRelResourceCommand.getRealtedResources(), realtedResources));
		LOGGER.info("productSpecRelCommand: {}", productSpecPartyAndRelResourceCommand);
	}

}
