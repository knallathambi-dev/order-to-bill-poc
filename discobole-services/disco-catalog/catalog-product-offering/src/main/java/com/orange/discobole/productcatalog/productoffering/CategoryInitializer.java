// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering;


import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.projection.CategoryProjector;
@Component
public class CategoryInitializer implements CommandLineRunner {
	@Resource
	private Publisher publisher;
	
	@Resource
	private CategoryProjector projector;
	
	@Override
	public void run(String... args) throws Exception {
	
		publisher.register(AssociatedEntityModifiedEvent.class, projector::handle);
	
	}


}
