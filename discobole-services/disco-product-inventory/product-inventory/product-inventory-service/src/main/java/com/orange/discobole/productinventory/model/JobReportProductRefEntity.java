// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class JobReportProductRefEntity {
    private String id;
    private String name;
    private String atType;
    private String externalIdentifier;
    private String failReason;


}
