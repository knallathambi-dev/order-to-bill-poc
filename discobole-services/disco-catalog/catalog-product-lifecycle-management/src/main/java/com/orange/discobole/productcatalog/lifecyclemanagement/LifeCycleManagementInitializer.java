// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement;


import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleEntitySelectEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.ProductSpecificationStateChangeEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.projection.LifeCycleManagerProjector;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LifeCycleManagementInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private LifeCycleManagerProjector lifeCycleManagerProjector;

	/**
	 * This method will register events with the corresponding projection.
	 *
	 * @param args the string argument
	 */
	@Override
	public void run(final String... args) {
		publisher.register(LifeCycleEntitySelectEvent.class, lifeCycleManagerProjector::handle);
		publisher.register(InvalidLifeCycleStateSelectedEvent.class, lifeCycleManagerProjector::handle);
		publisher.register(LifeCycleStateSelectedEvent.class, lifeCycleManagerProjector::handle);
		publisher.register(ProductSpecificationStateChangeEvent.class, lifeCycleManagerProjector::handle);
	}

}
