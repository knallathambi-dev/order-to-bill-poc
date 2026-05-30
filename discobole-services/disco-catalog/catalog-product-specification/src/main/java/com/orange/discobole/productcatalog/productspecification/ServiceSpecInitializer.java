// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification;


import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.CurrentServiceSpecNotAlreadyExistedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.InvalidStatusReceivedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecDuplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecEarlyTimeRejectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecNotificationSentEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.projection.ServiceSpecProjector;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * The ServiceSpecInitializer Class register the events with the respective Projector handle method.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */

@Component
public class ServiceSpecInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ServiceSpecProjector serviceSpecProjector;

	/**
	 * This method will register events with the corresponding projection.
	 *
	 * @param args the string argument
	 */
	@Override
	public void run(final String... args) {
		publisher.register(ServiceSpecReplicatedEvent.class, serviceSpecProjector::handle);
		publisher.register(ServiceSpecNotificationSentEvent.class, serviceSpecProjector::handle);
		publisher.register(ServiceSpecDuplicatedEvent.class, serviceSpecProjector::handle);
		publisher.register(ServiceSpecStatusUpdatedEvent.class, serviceSpecProjector::handle);
		publisher.register(ServiceSpecAttributeUpdatedEvent.class, serviceSpecProjector::handle);
		publisher.register(CurrentServiceSpecNotAlreadyExistedEvent.class, serviceSpecProjector::handle);
		publisher.register(ServiceSpecEarlyTimeRejectedEvent.class, serviceSpecProjector::handle);
		publisher.register(InvalidStatusReceivedEvent.class, serviceSpecProjector::handle);

	}

}
