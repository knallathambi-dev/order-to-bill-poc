// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.crons;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.ReactiveFalloutIncidentsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.MongoTemplateWrapperService;
import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
public class PurgeFalloutScheduler {

    private final ReactiveFalloutIncidentsRepository reactiveFalloutIncidentsRepository;

    private final MongoTemplateWrapperService mongoTemplateWrapperService;

    @Value("${config.deleteFalloutThresholdDuration}")
    private Duration deleteFalloutThreshold;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public PurgeFalloutScheduler(MongoTemplateWrapperService mongoTemplateWrapperService, ReactiveFalloutIncidentsRepository reactiveFalloutIncidentsRepository) {
        this.mongoTemplateWrapperService = mongoTemplateWrapperService;
        this.reactiveFalloutIncidentsRepository = reactiveFalloutIncidentsRepository;
    }

    @Scheduled(cron = "${config.startPurgeCronExpression}")
    @Transactional
    public void startPurgeFalloutIncident() {
        log.debug("Purge Fallout Plan started");

        final int CHUNK_SIZE = 100;

        Instant now = Instant.now();

        long deleteEventsBeforeEpochMillis = now.toEpochMilli() - deleteFalloutThreshold.toMillis();

        Flux<FalloutIncident> falloutIncidentFlux = reactiveFalloutIncidentsRepository.findAllByStateInAndModificationDateIsBefore(List.of(State.COMPLETED, State.CANCELED), Instant.ofEpochMilli(deleteEventsBeforeEpochMillis).atOffset(ZoneOffset.UTC));

        falloutIncidentFlux
                .buffer(CHUNK_SIZE)
                .flatMap(falloutIncidents ->
                    Flux.fromIterable(falloutIncidents)
                            .filter(fallout -> fallout.getModificationDate() != null)
                            .flatMap(fallout -> {
                                Query queryByFalloutId = Query.query(Criteria.where("_id").is(fallout.getId()));
                                mongoTemplateWrapperService.delete(queryByFalloutId, ProcessFlow.class);
                                mongoTemplateWrapperService.delete(queryByFalloutId, DiscoTaskFlow.class);

                                return reactiveFalloutIncidentsRepository.delete(fallout);
                            })
                )
                .doOnError(throwable -> log.debug("Error purging events: {}", throwable.getMessage()))
                .blockLast();

        log.debug("Purge Fallout Plan finished");
    }

}
