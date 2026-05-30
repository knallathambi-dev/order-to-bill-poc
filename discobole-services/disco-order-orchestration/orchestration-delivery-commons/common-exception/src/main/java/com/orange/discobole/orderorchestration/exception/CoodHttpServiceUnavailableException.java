// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception;

import com.orange.discobole.orderorchestration.exception.model.CoodHttpFailedException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

public class CoodHttpServiceUnavailableException extends CoodHttpFailedException {

    public CoodHttpServiceUnavailableException(String url, String message) {
        this.exceptionCode = ExceptionCode.HTTP_SERVICE_UNAVAILABLE;
        this.coodError = CoodError.builder()
                .code(this.exceptionCode.getCode())
                .message(this.exceptionCode.getMessagePattern().formatted(url, message))
                .reason(this.exceptionCode.getReason())
                .build();
    }

}
