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
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

/**
 * Describes a given characteristic of an object or entity through a name/value pair.
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicEntity {
    private String id;
    @Indexed
    private String name;
    //this is the code of the characteristic defined in the catalog
    private String valueType;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;
    private List<CharacteristicRelationshipEntity> productCharacteristicRelationships;
    private Object value;
    private String addressId;
    private String city;
    private String country;
    private String subUnitNumber;
    private String streetName;
    private String postcode;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CharacteristicEntity that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


