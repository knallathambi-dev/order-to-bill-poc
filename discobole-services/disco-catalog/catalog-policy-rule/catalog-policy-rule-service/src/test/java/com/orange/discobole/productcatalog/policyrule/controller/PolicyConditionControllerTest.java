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
import com.orange.discobole.admin.PolicyCondition;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.service.PolicyConditionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(PolicyConditionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyConditionControllerTest extends DiscoPolicyRuleServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyConditionService policyConditionService;

    @Autowired
    private ObjectMapper objectMapper;

    private PolicyCondition policyCondition;

    @BeforeEach
    void setUp() {
        policyCondition = new PolicyCondition();
        policyCondition.setId("1");
        policyCondition.setName("Test Condition");
        policyCondition.setDescription("This is a test policy condition.");
        // Set other properties as needed
    }

    @Test
    void testCreatePolicyCondition() throws Exception {
        when(policyConditionService.createPolicyCondition(any(PolicyCondition.class))).thenReturn(policyCondition);

        mockMvc.perform(post("/policyRule/v1/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyCondition)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(policyCondition.getId()))
                .andExpect(jsonPath("$.name").value(policyCondition.getName()));

        verify(policyConditionService, times(1)).createPolicyCondition(any(PolicyCondition.class));
    }

    @Test
    void testGetAllPolicyCondition() throws Exception {
        // Arrange
        long totalRecords = 2L;

        // Create and set required values in policyCondition
        policyCondition.setName("Condition1");  // Example property to test JSON output

        List<PolicyCondition> policyConditions = Arrays.asList(policyCondition);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("count", totalRecords);
        serviceResponse.put("data", policyConditions);

        // Mock the service call
        when(policyConditionService.fetchPolicyConditionWithCount(anyMap(), eq(0L), eq(5L), any()))
                .thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(get("/policyRule/v1/condition")
                .param("offset", "0")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", String.valueOf(totalRecords)))
                .andExpect(jsonPath("$.length()").value(policyConditions.size()))
                .andExpect(jsonPath("$[0].name").value("Condition1"));

        // Verify correct method called
        verify(policyConditionService, times(1))
                .fetchPolicyConditionWithCount(anyMap(), eq(0L), eq(5L), any());
    }


    @Test
    void testGetPolicyConditionById() throws Exception {
        when(policyConditionService.getPolicyConditionById(policyCondition.getId())).thenReturn(policyCondition);

        mockMvc.perform(get("/policyRule/v1/condition/{id}", policyCondition.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyCondition.getId()))
                .andExpect(jsonPath("$.name").value(policyCondition.getName()));

        verify(policyConditionService, times(1)).getPolicyConditionById(policyCondition.getId());
    }

    @Test
    void testUpdatePolicyCondition() throws Exception {
        when(policyConditionService.updatePolicyCondition(any(PolicyCondition.class))).thenReturn(policyCondition);

        mockMvc.perform(patch("/policyRule/v1/condition/{id}", policyCondition.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyCondition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyCondition.getId()))
                .andExpect(jsonPath("$.name").value(policyCondition.getName()));

        verify(policyConditionService, times(1)).updatePolicyCondition(any(PolicyCondition.class));
    }

    @Test
    void testDeletePolicyCondition() throws Exception {
        doNothing().when(policyConditionService).deletePolicyCondition(policyCondition.getId());

        mockMvc.perform(delete("/policyRule/v1/condition/{id}", policyCondition.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted successfully"));

        verify(policyConditionService, times(1)).deletePolicyCondition(policyCondition.getId());
    }
}
