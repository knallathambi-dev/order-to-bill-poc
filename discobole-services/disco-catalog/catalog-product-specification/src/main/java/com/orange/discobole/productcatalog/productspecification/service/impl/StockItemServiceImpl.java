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
import com.orange.discobole.productcatalog.productspecification.command.stockitem.StockItemAttributeValueChangeCommand;
import com.orange.discobole.productcatalog.productspecification.command.stockitem.StockItemStateChangeCommand;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.service.StockItemService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockItemServiceImpl implements StockItemService {
    private static final Logger LOGGER = LogManager.getLogger(StockItemServiceImpl.class);

  
   
	private final CommandGateway commandGateway;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private Publisher publisher;
    
	@Autowired
	public StockItemServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
		
	}


    @Override
    public void processStockItemEvent(Event event) {
        StockItem stockItem;
        objectMapper.registerModule(new JavaTimeModule());
        try {
        	LOGGER.info("In method processStockItemEvent of StockItemServiceImpl");
            stockItem = objectMapper.convertValue(event.getEvent(), StockItem.class);
			String aggregateId = stockItem.getId();
			commandGateway.sendAndWait(new StockItemStateChangeCommand(aggregateId, event));

        } catch (Exception e) {
            LOGGER.error("Exception raised for stock item: {}", e);
        }
    }

    @Override
    public void processUpdateStockItemEvent(com.orange.discobole.productcatalog.productspecification.dto.generated.Event event) {
        StockItem stockItem;
        objectMapper.registerModule(new JavaTimeModule());
        try {
        	LOGGER.info("In method processUpdateStockItemEvent of StockItemServiceImpl");
        	 stockItem = objectMapper.convertValue(event.getEvent(), StockItem.class);
 			String aggregateId = stockItem.getId();
 			commandGateway.sendAndWait(new StockItemAttributeValueChangeCommand(aggregateId, event));

        } catch (Exception e) {
            LOGGER.error("Exception raised for stock item updation: {}", e);
        }
    }
}
