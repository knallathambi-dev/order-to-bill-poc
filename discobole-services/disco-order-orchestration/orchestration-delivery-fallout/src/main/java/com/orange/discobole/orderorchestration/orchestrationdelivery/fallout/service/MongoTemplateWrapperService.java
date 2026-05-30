// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoTemplateWrapperService {

    private final MongoTemplate mongoTemplate;

    private final FalloutRepository falloutRepository;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public MongoTemplateWrapperService(MongoTemplate mongoTemplate, FalloutRepository falloutRepository) {
        this.mongoTemplate = mongoTemplate;
        this.falloutRepository = falloutRepository;
    }

    public <T> T findOne(Query query, Class<T> entityClass) throws FalloutDBException {
        try {
            return mongoTemplate.findOne(query, entityClass);
        } catch (Exception e) {
            throw new FalloutDBException(e);
        }
    }

    public long count(Query query, Class<?> entityClass) throws FalloutDBException {
        try {
            return mongoTemplate.count(query, entityClass);
        } catch (Exception e) {
            throw new FalloutDBException(e);
        }
    }

    public <T> List<T> find(Query query, Class<T> entityClass) throws FalloutDBException {
        try {
            return mongoTemplate.find(query, entityClass);
        } catch (Exception e) {
            throw new FalloutDBException(e);
        }
    }

    public <T> void delete(Query query, Class<T> entityClass) throws FalloutDBException {
        try {
            mongoTemplate.remove(query, entityClass);
        } catch (Exception e) {
            throw new FalloutDBException(e);
        }
    }

    public FalloutIncident save(FalloutIncident entity) throws FalloutDBException {
        try {
            return falloutRepository.save(entity);
        } catch (Exception e) {
            throw new FalloutDBException(e);
        }
    }
}
