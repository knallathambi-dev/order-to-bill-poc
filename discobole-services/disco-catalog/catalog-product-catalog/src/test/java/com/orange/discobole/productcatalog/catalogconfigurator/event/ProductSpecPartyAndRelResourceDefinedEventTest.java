// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalogconfigurator.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecPartyAndRelResourceDefinedEvent;

class ProductSpecPartyAndRelResourceDefinedEventTest extends CatalogApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecPartyAndRelResourceDefinedEventTest.class);

	private final List<RelatedParty> relatedParties = new ArrayList<>();
	private final List<RelatedResource> realtedResources = new ArrayList<>();
	private ProductSpecPartyAndRelResourceDefinedEvent productSpecPartyAndRelResourceDefinedEvent;

	@BeforeEach
	public void setUp() {
		String productSpecId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		RelatedParty relatedParty = new RelatedParty().id("CUS");
		RelatedResource relatedResource = new RelatedResource();
		relatedResource.setId("SOM1");
		relatedParties.add(relatedParty);
		realtedResources.add(relatedResource);
		productSpecPartyAndRelResourceDefinedEvent = new ProductSpecPartyAndRelResourceDefinedEvent(productSpecId,
				relatedParties, realtedResources, lastUpdate);
	}

	@Test
	void productSpecPartyAndRelResourceDefinedEventTest() {
		Assertions.assertAll("productSpecPartyAndRelResourceDefinedEvent",
				() -> Assertions.assertEquals("PROD1", productSpecPartyAndRelResourceDefinedEvent.getProductSpecId()),
				() -> assertEquals(productSpecPartyAndRelResourceDefinedEvent.getRelatedParties(), relatedParties),
				() -> assertEquals(productSpecPartyAndRelResourceDefinedEvent.getRealtedResources(), realtedResources));
		LOGGER.debug("productSpecPartyAndRelResourceDefinedEvent: {}", productSpecPartyAndRelResourceDefinedEvent);
	}

}
