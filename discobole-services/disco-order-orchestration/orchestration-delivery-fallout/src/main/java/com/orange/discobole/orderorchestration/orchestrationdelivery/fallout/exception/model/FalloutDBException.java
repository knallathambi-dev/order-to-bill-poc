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

public class FalloutDBException extends FalloutException implements FalloutKafkaException {

    public FalloutDBException(Exception ex) {
        super(ex.getMessage());
        super.setStackTrace(ex.getStackTrace());
        this.code = ExceptionCode.FALLOUT_DB_EXCEPTION;
    }

    @Override
    public boolean getIsHandled() {
        return this.isHandled;
    }

    @Override
    public void setIsHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }

    public String getLogMessage() {
        return String.format("%s | message: {%S}", this.code, getMessage());
    }
}
