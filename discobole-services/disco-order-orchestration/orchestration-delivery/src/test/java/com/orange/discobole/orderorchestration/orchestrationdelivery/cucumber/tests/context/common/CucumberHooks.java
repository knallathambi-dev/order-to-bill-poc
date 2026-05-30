// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.common;

import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;

import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CucumberHooks {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    OrchestrationPlanRepository orchestrationPlanRepository;

    @Before
    public void clearDatabase() {
        mongoTemplate.getDb().drop();
        orchestrationPlanRepository.deleteAll();
        eventRepository.deleteAll();
        CommonStepDefinitions.fixedInstant = null;
    }
}
