// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.FalloutIncidentHrefSetter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class FalloutIncident {

    @Id
    @JsonProperty("id")
    @NotNull
    private String id;

    @CreatedDate
    private OffsetDateTime creationDate;

    @LastModifiedDate
    private OffsetDateTime modificationDate;

    private List<RelatedEntity> relatedEntity;

    private RelatedParty relatedParty;

    private State state;

    @JsonProperty("@Type")
    private String atType = FalloutIncident.class.getSimpleName();

    private String href;

    private Resolution resolution;

    private ErrorMessage errorMessage;

    public String getHref() {
        if (id != null) {
            return FalloutIncidentHrefSetter.generateHref(id, null);
        }
        return href;
    }
}
