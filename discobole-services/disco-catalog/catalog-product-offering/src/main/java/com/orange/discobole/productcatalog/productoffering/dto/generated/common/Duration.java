// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "An amount in a given unit")
@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-07-30T16:59:05.795+05:30")
public class Duration {
    @JsonProperty("amount")
    private Integer amount = null;

    @JsonProperty("units")
    private String units = null;

    public Duration amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Numeric value in a given unit
     *
     * @return amount
     **/
    @Schema(description = "Numeric value in a given unit")
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Duration units(String units) {
        this.units = units;
        return this;
    }

    /**
     * Unit
     *
     * @return units
     **/
    @Schema(description = "Unit")
    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Duration duration = (Duration) o;
        return Objects.equals(this.amount, duration.amount) && Objects.equals(this.units, duration.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, units);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Duration {\n");

        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb.append("    units: ").append(toIndentedString(units)).append("\n");
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

