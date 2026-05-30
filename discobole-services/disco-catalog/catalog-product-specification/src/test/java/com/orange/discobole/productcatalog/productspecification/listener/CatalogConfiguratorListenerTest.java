// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.service.ServiceSpecService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;


public class CatalogConfiguratorListenerTest extends ProductSpecificationApplicationTests {

	private String jsonInString = null;
	private Event event;
	private ServiceSpecification serviceSpecification;
	@InjectMocks
	private CatalogConfiguratorListener catalogConfiguratorListener;

	@Mock
	private ConfigurableProperties configurableProperties;

	@Mock
	private ServiceSpecService serviceSpecService;

	@Mock
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(catalogConfiguratorListener, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(catalogConfiguratorListener, "configurableProperties", configurableProperties);
		ReflectionTestUtils.setField(catalogConfiguratorListener, "serviceSpecService", serviceSpecService);
		serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("CFS1");
		event = new Event();
		event.setEventId("1");
		event.setEvent(serviceSpecification);
	}

	@Test
	void listenServiceSpecStateChangeExternalNotificationTest() throws JsonProcessingException {
		List<String> eventType = new ArrayList<>();
		eventType.add("ServiceSpecificationStateChange");
		Mockito.when(configurableProperties.getEventType()).thenReturn(eventType);
		jsonInString = "{\"eventId\":\"1\", \"eventType\":\"ServiceSpecificationStateChange\",\"event\":{\"id\":\"1\"}}";
		event.setEventType("ServiceSpecificationStateChange");
		Message<Event> attributeChangeMessage = mock(Message.class);
		Mockito.when(attributeChangeMessage.getPayload()).thenReturn(event);
		catalogConfiguratorListener.producerNotification().accept(attributeChangeMessage);
		Mockito.verify(serviceSpecService, Mockito.times(1)).processEvent(event);
	}

	@Test
	void listenServiceSpecAttributeValueChangeExternalNotificationTest() throws JsonProcessingException {
		List<String> eventType = new ArrayList<>();
		eventType.add("ServiceSpecificationAttributeValueChange");
		Mockito.when(configurableProperties.getEventType()).thenReturn(eventType);
		jsonInString = "{\"eventId\":\"1\", \"eventType\":\"ServiceSpecificationAttributeValueChange\",\"event\":{\"id\":\"1\"}}";
		event.setEventType("ServiceSpecificationAttributeValueChange");
		Message<Event> attributeChangeMessage = mock(Message.class);
		Mockito.when(attributeChangeMessage.getPayload()).thenReturn(event);
		catalogConfiguratorListener.producerNotification().accept(attributeChangeMessage);
		Mockito.verify(serviceSpecService, Mockito.times(1)).processUpdateServiceSpecEvent(event);
	}

	@Test
	void listenExternalNotificationWhenEventTypeDoesNotMatchTest() throws JsonProcessingException {
		// get Logback Logger
		Logger logger = (Logger) LoggerFactory.getLogger(CatalogConfiguratorListener.class);

		// create and start a ListAppender
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		// add the appender to the logger
		logger.addAppender(listAppender);

		List<String> eventType = new ArrayList<>();
		eventType.add("ServiceSpecificationStateChange");
		eventType.add("ServiceSpecificationAttributeValueChange");
		Mockito.when(configurableProperties.getEventType()).thenReturn(eventType);
		jsonInString = "{\"eventId\":\"1\", \"eventType\":\"eventType1\",\"event\":{\"id\":\"1\"}}";
		event.setEventType("eventType1");
		Message<Event> attributeChangeMessage = mock(Message.class);
		Mockito.when(attributeChangeMessage.getPayload()).thenReturn(event);
		catalogConfiguratorListener.producerNotification().accept(attributeChangeMessage);
		// JUnit assertions
		List<ILoggingEvent> logsList = listAppender.list;
		assertTrue(logsList.get(2).getMessage().contains("Event type not found for notification:"));
		assertEquals(Level.INFO, logsList.get(2).getLevel());
	}

	@Test
	public void listenExternalNotificationWhenExceptionIsThrown() {

		// get Logback Logger
		Logger logger = (Logger) LoggerFactory.getLogger(CatalogConfiguratorListener.class);

		// create and start a ListAppender
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();

		// add the appender to the logger
		logger.addAppender(listAppender);


		Message<Event> message = mock(Message.class);
		catalogConfiguratorListener.producerNotification().accept(message);

		// JUnit assertions
		List<ILoggingEvent> logsList = listAppender.list;
		assertEquals("cannot deserialize", logsList.get(2).getMessage());
		assertEquals(Level.ERROR, logsList.get(2).getLevel());
	}

}
