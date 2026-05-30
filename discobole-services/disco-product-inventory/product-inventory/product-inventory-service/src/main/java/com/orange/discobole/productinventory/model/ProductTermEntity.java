// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;


/**
 * Description of a productTerm linked to this product. This represent a commitment with a duration
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTermEntity {
    private String description;
    private String name;
    private DurationEntity duration;
    private TimePeriodEntity validFor;
    private String baseType;
    private String schemaLocation;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductTermEntity that)) {
            return false;
        }
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


