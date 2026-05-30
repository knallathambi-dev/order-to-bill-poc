// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;

class PublisherConfigTest extends ProductOfferingPriceApplicationTests{

	@InjectMocks
	private PublisherConfig publisherConfig;

	@Test
	void publisherConfigTest() {
		Publisher publiserActual = publisherConfig.publisher();
		Assertions.assertNotNull(publiserActual);
	}

}
