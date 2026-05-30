// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification;



import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;
import com.orange.discobole.productcatalog.productspecification.projection.ProductSpecProjector;
/**
 * The Class Initializer to register event with the Projection.
 *
 * @author Piyush Goel
 * @since 1.0
 */

@Component
public class ProductSpecInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

//	@Resource


	@Resource
	private ProductSpecProjector productSpecProjector;

	@PostConstruct
	private void init() {
		publisher.register(ProductSpecInitiatedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecIdentityDataEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecOpDefinedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecCharacteristicsDefinedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecRelationDefinedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecValidatedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecVersionCreatedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecCancelledEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecDefineIdentityModifiedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecCharacteristicsModifiedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecRelationModifiedEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecModificationValidatedEvent.class,productSpecProjector::handle);
		publisher.register(ComputeProductConfigurationEvent.class, productSpecProjector::handle);
		publisher.register(LinkProductSpecificationToStockItemEvent.class, productSpecProjector::handle);
		publisher.register(LinkProductSpecificationToStockItemModificationEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecificationDeleteEvent.class, productSpecProjector::handle);
		publisher.register(ProductSpecificationTemporaryDeleteEvent.class, productSpecProjector::handle);
		publisher.register(ProductConfigurationModificationEvent.class, productSpecProjector::handle);

	}

	/**
	 * This method starts the product specification creation process.
	 *
	 * @param args the string argument
	 * @throws Exception the exception
	 */
	@Override
	public void run(final String... args) throws Exception {
		//the method is empty

	}

}
