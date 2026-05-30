// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service.impl;

import com.orange.discobole.permission.ComponentConfiguration;
import com.orange.role.service.ComponentConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ComponentConfigurationServiceImpl implements ComponentConfigurationService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public ComponentConfigurationServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public ComponentConfiguration save(ComponentConfiguration componentConfiguration) {
        return mongoTemplate.save(componentConfiguration);
    }

    @Override
    public List<ComponentConfiguration> getComponentConfiguration() {
        return mongoTemplate.findAll(ComponentConfiguration.class);
    }

    @Override
    public ComponentConfiguration fetchComponentById(String id) {
        return mongoTemplate.findById(id, ComponentConfiguration.class);
    }

    /**
     * @param id
     */
    @Override
    public void deleteComponent(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, ComponentConfiguration.class);
    }
}
