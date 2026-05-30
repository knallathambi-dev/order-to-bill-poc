// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.lifecyclemanagement.aggregate.ManageLifeCycleAggregate;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;

class LifeCycleServiceImplTest {
	
	   @Mock
	    ObjectMapper objectMapper;
	    CommandGateway commandGateway;
	    
	    @InjectMocks
	    LifeCycleServiceImpl lifeCycleServiceImpl;
	    
        ApplicationContext appCtx = Mockito.mock(ApplicationContext.class);
	    
	    @Mock
	    ManageLifeCycleAggregate manageLifeCycleAggregate;
	    
	    public LifeCycleServiceImplTest() {
	        commandGateway = Mockito.mock(CommandGateway.class);
	        lifeCycleServiceImpl = new LifeCycleServiceImpl(commandGateway);
	    }
	    
	    
		@Test void triggerselectEntity() { 
			
			String entityId = "product_spec_id";
			String entityType = "PRODUCTOFFERINGPRICE";
			lifeCycleServiceImpl.selectEntity(entityId, entityType);
			Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

		   
			
			}
		
		@Test void triggerupdateStatus() { 
			
			String aggregateId = "product_spec_id";
			LifecycleState state = LifecycleState.LAUNCHED;
			String version = "0.1";
			lifeCycleServiceImpl.updateStatus(aggregateId, state,version);
			Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());

		  
			
			}

}
