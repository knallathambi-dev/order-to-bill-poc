package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Service(
    String id,
    String href,
    String name,
    String serviceType,
    List<ServiceCharacteristic> serviceCharacteristic,
    ServiceSpecification serviceSpecification) {
}
