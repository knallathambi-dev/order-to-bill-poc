// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import java.time.OffsetDateTime;


import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.aggregate.StockItemAggregate;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.StockItem;
import com.orange.discobole.productcatalog.productspecification.service.StockItemService;


public class StockItemServiceTest {



	@InjectMocks
	StockItemService stockItemService;

	private Publisher publisher = Mockito.mock(Publisher.class);

	@Mock
	StockItemAggregate stockItemAggregate;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	CommandGateway commandGateway;

	public StockItemServiceTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		stockItemService = new StockItemServiceImpl(commandGateway);
	}

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(stockItemService, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(stockItemService, "publisher", publisher);
	}

	@Test
	void processCreateStockItemEvent() {
		Event event = new Event();
		event.setEventId("eventId1");
		event.setEventType("StockItemStateChange");
		StockItem stockItem = new StockItem();
		stockItem.setId("1");
		stockItem.setState("Active");
		event.setEvent(stockItem);
		stockItemService.processStockItemEvent(event);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void processUpdateStockItemEvent() {
		StockItem existingStockItem = new StockItem();
		existingStockItem.setId("1");
		existingStockItem.setState("Active");

		Event event = new Event();
		event.setEventId("eventId1");
		event.setEventType("StockItemAttributeValueChange");
		event.setTimeOcurred(OffsetDateTime.now());
		StockItem stockItem = new StockItem();
		stockItem.setId("1");
		stockItem.setState("Active");
		event.setEvent(stockItem);

		stockItemService.processUpdateStockItemEvent(event);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}
}
