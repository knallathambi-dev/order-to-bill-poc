// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model;

import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import lombok.Getter;
import lombok.Setter;

public abstract class CoodException extends RuntimeException {

    @Getter
    @Setter
    protected CoodError coodError;

    @Getter
    @Setter
    protected String exceptionCode;

    public CoodException() {
    }

    protected CoodException(String message, Exception exception) {
        super(message, exception);
    }

    protected CoodException(String message) {
        super(message);
    }

    protected CoodException(CoodError coodError) {
        super(coodError.message());
        this.coodError = coodError;
    }

    @Override
    public abstract String getMessage();

    public String getCode() {
        return coodError.code();
    }

}
