// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FalloutFilter {
    private String id;
    private String state;
    private String relatedPartyId;
    private String relatedPartyRole;
    private String relatedPartyName;
    private String relatedEntityRole;
    private String relatedEntityId;
    private String relatedEntityState;
    private String fields;
    private OffsetDateTime creationDateGte;
    private OffsetDateTime creationDateLte;
    private OffsetDateTime lastModifiedDateGte;
    private OffsetDateTime lastModifiedDateLte;
    private Integer offset;
    private Integer limit;

}
