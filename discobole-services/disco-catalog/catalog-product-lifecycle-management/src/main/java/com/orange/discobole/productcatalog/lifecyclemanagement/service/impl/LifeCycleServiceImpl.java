// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle.SelectProductEntityCommand;
import com.orange.discobole.productcatalog.lifecyclemanagement.command.lifecycle.UpdateLifeCycleCommand;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.LifeCycleService;
import jakarta.annotation.Resource;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LifeCycleServiceImpl implements LifeCycleService {

	private final CommandGateway commandGateway;

	
	@Resource
	private Publisher lifeCyclePublisher;
	
	@Autowired
	public LifeCycleServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
		
	}
	@Override
	public String selectEntity(String entityId, String entityType) {
		String aggregateId = UUID.randomUUID().toString();
		return commandGateway.sendAndWait(new SelectProductEntityCommand(aggregateId,entityId,entityType));
	}

	@Override
	public void updateStatus(String aggregateId, LifecycleState state,String version) {
	commandGateway.sendAndWait(new UpdateLifeCycleCommand(aggregateId,state,version));
	}

}
