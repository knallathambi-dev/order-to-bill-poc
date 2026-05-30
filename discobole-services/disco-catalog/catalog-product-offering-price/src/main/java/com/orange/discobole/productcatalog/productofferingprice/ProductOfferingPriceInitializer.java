// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice;

import com.orange.discobole.productcatalog.productofferingprice.event.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.projection.ProductOfferingPriceProjector;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class Initializer to register event for Product Offering price with the
 * Projection.
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Component
public class ProductOfferingPriceInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ProductOfferingPriceProjector projector;

	@PostConstruct
	private void init() {

		publisher.register(ProductOfferingPriceInitiatedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceChargeIdentityDataDescribedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceAlterationIdentityDataDescribedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceRelationshipDefinedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceValidatedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceVersionCreatedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceCreationCompletedEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceCancelledEvent.class, projector::handle);
		publisher.register(ProductOfferingPriceDeleteEvent.class, projector::handle );
		publisher.register(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent.class, projector::handle );

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
		// please uncomment for future use
	}
}
