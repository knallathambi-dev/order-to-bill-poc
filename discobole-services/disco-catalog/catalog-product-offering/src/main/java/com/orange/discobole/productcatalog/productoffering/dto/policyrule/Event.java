// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
package com.orange.discobole.productcatalog.productoffering.dto.policyrule;


import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.EntityRef;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * event with common attributes.
 */

@Schema(name = "Event", description = "event with common attributes.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-08T11:11:10.846104900+05:30[Asia/Calcutta]")
public class Event {

    private String correlationId;

    private String description;

    private String domain;

    private String eventId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventTime;

    private String eventType;

    private String priority;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timeOcurred;

    private String title;

    @Valid
    private List<@Valid Characteristic> analyticCharacteristic;

    private Object event;

    @Valid
    private List<@Valid RelatedParty> relatedParty;

    private EntityRef reportingSystem;

    private EntityRef source;

    private String atBaseType;

    private URI atSchemaLocation;

    private String atType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastUpdate;

    public Event correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    /**
     * The correlation id for this event.
     * @return correlationId
     */

    @Schema(name = "correlationId", description = "The correlation id for this event.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("correlationId")
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Event description(String description) {
        this.description = description;
        return this;
    }

    /**
     * An explnatory of the event.
     * @return description
     */

    @Schema(name = "description", description = "An explnatory of the event.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Event domain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * The domain of the event.
     * @return domain
     */

    @Schema(name = "domain", description = "The domain of the event.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Event eventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    /**
     * The identifier of the notification.
     * @return eventId
     */

    @Schema(name = "eventId", description = "The identifier of the notification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("eventId")
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Event eventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    /**
     * Time of the event occurrence.
     * @return eventTime
     */
    @Valid
    @Schema(name = "eventTime", description = "Time of the event occurrence.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("eventTime")
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public Event eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * The type of the notification.
     * @return eventType
     */

    @Schema(name = "eventType", description = "The type of the notification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("eventType")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Event priority(String priority) {
        this.priority = priority;
        return this;
    }

    /**
     * A priority.
     * @return priority
     */

    @Schema(name = "priority", description = "A priority.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("priority")
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Event timeOcurred(LocalDateTime timeOcurred) {
        this.timeOcurred = timeOcurred;
        return this;
    }

    /**
     * The time the event occured.
     * @return timeOcurred
     */
    @Valid
    @Schema(name = "timeOcurred", description = "The time the event occured.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("timeOcurred")
    public LocalDateTime getTimeOcurred() {
        return timeOcurred;
    }

    public void setTimeOcurred(LocalDateTime timeOcurred) {
        this.timeOcurred = timeOcurred;
    }

    public Event title(String title) {
        this.title = title;
        return this;
    }

    /**
     * The title of the event.
     * @return title
     */

    @Schema(name = "title", description = "The title of the event.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Event analyticCharacteristic(List<@Valid Characteristic> analyticCharacteristic) {
        this.analyticCharacteristic = analyticCharacteristic;
        return this;
    }

    public Event addAnalyticCharacteristicItem(Characteristic analyticCharacteristicItem) {
        if (this.analyticCharacteristic == null) {
            this.analyticCharacteristic = new ArrayList<>();
        }
        this.analyticCharacteristic.add(analyticCharacteristicItem);
        return this;
    }

    /**
     * Get analyticCharacteristic
     * @return analyticCharacteristic
     */
    @Valid
    @Schema(name = "analyticCharacteristic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("analyticCharacteristic")
    public List<@Valid Characteristic> getAnalyticCharacteristic() {
        return analyticCharacteristic;
    }

    public void setAnalyticCharacteristic(List<@Valid Characteristic> analyticCharacteristic) {
        this.analyticCharacteristic = analyticCharacteristic;
    }

    public Event event(Object event) {
        this.event = event;
        return this;
    }

    /**
     * Get event
     * @return event
     */

    @Schema(name = "event", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("event")
    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public Event relatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    public Event addRelatedPartyItem(RelatedParty relatedPartyItem) {
        if (this.relatedParty == null) {
            this.relatedParty = new ArrayList<>();
        }
        this.relatedParty.add(relatedPartyItem);
        return this;
    }

    /**
     * Get relatedParty
     * @return relatedParty
     */
    @Valid
    @Schema(name = "relatedParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedParty")
    public List<@Valid RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<@Valid RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public Event reportingSystem(EntityRef reportingSystem) {
        this.reportingSystem = reportingSystem;
        return this;
    }

    /**
     * Get reportingSystem
     * @return reportingSystem
     */
    @Valid
    @Schema(name = "reportingSystem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("reportingSystem")
    public EntityRef getReportingSystem() {
        return reportingSystem;
    }

    public void setReportingSystem(EntityRef reportingSystem) {
        this.reportingSystem = reportingSystem;
    }

    public Event source(EntityRef source) {
        this.source = source;
        return this;
    }

    /**
     * Get source
     * @return source
     */
    @Valid
    @Schema(name = "source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("source")
    public EntityRef getSource() {
        return source;
    }

    public void setSource(EntityRef source) {
        this.source = source;
    }

    public Event atBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
        return this;
    }

    /**
     * When sub-classing, this defines the super-class
     * @return atBaseType
     */

    @Schema(name = "@baseType", description = "When sub-classing, this defines the super-class", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("@baseType")
    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }

    public Event atSchemaLocation(URI atSchemaLocation) {
        this.atSchemaLocation = atSchemaLocation;
        return this;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     * @return atSchemaLocation
     */
    @Valid
    @Schema(name = "@schemaLocation", description = "A URI to a JSON-Schema file that defines additional attributes and relationships", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("@schemaLocation")
    public URI getAtSchemaLocation() {
        return atSchemaLocation;
    }

    public void setAtSchemaLocation(URI atSchemaLocation) {
        this.atSchemaLocation = atSchemaLocation;
    }

    public Event atType(String atType) {
        this.atType = atType;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class entity name
     * @return atType
     */

    @Schema(name = "@type", description = "When sub-classing, this defines the sub-class entity name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("@type")
    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
    }

    public Event lastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    /**
     * Get lastUpdate
     * @return lastUpdate
     */
    @Valid
    @Schema(name = "lastUpdate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("lastUpdate")
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(this.correlationId, event.correlationId) &&
                Objects.equals(this.description, event.description) &&
                Objects.equals(this.domain, event.domain) &&
                Objects.equals(this.eventId, event.eventId) &&
                Objects.equals(this.eventTime, event.eventTime) &&
                Objects.equals(this.eventType, event.eventType) &&
                Objects.equals(this.priority, event.priority) &&
                Objects.equals(this.timeOcurred, event.timeOcurred) &&
                Objects.equals(this.title, event.title) &&
                Objects.equals(this.analyticCharacteristic, event.analyticCharacteristic) &&
                Objects.equals(this.event, event.event) &&
                Objects.equals(this.relatedParty, event.relatedParty) &&
                Objects.equals(this.reportingSystem, event.reportingSystem) &&
                Objects.equals(this.source, event.source) &&
                Objects.equals(this.atBaseType, event.atBaseType) &&
                Objects.equals(this.atSchemaLocation, event.atSchemaLocation) &&
                Objects.equals(this.atType, event.atType) &&
                Objects.equals(this.lastUpdate, event.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correlationId, description, domain, eventId, eventTime, eventType, priority, timeOcurred, title, analyticCharacteristic, event, relatedParty, reportingSystem, source, atBaseType, atSchemaLocation, atType, lastUpdate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Event {\n");
        sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
        sb.append("    eventId: ").append(toIndentedString(eventId)).append("\n");
        sb.append("    eventTime: ").append(toIndentedString(eventTime)).append("\n");
        sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
        sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
        sb.append("    timeOcurred: ").append(toIndentedString(timeOcurred)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    analyticCharacteristic: ").append(toIndentedString(analyticCharacteristic)).append("\n");
        sb.append("    event: ").append(toIndentedString(event)).append("\n");
        sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
        sb.append("    reportingSystem: ").append(toIndentedString(reportingSystem)).append("\n");
        sb.append("    source: ").append(toIndentedString(source)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
        sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

