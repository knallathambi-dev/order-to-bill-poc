// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.service.PolicyEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyEventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyEventControllerTest extends DiscoPolicyRuleServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyEventService policyEventService;

    @Autowired
    private ObjectMapper objectMapper;

    private PolicyEvent policyEvent;

    @BeforeEach
    void setUp() {
        policyEvent = new PolicyEvent();
        policyEvent.setId("1");
        policyEvent.setName("Test Policy");
        policyEvent.setDescription("This is a test policy event.");
        // Set other properties as needed
    }

    @Test
    void testCreatePolicyEvent() throws Exception {
        when(policyEventService.createPolicyEvent(any(PolicyEvent.class))).thenReturn(policyEvent);

        mockMvc.perform(post("/policyRule/v1/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(policyEvent.getId()))
                .andExpect(jsonPath("$.name").value(policyEvent.getName()));

        verify(policyEventService, times(1)).createPolicyEvent(any(PolicyEvent.class));
    }

    @Test
    void testGetAllPolicyEvent() throws Exception {
        // Arrange
        long totalRecords = 1L; // total count of events
        List<PolicyEvent> policyEvents = Arrays.asList(policyEvent);

        // Mock service response map
        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("count", totalRecords);
        serviceResponse.put("data", policyEvents);

        // Mock service call for events
        when(policyEventService.fetchPolicyEventWithCount(anyMap(), eq(0L), eq(5L), any()))
                .thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(get("/policyRule/v1/event")
                .param("offset", "0")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", String.valueOf(totalRecords)))
                .andExpect(jsonPath("$.length()").value(policyEvents.size()));

        // Verify correct service method was called
        verify(policyEventService, times(1))
                .fetchPolicyEventWithCount(anyMap(), eq(0L), eq(5L), any());
    }


    @Test
    void testGetPolicyEventById() throws Exception {
        when(policyEventService.getPolicyEventById(policyEvent.getId())).thenReturn(policyEvent);

        mockMvc.perform(get("/policyRule/v1/event/{id}", policyEvent.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyEvent.getId()))
                .andExpect(jsonPath("$.name").value(policyEvent.getName()));

        verify(policyEventService, times(1)).getPolicyEventById(policyEvent.getId());
    }

    @Test
    void testUpdatePolicyEvent() throws Exception {
        when(policyEventService.updatePolicyEvent(any(PolicyEvent.class))).thenReturn(policyEvent);

        mockMvc.perform(patch("/policyRule/v1/event/{id}", policyEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyEvent.getId()))
                .andExpect(jsonPath("$.name").value(policyEvent.getName()));

        verify(policyEventService, times(1)).updatePolicyEvent(any(PolicyEvent.class));
    }

    @Test
    void testDeletePolicyEvent() throws Exception {
        doNothing().when(policyEventService).deletePolicyEvent(policyEvent.getId());

        mockMvc.perform(delete("/policyRule/v1/event/{id}", policyEvent.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted successfully"));

        verify(policyEventService, times(1)).deletePolicyEvent(policyEvent.getId());
    }
}
