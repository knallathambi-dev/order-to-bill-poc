package com.orange.discobole.eventservice.dto.generated;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The Class Event.
 * <p>
 * This dto provides info. about the launched process
 *
 * @since 1.0
 */
@ApiModel(description = "Specific Class to provide info about the launched process")
public class Event {

    @JsonProperty("eventId")
    private String eventId;

    @NotBlank
    @JsonProperty("eventType")
    private String eventType = null;

    @JsonProperty("eventTime")
    private OffsetDateTime eventTime = null;

    @JsonProperty("correlationId")
    private String correlationId = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("domain")
    private String domain = null;

    @JsonProperty("priority")
    private String priority = null;

    @JsonProperty("timeOcurred")
    private OffsetDateTime timeOcurred = null;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("analyticCharacteristic")
    private List<Object> analyticCharacteristic = null;

    @NotNull
    @JsonProperty("event")
    private Object event;

    @JsonProperty("relatedParty")
    private List<Object> relatedParty = null;

    @JsonProperty("reportingSystem")
    private Object reportingSystem = null;

    @JsonProperty("source")
    private Object source = null;

    @JsonProperty("@baseType")
    private String baseType = null;

    @JsonProperty("@schemaLocation")
    private String schemaLocation = null;

    @JsonProperty("@type")
    private String type = null;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(OffsetDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public OffsetDateTime getTimeOcurred() {
        return timeOcurred;
    }

    public void setTimeOcurred(OffsetDateTime timeOcurred) {
        this.timeOcurred = timeOcurred;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Object> getAnalyticCharacteristic() {
        return analyticCharacteristic;
    }

    public void setAnalyticCharacteristic(List<Object> analyticCharacteristic) {
        this.analyticCharacteristic = analyticCharacteristic;
    }

    public Object getEvent() {
        return event;                    
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public List<Object> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<Object> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public Object getReportingSystem() {
        return reportingSystem;
    }

    public void setReportingSystem(Object reportingSystem) {
        this.reportingSystem = reportingSystem;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
