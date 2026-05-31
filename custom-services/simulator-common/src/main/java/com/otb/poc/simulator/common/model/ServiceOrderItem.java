package com.otb.poc.simulator.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceOrderItem(
    String id,
    Integer quantity,
    String action,
    List<ErrorMessage> errorMessage,
    Service service,
    String state) {

  @JsonProperty("@type")
  public String type() {
    return null;
  }
}
