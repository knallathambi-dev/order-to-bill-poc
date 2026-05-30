// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.resource.inventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivationFeature {
    private List<Characteristic> featureCharacteristic;
    private String name;
    private String id;
    private Boolean isBundle;
    private Boolean isEnabled;
    private List<Constraint> constraint;
    private List<FeatureRelationship> featureRelationship;
}