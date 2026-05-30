// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemStatusUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.projection.StockItemProjector;

import jakarta.annotation.Resource;

@Component
public class StockItemInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private StockItemProjector stockItemProjector;

	/**
	 * This method will register events with the corresponding projection.
	 *
	 * @param args the string argument
	 */
	@Override
	public void run(final String... args) {
		publisher.register(StockItemReplicatedEvent.class, stockItemProjector::handle);
		publisher.register(StockItemStatusUpdatedEvent.class, stockItemProjector::handle);
		publisher.register(StockItemAttributeUpdatedEvent.class, stockItemProjector::handle);		
	}


}
