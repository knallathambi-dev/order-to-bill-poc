// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.PolicyActionOperation;
import com.orange.discobole.admin.PolicyDomainRef;
import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.exception.DuplicateBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyDomainRepository;
import com.orange.discobole.productcatalog.policyrule.service.impl.PolicyDomainServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PolicyDomainServiceImplTest extends DiscoPolicyRuleServiceApplicationTests {

    @Mock
    private PolicyDomainRepository policyDomainRepository;

    @InjectMocks
    private PolicyDomainServiceImpl policyDomainService;

    private PolicyDomainRef policyDomain;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        policyDomain = new PolicyDomainRef();
        policyDomain.setId("1");
        policyDomain.setName("Test Domain");
        ReflectionTestUtils.setField(policyDomainService, "mongoTemplate", mongoTemplate);
    }

    @Test
    void testCreatePolicyDomain() {
        when(policyDomainRepository.findByName(any(String.class))).thenReturn(null);
        when(policyDomainRepository.save(any(PolicyDomainRef.class))).thenReturn(policyDomain);

        PolicyDomainRef result = policyDomainService.createPolicyDomain(policyDomain);
        assertEquals("Test Domain", result.getName());
        verify(policyDomainRepository, times(1)).save(policyDomain);
    }

    @Test
    void testCreatePolicyDomain_NullPolicyDomain() {
        assertThrows(MissingBodyFieldException.class, () -> policyDomainService.createPolicyDomain(null));
    }

    @Test
    void testCreatePolicyDomain_NullName() {
        policyDomain.setName(null);
        assertThrows(MissingBodyFieldException.class, () -> policyDomainService.createPolicyDomain(policyDomain));
    }

    @Test
    void testCreatePolicyDomain_DuplicateName() {
        when(policyDomainRepository.findByName(any(String.class))).thenReturn(policyDomain);

        assertThrows(DuplicateBodyFieldException.class, () -> policyDomainService.createPolicyDomain(policyDomain));
    }

    @Test
    void testGetAllPolicyDomain_with_pagination() {
        List<PolicyDomainRef> domains = Arrays.asList(policyDomain);
        AggregationResults<PolicyDomainRef> aggregationResults = mock(AggregationResults.class);
        when(aggregationResults.getMappedResults()).thenReturn(domains);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("policyDomainRef"), eq(PolicyDomainRef.class)))
                .thenReturn(aggregationResults);

        List<PolicyDomainRef> result = policyDomainService.getAllPolicyDomain(0L,5L);
        assertEquals(1, result.size());
        verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("policyDomainRef"), eq(PolicyDomainRef.class));
    }

    @Test
    void testGetAllPolicyDomain_without_pagination() {
        List<PolicyDomainRef> domains = Arrays.asList(policyDomain);
        when(mongoTemplate.findAll(PolicyDomainRef.class)).thenReturn(domains);

        List<PolicyDomainRef> result = policyDomainService.getAllPolicyDomain(null, null);
        assertEquals(1, result.size());
        verify(mongoTemplate, times(1)).findAll(PolicyDomainRef.class);
    }

    @Test
    void testGetPolicyDomainById() {
        when(policyDomainRepository.findById(any(String.class))).thenReturn(Optional.of(policyDomain));

        PolicyDomainRef result = policyDomainService.getPolicyDomainById("1");
        assertEquals("Test Domain", result.getName());
    }

    @Test
    void testGetPolicyDomainById_NotFound() {
        when(policyDomainRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyDomainService.getPolicyDomainById("1"));
    }

    @Test
    void testUpdatePolicyDomain() {
        when(policyDomainRepository.findById(any(String.class))).thenReturn(Optional.of(policyDomain));
        when(policyDomainRepository.save(any(PolicyDomainRef.class))).thenReturn(policyDomain);

        policyDomain.setName("Updated Domain");
        PolicyDomainRef result = policyDomainService.updatePolicyDomain(policyDomain);
        assertEquals("Updated Domain", result.getName());
    }

    @Test
    void testUpdatePolicyDomain_NotFound() {
        when(policyDomainRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyDomainService.updatePolicyDomain(policyDomain));
    }

    @Test
    void testDeletePolicyDomain() {
        when(policyDomainRepository.findById(any(String.class))).thenReturn(Optional.of(policyDomain));
        doNothing().when(policyDomainRepository).delete(any(PolicyDomainRef.class));

        policyDomainService.deletePolicyDomain("1");
        verify(policyDomainRepository, times(1)).delete(policyDomain);
    }

    @Test
    void testDeletePolicyDomain_NotFound() {
        when(policyDomainRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyDomainService.deletePolicyDomain("1"));
    }
}

