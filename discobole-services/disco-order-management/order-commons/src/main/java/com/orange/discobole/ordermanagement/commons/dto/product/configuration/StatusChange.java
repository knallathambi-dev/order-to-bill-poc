// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatusChange{
    private LocalDateTime changeDate = null;
    private String changeReason = null;
    private ProductMainStatus status;
    private String type = null;
    private String referredType = null;

}
