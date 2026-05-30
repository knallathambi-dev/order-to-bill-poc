// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;



/**
 * application properties to manage Command and Query controllers
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Configuration
@ConfigurationProperties("cqrs")
public class CQRSProperties {

    private boolean commandEnabled = true;
    private boolean queryEnabled = true;
    private String commandBaseUrl;
    private String queryBaseUrl;

    @PostConstruct
    private void init() {
        if (!commandEnabled && null == commandBaseUrl)
            throw new RuntimeException("cqrs.command-base-url is mandatory if cqrs.command-enabled=false");
        if (!queryEnabled && null == queryBaseUrl)
            throw new RuntimeException("cqrs.query-base-url is mandatory if cqrs.query-enabled=false");
    }

    public boolean isCommandEnabled() {
        return commandEnabled;
    }

    public void setCommandEnabled(boolean commandEnabled) {
        this.commandEnabled = commandEnabled;
    }

    public boolean isQueryEnabled() {
        return queryEnabled;
    }

    public void setQueryEnabled(boolean queryEnabled) {
        this.queryEnabled = queryEnabled;
    }

    public String getCommandBaseUrl() {
        return commandBaseUrl;
    }

    public void setCommandBaseUrl(String commandBaseUrl) {
        this.commandBaseUrl = commandBaseUrl;
    }

    public String getQueryBaseUrl() {
        return queryBaseUrl;
    }

    public void setQueryBaseUrl(String queryBaseUrl) {
        this.queryBaseUrl = queryBaseUrl;
    }

}