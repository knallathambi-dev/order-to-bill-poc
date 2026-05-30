// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture;


import com.orange.discobole.ordermanagement.ordercapture.config.EmbeddedKafka;
import com.orange.discobole.ordermanagement.ordercapture.config.EmbeddedMongo;
import com.orange.discobole.ordermanagement.ordercapture.config.WebClientConfigTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {OrderCaptureApplication.class})
@EmbeddedMongo
@EmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Import(WebClientConfigTest.class)
@TestPropertySource(properties = {
        "app.security.ssl.enabled=false",
        "management.endpoints.enabled-by-default=false",
        "spring.main.allow-bean-definition-overriding=true"
})
public @interface IntegrationTest {
}