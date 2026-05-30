// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.projection;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;



import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceCancelledEvent;

import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceChargeIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationCompletedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceStatusValidityPeriodModifiedEvent;


class ModifyProductOfferingPriceProjectorTest {

	@InjectMocks
	private ModifyProductOfferingPriceProjector modifyProductOfferingPriceProjector;

	private static String popId = "pop1";

	private StreamBridge bridge;


	ModifyProductOfferingPriceProjectorTest() {

		bridge = Mockito.mock(StreamBridge.class);
		modifyProductOfferingPriceProjector = new ModifyProductOfferingPriceProjector(bridge);
	}

	@Test
	void testProductOfferingPriceChargeIdentityDataModifiedEvent() {
		ProductOfferingPriceChargeIdentityDataModifiedEvent event = new ProductOfferingPriceChargeIdentityDataModifiedEvent();
		modifyProductOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void testProductOfferingPriceRelationshipModifiedEvent() {
		ProductOfferingPriceRelationshipModifiedEvent event = new ProductOfferingPriceRelationshipModifiedEvent();
		modifyProductOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void testProductOfferingPriceStatusValidityPeriodModifiedEvent() {
		ProductOfferingPriceStatusValidityPeriodModifiedEvent event = new ProductOfferingPriceStatusValidityPeriodModifiedEvent(
				popId, ProductOfferingPriceLifecycle.LAUNCHED, new TimePeriod(), OffsetDateTime.now());
		modifyProductOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	@Test
	void testProductOfferingPriceModificationCompletedEvent() {
		ProductOfferingPriceModificationCompletedEvent event = new ProductOfferingPriceModificationCompletedEvent(new ProductOfferingPrice());
		modifyProductOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

	/*@Test
	void testProductOfferingPriceAlterationIdentityDataModifiedEvent() {
		ApplicationDuration duration = new ApplicationDuration().amount(24);
		ProductOfferingPriceAlterationIdentityDataModifiedEvent event = new ProductOfferingPriceAlterationIdentityDataModifiedEvent(
				popId, "pop", "this is pop", PriceType.RC, duration, 1, 20F, new Money(), new Quantity(), ProductOfferingPriceLifecycle.LAUNCHED, new TimePeriod(),
				OffsetDateTime.now());
		modifyProductOfferingPriceProjector.handle(event);
		Mockito.verify(productOfferingPrice).send(ArgumentMatchers.any(Message.class));
	}*/

	@Test
	void testProductOfferingPriceCancelledEvent() {
		ProductOfferingPriceCancelledEvent event = new ProductOfferingPriceCancelledEvent(popId,
				new ProductOfferingPrice());
		modifyProductOfferingPriceProjector.handle(event);
		Mockito.verify(bridge).send(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
	}

}
