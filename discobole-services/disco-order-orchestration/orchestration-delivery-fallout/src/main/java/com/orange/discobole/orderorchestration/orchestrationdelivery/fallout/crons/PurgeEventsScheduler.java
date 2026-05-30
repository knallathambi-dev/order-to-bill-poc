
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.crons;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutTechnicalException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.ReactiveEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class PurgeEventsScheduler {

    private final ReactiveEventsRepository reactiveEventsRepository;

    @Value("${config.deleteEventsThresholdDuration}")
    private Duration deleteEventsThresholdDuration;

    @Scheduled(cron = "${config.eventPurgeCronExpression}")
    public void startPurgeEvents() {
        log.info("Purge events started");

        Instant now = Instant.now();

        final int CHUNK_SIZE = 100;

        long deleteEventsBeforeEpochMillis = now.toEpochMilli() - deleteEventsThresholdDuration.toMillis();


        Flux<EventEntity> eventEntityFlux = reactiveEventsRepository.findEventsByTimestampIsBefore(deleteEventsBeforeEpochMillis);

        eventEntityFlux
                .buffer(CHUNK_SIZE)
                .flatMap(reactiveEventsRepository::deleteAll)
                .doOnError(throwable -> log.debug("Error purging events: {}", throwable.getMessage()))
                .subscribe(
                        unused -> {},
                        throwable -> {
                            throw new FalloutTechnicalException("Failed to purge events");
                        }
                );

    }
}
