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
 * Related Entity reference. A related place defines a place described by reference or by value linked to a specific entity. The polymorphic attributes @type, @schemaLocation &amp; @referredType are related to the place entity and not the RelatedPlaceRefOrValue class itself
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedPlaceRefOrValueEntity {
    private String id;
    private String href;
    private String name;
    private String role;
    //TODO add length validation in dto and entity length: 0..256
    // same for any referredType
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;
    //TODO add length validation in dto and entity length: 0..256
    // same for any type
    // class of the target place
    // this entity is managed by either TMF673, TMF674 or TMF675.
    // This property MUST be provided. Typically "Place", "GeographicalSite", ...
    @JsonProperty("@referredType")
    @JsonAlias("atReferredType")
    private String atReferredType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelatedPlaceRefOrValueEntity that)) {
            return false;
        }
        return Objects.equals(role, that.role) && Objects.equals(atReferredType, that.atReferredType);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


