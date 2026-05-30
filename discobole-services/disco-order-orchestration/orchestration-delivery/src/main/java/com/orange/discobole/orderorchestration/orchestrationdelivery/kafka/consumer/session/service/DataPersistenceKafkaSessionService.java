// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service;

import com.orange.discobole.orderorchestration.exception.model.CoodNoSessionFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Service class for managing Kafka consumer sessions and persisting data related to orchestration plans.
 * This service is scoped to a Kafka session using the @KafkaSessionScope annotation.
 */
@KafkaSessionScope
@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceKafkaSessionService {

    private final DataPersistenceKafkaService dataPersistenceKafkaService;

    /**
     * Retrieves the current Kafka consumer session.
     *
     * @return the current KafkaConsumerSession
     * @throws CoodNoSessionFoundException if no request attributes are found
     */
    public KafkaConsumerSession getSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new CoodNoSessionFoundException();
        }
        return (KafkaConsumerSession) requestAttributes.getAttribute("kafkaConsumerSession", RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * Adds a node state change to the current Kafka consumer session.
     *
     * @param node the orchestration plan node
     */
    public void addNodeStateChange(OrchestrationPlanNode node) {
        getSession().addNodeStateChange(node);
    }

    /**
     * Adds a node related product change to the current Kafka consumer session.
     *
     * @param node the orchestration plan node
     */
    public void addNodeRelatedProductChange(OrchestrationPlanNode node) {
        getSession().addNodeRelatedProductChange(node);
    }


    /**
     * Persists all changes recorded in the current Kafka consumer session to the database.
     * This method is transactional, ensuring all changes are committed atomically.
     */
    @Transactional
    public void persistAll() {
        KafkaConsumerSession session = getSession();

        dataPersistenceKafkaService.persistRelatedProductChange(session);

        dataPersistenceKafkaService.persistNodesStateChange(session);

        dataPersistenceKafkaService.persistPlan(session);

        session.clear(); // Clear the session after persisting
    }


    public void setPlanToBePersisted(OrchestrationPlan orchestrationPlan) {
        getSession().setPlanToBePersisted(orchestrationPlan);
    }
}
