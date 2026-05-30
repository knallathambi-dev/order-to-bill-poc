// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service;

import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoTemplateWrapperService {

    private final MongoTemplate mongoTemplate;

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public MongoTemplateWrapperService(MongoTemplate mongoTemplate, OrchestrationPlanRepository orchestrationPlanRepository) {
        this.mongoTemplate = mongoTemplate;
        this.orchestrationPlanRepository = orchestrationPlanRepository;
    }

    public <T> T findOne(Query query, Class<T> entityClass) throws CoodDBException {
        try {
            return mongoTemplate.findOne(query, entityClass);
        } catch (Exception e) {
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while fetching orchestration plan");
        }
    }

    public long count(Query query, Class<?> entityClass) throws CoodDBException {
        try {
            return mongoTemplate.count(query, entityClass);
        } catch (Exception e) {
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while counting orchestration plan");
        }
    }

    public <T> List<T> find(Query query, Class<T> entityClass) throws CoodDBException {
        try {
            return mongoTemplate.find(query, entityClass);
        } catch (Exception e) {
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while finding orchestration plans with query");
        }
    }

    public OrchestrationPlan save(OrchestrationPlan entity) throws CoodDBException {
        try {
            return orchestrationPlanRepository.save(entity);
        } catch (Exception e) {
            throw new CoodRecoverableAndNonRetryableException(new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while updating orchestration plan"));
        }
    }
}
