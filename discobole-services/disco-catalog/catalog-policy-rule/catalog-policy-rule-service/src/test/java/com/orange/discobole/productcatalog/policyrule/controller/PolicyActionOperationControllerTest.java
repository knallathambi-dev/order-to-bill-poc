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
import com.orange.discobole.admin.PolicyActionOperation;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.service.PolicyActionOperationService;
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

@WebMvcTest(PolicyActionOperationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyActionOperationControllerTest extends DiscoPolicyRuleServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyActionOperationService policyActionOperationService;

    @Autowired
    private ObjectMapper objectMapper;

    private PolicyActionOperation policyActionOperation;

    @BeforeEach
    void setUp() {
        policyActionOperation = new PolicyActionOperation();
        policyActionOperation.setId("1");
        policyActionOperation.setName("Test Action Operation");
        policyActionOperation.setDescription("This is a test policy action operation.");
        // Set other properties as needed
    }

    @Test
    void testCreatePolicyActionOperation() throws Exception {
        when(policyActionOperationService.createPolicyActionOperation(any(PolicyActionOperation.class))).thenReturn(policyActionOperation);

        mockMvc.perform(post("/policyRule/v1/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyActionOperation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(policyActionOperation.getId()))
                .andExpect(jsonPath("$.name").value(policyActionOperation.getName()));

        verify(policyActionOperationService, times(1)).createPolicyActionOperation(any(PolicyActionOperation.class));
    }

    @Test
    void testGetAllPolicyActionOperation_WithCount() throws Exception {
        // Arrange
        long totalRecords = 2L;
        List<PolicyActionOperation> policyActionOperations = Arrays.asList(policyActionOperation);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("count", totalRecords);
        serviceResponse.put("data", policyActionOperations);

        when(policyActionOperationService.policyActionOperationWithCount(
                anyMap(), eq(0L), eq(5L), any()))
                .thenReturn(serviceResponse);

        // Act & Assert
        mockMvc.perform(get("/policyRule/v1/action")
                .param("offset", "0")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", String.valueOf(totalRecords)))
                .andExpect(jsonPath("$.length()").value(policyActionOperations.size()));

        // Verify service was called correctly
        verify(policyActionOperationService, times(1))
                .policyActionOperationWithCount(anyMap(), eq(0L), eq(5L), any());
    }


    @Test
    void testGetPolicyActionOperationById() throws Exception {
        when(policyActionOperationService.getPolicyActionOperationById(policyActionOperation.getId())).thenReturn(policyActionOperation);

        mockMvc.perform(get("/policyRule/v1/action/{id}", policyActionOperation.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyActionOperation.getId()))
                .andExpect(jsonPath("$.name").value(policyActionOperation.getName()));

        verify(policyActionOperationService, times(1)).getPolicyActionOperationById(policyActionOperation.getId());
    }

    @Test
    void testUpdatePolicyActionOperation() throws Exception {
        when(policyActionOperationService.updatePolicyActionOperation(any(PolicyActionOperation.class))).thenReturn(policyActionOperation);

        mockMvc.perform(patch("/policyRule/v1/action/{id}", policyActionOperation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyActionOperation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyActionOperation.getId()))
                .andExpect(jsonPath("$.name").value(policyActionOperation.getName()));

        verify(policyActionOperationService, times(1)).updatePolicyActionOperation(any(PolicyActionOperation.class));
    }

    @Test
    void testDeletePolicyActionOperation() throws Exception {
        doNothing().when(policyActionOperationService).deletePolicyActionOperation(policyActionOperation.getId());

        mockMvc.perform(delete("/policyRule/v1/action/{id}", policyActionOperation.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted successfully"));

        verify(policyActionOperationService, times(1)).deletePolicyActionOperation(policyActionOperation.getId());
    }
}
