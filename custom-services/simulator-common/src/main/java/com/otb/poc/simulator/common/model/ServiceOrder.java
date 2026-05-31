package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceOrder(
    String id,
    String href,
    String category,
    String description,
    Instant requestedCompletionDate,
    Instant requestedStartDate,
    Instant orderDate,
    Instant completionDate,
    String state,
    List<ServiceOrderItem> serviceOrderItem) {

  public String externalId() {
    return id;
  }
}
