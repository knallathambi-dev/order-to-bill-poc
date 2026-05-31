package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorMessage(String code, String message, String reason) {
}
