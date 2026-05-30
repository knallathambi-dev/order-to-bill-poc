// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.projection;

import com.orange.discobole.productcatalog.category.projection.ModifyProductOfferingProjector;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingCategoryModifiedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

public class ProductOfferingProjectorTest {

	private final ModifyProductOfferingProjector projector;
	private StreamBridge bridge;

	public ProductOfferingProjectorTest() {
		projector = new ModifyProductOfferingProjector();
		bridge = Mockito.mock(StreamBridge.class);
		ReflectionTestUtils.setField(projector, "bridge", bridge);
	}

	private static final String PRODUCTOFFERINGID = "poId";
	@Test
	public void CategoryIdentityDataDefinedEvent() {
		projector.handle(new AtomicProductOfferingCategoryModifiedEvent(PRODUCTOFFERINGID, null, null, null));
		verify(bridge).send(anyString(), any(Message.class));
	}

}
