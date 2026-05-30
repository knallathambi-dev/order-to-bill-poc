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
import com.orange.discobole.admin.PolicyRule;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;
import com.orange.discobole.productcatalog.policyrule.service.PolicyRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyRuleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyRuleControllerTest extends DiscoPolicyRuleServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyRuleService policyRuleService;

    private PolicyRuleController controller;


    @Autowired
    private ObjectMapper objectMapper;

    private PolicyRule policyRule;

    @BeforeEach
    void setUp() {
        policyRule = new PolicyRule();
        policyRule.setId("1");
        policyRule.setName("Test Rule");
        policyRule.setDescription("This is a test policy rule.");
        // Set other properties as needed
        controller = new PolicyRuleController(policyRuleService);
    }

    @Test
    void testCreatePolicyRule() throws Exception {
        when(policyRuleService.createPolicyRule(any(PolicyRule.class))).thenReturn(policyRule);

        mockMvc.perform(post("/policyRule/v1/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyRule)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(policyRule.getId()))
                .andExpect(jsonPath("$.name").value(policyRule.getName()));

        verify(policyRuleService, times(1)).createPolicyRule(any(PolicyRule.class));
    }


    @Test
    void testGetPolicyRuleById() throws Exception {
        when(policyRuleService.getPolicyRuleById(policyRule.getId())).thenReturn(policyRule);

        mockMvc.perform(get("/policyRule/v1/rule/{id}", policyRule.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyRule.getId()))
                .andExpect(jsonPath("$.name").value(policyRule.getName()));

        verify(policyRuleService, times(1)).getPolicyRuleById(policyRule.getId());
    }

    @Test
    void testUpdatePolicyRule() throws Exception {
        when(policyRuleService.updatePolicyRule(any(PolicyRule.class))).thenReturn(policyRule);

        mockMvc.perform(patch("/policyRule/v1/rule/{id}", policyRule.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policyRule)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(policyRule.getId()))
                .andExpect(jsonPath("$.name").value(policyRule.getName()));

        verify(policyRuleService, times(1)).updatePolicyRule(any(PolicyRule.class));
    }

    @Test
    void testDeletePolicyRule() throws Exception {
        doNothing().when(policyRuleService).deletePolicyRule(policyRule.getId());

        mockMvc.perform(delete("/policyRule/v1/rule/{id}", policyRule.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Policy rule deleted successfully"));

        verify(policyRuleService, times(1)).deletePolicyRule(policyRule.getId());
    }

        @Test
        void getPOPByPO_shouldReturnOkResponse() {
            // Arrange
            String poId = "12345";
            List<ProductOfferingPrice> mockPrices = List.of(new ProductOfferingPrice("pop-1"));
            when(policyRuleService.getPOPByPO(poId)).thenReturn(mockPrices);

            // Act
            ResponseEntity<List<ProductOfferingPrice>> response = controller.getPOPByPO(poId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            assertEquals("pop-1", response.getBody().get(0).getId());
        }
}
