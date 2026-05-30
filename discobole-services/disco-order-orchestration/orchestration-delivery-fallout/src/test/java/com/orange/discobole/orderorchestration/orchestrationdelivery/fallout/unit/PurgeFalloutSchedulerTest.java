// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.unit;

import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.crons.PurgeFalloutScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.ReactiveFalloutIncidentsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.MongoTemplateWrapperService;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurgeFalloutSchedulerTest {

    @Mock
    private FalloutRepository falloutRepository;

    @Mock
    private MongoTemplateWrapperService mongoTemplateWrapperService;

    @Mock
    private ReactiveFalloutIncidentsRepository reactiveFalloutIncidentsRepository;

    @InjectMocks
    private PurgeFalloutScheduler purgeFalloutScheduler;

    @Captor
    private ArgumentCaptor<FalloutIncident> falloutIncidentCaptor;

    @Value("${config.deleteFalloutThresholdDuration}")
    private final Duration deleteFalloutThreshold = Duration.ofDays(7); // Default for test purposes

    @BeforeEach
    void setUp() {
        // Inject the deleteFalloutThreshold value into the class under test
        ReflectionTestUtils.setField(purgeFalloutScheduler, "deleteFalloutThreshold", deleteFalloutThreshold);
    }

    @Test
    void givenFalloutEligibleForDeletion_whenStartPurgeFalloutIncident_thenFalloutIsDeleted() {
        // Given
        FalloutIncident incident = new FalloutIncident();
        incident.setId("incident1");
        incident.setState(State.COMPLETED);
        incident.setModificationDate(OffsetDateTime.now().minusDays(10)); // Older than threshold

        when(reactiveFalloutIncidentsRepository.findAllByStateInAndModificationDateIsBefore(any(), any())).thenReturn(Flux.just(incident));
        when(reactiveFalloutIncidentsRepository.delete(any())).thenReturn(Mono.empty());

        // When
        purgeFalloutScheduler.startPurgeFalloutIncident();

        // Then
        verify(reactiveFalloutIncidentsRepository, times(1)).delete(falloutIncidentCaptor.capture());

        verify(mongoTemplateWrapperService, times(1)).delete(any(), eq(ProcessFlow.class));
        verify(mongoTemplateWrapperService, times(1)).delete(any(), eq(DiscoTaskFlow.class));

        FalloutIncident deletedIncident = falloutIncidentCaptor.getValue();
        assertEquals("incident1", deletedIncident.getId());
    }

    @Test
    void givenFalloutNotEligibleForDeletion_whenStartPurgeFalloutIncident_thenFalloutIsNotDeleted() {
        // Given
        FalloutIncident incident = new FalloutIncident();
        incident.setId("incident1");
        incident.setState(State.COMPLETED);
        incident.setModificationDate(OffsetDateTime.now().minusDays(5)); // Within the threshold

        when(reactiveFalloutIncidentsRepository.findAllByStateInAndModificationDateIsBefore(any(), any())).thenReturn(Flux.empty());

        // When
        purgeFalloutScheduler.startPurgeFalloutIncident();

        // Then
        verify(reactiveFalloutIncidentsRepository, never()).delete(any());
        verify(mongoTemplateWrapperService, never()).delete(any(), eq(ProcessFlow.class));
        verify(mongoTemplateWrapperService, never()).delete(any(), eq(DiscoTaskFlow.class));
    }

    @Test
    void givenFalloutWithNullModificationDate_whenStartPurgeFalloutIncident_thenFalloutIsNotDeleted() {
        // Given
        FalloutIncident incident = new FalloutIncident();
        incident.setId("incident1");
        incident.setState(State.COMPLETED);
        incident.setModificationDate(null); // Null modification date

        when(reactiveFalloutIncidentsRepository.findAllByStateInAndModificationDateIsBefore(any(), any())).thenReturn(Flux.just(incident));

        // When
        purgeFalloutScheduler.startPurgeFalloutIncident();

        // Then
        verify(falloutRepository, never()).delete(any());
        verify(mongoTemplateWrapperService, never()).delete(any(), eq(ProcessFlow.class));
        verify(mongoTemplateWrapperService, never()).delete(any(), eq(DiscoTaskFlow.class));
    }
}