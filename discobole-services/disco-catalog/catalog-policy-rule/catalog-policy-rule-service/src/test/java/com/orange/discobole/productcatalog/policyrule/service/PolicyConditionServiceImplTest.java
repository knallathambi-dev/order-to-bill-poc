// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.*;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyConditionRepository;
import com.orange.discobole.productcatalog.policyrule.service.impl.PolicyConditionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PolicyConditionServiceImplTest extends DiscoPolicyRuleServiceApplicationTests {

    @Mock
    private PolicyConditionRepository policyConditionRepository;

    @InjectMocks
    private PolicyConditionServiceImpl policyConditionService;

    @Mock
    private MongoTemplate mongoTemplate;

    private PolicyCondition policyCondition;
    private PolicyConditionStatement policyConditionStatement;
    private PolicyConditionVariable policyConditionVariable;
    private PolicyConditionValue policyConditionValue;
    private Note note;

    @BeforeEach
    void setUp() {
        policyCondition = new PolicyCondition();
        policyCondition.setId("1");

        policyConditionStatement = new PolicyConditionStatement();
        policyConditionVariable = new PolicyConditionVariable();
        policyConditionVariable.setPath("some.path");

        policyConditionValue = new PolicyConditionValue();
        policyConditionValue.setId(UUID.randomUUID().toString());

        policyConditionStatement.setPolicyConditionVariable(policyConditionVariable);
        policyConditionStatement.setPolicyConditionValue(Collections.singletonList(policyConditionValue));

        policyCondition.setPolicyConditionStatement(Collections.singletonList(policyConditionStatement));

        note = new Note();
        note.setText("Sample Note");
        note.setId(UUID.randomUUID().toString());
        policyCondition.setNote(Collections.singletonList(note));

        ReflectionTestUtils.setField(policyConditionService, "mongoTemplate", mongoTemplate);
    }

    @Test
    void testCreatePolicyCondition() {
        when(policyConditionRepository.save(any(PolicyCondition.class))).thenReturn(policyCondition);

        PolicyCondition result = policyConditionService.createPolicyCondition(policyCondition);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("some.path", result.getPolicyConditionStatement().get(0).getPolicyConditionVariable().getPath());
        assertNotNull(result.getPolicyConditionStatement().get(0).getPolicyConditionVariable().getId());
        verify(policyConditionRepository, times(1)).save(any(PolicyCondition.class));
    }

    @Test
    void testCreatePolicyCondition_MissingConditionStatement() {
        policyCondition.setPolicyConditionStatement(null);
        assertThrows(MissingBodyFieldException.class, () -> policyConditionService.createPolicyCondition(policyCondition));
    }

    @Test
    void testGetAllPolicyCondition_with_pagination() {
        List<PolicyCondition> conditions = Collections.singletonList(policyCondition);
        AggregationResults<PolicyCondition> aggregationResults = mock(AggregationResults.class);
        when(aggregationResults.getMappedResults()).thenReturn(conditions);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("policyCondition"), eq(PolicyCondition.class)))
                .thenReturn(aggregationResults);

        List<PolicyCondition> result = policyConditionService.getAllPolicyCondition(0L,5L);
        assertEquals(1, result.size());
        verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("policyCondition"), eq(PolicyCondition.class));
    }
    @Test
    void testGetAllPolicyCondition_without_pagination() {
        List<PolicyCondition> conditions = Collections.singletonList(policyCondition);
        when(mongoTemplate.findAll(PolicyCondition.class)).thenReturn(conditions);

        List<PolicyCondition> result = policyConditionService.getAllPolicyCondition(null,null);
        assertEquals(1, result.size());
        verify(mongoTemplate, times(1)).findAll(PolicyCondition.class);
    }

    @Test
    void testGetPolicyConditionById() {
        when(policyConditionRepository.findById(any(String.class))).thenReturn(Optional.of(policyCondition));

        PolicyCondition result = policyConditionService.getPolicyConditionById("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void testGetPolicyConditionById_NotFound() {
        when(policyConditionRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyConditionService.getPolicyConditionById("1"));
    }

    @Test
    void testUpdatePolicyCondition() {
        when(policyConditionRepository.findById(any(String.class))).thenReturn(Optional.of(policyCondition));
        when(policyConditionRepository.save(any(PolicyCondition.class))).thenReturn(policyCondition);

        policyCondition.setName("Updated Name");
        PolicyCondition result = policyConditionService.updatePolicyCondition(policyCondition);
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(policyConditionRepository, times(1)).save(any(PolicyCondition.class));
    }

    @Test
    void testUpdatePolicyCondition_NotFound() {
        when(policyConditionRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyConditionService.updatePolicyCondition(policyCondition));
    }

    @Test
    void testDeletePolicyCondition() {
        when(policyConditionRepository.findById(any(String.class))).thenReturn(Optional.of(policyCondition));
        doNothing().when(policyConditionRepository).delete(any(PolicyCondition.class));

        policyConditionService.deletePolicyCondition("1");
        verify(policyConditionRepository, times(1)).delete(policyCondition);
    }

    @Test
    void testDeletePolicyCondition_NotFound() {
        when(policyConditionRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyConditionService.deletePolicyCondition("1"));
    }
}
