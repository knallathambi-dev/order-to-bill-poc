// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.OrchestrationPlanHrefSetter;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrchestrationPlanHrefSetterTest {
    private static final String ORCHESTRATION_PLAN_URI_WITH_ID = "/orchestrationPlan/%s";
    private static final String ORCHESTRATION_PLAN_URI_WITH_ID_AND_FIELDS = ORCHESTRATION_PLAN_URI_WITH_ID + "?fields=%s";

    @Test
    void givenIdAndFields_whenGetHref_thenReturnURIString() {
        String id = RandomString.make(15);
        String fields = "id,receivedDate,relatedParty";
        String expectedResult = ORCHESTRATION_PLAN_URI_WITH_ID_AND_FIELDS.formatted(id, fields.replaceAll(",", "%2C"));
        String actualResult = OrchestrationPlanHrefSetter.generateHref(id, fields);
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void givenId_whenGetHref_thenReturnURIStringWithoutFields() {
        String id = RandomString.make(15);
        String fields = null;
        String expectedResult = ORCHESTRATION_PLAN_URI_WITH_ID.formatted(id);
        String actualResult = OrchestrationPlanHrefSetter.generateHref(id, fields);
        assertEquals(expectedResult, actualResult);
    }


    @Test
    void givenOrchestrationPlanList_whenSetHrefInList_thenReturnURIInsideEachOrchestrationPlan() {

        List<OrchestrationPlan> orchestrationPlanList = new ArrayList<>();
        OrchestrationPlan expectedOrchestrationPlan1 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan2);
        OrchestrationPlan expectedOrchestrationPlan3 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan3);
        OrchestrationPlan expectedOrchestrationPlan4 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan4);

        OrchestrationPlanHrefSetter.setHrefForOrchestrationPlans(orchestrationPlanList, null);

        Set<String> ids = new HashSet<>(orchestrationPlanList.stream().map(OrchestrationPlan::getId).toList());

        orchestrationPlanList.forEach(orchestrationPlan -> {
            Assertions.assertThat(orchestrationPlan.getHref())
                    .isNotBlank()
                    .isNotEmpty()
                    .isEqualTo(ORCHESTRATION_PLAN_URI_WITH_ID.formatted(orchestrationPlan.getId()));
            assertTrue(ids.contains(orchestrationPlan.getId()));
        });
    }

    @Test
    void givenOrchestrationPlanListAndFields_whenSetHrefInList_thenReturnURIInsideEachOrchestrationPlan() {

        List<OrchestrationPlan> orchestrationPlanList = new ArrayList<>();
        OrchestrationPlan expectedOrchestrationPlan1 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan1);
        OrchestrationPlan expectedOrchestrationPlan2 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan2);
        OrchestrationPlan expectedOrchestrationPlan3 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan3);
        OrchestrationPlan expectedOrchestrationPlan4 = OrchestrationPlan.builder().id(RandomString.make(15)).build();
        orchestrationPlanList.add(expectedOrchestrationPlan4);
        String fields = "id,receivedDate,relatedParty";

        OrchestrationPlanHrefSetter.setHrefForOrchestrationPlans(orchestrationPlanList, fields);

        Set<String> ids = new HashSet<>(orchestrationPlanList.stream().map(OrchestrationPlan::getId).toList());

        orchestrationPlanList.forEach(orchestrationPlan -> {
            Assertions.assertThat(orchestrationPlan.getHref())
                    .isNotBlank()
                    .isNotEmpty()
                    .isEqualTo(ORCHESTRATION_PLAN_URI_WITH_ID_AND_FIELDS.formatted(orchestrationPlan.getId(), fields.replaceAll(",", "%2C")));
            assertTrue(ids.contains(orchestrationPlan.getId()));
        });
    }
}

