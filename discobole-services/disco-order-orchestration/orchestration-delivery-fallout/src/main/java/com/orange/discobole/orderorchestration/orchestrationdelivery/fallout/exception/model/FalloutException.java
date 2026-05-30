// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ExceptionCode;
import lombok.Getter;
import lombok.Setter;

public abstract class FalloutException extends RuntimeException {


    @Getter
    protected boolean suspendNode = false;
    protected ExceptionCode code;
    @Setter
    @Getter
    protected String reason;
    @Getter
    protected boolean isHandled = false;


    protected FalloutException(String message) {
        super(message);
    }

    protected FalloutException(Throwable cause) {
        super(cause);
    }

    protected FalloutException() {
    }

    protected FalloutException(String message, Exception exception) {
        super(message, exception);
    }

    protected FalloutException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public abstract String getLogMessage();

    public String getCode() {
        return code.toString();
    }

}
