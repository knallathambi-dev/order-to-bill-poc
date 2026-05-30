// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service.impl;

import com.orange.discobole.permission.FunctionConfiguration;
import com.orange.role.service.FunctionConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionConfigurationServiceImpl implements FunctionConfigurationService {
    private MongoTemplate mongoTemplate;

    @Autowired
    public FunctionConfigurationServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public FunctionConfiguration save(FunctionConfiguration functionConfiguration) {
        return mongoTemplate.save(functionConfiguration);
    }

    @Override
    public List<FunctionConfiguration> getFunctionConfiguration() {
        return mongoTemplate.findAll(FunctionConfiguration.class);
    }

    @Override
    public FunctionConfiguration fetchFunctionById(String id) {
        return mongoTemplate.findById(id, FunctionConfiguration.class);
    }

    /**
     * @param id
     */
    @Override
    public void deleteFunction(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, FunctionConfiguration.class);

    }
}
