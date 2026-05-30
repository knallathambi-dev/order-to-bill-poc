// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.COMPLETED;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.FAILED;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {"config.deletePlanThresholdDuration = P1DT0H0M", "config.archivePlanThresholdDuration = P1DT0H0M", "config.deleteArchivedPlanThresholdDuration = P1DT0H0M"})
@ContextConfiguration(classes = {PurgeOrchestrationPlanScheduler.class}, initializers = PurgeOrchestrationPlanSchedulerTests.ConversionInitializer.class)
@ExtendWith(SpringExtension.class)
class PurgeOrchestrationPlanSchedulerTests {

    @MockBean
    OrchestrationPlanRepository orchestrationPlanRepository;

    @Autowired
    PurgeOrchestrationPlanScheduler purgeOrchestrationPlanScheduler;

    @Test
    void testStartPurgeOrchestrationPlan() {
        when(orchestrationPlanRepository.findOrchestrationPlanByState(Mockito.<State>any())).thenReturn(new ArrayList<>());
        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        verify(orchestrationPlanRepository).findOrchestrationPlanByStateIn(any());
    }

    @Test
    void givenExecutedOrchestrationPlanWithCompletedNodeAndLastModifiedDateBeforeThreshold_whenStartPurgeOrchestrationPlan_thenNoThingShouldHappen() {
        when(orchestrationPlanRepository.findOrchestrationPlanByState(any())).thenReturn(List.of(
                OrchestrationPlan.builder()
                        .state(State.EXECUTED)
                        .lastModifiedDate(Instant.now())
                        .archived(false)
                        .orchestrationPlanNodes(Set.of(OrchestrationPlanNode.builder()
                                .state(COMPLETED)
                                .build()))
                        .build()));

        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        verify(orchestrationPlanRepository, never()).delete(any());
    }

    @Test
    void givenExecutedOrchestrationPlanWithCompletedNodeAndLastModifiedDateExceedsThreshold_whenStartPurgeOrchestrationPlan_thenPlanDeleted() {
        when(orchestrationPlanRepository.findOrchestrationPlanByStateIn(any())).thenReturn(List.of(
                OrchestrationPlan.builder()
                        .state(State.EXECUTED)
                        .lastModifiedDate(Instant.now().minus(2, ChronoUnit.DAYS))
                        .archived(false)
                        .orchestrationPlanNodes(Set.of(OrchestrationPlanNode.builder()
                                .state(COMPLETED)
                                .build()))
                        .build()));

        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        verify(orchestrationPlanRepository).delete(any());
    }

    @Test
    void givenExecutedOrchestrationPlanWithFailedNodeAndLastModifiedDateBeforeThreshold_whenPurgeOrchestrationPlan_thenDoNothing() {
        when(orchestrationPlanRepository.findOrchestrationPlanByState(any())).thenReturn(List.of(
                OrchestrationPlan.builder()
                        .state(State.EXECUTED)
                        .lastModifiedDate(Instant.now())
                        .archived(false)
                        .orchestrationPlanNodes(Set.of(OrchestrationPlanNode.builder()
                                .state(FAILED)
                                .build()))
                        .build()));

        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        verify(orchestrationPlanRepository, never()).delete(any());
        verify(orchestrationPlanRepository, never()).save(any());
    }

    @Test
    void givenExecutedOrchestrationPlanWithFailedNodeAndLastModifiedDateExceedsThreshold_whenPurgeOrchestrationPlan_thenPlanArchived() {
        when(orchestrationPlanRepository.findOrchestrationPlanByStateIn(any())).thenReturn(List.of(
                OrchestrationPlan.builder()
                        .state(State.EXECUTED)
                        .lastModifiedDate(Instant.now().minus(2, ChronoUnit.DAYS))
                        .archived(false)
                        .orchestrationPlanNodes(Set.of(OrchestrationPlanNode.builder()
                                .state(FAILED)
                                .build()))
                        .build()));

        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        ArgumentCaptor<OrchestrationPlan> orchestrationPlanArgumentCaptor = ArgumentCaptor.forClass(OrchestrationPlan.class);
        verify(orchestrationPlanRepository).save(orchestrationPlanArgumentCaptor.capture());
        assertTrue(orchestrationPlanArgumentCaptor.getValue().getArchived());
    }

    @Test
    void givenArchivedExecutedOrchestrationPlanAndLastModifiedDateExceedsThreshold_whenPurgeOrchestrationPlan_thenPlanDeleted() {
        when(orchestrationPlanRepository.findOrchestrationPlanByStateIn(any())).thenReturn(List.of(
                OrchestrationPlan.builder()
                        .state(State.EXECUTED)
                        .lastModifiedDate(Instant.now().minus(2, ChronoUnit.DAYS))
                        .archived(true)
                        .orchestrationPlanNodes(Set.of(OrchestrationPlanNode.builder()
                                .state(FAILED)
                                .build()))
                        .build()));
        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        verify(orchestrationPlanRepository).delete(any());
    }

    /**
     * JUint context doesn't contain ApplicationConversionService so this initializer class has been added to inject ConversionService into the tests context
     */
    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }

    @Test
    void givenExecutedOrchestrationPlanWithCompletedNodeAndLastModifiedIsNull_whenStartPurgeOrchestrationPlan_thenNoThingShouldHappen() {
        when(orchestrationPlanRepository.findOrchestrationPlanByState(any())).thenReturn(List.of(
                OrchestrationPlan.builder()
                        .state(State.EXECUTED)
                        .lastModifiedDate(null)
                        .archived(false)
                        .orchestrationPlanNodes(Set.of(OrchestrationPlanNode.builder()
                                .state(COMPLETED)
                                .build()))
                        .build()));

        purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        verify(orchestrationPlanRepository, never()).delete(any());
        verify(orchestrationPlanRepository, never()).save(any());
    }
}


