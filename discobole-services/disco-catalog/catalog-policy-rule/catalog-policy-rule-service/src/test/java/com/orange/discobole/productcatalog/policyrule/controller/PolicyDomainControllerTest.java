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
import com.orange.discobole.admin.PolicyDomainRef;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.service.PolicyDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PolicyDomainController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyDomainControllerTest extends DiscoPolicyRuleServiceApplicationTests {
 @InjectMocks
 PolicyDomainController policyDomainController;
 @Autowired
 private MockMvc mockMvc;

 @MockBean
 private PolicyDomainService policyDomainService;

 private PolicyDomainRef policyDomainRef1;
 private PolicyDomainRef policyDomainRef2;
 private ObjectMapper objectMapper;

 @BeforeEach
 void setUp() {

  policyDomainRef1 = new PolicyDomainRef();
  policyDomainRef1.setId("1");
  policyDomainRef1.setName("Policy Domain 1");

  policyDomainRef2 = new PolicyDomainRef();
  policyDomainRef2.setId("2");
  policyDomainRef2.setName("Policy Domain 2");
  objectMapper = new ObjectMapper();

 }

 @Test
 void testCreatePolicyDomain() throws Exception {
  // Convert the policyDomainRef1 object to a JSON string
  String json = objectMapper.writeValueAsString(policyDomainRef1);
  mockMvc.perform(post("/policyRule/v1/domain")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isCreated())
          .andExpect(content().string("created successfully"));

  verify(policyDomainService, times(1)).createPolicyDomain(policyDomainRef1);
 }

 @Test
 void testGetAllPolicyDomain() throws Exception {
  List<PolicyDomainRef> policyDomains = Arrays.asList(policyDomainRef1, policyDomainRef2);

  when(policyDomainService.getAllPolicyDomain(0L,5L)).thenReturn(policyDomains);
  mockMvc.perform(get("/policyRule/v1/domain?offset=0&limit=5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value("1"))
          .andExpect(jsonPath("$[0].name").value("Policy Domain 1"))
          .andExpect(jsonPath("$[1].id").value("2"))
          .andExpect(jsonPath("$[1].name").value("Policy Domain 2"));

  verify(policyDomainService, times(1)).getAllPolicyDomain(0L,5L);
 }

 @Test
 void testGetPolicyDomainById() throws Exception {
  when(policyDomainService.getPolicyDomainById("1")).thenReturn(policyDomainRef1);

  mockMvc.perform(get("/policyRule/v1/domain/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("1"))
          .andExpect(jsonPath("$.name").value("Policy Domain 1"));

  verify(policyDomainService, times(1)).getPolicyDomainById("1");
 }

 @Test
 void testUpdatePolicyDomain() throws Exception {
  String json = objectMapper.writeValueAsString(policyDomainRef1);

  mockMvc.perform(patch("/policyRule/v1/domain/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk())
          .andExpect(content().string("updated successfully"));

  verify(policyDomainService, times(1)).updatePolicyDomain(policyDomainRef1);
 }

 @Test
 void testDeletePolicyDomain() throws Exception {
  mockMvc.perform(delete("/policyRule/v1/domain/1"))
          .andExpect(status().isOk())
          .andExpect(content().string("deleted successfully"));

  verify(policyDomainService, times(1)).deletePolicyDomain("1");
 }
}
