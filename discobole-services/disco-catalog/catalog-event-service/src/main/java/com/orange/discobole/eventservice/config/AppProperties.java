package com.orange.discobole.eventservice.config;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application properties for swagger and others
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Configuration
@ConfigurationProperties("app")
@JsonIgnoreProperties({"$$beanFactory", "others"})
public class AppProperties {

    private String name;
    private String description;
    private String version;
    private String ownerName;
    private String ownerMail;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerMail() {
        return ownerMail;
    }

    public void setOwnerMail(final String ownerMail) {
        this.ownerMail = ownerMail;
    }

}