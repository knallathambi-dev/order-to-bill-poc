// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

//package com.orange.bos.eventservice.controller.impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.orange.bos.eventservice.controller.EventController;
//import com.orange.bos.eventservice.dto.generated.Event;
//import com.orange.bos.eventservicetest.ApplicationTest;
//import jakarta.annotation.Resource;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//
//@WebMvcTest(EventController.class)
//public class EventControllerTest extends ApplicationTest {
//
//	private final String baseUrl = "/topic/123/event";
//	private Event event = null;
//
//	@Resource
//	ObjectMapper mapper;
//
//	@Resource
//	protected MockMvc mvc;
//
//	@MockBean
//	private KafkaTemplate<String, Object> kafkaTemplate;
//
//	@Test
//	public void eventNotifyTest() throws Exception {
//		event = new Event();
//		event.setEventType("ServiceSpecificationEvent");
//		String s = "{id:1}";
//		event.setEvent(s);
//		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//		String requestJson = ow.writeValueAsString(event);
//
//		mvc.perform(
//				MockMvcRequestBuilders.post(baseUrl).contentType(MediaType.APPLICATION_JSON_UTF8).content(requestJson))
//				.andExpect(MockMvcResultMatchers.status().isAccepted());
//	}
//
//	@Test
//	public void eventNotifyNegativeTestcase1() throws Exception {
//		event = new Event();
//		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//		String requestJson = ow.writeValueAsString(event);
//
//		mvc.perform(
//				MockMvcRequestBuilders.post(baseUrl).contentType(MediaType.APPLICATION_JSON_UTF8).content(requestJson))
//				.andExpect(MockMvcResultMatchers.status().isBadRequest());
//	}
//
//	@Test
//	public void eventNotifyNegativeTestcase2() throws Exception {
//		event = new Event();
//		event.setEventType("ServiceSpecificationEvent");
//		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//		String requestJson = ow.writeValueAsString(event);
//
//		mvc.perform(
//				MockMvcRequestBuilders.post(baseUrl).contentType(MediaType.APPLICATION_JSON_UTF8).content(requestJson))
//				.andExpect(MockMvcResultMatchers.status().isBadRequest());
//	}
//}