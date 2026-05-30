// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@ApiModel(description = "A CPIB Configuration check from Admin API")
@JsonInclude(NON_EMPTY)
public class CpibConfiguration {

    private Boolean cpibCheck;

    public Boolean getCpibCheck() {
        return cpibCheck;
    }

    public void setCpibCheck(Boolean cpibCheck) {
        this.cpibCheck = cpibCheck;
    }
}
