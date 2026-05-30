// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.disco.ordermanagement.configs;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WiremockConfig {
    @Value("${wiremock.mapping.path}")
    private String wireMockPath;

    @Value("${wiremock.port}")
    private Integer wireMockPort;

    @Value("${wiremock.enable-request-journal:true}")
    private Boolean enableRequestJournal;

    @Value("${wiremock.max-request-journal-entries}")
    private Integer maxRequestJournalEntries;

    @Bean
    Options wireMockServer() {
        WireMockConfiguration wireMockConfiguration = WireMockSpring.options().port(wireMockPort)
                .usingFilesUnderClasspath(wireMockPath)
                .extensions(new ResponseTemplateTransformer(true))
                .maxRequestJournalEntries(maxRequestJournalEntries);
        if (Boolean.FALSE.equals(enableRequestJournal)) {
            wireMockConfiguration.disableRequestJournal();
        }
        return wireMockConfiguration;
    }
}
