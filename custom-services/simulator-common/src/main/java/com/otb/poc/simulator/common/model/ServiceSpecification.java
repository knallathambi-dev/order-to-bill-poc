package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceSpecification(String id, String href, String name) {
}
