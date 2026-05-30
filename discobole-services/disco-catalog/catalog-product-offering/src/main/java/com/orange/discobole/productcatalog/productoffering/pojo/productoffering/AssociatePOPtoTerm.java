// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;


public class AssociatePOPtoTerm {
    @JsonProperty("duration")
    @NotNull
    private Duration duration = null;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public AssociatePOPtoTerm duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssociatePOPtoTerm that = (AssociatePOPtoTerm) o;
        return Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(duration);
    }

    @Override
    public String toString() {
        return "AssociatePOPtoTerm{" +
                "duration=" + duration +
                '}';
    }
}
