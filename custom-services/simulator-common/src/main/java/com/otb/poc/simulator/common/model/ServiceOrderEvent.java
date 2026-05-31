package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceOrderEvent(
    String eventId,
    Instant eventTime,
    String eventType,
    Payload event) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Payload(ServiceOrder serviceOrder) {
  }
}
