// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.command.servicespec.ServiceSpecAttributeValueChangeCommand;
import com.orange.discobole.productcatalog.productspecification.command.servicespec.ServiceSpecificationStateChangeCommand;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.service.ServiceSpecService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The ServiceSpecServiceImpl type delegates the call for business processing.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
@Service
public class ServiceSpecServiceImpl implements ServiceSpecService {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecServiceImpl.class);

	private final CommandGateway commandGateway;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private Publisher publisher;

	
	@Autowired
	public ServiceSpecServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
		
	}

	
	@Override
	public void processEvent(com.orange.discobole.productcatalog.productspecification.dto.generated.Event event) {
		LOGGER.info("processEvent : {}", event);
		ServiceSpecification serviceSpecification;
		objectMapper.registerModule(new JavaTimeModule());
		try {
			serviceSpecification = objectMapper.convertValue(event.getEvent(), ServiceSpecification.class);
			String aggregateId = serviceSpecification.getId();
			commandGateway.sendAndWait(new ServiceSpecificationStateChangeCommand(aggregateId, event));
		} catch (Exception e) {
			LOGGER.error("Error while parsing event: {}, Exception raised for service specification: {}", event,e);
		}
	}

	@Override
	public void processUpdateServiceSpecEvent(com.orange.discobole.productcatalog.productspecification.dto.generated.Event event) {
		LOGGER.info("processUpdateServiceSpecEvent : {}", event);
		ServiceSpecification serviceSpecification;
		objectMapper.registerModule(new JavaTimeModule());
		try {
			serviceSpecification = objectMapper.convertValue(event.getEvent(), ServiceSpecification.class);
			String aggregateId = serviceSpecification.getId();
			commandGateway.sendAndWait(new ServiceSpecAttributeValueChangeCommand(aggregateId, event));
		} catch (Exception e) {
			LOGGER.error("Error while parsing event: {}, Exception raised for service specification: {}", event,e);
		}
	}

}
