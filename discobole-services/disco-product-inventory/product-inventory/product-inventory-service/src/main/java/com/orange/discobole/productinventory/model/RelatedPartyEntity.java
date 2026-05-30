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

import java.util.Objects;


@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedPartyEntity {
    @Indexed
    private String id;
    @Indexed
    private String name;
    private String href;
    private String role;
    @JsonProperty("@referredType")
    @JsonAlias("atReferredType")
    private String atReferredType;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType; //refers to the @type of the underlining PartyOrPartyRole (PartyRef or PartyRoleRef) not to the relatedParty type since relatedParty @type is always RelatedPartyRefOrPartyRoleRef.
    private String partyId;
    private String partyName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelatedPartyEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id) && Objects.equals(atReferredType, that.atReferredType);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
