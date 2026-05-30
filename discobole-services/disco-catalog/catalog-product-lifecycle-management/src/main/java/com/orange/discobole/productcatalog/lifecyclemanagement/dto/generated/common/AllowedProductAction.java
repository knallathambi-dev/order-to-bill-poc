package com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class AllowedProductAction {

    @JsonProperty("action")
    private CommercialOperationRef action;

    @JsonProperty("channelRef")
    private List<ChannelRef> channelRef = null;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("@baseType")
    private String baseType;

    @JsonProperty("@schemaLocation")
    private String schemaLocation;

    public AllowedProductAction() {
    }

    /** Copy constructor */
    public AllowedProductAction(AllowedProductAction other) {
        this.action = other.action;
        this.channelRef = other.channelRef != null ? new ArrayList<>(other.channelRef) : null;
        this.type = other.type;
        this.baseType = other.baseType;
        this.schemaLocation = other.schemaLocation;
    }

    /* ---------- Fluent setters ---------- */

    public AllowedProductAction action(CommercialOperationRef action) {
        this.action = action;
        return this;
    }

    public AllowedProductAction channelRef(List<ChannelRef> channelRef) {
        this.channelRef = channelRef;
        return this;
    }

    public CommercialOperationRef getAction() {
        return action;
    }

    public void setAction(CommercialOperationRef action) {
        this.action = action;
    }

    public List<ChannelRef> getChannelRef() {
        return channelRef;
    }

    public void setChannelRef(List<ChannelRef> channelRef) {
        this.channelRef = channelRef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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


    @Override
    public String toString() {
        return "AllowedProductAction{" +
                "action=" + action +
                ", channelRef=" + channelRef +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                '}';
    }

    private String toIndentedString(Object o) {
        if (o == null) return "null";
        return o.toString().replace("\n", "\n    ");
    }


}
