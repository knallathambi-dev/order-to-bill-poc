// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.context.mocked.eventPublisher;

import io.cucumber.java.AfterAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContextManager;

@Slf4j
public class CucumberContextCleanupHook {

    private static final TestContextManager testContextManager = new TestContextManager(CucumberTest.class);

    @AfterAll
    public static void cleanUpContext() {
        log.info("Destroying Spring Context for Cucumber Test: {}", CucumberTest.class.getSimpleName());
        try {
            // Mark the application context as dirty to force cleanup
            testContextManager.getTestContext().markApplicationContextDirty(org.springframework.test.annotation.DirtiesContext.HierarchyMode.EXHAUSTIVE);
        } catch (Exception e) {
            log.error("Error while cleaning up Spring context", e);
        }
    }
}