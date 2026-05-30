// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice;



import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceAlterationIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceChargeIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceStatusValidityPeriodModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.projection.ModifyProductOfferingPriceProjector;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class Initializer to register event for Product Offering price with the
 * Projection.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
@Component
public class ModifyProductOfferingPriceInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ModifyProductOfferingPriceProjector projector;

	@PostConstruct
	private void init() {
		publisher.register(ProductOfferingPriceAlterationIdentityDataModifiedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceChargeIdentityDataModifiedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceRelationshipModifiedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceStatusValidityPeriodModifiedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceModificationValidatedEvent.class, projector::handle);
	}

	/**
	 * This method is used to initiate service calls and triggering commands for
	 * test purpose.
	 *
	 * @param args the string argument
	 * @throws Exception the exception
	 */
	@Override
	public void run(String... args) throws Exception {
     //uncomment for future use
	}
}
