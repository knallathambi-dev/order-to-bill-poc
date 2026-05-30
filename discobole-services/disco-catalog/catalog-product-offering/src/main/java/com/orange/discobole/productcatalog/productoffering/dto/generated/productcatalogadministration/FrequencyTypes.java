// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.productcatalogadministration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class is used to identify frequency types associated with time. These can be day(s), months(s) etc. which are to be used in the product catalog while administering/creating product catalog entities.  **Polymorphism**    Parent: productCatalogAdministration    Discriminator: @type
 */

@Schema(name = "frequencyTypes", description = "This class is used to identify frequency types associated with time. These can be day(s), months(s) etc. which are to be used in the product catalog while administering/creating product catalog entities.  **Polymorphism**    Parent: productCatalogAdministration    Discriminator: @type")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-05-07T10:03:17.901452300+05:30[Asia/Calcutta]")
public class FrequencyTypes extends ProductCatalogAdministration {

  private String frequencyCode;

  private String frequencyLabel;

  private Integer numberOfDays;

  public FrequencyTypes frequencyCode(String frequencyCode) {
    this.frequencyCode = frequencyCode;
    return this;
  }

  /**
   * Get frequencyCode
   * @return frequencyCode
  */
  @NotNull 
  @Schema(name = "frequencyCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("frequencyCode")
  public String getFrequencyCode() {
    return frequencyCode;
  }

  public void setFrequencyCode(String frequencyCode) {
    this.frequencyCode = frequencyCode;
  }

  public FrequencyTypes frequencyLabel(String frequencyLabel) {
    this.frequencyLabel = frequencyLabel;
    return this;
  }

  /**
   * Get frequencyLabel
   * @return frequencyLabel
  */
  @NotNull 
  @Schema(name = "frequencyLabel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("frequencyLabel")
  public String getFrequencyLabel() {
    return frequencyLabel;
  }

  public void setFrequencyLabel(String frequencyLabel) {
    this.frequencyLabel = frequencyLabel;
  }

  public FrequencyTypes numberOfDays(Integer numberOfDays) {
    this.numberOfDays = numberOfDays;
    return this;
  }

  /**
   * Get numberOfDays
   * @return numberOfDays
  */
  @NotNull 
  @Schema(name = "numberOfDays", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("numberOfDays")
  public Integer getNumberOfDays() {
    return numberOfDays;
  }

  public void setNumberOfDays(Integer numberOfDays) {
    this.numberOfDays = numberOfDays;
  }


  public FrequencyTypes id(String id) {
    super.id(id);
    return this;
  }

  public FrequencyTypes atType(String atType) {
    super.atType(atType);
    return this;
  }

  public FrequencyTypes atBaseType(String atBaseType) {
    super.atBaseType(atBaseType);
    return this;
  }

  public FrequencyTypes schemaLocation(String schemaLocation) {
    super.schemaLocation(schemaLocation);
    return this;
  }

  public FrequencyTypes href(String href) {
    super.href(href);
    return this;
  }

  public FrequencyTypes lastUpdate(LocalDateTime lastUpdate) {
    super.lastUpdate(lastUpdate);
    return this;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FrequencyTypes frequencyTypes = (FrequencyTypes) o;
    return Objects.equals(this.frequencyCode, frequencyTypes.frequencyCode) &&
        Objects.equals(this.frequencyLabel, frequencyTypes.frequencyLabel) &&
        Objects.equals(this.numberOfDays, frequencyTypes.numberOfDays) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(frequencyCode, frequencyLabel, numberOfDays, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FrequencyTypes {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    frequencyCode: ").append(toIndentedString(frequencyCode)).append("\n");
    sb.append("    frequencyLabel: ").append(toIndentedString(frequencyLabel)).append("\n");
    sb.append("    numberOfDays: ").append(toIndentedString(numberOfDays)).append("\n");
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

