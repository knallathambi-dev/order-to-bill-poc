// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.RestErrorCode;
import lombok.Getter;

@Getter
public class FalloutIncidentApiQueryParamException extends RuntimeException {
    private final String message;
    private final String code;
    private final String reason;

    public FalloutIncidentApiQueryParamException(String message, Integer code) {
        this.reason = "Invalid query-string parameter value.";
        this.message = message;
        this.code = String.valueOf(code);
    }

    public FalloutIncidentApiQueryParamException(RestErrorCode restErrorCode, String message) {
        this.reason = restErrorCode.getMessage();
        this.code = String.valueOf(restErrorCode.getErrorCode());
        this.message = message;
    }

}
