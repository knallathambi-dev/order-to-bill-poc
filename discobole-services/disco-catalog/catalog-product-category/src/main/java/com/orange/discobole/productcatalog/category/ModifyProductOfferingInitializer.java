// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.projection.ModifyProductOfferingProjector;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingCategoryModifiedEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * The Class Initializer to register event for Modified Product Offering with
 * the Projection.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Component
public class ModifyProductOfferingInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ModifyProductOfferingProjector projector;


	@PostConstruct
	private void init() {
		publisher.register(AtomicProductOfferingCategoryModifiedEvent.class, projector::handle);
		}

	/**
	 * This method is used to initiate service calls and triggering commands for
	 * test purpose.
	 *
	 * @param args the string argument
	 * @throws Exception the exception
	 */
	@Override
	public void run(final String... args) throws Exception {
		// method to initiate service calls and triggering commands for test purpose.
	}

}
