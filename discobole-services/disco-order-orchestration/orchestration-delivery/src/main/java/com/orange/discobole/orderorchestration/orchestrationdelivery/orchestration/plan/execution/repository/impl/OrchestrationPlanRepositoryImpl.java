// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.repository.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.repository.OrchestrationPlanCustomRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrchestrationPlanRepositoryImpl implements OrchestrationPlanCustomRepository {

    private static final Logger log = LoggerFactory.getLogger(OrchestrationPlanRepositoryImpl.class);
    private final MongoTemplate mongoTemplate;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrchestrationPlanRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public OrchestrationPlan findByServiceOrder(String serviceOrderId, String serviceOrderItemId) {
        log.info("OrchestrationPlanRepositoryImpl | findByServiceOrder | find orchestration plan by serviceOrderId: {} | serviceOrderItemId: {}", serviceOrderId, serviceOrderItemId);

        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("orchestrationPlanNodes.relatedServiceOrder.id").is(serviceOrderId),
                Criteria.where("orchestrationPlanNodes.relatedServiceOrder.orderItemId").is(serviceOrderItemId)
        );

        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, OrchestrationPlan.class);


    }
}
