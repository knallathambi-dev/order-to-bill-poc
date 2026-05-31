package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceCharacteristic(
    String name,
    Object value,
    @JsonProperty("@type") String type,
    String valueType) {
}
