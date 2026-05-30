// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.productcatalog.policyrule.DiscoPolicyRuleServiceApplicationTests;
import com.orange.discobole.productcatalog.policyrule.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.policyrule.repository.PolicyEventRepository;
import com.orange.discobole.productcatalog.policyrule.service.impl.PolicyEventServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PolicyEventServiceImplTest extends DiscoPolicyRuleServiceApplicationTests {

    @Mock
    private PolicyEventRepository policyEventRepository;

    @Mock
    private ProductCatalogQueryService productCatalogQueryService;

    @InjectMocks
    private PolicyEventServiceImpl policyEventService;

    private PolicyEvent policyEvent;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        policyEvent = new PolicyEvent();
        policyEvent.setId("1");
        policyEvent.setQuery("productConfiguration.productOffering.id=='12345'");
        ReflectionTestUtils.setField(policyEventService, "mongoTemplate", mongoTemplate);
    }

    @Test
    void testCreatePolicyEvent() {
        when(productCatalogQueryService.fetchProductOfferingById(any(String.class))).thenReturn("12345");
        when(policyEventRepository.save(any(PolicyEvent.class))).thenReturn(policyEvent);

        PolicyEvent result = policyEventService.createPolicyEvent(policyEvent);
        assertNotNull(result);
        assertEquals("12345", result.getQuery().split("==")[1].replaceAll("'", "").trim());
        verify(policyEventRepository, times(1)).save(policyEvent);
    }

    @Test
    void testCreatePolicyEvent_MissingQuery() {
        policyEvent.setQuery(null);
        assertThrows(MissingBodyFieldException.class, () -> policyEventService.createPolicyEvent(policyEvent));
    }

    @Test
    void testCreatePolicyEvent_ProductOfferingNotFound() {
        when(productCatalogQueryService.fetchProductOfferingById(any(String.class))).thenReturn(null);
        assertThrows(MissingBodyFieldException.class, () -> policyEventService.createPolicyEvent(policyEvent));
    }

    @Test
    void testGetAllPolicyEvent_with_pagination() {
        Long skip = 10L;
        Long limit = 5L;
        PolicyEvent event1 = new PolicyEvent();
        PolicyEvent event2 = new PolicyEvent();
        List<PolicyEvent> expectedEvents = Arrays.asList(event1, event2);

        AggregationResults<PolicyEvent> aggregationResults = mock(AggregationResults.class);
        when(aggregationResults.getMappedResults()).thenReturn(expectedEvents);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("policyEvent"), eq(PolicyEvent.class)))
                .thenReturn(aggregationResults);

        List<PolicyEvent> result = policyEventService.getAllPolicyEvent(skip, limit);
        assertEquals(expectedEvents, result);
        verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("policyEvent"), eq(PolicyEvent.class));
    }

    @Test
    void testGetAllPolicyEvent_without_pagination() {
        List<PolicyEvent> events = Arrays.asList(policyEvent);
        when(mongoTemplate.findAll(PolicyEvent.class)).thenReturn(events);

        List<PolicyEvent> result = policyEventService.getAllPolicyEvent(null,null);
        assertEquals(1, result.size());
        verify(mongoTemplate, times(1)).findAll(PolicyEvent.class);
    }

    @Test
    void testGetPolicyEventById() {
        when(policyEventRepository.findById(any(String.class))).thenReturn(Optional.of(policyEvent));

        PolicyEvent result = policyEventService.getPolicyEventById("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void testGetPolicyEventById_NotFound() {
        when(policyEventRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyEventService.getPolicyEventById("1"));
    }

    @Test
    void testUpdatePolicyEvent() {
        when(policyEventRepository.findById(any(String.class))).thenReturn(Optional.of(policyEvent));
        when(productCatalogQueryService.fetchProductOfferingById(any(String.class))).thenReturn("12345");
        when(policyEventRepository.save(any(PolicyEvent.class))).thenReturn(policyEvent);

        policyEvent.setQuery("productConfiguration.productOffering.id=='12345'");
        PolicyEvent result = policyEventService.updatePolicyEvent(policyEvent);
        assertNotNull(result);
        assertEquals("12345", result.getQuery().split("==")[1].replaceAll("'", "").trim());
        verify(policyEventRepository, times(1)).save(policyEvent);
    }

    @Test
    void testUpdatePolicyEvent_NotFound() {
        when(policyEventRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyEventService.updatePolicyEvent(policyEvent));
    }

    @Test
    void testDeletePolicyEvent() {
        when(policyEventRepository.findById(any(String.class))).thenReturn(Optional.of(policyEvent));
        doNothing().when(policyEventRepository).delete(any(PolicyEvent.class));

        policyEventService.deletePolicyEvent("1");
        verify(policyEventRepository, times(1)).delete(policyEvent);
    }

    @Test
    void testDeletePolicyEvent_NotFound() {
        when(policyEventRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(MissingBodyFieldException.class, () -> policyEventService.deletePolicyEvent("1"));
    }
}

