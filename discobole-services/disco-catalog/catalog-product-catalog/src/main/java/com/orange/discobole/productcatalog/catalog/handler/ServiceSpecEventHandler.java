// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.service.ServiceSpecService;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


/**
 * The ServiceSpecEventHandler handle the events to persist the data in the database.
 *
 * @author Vivek Singh
 * @author Ankur Singh
 * @since 1.0
 */
@Component
public class ServiceSpecEventHandler {

	@Resource
	private ServiceSpecService serviceSpecService;

	/**
	 * Handle the {@link ServiceSpecReplicatedEvent}.
	 *
	 * @param event the event
	 */
	public void handle(final ServiceSpecReplicatedEvent event) {
		ServiceSpecification serviceSpecification = event.getServiceSpecification();
		serviceSpecService.saveServiceSpecification(serviceSpecification);
	}

	/**
	 * Handle the {@link ServiceSpecStatusUpdatedEvent}.
	 *
	 * @param event the event
	 */
	public void handle(final ServiceSpecStatusUpdatedEvent event) {
		ServiceSpecification serviceSpecification = serviceSpecService.fetchServiceSpecificationById(event.getCfsId());
		
		if (serviceSpecification == null) {
			// CFS doesn't exist yet - likely arrived before ServiceSpecReplicatedEvent
			// Skip processing - will be handled when ServiceSpecReplicatedEvent arrives
			return;
		}
		
		serviceSpecification.setLifecycleStatus(event.getLifecycleStatus().toString());
		serviceSpecification.setLastUpdate(event.getCfsTimeOccurred());
		serviceSpecService.saveServiceSpecification(serviceSpecification);
	}

	/**
	 * Handle the {@link ServiceSpecAttributeUpdatedEvent}.
	 *
	 * @param event the event
	 */
	public void handle(ServiceSpecAttributeUpdatedEvent event) {
		ServiceSpecification serviceSpecification = event.getServiceSpecification();
		serviceSpecService.saveServiceSpecification(serviceSpecification);
	}
}
