// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.*;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceSpecEventHandlerService {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecEventHandlerService.class);

    @Resource
    Publisher publisher;

    @EventHandler
    public void handle(ServiceSpecExpurgedEvent event) {
		LOGGER.info("handling ServiceSpecExpurgedEvent {}", event);
		publisher.project(List.of(event));
    }

    @EventHandler
    public void handle(StatusVerifiedEvent event) {
		LOGGER.info("handling StatusVerifiedEvent {}", event);
		publisher.project(List.of(event));
    }

    @EventHandler
    public void handle(ServiceSpecReplicatedEvent event) {
		LOGGER.info("handling ServiceSpecReplicatedEvent {}", event);
		publisher.project(List.of(event));
    }

    @EventHandler
    public void handle(ServiceSpecNotificationSentEvent event) {
		LOGGER.info("handling ServiceSpecNotificationSentEvent {}", event);
		publisher.project(List.of(event));
    }

    @EventHandler
    public void handle(ServiceSpecAttributeUpdatedEvent event) {
		LOGGER.info("handling ServiceSpecAttributeUpdatedEvent {}", event);
		publisher.project(List.of(event));
    }

    @EventHandler
    public void handle(ServiceSpecStatusUpdatedEvent event) {
		LOGGER.info("handling ServiceSpecStatusUpdatedEvent {}", event);
		publisher.project(List.of(event));
    }

    @EventHandler
    public void handle(ServiceSpecDuplicatedEvent event) {
        // Intentionally left blank: No action required for this event
    }

    @EventHandler
    public void handle(InvalidStatusReceivedEvent event) {
        // Intentionally left blank: No action required for this event
    }

    @EventHandler
    public void handle(CurrentServiceSpecNotAlreadyExistedEvent event) {
        // Intentionally left blank: No action required for this event
    }

    @EventHandler
    public void handle(ServiceSpecEarlyTimeRejectedEvent event) {
        // Intentionally left blank: No action required for this event
    }

}
